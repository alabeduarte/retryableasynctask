package retryable.asynctask;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutionException;

import retryable.asynctask.checkers.ConnectivityChecker;
import retryable.asynctask.dialogs.RetryableDialog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.flushBackgroundThreadScheduler;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RetryableAsyncTaskTest {

  RetryableAsyncTask<String, Void, String> retryableAsyncTask;
  Activity activity;

  @Mock ConnectivityChecker connectivityCheckerMock;
  @Mock RetryableDialog retryableDialogMock;

  @Mock AsyncTaskStep onPreExecuteMock;
  @Mock AsyncTaskStep doInBackgroundMock;
  @Mock AsyncTaskStep onPostExecuteMock;

  @Before
  public void setUp() {
    initMocks(this);

    when(connectivityCheckerMock.isConnected()).thenReturn(true);

    activity = buildActivity(Activity.class).create().get();
    retryableAsyncTask = new RetryableAsyncTask<String, Void, String>(activity, connectivityCheckerMock, retryableDialogMock) {

      @Override
      protected void onPreExecute() {
        onPreExecuteMock.call();
      }

      @Override
      protected String doInBackground(String... params) {
        doInBackgroundMock.call((Object[]) params);

        return "bar";
      }

      @Override
      protected void onPostExecute(String result) {
        onPostExecuteMock.call(result);
      }
    };
  }

  @Test
  public void itRetriesAsyncTaskExecution() throws ExecutionException, InterruptedException {
    retryableAsyncTask.retry();

    assertThat(retryableAsyncTask.get(), is("bar"));
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
  public void itShowsDialogWhenThereIsNoConnectivity() {
    when(connectivityCheckerMock.isConnected()).thenReturn(false);

    retryableAsyncTask.execute();

    verify(retryableDialogMock).show(application.getString(R.string.no_internet_connection));
  }

  @Test
  public void itDoesNotExecutAsyncTaskIfThereIsNoConnectivity() throws ExecutionException, InterruptedException {
    when(connectivityCheckerMock.isConnected()).thenReturn(false);

    retryableAsyncTask.execute();

    flushBackgroundThreadScheduler();

    assertNull(retryableAsyncTask.get());
  }

  @Test
  public void itDoesNotShowDialogWhenThereIsConnectivity() {
    when(connectivityCheckerMock.isConnected()).thenReturn(true);

    retryableAsyncTask.execute();

    verify(retryableDialogMock, never()).show(application.getString(R.string.no_internet_connection));
  }

  @Test
  public void itShowsDialogOnError() {
    String params = "foo";

    retryableAsyncTask.execute(params);

    retryableAsyncTask.onError(new RuntimeException(), params);

    verify(retryableDialogMock).show(application.getString(R.string.something_went_wrong), params);
  }

  private class AsyncTaskStep {
    public void call() {}

    public void call(Object... args) {}
  }
}