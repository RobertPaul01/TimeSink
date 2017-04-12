package com.abe.robert.timesink;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * This activity is where the content will be displayed.
 * In order for the YouTubePlayerSupportFragment to work,
 * the YouTube app must be installed on the device.
 *
 * Created by Robby on 4/4/17.
 */

public class ContentActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = "ContentActivity.java";

    // From www.console.developers.google.com
    public static final String YOUTUBE_API_KEY = "AIzaSyDp0k5y9Ru1GU7ftvlQ3jCVaxJjRQqmcWs";

    // Duration of content to display
    private int contentTime;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_content);

        // Catch the content time from MainActivity intent
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(MainActivity.CONTENT_TIME)) {
                String time = (String) b.getCharSequence(MainActivity.CONTENT_TIME);
                Log.i(TAG, "Bundle contains " + time + " in onCreate()");
                this.contentTime = Integer.parseInt(time);
            } else {
                Log.e(TAG, "Bundle b does not contain " + MainActivity.CONTENT_TIME + " in onCreate()");
            }
        } else {
            Log.e(TAG, "Bundle b is null in onCreate()");
        }

        // Initialize the YouTubePlayerSupportFragment
        YouTubePlayerSupportFragment youTubeFrag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubeFrag.initialize(ContentManager.YOUTUBE_API_KEY, this);
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
