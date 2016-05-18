package retryable.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import retryable.asynctask.checkers.ConnectivityChecker;
import retryable.asynctask.dialogs.RetryableDialog;

public abstract class RetryableAsyncTask<Params, Progress, Result> {
  private final Context context;
  private final ConnectivityChecker connectivityChecker;
  private final RetryableDialog retryableDialog;

  private AsyncTask task;

  public RetryableAsyncTask(Context context) {
    this.context = context;
    connectivityChecker = new ConnectivityChecker(context);
    retryableDialog = new RetryableDialog(this);
  }

  protected RetryableAsyncTask(Context context, ConnectivityChecker connectivityChecker, RetryableDialog retryableDialog) {
    this.context = context;
    this.connectivityChecker = connectivityChecker;
    this.retryableDialog = retryableDialog;
  }

  public final AsyncTask<Params, Progress, Result> execute(final Params... params) {
    if (connectivityChecker.isConnected()) {
      task = new ConcreteAsyncTask(this);
    } else {
      retryableDialog.show(context.getResources().getString(R.string.no_internet_connection), params);

      task = new NullableAsyncTask();
    }

    return task.execute(params);
  }

  public final AsyncTask<Params, Progress, Result> retry(Params... params) {
    return this.execute(params);
  }

  public final Result get() throws InterruptedException, ExecutionException {
    if (task.get() == null) { return null; }

    return (Result) ((AsyncTaskResult) task.get()).successValue();
  }

  public Context getContext() {
    return context;
  }

  protected void onPreExecute() {};

  protected abstract Result doInBackground(final Params... params);

  protected void onPostExecute(final Result result) {}

  protected void onError(Throwable error, final Params... params) {
    Log.e(getClass().getCanonicalName(), error.getMessage(), error);

    retryableDialog.show(context.getResources().getString(R.string.something_went_wrong), params);
  }
}