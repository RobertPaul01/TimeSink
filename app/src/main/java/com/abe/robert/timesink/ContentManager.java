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
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Robby on 4/7/17.
 */

public class ContentManager {

    // From Google developer console
    public static final String YOUTUBE_API_KEY = "***REMOVED***";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    private static ContentManager instance;
    private static String token;

    private YouTube youtube;

    private ContentManager() {
    }

    public static ContentManager getInstance() {
        if (instance == null)
            instance = new ContentManager();
        return instance;
    }

    public static ContentManager getInstance(String token) {
        ContentManager.token = token;
        if (instance == null)
            instance = new ContentManager();
        return instance;
    }

    public void makeQuery() {
        new YouTubeQuery().execute();
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private static void prettyPrint(Iterator<Video> iteratorSearchResults, String query) {
        Log.d("YouTubeQuery", "\n=============================================================");
        Log.d("YouTubeQuery", "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        Log.d("YouTubeQuery", "=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            Log.d("YouTubeQuery", " There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {
            Video singleVideo = iteratorSearchResults.next();
            String rId = singleVideo.getId();

            Log.d("YouTubeQuery", " Video Id" + rId);
            Log.d("YouTubeQuery", " Title: " + singleVideo.getSnippet().getTitle());
            Log.d("YouTubeQuery", " Description: " + singleVideo.getSnippet().getDescription());
            Log.d("YouTubeQuery", " Duration: " + singleVideo.getContentDetails().getDuration());
            Log.d("YouTubeQuery", "\n-------------------------------------------------------------\n");
        }
    }

    private class YouTubeQuery extends AsyncTask<String, Void, String> {

        public YouTubeQuery() {
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // This object is used to make YouTube Data API requests. The last
                // argument is required, but since we don't need anything
                // initialized when the HttpRequest is initialized, we override
                // the interface and provide a no-op function.
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("time-sink-94a21").build();

                // Define the API request for retrieving search results.
                YouTube.Search.List search = youtube.search().list("id,snippet");

                // Set your developer key from the {{ Google Cloud Console }} for
                // non-authenticated requests. See:
                // {{ https://cloud.google.com/console }}
                search.setOauthToken(ContentManager.token);
                search.set("key", YOUTUBE_API_KEY);

                String query = "Education";

                search.setQ(query);

                // Restrict the search results to only include videos. See:
                // https://developers.google.com/youtube/v3/docs/search/list#type
                search.setType("video");

                // To increase efficiency, only retrieve the fields that the
                // application uses.
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/description)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList != null) {
                    //prettyPrint(searchResultList.iterator(), query);
                }

                List<String> videoIds = new ArrayList<String>();

                if (searchResultList != null) {

                    // Merge video IDs
                    for (SearchResult searchResult : searchResultList) {
                        videoIds.add(searchResult.getId().getVideoId());
                    }
                    Joiner stringJoiner = Joiner.on(',');
                    String videoId = stringJoiner.join(videoIds);

                    // Call the YouTube Data API's youtube.videos.list method to
                    // retrieve the resources that represent the specified videos.
                    YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, recordingDetails, contentDetails").setId(videoId);
                    VideoListResponse listResponse = listVideosRequest.execute();

                    List<Video> videoList = listResponse.getItems();

                    if (videoList != null) {
                        prettyPrint(videoList.iterator(), query);
                    }
                }
            } catch (GoogleJsonResponseException e) {
                Log.e("YouTubeQuery", "There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
            } catch (IOException e) {
                Log.e("YouTubeQuery", "There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

}
