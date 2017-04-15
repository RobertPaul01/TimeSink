package com.abe.robert.timesink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.Random;

/**
 * This activity is where the content will be displayed.
 * In order for the YouTubePlayerSupportFragment to work,
 * the YouTube app must be installed on the device.
 *
 * Created by Robby on 4/4/17.
 */

public class ContentActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    // Logging tag
    private static final String TAG = "ContentActivity.java";

    // From www.console.developers.google.com
    public static final String YOUTUBE_API_KEY = "AIzaSyDp0k5y9Ru1GU7ftvlQ3jCVaxJjRQqmcWs";

    // Data from MainActivity bundle
    private int contentTime;
    private ArrayList<String> queryTerms;

    // Current video ID
    private String currentId;

    // UI views
    private YouTubePlayer youTubePlayer;
    private Button nextButton;
    private Button prevButton;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_content);

        nextButton = (Button) findViewById(R.id.bt_next);
        prevButton = (Button) findViewById(R.id.bt_prev);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextVideo();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrevVideo();
            }
        });

        // Catch the content time from MainActivity intent
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(MainActivity.CONTENT_TIME)) {
                String time = (String) b.getCharSequence(MainActivity.CONTENT_TIME);
                Log.i(TAG, "Bundle contains " + time + " in onCreate()");
                contentTime = Integer.parseInt(time);
            } else {
                Log.e(TAG, "Bundle b does not contain " + MainActivity.CONTENT_TIME + " in onCreate()");
            }

            if (b.containsKey(MainActivity.CHECK_BOXES)) {
                queryTerms = b.getStringArrayList(MainActivity.CHECK_BOXES);
            } else {
                Log.e(TAG, "Bundle b does not contain " + MainActivity.CHECK_BOXES + " in onCreate()");
            }
        } else {
            Log.e(TAG, "Bundle b is null in onCreate()");
        }

        StringBuilder queryStr = new StringBuilder();
        for (String term : queryTerms) {
            queryStr.append(term).append("|");
        }
        queryStr.deleteCharAt(queryStr.length()-1);

        ContentManager.getInstance().makeQuery(contentTime, queryStr.toString());

        // Initialize the YouTubePlayerSupportFragment
        YouTubePlayerSupportFragment youTubeFrag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubeFrag.initialize(ContentManager.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Log.d(TAG, "Youtube initialization success. Need to look for content duration: " + contentTime + " mins.");
        this.youTubePlayer = youTubePlayer;
        loadNextVideo();
    }

    private void loadNextVideo() {
        String nextId = ContentManager.getInstance().getNextVideo(currentId);
        if (nextId != null) {
            currentId = nextId;
            youTubePlayer.loadVideo(nextId);
        }
    }

    private void loadPrevVideo() {
        // If prevVideo id is null, nothing should happen.
        String prevId = ContentManager.getInstance().getPrevVideo();
        if (prevId != null) {
            currentId = prevId;
            youTubePlayer.loadVideo(prevId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(ContentActivity.this, "Youtube initialization failure!! " + youTubeInitializationResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        isYoutubeInstalled();
    }

    private void isYoutubeInstalled() {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(LoginActivity.YOUTUBE_PACKAGE_NAME);
        if (mIntent == null) {
            Toast.makeText(ContentActivity.this, "Youtube needs to be installed for Time Sink to work", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + LoginActivity.YOUTUBE_PACKAGE_NAME)));
            this.finishAffinity();
        }
    }
}
