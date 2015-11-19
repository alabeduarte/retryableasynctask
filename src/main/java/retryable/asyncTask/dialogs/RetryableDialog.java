package retryable.asyncTask.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;

import retryable.asyncTask.R;
import retryable.asyncTask.RetryableAsyncTask;

public class RetryableDialog {
  private final RetryableAsyncTask retryableAsyncTask;

  public RetryableDialog(RetryableAsyncTask retryableAsyncTask) {
    this.retryableAsyncTask = retryableAsyncTask;
  }

  public AlertDialog show(String message, final Object... params) {
    AlertDialog dialog = new AlertDialog.Builder(retryableAsyncTask.getContext())
      .setMessage(message)
      .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          retryableAsyncTask.retry(params);
        }
      })
      .setNegativeButton(R.string.cancel, noClickListener())
      .create();

    dialog.show();

    return dialog;
  }

  private DialogInterface.OnClickListener noClickListener() {
    return new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
      }
    };
  }
}
