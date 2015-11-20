package retryable.asyncTask;

public class AsyncTaskResult<SuccessT, FailureT> {
  private final SuccessT success;
  private final FailureT failure;
  private final Object[] params;

  public static <SuccessT, FailureT> AsyncTaskResult<SuccessT, FailureT> success(SuccessT success) {
    return new AsyncTaskResult<>(success, null);
  }

  public static <SuccessT, FailureT> AsyncTaskResult<SuccessT, FailureT> failure(FailureT failure, Object... params) {
    return new AsyncTaskResult<>(null, failure, params);
  }

  private AsyncTaskResult(SuccessT success, FailureT failure, Object... params) {
    this.success = success;
    this.failure = failure;
    this.params = params;
  }

  public boolean isFailure() {
    return failure != null;
  }

  public SuccessT successValue() {
    return success;
  }

  public FailureT failureValue() {
    return failure;
  }

  public Object[] getParams() {
    return params;
  }
}
