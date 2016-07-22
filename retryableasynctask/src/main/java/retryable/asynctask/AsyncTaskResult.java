package retryable.asynctask;

public class AsyncTaskResult<FailureT, SuccessT> {
  private final SuccessT success;
  private final FailureT failure;
  private final Object[] params;

  public static <FailureT, SuccessT> AsyncTaskResult<FailureT, SuccessT> success(SuccessT success) {
    return new AsyncTaskResult<>(null, success);
  }

  public static <FailureT, SuccessT> AsyncTaskResult<FailureT, SuccessT> failure(FailureT failure, Object... params) {
    return new AsyncTaskResult<>(failure, null, params);
  }

  private AsyncTaskResult(FailureT failure, SuccessT success, Object... params) {
    this.failure = failure;
    this.success = success;
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
