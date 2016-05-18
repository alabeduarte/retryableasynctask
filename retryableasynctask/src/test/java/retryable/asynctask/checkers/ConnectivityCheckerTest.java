package retryable.asynctask.checkers;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNetworkInfo;

import retryable.asynctask.BuildConfig;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static android.net.NetworkInfo.DetailedState.CONNECTED;
import static android.net.NetworkInfo.DetailedState.DISCONNECTED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ConnectivityCheckerTest {

  ConnectivityChecker connectivityChecker;
  Activity activity;

  @Before
  public void setUp() {
    activity = buildActivity(Activity.class).create().get();

    connectivityChecker = new ConnectivityChecker(activity);
  }

  @Test
  public void itReturnsFalseWhenThereIsNoActivityInfoAvailable() {
    shadowOf(getConnectivityManager()).setActiveNetworkInfo(null);

    assertThat(connectivityChecker.isConnected(), is(false));
  }

  @Test
  public void itReturnsTrueWhenThereIsConnectivity() {
    connectToWifiNetwork();

    assertThat(connectivityChecker.isConnected(), is(true));
  }

  @Test
  public void itReturnsFalseWhenThereIsNoConnectivity() {
    disconnectToWifiNetwork();

    assertThat(connectivityChecker.isConnected(), is(false));
  }

  private void connectToWifiNetwork() {
    setWitiNetworkConnectivity(CONNECTED, true);
  }

  private void disconnectToWifiNetwork() {
    setWitiNetworkConnectivity(DISCONNECTED, false);
  }

  private void setWitiNetworkConnectivity(NetworkInfo.DetailedState status, boolean isConnected) {
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

}