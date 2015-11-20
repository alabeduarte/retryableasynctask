package retryable.asyncTask;

import android.os.AsyncTask;

import static retryable.asyncTask.AsyncTaskResult.failure;
import static retryable.asyncTask.AsyncTaskResult.success;

class ConcreteAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, AsyncTaskResult<Result, Throwable>> {
  private RetryableAsyncTask parentTask;

  public ConcreteAsyncTask(RetryableAsyncTask parentTask) {
    this.parentTask = parentTask;
  }

  @Override
  protected void onPreExecute() {
    parentTask.onPreExecute();
  }

  @Override
  protected AsyncTaskResult<Result, Throwable> doInBackground(final Params... params) {
    try {
      return success((Result) parentTask.doInBackground(params));
    } catch(Throwable ex) {
      return failure(ex, params);
    }
  }

  @Override
  protected void onPostExecute(AsyncTaskResult<Result, Throwable> result) {
    if (result.isFailure()) {
      parentTask.onError(result.failureValue(), result.getParams());
    } else {
      parentTask.onPostExecute(result.successValue());
    }
  }
}
