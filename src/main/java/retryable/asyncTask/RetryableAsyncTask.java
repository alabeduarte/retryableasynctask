package retryable.asyncTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import retryable.asyncTask.checkers.ConnectivityChecker;

public abstract class RetryableAsyncTask<Params, Progress, Result> {
  private final Context context;
  private final ConnectivityChecker connectivityChecker;

  private AsyncTask task;

  public RetryableAsyncTask(Context context) {
    this.context = context;
    connectivityChecker = new ConnectivityChecker(context);
  }

  public final AsyncTask<Params, Progress, Result> execute(final Params... params) {
    if (connectivityChecker.isConnected()) {
      task = new ConcreteAsyncTask(this);
    } else {
      showDialog(context.getResources().getString(R.string.no_internet_connection), params);

      task = new NullableAsyncTask();
    }

    return task.execute(params);
  }

  public final Result get() throws InterruptedException, ExecutionException {
    return (Result) task.get();
  }

  protected void onPreExecute() {};

  protected abstract Result doInBackground(final Params... params);
  protected abstract void onPostExecute(final Result result);

  private void showDialog(String message, final Params... params) {
    AlertDialog dialog = new AlertDialog.Builder(context)
        .setMessage(message)
        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            RetryableAsyncTask.this.execute(params);
            dialog.dismiss();
          }
        })
        .create();

    dialog.show();
  }
}
