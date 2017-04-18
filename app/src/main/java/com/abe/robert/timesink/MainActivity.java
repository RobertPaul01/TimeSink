package com.abe.robert.timesink;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // static intent keys
    public static final String CONTENT_TIME = "CONTENT_TIME";
    public static final String CHECK_BOXES = "CHECK_BOXES";

    // constant private strings
    private static final String TAG = "MainActivity.java";

    // layout variables
    private SeekBar seekBar;
    private TextView tvMinutes, tvLogout;
    private ImageView ivLogout;
    private FloatingActionButton bSink;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7,
            checkBox8, checkBox9, checkBox10;
    private EditText etCustom;

    //dislikes
    public static HashSet<String> dislikes;
    public static HashSet<String> likes;

    // Firebase variables
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    public static DatabaseReference mFirebaseDatabase;
    public static String mFireBaseUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.sb_minute_slider);
        tvMinutes = (TextView) findViewById(R.id.tv_time_counter);
        etCustom = (EditText) findViewById(R.id.et_custom);

        tvLogout = (TextView) findViewById(R.id.tv_logout);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvLogout.setOnClickListener(this);
        ivLogout.setOnClickListener(this);

        bSink = (FloatingActionButton) findViewById(R.id.b_Sink);
        bSink.setOnClickListener(this);

        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
        checkBox7 = (CheckBox) findViewById(R.id.checkBox7);
        checkBox8 = (CheckBox) findViewById(R.id.checkBox8);
        checkBox9 = (CheckBox) findViewById(R.id.checkBox9);
        checkBox10 = (CheckBox) findViewById(R.id.checkBox10);
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        checkBox4.setOnClickListener(this);
        checkBox5.setOnClickListener(this);
        checkBox6.setOnClickListener(this);
        checkBox7.setOnClickListener(this);
        checkBox8.setOnClickListener(this);
        checkBox9.setOnClickListener(this);
        checkBox10.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String minutes = "" + (i + 1); // SeekBar has an unchangeable minimum of 0, I want 1.
                tvMinutes.setText(minutes);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveCheckBoxesToDatabase();
            }
        });
        bSink.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the sign in activity
            finish();
        } else {
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
            mFireBaseUserId = mFirebaseAuth.getCurrentUser().getUid();
            preLoadCheckBoxesFromDatabase();
            likes = new HashSet<String>();
            dislikes = new HashSet<String>();
            initializeLikesAndDislikes();
        }
    }

    private void signOut() {

        // Firebase sign out
        mFirebaseAuth.signOut();
        Log.d(TAG, "Current User: " + mFirebaseAuth.getCurrentUser());

        // Google sign out
        LoginActivity.mGoogleApiClient.connect();
        LoginActivity.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Auth.GoogleSignInApi.signOut(LoginActivity.mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                saveCheckBoxesToDatabase();
                                finish();
                                // Get sign out result
                            }
                        });
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });
    }

    /**
     * The list of checkboxes that are checked on the UI
     *
     * @return null if nothing is checked, otherwise the list of checked boxes
     */
    public ArrayList<String> getChecked() {
        ArrayList<String> list = new ArrayList<>();
        if (checkBox1.isChecked()) list.add(checkBox1.getText().toString());
        if (checkBox2.isChecked()) list.add(checkBox2.getText().toString());
        if (checkBox3.isChecked()) list.add(checkBox3.getText().toString());
        if (checkBox4.isChecked()) list.add(checkBox4.getText().toString());
        if (checkBox5.isChecked()) list.add(checkBox5.getText().toString());
        if (checkBox6.isChecked()) list.add(checkBox6.getText().toString());
        if (checkBox7.isChecked()) list.add(checkBox7.getText().toString());
        if (checkBox8.isChecked()) list.add(checkBox8.getText().toString());
        if (checkBox9.isChecked()) list.add(checkBox9.getText().toString());
        if (checkBox10.isChecked()) list.add(checkBox10.getText().toString());

        return (list.isEmpty()) ? null : list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkBox1:
            case R.id.checkBox2:
            case R.id.checkBox3:
            case R.id.checkBox4:
            case R.id.checkBox5:
            case R.id.checkBox6:
            case R.id.checkBox7:
            case R.id.checkBox8:
            case R.id.checkBox9:
            case R.id.checkBox10:
                saveCheckBoxesToDatabase();
                break;
            case R.id.b_Sink:
                Intent i = new Intent(MainActivity.this, ContentActivity.class);
                i.putExtra(MainActivity.CONTENT_TIME, tvMinutes.getText());
                i.putStringArrayListExtra(MainActivity.CHECK_BOXES, getChecked());
                startActivity(i);
                break;
            case R.id.iv_logout:
            case R.id.tv_logout:
                signOut();
                break;

        }
    }

    /*
     * loads whether the checkboxes should start checked or unchecked based on the database info
     */
    private void preLoadCheckBoxesFromDatabase() {
        mFirebaseDatabase.child("users").child(mFireBaseUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.d("on_child1", ""+ Boolean.parseBoolean(String.valueOf(map.get("checkbox1"))));
                    Log.d("on_child_minutes", String.valueOf(map.get("minutes")));

                    checkBox1.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox1"))));
                    checkBox2.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox2"))));
                    checkBox3.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox3"))));
                    checkBox4.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox4"))));
                    checkBox5.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox5"))));
                    checkBox6.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox6"))));
                    checkBox7.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox7"))));
                    checkBox8.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox8"))));
                    checkBox9.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox9"))));
                    checkBox10.setChecked(Boolean.parseBoolean(String.valueOf(map.get("checkbox10"))));
                    etCustom.setText(String.valueOf(map.get("custom")));
                    tvMinutes.setText(String.valueOf(map.get("minutes")));
                    seekBar.setProgress(Integer.parseInt(String.valueOf(map.get("minutes")))-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeLikesAndDislikes() {
        mFirebaseDatabase.child("videos").child(mFireBaseUserId).child("dislikes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for(String key : map.keySet()) {
                        dislikes.add( (String) map.get(key));
                    }
                    Log.d(TAG, "Dislikes: " + Arrays.toString(dislikes.toArray()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFirebaseDatabase.child("videos").child(mFireBaseUserId).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for(String key : map.keySet()) {
                        likes.add( (String) map.get(key));
                    }
                    Log.d(TAG, "Likes: " + Arrays.toString(dislikes.toArray()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
     * saves the checkboxes states to the database
     */
    private void saveCheckBoxesToDatabase() {
        Log.d(TAG, "Saved to database");
        if(etCustom.getText().toString().contains(" ")) {///*regex that matches one word*/"")) {
            Toast.makeText(this, "Custom field is limited to one word", Toast.LENGTH_SHORT).show();
            etCustom.setText(
                    etCustom.getText().toString().substring(
                            0, etCustom.getText().toString().indexOf(" ")
                    ));
        }
        etCustom.setText(etCustom.getText().toString().replaceAll(" ", "")); // remove any beginning whitespace

        User user = new User(checkBox1.isChecked(), checkBox2.isChecked(), checkBox3.isChecked(),
                checkBox4.isChecked(), checkBox5.isChecked(), checkBox6.isChecked(), checkBox7.isChecked(),
                checkBox8.isChecked(), checkBox9.isChecked(), checkBox10.isChecked(), etCustom.getText().toString(),
                Integer.parseInt(tvMinutes.getText().toString()));

        mFirebaseDatabase.child("users").child(mFireBaseUserId).setValue(user);
    }

    @Override
    public void onBackPressed() {
        saveCheckBoxesToDatabase();
        signOut();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        isYoutubeInstalled();
    }

    private void isYoutubeInstalled() {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(LoginActivity.YOUTUBE_PACKAGE_NAME);
        if (mIntent == null) {
            Toast.makeText(MainActivity.this, "Youtube needs to be installed for Time Sink to work", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + LoginActivity.YOUTUBE_PACKAGE_NAME)));
            this.finishAffinity();
        }
    }

}

