package retryableasynctask.web;

import java.util.List;

import retrofit.http.GET;

public interface HackerNewsApi {
    @GET("/askstories.json?print=pretty")
    List<Long> askStories();
}
