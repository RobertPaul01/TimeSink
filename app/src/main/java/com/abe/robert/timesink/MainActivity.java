package com.abe.robert.timesink;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // static intent keys
    public static final String CONTENT_TIME = "CONTENT_TIME";

    // constant private strings
    private static final String TAG = "MainActivity.java";
    private final String SERVER_CLIENT_ID = "599202828976-d1921squujdnk28tee49multc6p2n9ks.apps.googleusercontent.com";

    // layout variables
    private SeekBar seekBar;
    private TextView tvMinutes;
    private Button bSink;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;

    // Firebase variables
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccountManager am = AccountManager.get(this);
        Bundle options = new Bundle();

        // This call will need to be changed or removed
        am.getAuthToken(
                am.getAccounts()[0],        // Account retrieved using getAccountsByType()
                "Youtube",                  // Auth scope
                options,                    // Authenticator-specific options
                this,                       // Your activity
                new OnTokenAcquired(),      // Callback called when a token is successfully acquired
                new Handler());             // Callback called if an error occurs

        seekBar = (SeekBar) findViewById(R.id.sb_minute_slider);
        tvMinutes = (TextView) findViewById(R.id.tv_time_counter);
        bSink = (Button) findViewById(R.id.b_Sink);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6);

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

            }
        });
        bSink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "go make this work", Toast.LENGTH_SHORT).show();
                // TODO: start activity for finding content
                Intent i = new Intent(MainActivity.this, ContentActivity.class);
                i.putExtra(MainActivity.CONTENT_TIME, tvMinutes.getText());
                startActivity(i);
//                overridePendingTransition(R.transition.fadein, R.transition.fadeout);

            }
        });


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
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
                            public void onResult(@NonNull Status status) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The list of checkboxes that are checked on the UI
     *
     * @return null if nothing is checked, otherwise the list of checked boxes
     */
    public List<String> getChecked() {
        List<String> list = new ArrayList<String>();
        if (checkBox1.isChecked()) list.add(checkBox1.getText().toString());
        if (checkBox2.isChecked()) list.add(checkBox2.getText().toString());
        if (checkBox3.isChecked()) list.add(checkBox3.getText().toString());
        if (checkBox4.isChecked()) list.add(checkBox4.getText().toString());
        if (checkBox5.isChecked()) list.add(checkBox5.getText().toString());
        if (checkBox6.isChecked()) list.add(checkBox6.getText().toString());

        return (list.get(0) == null) ? null : list;
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Get the result of the operation from the AccountManagerFuture.
            Bundle bundle = null;
            try {
                bundle = result.getResult();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.i(TAG, "Token is: " + token);
            ContentManager.getInstance(token).makeQuery();
        }
    }
    
}

