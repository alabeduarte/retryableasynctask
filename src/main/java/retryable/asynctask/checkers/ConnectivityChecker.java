package retryable.asynctask.checkers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityChecker {
  private final Context context;

  public ConnectivityChecker(Context context) {
    this.context = context;
  }

  public boolean isConnected() {
    return getActiveNetworkInfo() != null &&
        getActiveNetworkInfo().isConnectedOrConnecting();
  }

  private NetworkInfo getActiveNetworkInfo() {
    return getConnectivityManager().getActiveNetworkInfo();
  }

  private ConnectivityManager getConnectivityManager() {
    return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }
}
