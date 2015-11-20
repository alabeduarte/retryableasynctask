package retryable.asynctask.dialogs;

import android.app.Activity;
import android.app.AlertDialog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import retryable.asynctask.BuildConfig;
import retryable.asynctask.R;
import retryable.asynctask.RetryableAsyncTask;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowAlertDialog.getLatestAlertDialog;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RetryableDialogTest {

  static final String ALERT_MESSAGE = "Uh oh!";
  static final String ASYNC_TASK_PARAMS = "foo";

  RetryableDialog retryableDialog;

  RetryableAsyncTask spiedRetryableAsyncTask;

  @Before
  public void setUp() {
    initMocks(this);

    Activity activity = buildActivity(Activity.class).create().get();
    spiedRetryableAsyncTask = spy(new RetryableAsyncTask<String, Void, String>(activity) {
      @Override
      protected String doInBackground(String... params) {
        return "foo bar";
      }
    });

    retryableDialog = new RetryableDialog(spiedRetryableAsyncTask);
  }

  @Test
  public void itSetsAlertMessage() {
    retryableDialog.show(ALERT_MESSAGE);

    AlertDialog alert = getLatestAlertDialog();
    String alertMessage = valueOf(shadowOf(alert).getMessage());

    assertThat(alertMessage, is(ALERT_MESSAGE));
  }

  @Test
  public void itSetsButtonPositiveLabel() {
    retryableDialog.show(ALERT_MESSAGE);

    AlertDialog alert = getLatestAlertDialog();
    String buttonPositiveText = valueOf(alert.getButton(BUTTON_POSITIVE).getText());

    assertThat(buttonPositiveText, is(application.getString(R.string.retry)));
  }

  @Test
  public void itSetsButtonNegativeLabel() {
    retryableDialog.show(ALERT_MESSAGE);

    AlertDialog alert = getLatestAlertDialog();
    String buttonNegativeText = valueOf(alert.getButton(BUTTON_NEGATIVE).getText());

    assertThat(buttonNegativeText, is(application.getString(R.string.cancel)));
  }

  @Test
  public void itDelegatesToRetryableAsyncTaskToTryAgain() {
    retryableDialog.show(ALERT_MESSAGE, ASYNC_TASK_PARAMS);

    getLatestAlertDialog().getButton(BUTTON_POSITIVE).performClick();

    verify(spiedRetryableAsyncTask).retry(ASYNC_TASK_PARAMS);
  }

}