package com.abe.robert.timesink;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.api.services.youtube.YouTube;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Robby on 4/7/17.
 */

public class ContentManager {

    // Logging tag
    private static final String TAG = "ContentManager";

    // From Google developer console
    public static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY;

    // Singleton
    private static ContentManager instance;

    // Video data
    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
    private Stack<VideoData> videoIds;

    private ContentManager() {
        videoIds = new Stack<>();
    }

    public static ContentManager getInstance() {
        if (instance == null)
            instance = new ContentManager();
        return instance;
    }

    public VideoData getNextVideo() {
        if (videoIds.empty()) {
            // TODO This will crash for now
            return null;
        }
        VideoData vD = videoIds.pop();
        return vD;
    }

    public void makeQuery(int durataion, String terms) {
        try {
            new YouTubeQuery(durataion, terms).execute().get(10000L, TimeUnit.MILLISECONDS);
            Collections.shuffle(videoIds);
        } catch (InterruptedException e) {
            Log.d(TAG, "makeQuery() InterruptedException");
        } catch (ExecutionException e) {
            Log.d(TAG, "makeQuery() ExecutionException");
        } catch (TimeoutException e) {
            Log.d(TAG, "makeQuery() TimeoutException");
        }
    }

    private class YouTubeQuery extends AsyncTask<String, Void, String> {

        private final String TAG = "YouTubeQuery";

        // Query information.
        private int duration;
        private String terms;

        public YouTubeQuery(int duration, String terms) {
            this.duration = duration;
            this.terms = terms;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Recreate the stack
                videoIds = new Stack<>();

                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setYouTubeRequestInitializer(new YouTubeRequestInitializer(YOUTUBE_API_KEY)).setApplicationName("TimeSink").build();

                // Define the API request for retrieving search results.
                YouTube.Search.List search = youtube.search().list("id,snippet");

                // Set query terms
                search.setQ(terms);

                // Set video durations.
                if (this.duration < 4) {
                    search.setVideoDuration("short");
                } else if (this.duration <= 20) {
                    search.setVideoDuration("medium");
                } else {
                    search.setVideoDuration("long");
                }

                // Restrict the search results to only include videos. See:
                // https://developers.google.com/youtube/v3/docs/search/list#type
                search.setType("video");

                // To increase efficiency, only retrieve the fields that the
                // application uses.
                search.setFields("items(id/videoId)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList == null) {
                    // TODO
                    Log.e(TAG, "searchResults are null");
                }

                List<String> searchVideoIds = new ArrayList<>();

                if (searchResultList != null) {
                    // Merge video IDs
                    for (SearchResult searchResult : searchResultList) {
                        searchVideoIds.add(searchResult.getId().getVideoId());
                    }
                    Joiner stringJoiner = Joiner.on(',');
                    String videoId = stringJoiner.join(searchVideoIds);

                    YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, contentDetails").setId(videoId);
                    VideoListResponse listResponse = listVideosRequest.execute();
                    List<Video> videoList = listResponse.getItems();
                    //debugPrint(videoList.iterator());
                    for (Video video : videoList) {
                        VideoSnippet snip = video.getSnippet();
                        VideoContentDetails contentDetails = video.getContentDetails();
                        String durationStr = contentDetails.getDuration();
                        int minInt = Integer.parseInt(durationStr.substring(2, durationStr.indexOf('M')));
                        if (minInt <= duration+2 && minInt >= duration-2) {
                            VideoData data = new VideoData(video.getId(), snip.getTitle(), snip.getDescription());
                            videoIds.add(data);
                        }
                    }
                }
            } catch (GoogleJsonResponseException e) {
                Log.e(TAG, "There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
            } catch (IOException e) {
                Log.e(TAG, "There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        // This should be used to debug queries.
        private void debugPrint(Iterator<Video> iteratorSearchResults) {
            Log.d(TAG, "\n=============================================================");
            Log.d(TAG, "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos");
            Log.d(TAG, "=============================================================\n");

            if (!iteratorSearchResults.hasNext()) {
                Log.d(TAG, " There aren't any results for your query.");
            }

            while (iteratorSearchResults.hasNext()) {
                Video singleVideo = iteratorSearchResults.next();
                String rId = singleVideo.getId();

                Log.d(TAG, " Video Id" + rId);
                Log.d(TAG, " Title: " + singleVideo.getSnippet().getTitle());
                Log.d(TAG, " Audio Language: " + singleVideo.getSnippet().getLocalized());
                Log.d(TAG, " Duration: " + singleVideo.getContentDetails().getDuration());
                Log.d(TAG, "\n-------------------------------------------------------------\n");
            }
        }
    }
}
