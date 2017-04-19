package com.abe.robert.timesink;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;

/**
 * Created by Abe on 4/18/2017.
 */

public class LikedVideosActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    public static final String TAG = "LikedVideosActivity";

    // because I don't want to keep typing MainActivity.likes
    private ArrayList<VideoData> likes;

    // ui stuff
    ListView listView;
    Button btRemove;

    // UI views
    private YouTubePlayer youTubePlayer;
    private VideoData curVideo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_liked_videos);
        likes = new ArrayList<>();

        ArrayList<VideoData> vDList = ContentManager.getInstance().findLikedVideos(MainActivity.likes);
        if (vDList != null) {
            likes.addAll(vDList);
        }

        final VideoAdapter itemsAdapter = new VideoAdapter(this, likes);

        listView = (ListView) findViewById(R.id.lv_videos);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                //Log.d(TAG, "Items " +  likes.get(position).toString());
                curVideo = likes.get(position);
                youTubePlayer.loadVideo(curVideo.videoId);
            }

        });

        btRemove = (Button) findViewById(R.id.bt_remove);
        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO update with correct ids/objects
                if(youTubePlayer.isPlaying()) {
                    MainActivity.likes.remove(curVideo.videoId); //remove from local likes
                    MainActivity.mFirebaseDatabase.child("videos").child(MainActivity.mFireBaseUserId).child(curVideo.videoId/* replace with current video id*/).getRef().removeValue(); //remove from database
                    itemsAdapter.remove(new VideoData(curVideo.videoId, null, null)/* VideoData object that relates to currently playing video */);
                    itemsAdapter.notifyDataSetChanged();
                    youTubePlayer.pause();
                    if (!likes.isEmpty()) {
                        youTubePlayer.loadVideo(likes.get(0).getVideoId());
                    }
                    else finish();
                }
            }
        });

        // Initialize the YouTubePlayerSupportFragment
        YouTubePlayerSupportFragment youTubeFrag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment_likes);
        youTubeFrag.initialize(ContentManager.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.e(TAG, "Error initializing youtubeVie");
    }
}
