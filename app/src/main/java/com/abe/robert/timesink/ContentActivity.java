package com.abe.robert.timesink;

import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * This activity is where the content will be displayed.
 * Currently, this is extending YouTubeBaseActivity and only
 * supporting YouTube videos. In order for these videos to work,
 * the YouTube app must be installed on the device.
 *
 * Created by Robby on 4/4/17.
 */

public class ContentActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = "ContentActivity.java";

    // From www.console.developers.google.com
    public static final String YOUTUBE_API_KEY = "AIzaSyDp0k5y9Ru1GU7ftvlQ3jCVaxJjRQqmcWs";

    // Duration of content to display
    private int contentTime;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_content);
        // Catch the content time from MainActivity intent
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("CONTENT_TIME")) {
                String time = (String) b.getCharSequence("CONTENT_TIME");
                Log.i(TAG, "Bundle contains " + time + " in onCreate()");
                this.contentTime = Integer.parseInt(time);
            } else {
                Log.e(TAG, "Bundle b does not contain CONTENT_TIME in onCreate()");
            }
        } else {
            Log.e(TAG, "Bundle b is null in onCreate()");
        }

        // Initialize the youtube view
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubeView.initialize(YOUTUBE_API_KEY, ContentActivity.this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Toast.makeText(ContentActivity.this, "Youtube initialization success. Need to look for content duration: " + contentTime + " mins.", Toast.LENGTH_LONG).show();
        youTubePlayer.loadVideo("dQw4w9WgXcQ");
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(ContentActivity.this, "Youtube initialization failure!! " + youTubeInitializationResult, Toast.LENGTH_LONG).show();
    }
}
