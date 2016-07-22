package retryable.asynctask;

import android.os.AsyncTask;

import static retryable.asynctask.AsyncTaskResult.failure;
import static retryable.asynctask.AsyncTaskResult.success;

class ConcreteAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, AsyncTaskResult<Throwable, Result>> {
  private final RetryableAsyncTask parentTask;

  public ConcreteAsyncTask(RetryableAsyncTask parentTask) {
    this.parentTask = parentTask;
  }

  @Override
  protected void onPreExecute() {
    parentTask.onPreExecute();
  }

  @Override
  protected AsyncTaskResult<Throwable, Result> doInBackground(final Params... params) {
    try {
      return success((Result) parentTask.doInBackground(params));
    } catch(Throwable ex) {
      return failure(ex, params);
    }
  }

  @Override
  protected void onPostExecute(AsyncTaskResult<Throwable, Result> result) {
    if (result.isFailure()) {
      parentTask.onError(result.failureValue(), result.getParams());
    } else {
      parentTask.onPostExecute(result.successValue());
    }
  }

  @Override
  protected void onCancelled(AsyncTaskResult<Throwable, Result> result) {
    super.onCancelled(result);

    parentTask.onCancelled(result);
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();

    parentTask.onCancelled();
  }
}