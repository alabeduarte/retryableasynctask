package retryable.asynctask.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;

import retryable.asynctask.R;
import retryable.asynctask.RetryableAsyncTask;

public class RetryableDialog {
  private final RetryableAsyncTask retryableAsyncTask;

  public RetryableDialog(RetryableAsyncTask retryableAsyncTask) {
    this.retryableAsyncTask = retryableAsyncTask;
  }

  public AlertDialog show(String message, final Object... params) {
    AlertDialog dialog = new AlertDialog.Builder(retryableAsyncTask.getContext())
      .setMessage(message)
      .setPositiveButton(R.string.retry, retry(retryableAsyncTask, params))
      .setNegativeButton(R.string.cancel, cancel(retryableAsyncTask))
      .create();

    dialog.show();

    return dialog;
  }

  private DialogInterface.OnClickListener retry(final RetryableAsyncTask retryableAsyncTask, final Object... params) {
    return new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        retryableAsyncTask.retry(params);
      }
    };
  }

  private DialogInterface.OnClickListener cancel(final RetryableAsyncTask retryableAsyncTask) {
    return new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        retryableAsyncTask.cancel();
      }
    };
  }
}
