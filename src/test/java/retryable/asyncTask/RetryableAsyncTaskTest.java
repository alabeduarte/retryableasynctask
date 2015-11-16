package retryable.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.util.concurrent.ExecutionException;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static android.net.NetworkInfo.DetailedState.CONNECTED;
import static android.net.NetworkInfo.DetailedState.DISCONNECTED;
import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.flushBackgroundThreadScheduler;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowAlertDialog.getLatestAlertDialog;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RetryableAsyncTaskTest {

  RetryableAsyncTask<String, Void, String> retryableAsyncTask;
  Activity activity;

  @Mock AsyncTaskStep onPreExecuteMock;
  @Mock AsyncTaskStep doInBackgroundMock;
  @Mock AsyncTaskStep onPostExecuteMock;

  @Before
  public void setUp() {
    initMocks(this);

    activity = buildActivity(Activity.class).create().get();
    retryableAsyncTask = new RetryableAsyncTask<String, Void, String>(activity) {

      @Override
      protected void onPreExecute() {
        onPreExecuteMock.call();
      }

      @Override
      protected String doInBackground(String... params) {
        doInBackgroundMock.call(params);

        return "bar";
      }

      @Override
      protected void onPostExecute(String result) {
        onPostExecuteMock.call(result);
      }
    };
  }

  @Test
  public void itShowsDialogWhenThereIsNoConnectivity() {
    disconnectToWifiNetwork();
    retryableAsyncTask.execute();

    assertThatAlertNoInternetDialogIsShown();
  }

  @Test
  public void itRetriesAsyncTaskExecutionOnRetryButtonTouch() throws ExecutionException, InterruptedException {
    disconnectToWifiNetwork();
    retryableAsyncTask.execute();

    flushBackgroundThreadScheduler();

    assertThatAlertNoInternetDialogIsShown();
    assertNull(retryableAsyncTask.get());

    connectToWifiNetwork();

    AlertDialog alert = getLatestAlertDialog();
    alert.getButton(BUTTON_POSITIVE).performClick();

    flushBackgroundThreadScheduler();

    assertThat(retryableAsyncTask.get(), is("bar"));
    assertThat(getLatestAlertDialog().isShowing(), is(false));
  }

  @Test
  public void itShowsDialogWhenActiveNetworkIsNull() {
    shadowOf(getConnectivityManager()).setActiveNetworkInfo(null);

    retryableAsyncTask.execute();

    assertThatAlertNoInternetDialogIsShown();
  }

  @Test
  public void itDoesNotShowDialogWhenThereIsConnectivity() {
    connectToWifiNetwork();
    retryableAsyncTask.execute();

    assertNull(getLatestAlertDialog());
  }

  @Test
  public void itDoesNotExecutAsyncTaskIfThereIsNoConnectivity() throws ExecutionException, InterruptedException {
    disconnectToWifiNetwork();
    retryableAsyncTask.execute();

    flushBackgroundThreadScheduler();

    assertNull(retryableAsyncTask.get());
  }

  @Test
  public void itDelegatesTaskToAsyncTaskWhenGetIsCalled() throws ExecutionException, InterruptedException {
    retryableAsyncTask.execute("foo");

    flushBackgroundThreadScheduler();

    assertThat(retryableAsyncTask.get(), is("bar"));
  }

  @Test
  public void itDelegatesTaskToAsyncTaskWhenOnPreExecuteIsCalled() {
    retryableAsyncTask.execute("foo");

    verify(onPreExecuteMock).call();
  }

  @Test
  public void itDelegatesTaskToAsyncTaskWhenDoInBackgroundIsCalled() {
    retryableAsyncTask.execute("foo");

    verify(doInBackgroundMock).call("foo");
  }

  @Test
  public void itDelegatesTaskToAsyncTaskWhenOnPostExecuteIsCalled() {
    retryableAsyncTask.execute("foo");

    verify(onPostExecuteMock).call("bar");
  }

  @Test
  public void itShowsDialogOnDoingBackgroundFailure() {
    doThrow(new RuntimeException()).when(doInBackgroundMock).call("foo");

    retryableAsyncTask.execute("foo");

    assertThatAlertGenericErrorDialogIsShown(application.getString(R.string.something_went_wrong));
  }

  @Test
  public void itDismissesAlertDialogOnCancelButtonTouch() throws ExecutionException, InterruptedException {
    doThrow(new RuntimeException()).when(doInBackgroundMock).call("foo");

    retryableAsyncTask.execute("foo");

    assertThat(getLatestAlertDialog().isShowing(), is(true));

    AlertDialog alert = getLatestAlertDialog();
    alert.getButton(BUTTON_NEGATIVE).performClick();

    assertThat(getLatestAlertDialog().isShowing(), is(false));
  }

  private void connectToWifiNetwork() {
    setWiFiNetworkConnectivity(CONNECTED, true);
  }

  private void disconnectToWifiNetwork() {
    setWiFiNetworkConnectivity(DISCONNECTED, false);
  }

  private void setWiFiNetworkConnectivity(NetworkInfo.DetailedState status, boolean isConnected) {
    ConnectivityManager connectivityManager = getConnectivityManager();
    int subType = 0;
    boolean isAvailable = true;

    NetworkInfo networkInfo = ShadowNetworkInfo.newInstance(
        status, TYPE_WIFI, subType, isAvailable, isConnected
    );

    shadowOf(connectivityManager).setActiveNetworkInfo(networkInfo);
  }

  private ConnectivityManager getConnectivityManager() {
    return (ConnectivityManager) application.getSystemService(CONNECTIVITY_SERVICE);
  }

  private void assertThatAlertNoInternetDialogIsShown() {
    assertThatAlertGenericErrorDialogIsShown(application.getString(R.string.no_internet_connection));
  }

  private void assertThatAlertGenericErrorDialogIsShown(String message) {
    AlertDialog alert = getLatestAlertDialog();
    String alertMessage = valueOf(shadowOf(alert).getMessage());
    String buttonPositiveText = valueOf(alert.getButton(BUTTON_POSITIVE).getText());
    String buttonNegativeText = valueOf(alert.getButton(BUTTON_NEGATIVE).getText());

    assertThat(alertMessage, is(message));
    assertThat(buttonPositiveText, is(application.getString(R.string.retry)));
    assertThat(buttonNegativeText, is(application.getString(R.string.cancel)));
    assertThat(getLatestAlertDialog().isShowing(), is(true));
  }

  private class AsyncTaskStep {
    public void call() {}

    public void call(Object... args) {}
  }
}