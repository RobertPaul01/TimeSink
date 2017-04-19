package com.abe.robert.timesink;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Abe on 4/18/2017.
 */

public class LikedVideosActivity extends AppCompatActivity{

    public static final String TAG = "LikedVideosActivity";

    // because I don't want to keep typing MainActivity.likes
    private ArrayList<VideoData> likes;

    // ui stuff
    ListView listView;
    Button btRemove;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_liked_videos);
        likes = new ArrayList<>();

        //TODO make requests to get video information for each of the video id's in MainActvitiy.likes
        // likes.addAll() // set of VideoData objects created from requests
        //TODO remove test code
        likes.add(new VideoData("1234", "New Title", "New Description"));
        likes.add(new VideoData("12345", "New Title2", "New Description2"));

        final VideoAdapter itemsAdapter = new VideoAdapter(this, likes);

        listView = (ListView) findViewById(R.id.lv_videos);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                Log.d(TAG, "Items " +  likes.get(position).toString());
                //TODO play video with id likes.get(position).getVideoId()
            }

        });

        btRemove = (Button) findViewById(R.id.bt_remove);
        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO update with correct ids/objects
                MainActivity.likes.remove(123456/* replace with current video id*/ ); //remove from local likes
                MainActivity.mFirebaseDatabase.child("videos").child(MainActivity.mFireBaseUserId).child("123456"/* replace with current video id*/ ).getRef().removeValue(); //remove from database
                itemsAdapter.remove(new VideoData("1234", "New Title", "New Description")/* VideoData object that relates to currently playing video */);
                itemsAdapter.notifyDataSetChanged();
            }
        });

    }
}
