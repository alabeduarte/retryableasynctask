package retryable.asyncTask;

public class AsyncTaskResult<SuccessT, FailureT> {
  private final SuccessT success;
  private final FailureT failure;

  public static <SuccessT, FailureT> AsyncTaskResult<SuccessT, FailureT> success(SuccessT success) {
    return new AsyncTaskResult<>(success, null);
  }

  public static <SuccessT, FailureT> AsyncTaskResult<SuccessT, FailureT> failure(FailureT failure, Object... params) {
    return new AsyncTaskResult<>(null, failure);
  }

  private AsyncTaskResult(SuccessT success, FailureT failure) {
    this.success = success;
    this.failure = failure;
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
}
