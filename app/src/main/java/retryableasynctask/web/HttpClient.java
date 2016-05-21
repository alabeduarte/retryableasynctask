package retryableasynctask.web;

import retrofit.RestAdapter;

public class HttpClient {
    private final RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder();

    public HackerNewsApi getHackerNewsApi() {
        String endPoint = "https://hacker-news.firebaseio.com/v0";

        return restAdapterBuilder
                .setEndpoint(endPoint)
                .build()
                .create(HackerNewsApi.class);
    }

    public HackerNewsApi getInvalidApi() {
        String endPoint = "https://10.0.2.2";

        return restAdapterBuilder
                .setEndpoint(endPoint)
                .build()
                .create(HackerNewsApi.class);
    }
}
