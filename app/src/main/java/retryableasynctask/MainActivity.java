package retryableasynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import retryable.asynctask.RetryableAsyncTask;
import retryableasynctask.retryableasynctask.R;
import retryableasynctask.web.HttpClient;

public class MainActivity extends Activity {

    private final HttpClient httpClient = new HttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.valid_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HackerNewsStories().execute();
            }
        });

        findViewById(R.id.invalid_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InvalidHackerNewsStories().execute();
            }
        });

        findViewById(R.id.retryable_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetryableHackerNewsStories().execute();
            }
        });
    }

    private class HackerNewsStories extends RetryableAsyncTask<Void, Void, List<Long>> {
        public HackerNewsStories() {
            super(MainActivity.this);
        }

        @Override
        protected List<Long> doInBackground(Void... params) {
            return httpClient.getHackerNewsApi().askStories();
        }

        @Override
        protected void onPostExecute(List<Long> stories) {
            new AlertDialog.Builder(this.getContext())
                    .setMessage("Stories: " + stories.toString())
                    .create()
                    .show();
        }
    }

    private class InvalidHackerNewsStories extends RetryableAsyncTask<Void, Void, List<Long>> {
        public InvalidHackerNewsStories() {
            super(MainActivity.this);
        }

        @Override
        protected List<Long> doInBackground(Void... params) {
            return httpClient.getInvalidApi().askStories();
        }
    }

    private class RetryableHackerNewsStories extends RetryableAsyncTask<Void, Void, List<Long>> {
        public RetryableHackerNewsStories() {
            super(MainActivity.this);
        }

        @Override
        protected List<Long> doInBackground(Void... params) {
            return httpClient.getInvalidApi().askStories();
        }

        @Override
        protected void onError(Throwable error, Void... params) {
            new AlertDialog.Builder(getContext())
                    .setMessage("This is a custom dialog in case of something goes wrong. Click on retry and we'll give you a fallback.")
                    .setPositiveButton(retryable.asynctask.R.string.retry, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            new HackerNewsStories().execute();
                        }
                    })
                    .create()
                    .show();
        }
    }
}
