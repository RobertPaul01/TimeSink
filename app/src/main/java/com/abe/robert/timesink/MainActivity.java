package com.abe.robert.timesink;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity.java";
    private final String SERVER_CLIENT_ID = "599202828976-d1921squujdnk28tee49multc6p2n9ks.apps.googleusercontent.com";

    // layout variables
    private SeekBar seekBar;
    private TextView tvMinutes;
    private Button bSink;

    // Firebase variables
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        seekBar = (SeekBar) findViewById(R.id.sb_minute_slider);
        tvMinutes = (TextView) findViewById(R.id.tv_time_counter);
        bSink = (Button) findViewById(R.id.b_Sink);


        // temporary
        tvMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String minutes = "" + i;
                tvMinutes.setText(minutes);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bSink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "go make this work", Toast.LENGTH_SHORT).show();
                // TODO start activity for finding content
            }
        });


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser == null) {
            // Not signed in, launch the sign in activity
            finish();
        } else {
            mUsername = mFirebaseUser.getDisplayName();
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
                            public void onResult(Status status) {
                                finish();
                                // Get sign out result
                            }
                        });
            }

            @Override
            public void onConnectionSuspended(int i) {}
        });
    }
}
