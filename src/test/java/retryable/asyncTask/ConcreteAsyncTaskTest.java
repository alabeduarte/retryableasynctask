package retryable.asyncTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
    concreteAsyncTask.execute();

    verify(retryableAsyncTask).onPostExecute(null);
  }

}