package retryable.asyncTask;

import android.os.AsyncTask;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class NullableAsyncTaskTest {

  @Test
  public void itDoesNothing() throws ExecutionException, InterruptedException {
    AsyncTask nullableAsyncTask = new NullableAsyncTask();

    nullableAsyncTask.execute();

    assertNull(nullableAsyncTask.get());
  }
}