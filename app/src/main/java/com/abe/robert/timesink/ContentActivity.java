package com.abe.robert.timesink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;

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

    // Data from MainActivity bundle
    private ArrayList<String> queryTerms;

    // UI views
    private YouTubePlayer youTubePlayer;
    private Button nextButton;
    private ImageButton thumbsDown, thumbsUp;
    private TextView desc;

    // Current video data
    private String queryStr;
    private int contentTime;
    private VideoData curData;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_content);

        nextButton = (Button) findViewById(R.id.bt_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumbsDown.setSelected(false);
                thumbsDown.setImageResource(R.drawable.ic_thumb_down_black_36dp);
                thumbsUp.setSelected(false);
                thumbsUp.setImageResource(R.drawable.ic_thumb_up_black_36dp);
                loadNextVideo();
            }
        });

        thumbsDown = (ImageButton) findViewById(R.id.ib_thumbsDown);
        thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = curData.getVideoId();
                // already selected, deselect
                if(thumbsDown.isSelected()) {
                    thumbsDown.setImageResource(R.drawable.ic_thumb_down_black_36dp);
                    thumbsDown.setSelected(false);
                    MainActivity.dislikes.remove(id);
                    MainActivity.mFirebaseDatabase.child("videos").child(MainActivity.mFireBaseUserId).getRef().removeValue(); //remove from database
                }
                else {
                    thumbsDown.setImageResource(R.drawable.ic_thumb_down_white_36dp);
                    thumbsDown.setSelected(true);
                    if(thumbsUp.isSelected()) {
                        thumbsUp.setImageResource(R.drawable.ic_thumb_up_black_36dp);
                        thumbsUp.setSelected(false);
                    }
                    MainActivity.dislikes.add(id);
                    MainActivity.mFirebaseDatabase.child("videos").child(MainActivity.mFireBaseUserId).child(id).setValue(-1);
                    MainActivity.likes.remove(id); //remove from local likes
                }
            }
        });

        thumbsUp = (ImageButton) findViewById(R.id.ib_thumbsUp);
        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = curData.getVideoId();
                // already selected, deselect
                if(thumbsUp.isSelected()) {
                    thumbsUp.setImageResource(R.drawable.ic_thumb_up_black_36dp);
                    thumbsUp.setSelected(false);
                    MainActivity.likes.remove(id); //remove from local likes
                    MainActivity.mFirebaseDatabase.child("videos").child(MainActivity.mFireBaseUserId).child(id).getRef().removeValue(); //remove from database
                }
                // not selected, add to likes
                else {
                    thumbsUp.setImageResource(R.drawable.ic_thumb_up_white_36dp);
                    thumbsUp.setSelected(true);
                    if(thumbsDown.isSelected()) {
                        thumbsDown.setImageResource(R.drawable.ic_thumb_down_black_36dp);
                        thumbsDown.setSelected(false);
                    }
                    MainActivity.likes.add(id);
                    MainActivity.mFirebaseDatabase.child("videos").child(MainActivity.mFireBaseUserId).child(id).setValue(1);
                    MainActivity.dislikes.remove(id); //remove from local dislikes
                }
            }
        });

        desc = (TextView) findViewById(R.id.video_desc);
        desc.setMovementMethod(new ScrollingMovementMethod());

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

        // Construct query string
        StringBuilder sB = new StringBuilder();
        if (queryTerms != null && !queryTerms.isEmpty()) {
            for (String term : queryTerms) {
                sB.append(term).append("|");
            }
            // Remove last logical OR symbol
            sB.deleteCharAt(sB.length() - 1);
        }
        queryStr = sB.toString();

        // Initialize the YouTubePlayerSupportFragment
        YouTubePlayerSupportFragment youTubeFrag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubeFrag.initialize(ContentManager.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Log.d(TAG, "Youtube initialization success. Need to look for content duration: " + contentTime + " mins.");
        this.youTubePlayer = youTubePlayer;
        startNewVideo();
    }

    private void startNewVideo() {
        curData = ContentManager.getInstance().makeQuery(contentTime, queryStr);
        setPlayerData();
    }

    private void loadNextVideo() {
        curData = ContentManager.getInstance().getNextVideo(contentTime, queryStr.toString());
        setPlayerData();
    }

    private void setPlayerData() {
        desc.setText(curData.desc);
        desc.setScrollY(0);
        youTubePlayer.loadVideo(curData.videoId);
        if(MainActivity.dislikes.contains(curData.getVideoId())) {
            thumbsDown.setImageResource(R.drawable.ic_thumb_down_white_36dp);
            thumbsDown.setSelected(true);
        }
        else if(MainActivity.likes.contains(curData.getVideoId())) {
            thumbsUp.setImageResource(R.drawable.ic_thumb_up_white_36dp);
            thumbsUp.setSelected(true);
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
