package retryable.asyncTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.flushBackgroundThreadScheduler;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ConcreteAsyncTaskTest {

  ConcreteAsyncTask<String, Void, Object> concreteAsyncTask;

  @Mock RetryableAsyncTask retryableAsyncTask;

  @Before
  public void setUp() {
    initMocks(this);

    concreteAsyncTask = new ConcreteAsyncTask<>(retryableAsyncTask);
  }

  @Test
  public void itDelegatesToParentTaskOnPreExecute() {
    concreteAsyncTask.execute();

    verify(retryableAsyncTask).onPreExecute();
  }

  @Test
  public void itDelegatesToParentTaskOnDoingBackgroundTask() {
    String params = "foo";

    concreteAsyncTask.execute(params);

    verify(retryableAsyncTask).doInBackground(params);
  }

  @Test
  public void itDelegatesToParentTaskOnPostExecute() {
    when(retryableAsyncTask.doInBackground("foo")).thenReturn("bar");

    concreteAsyncTask.execute("foo");

    verify(retryableAsyncTask).onPostExecute("bar");
  }

  @Test
  public void itShowsDialogOnDoingBackgroundFailure() {
    RuntimeException error = new RuntimeException();

    doThrow(error).when(retryableAsyncTask).doInBackground("foo");

    concreteAsyncTask.execute("foo");

    verify(retryableAsyncTask).onError(error, "foo");
  }

}