package com.example.guardiancamera_wifi;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.util.Objects;


public class MainMenuActivity extends AppCompatActivity {

    // Views for control buttons
    TextView captureServiceBtn;
    TextView viewVideoBtn;
    TextView peerListBtn;
    TextView settingBtn;
    TextView homeBtn;

    // Status flags for camera and server
    boolean statCamRecording;
    boolean statCamConnection;
    boolean statServerConnection;

    // Camera configuration
    GuardianCamConfigs cameraConfig;




    /**
     * Helper function for changing fragment
     *
     * @param newFragment Target Fragment
     */
    private void changeFragment(Fragment newFragment) {
        FrameLayout container = (FrameLayout) findViewById(R.id.contentsFrame);
        container.removeAllViews();

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentsFrame, newFragment);
        transaction.commit();
    }




    /**
     *  Create activity intents and initialize control buttons' UI.
     *  Connect respective buttons to corresponding services and activities.
     */
    private void initButtonsUI(){
        final Intent captureIntent = new Intent(this, CamStreamer.class);
        captureIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        captureServiceBtn = findViewById(R.id.captureStartBtn);

        captureServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CamStreamer.isRunning()) {
                    startService(captureIntent);
                    captureServiceBtn.setText(R.string.MENU_STOP_CAPTURE);
                }

                else {
                    stopService(captureIntent);
                    captureServiceBtn.setText(R.string.MENU_START_CAPTURE);
                }
            }
        });

        final Intent videoViewIntent = new Intent(this, VideoViewActivity.class);
        videoViewIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        viewVideoBtn = (TextView) findViewById(R.id.videoViewBtn);
        viewVideoBtn.setText("View Video");
        viewVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(videoViewIntent);
            }
        });

        final Intent peerListIntent = new Intent(this, PeersActivity.class);
        peerListIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        peerListBtn = (TextView) findViewById(R.id.peerListBtn);
        peerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(peerListIntent);
            }
        });

        settingBtn = (TextView) findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment settingFragment = new SettingsFragment();
                //while (settingFragment.i)
                changeFragment(new SettingsFragment());
            }
        });


        homeBtn = (TextView) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment homeFragment = new HomeFragment();
                changeFragment(homeFragment);

            }
        });
    }


    /**
     *
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);



        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }


        // Initialize status flags
        statCamRecording = false;
        statCamConnection = false;
        statServerConnection = false;

        // Applicationwide authentication handler object
        final LazyWebAuthHandler authHandler = MyApplication.authHandler;

        // Initialize Control Buttons
        initButtonsUI();

        // Initialize Camera
        cameraConfig = new GuardianCamConfigs();


        /**
         *      Home Fragment Setup
         */
        /*
        // Log Window Setup
        TextView logWindow = findViewById(R.id.logWindow);
        logWindow.setMovementMethod(new ScrollingMovementMethod());

        // Observe application logs, and print it out when there is any change
        final Observer<ConcurrentLinkedDeque<String>> observer = new Observer<ConcurrentLinkedDeque<String>>() {
            @Override
            public void onChanged(ConcurrentLinkedDeque<String> strings) {
                TextView log = findViewById(R.id.logWindow);
                log.setText("");
                for (int i = 0; i < strings.size(); i++)
                    log.append((String)strings.toArray()[i]);
            }
        };
        */
        //SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(this);
        //String name = userPref.getString("list_preference_1","");


/*
        MyApplication.applicationLogLiveData.observe(this,observer);
        MyApplication.applicationLog("Hello World!\n");

        Thread httpHeartbeatThread= new Thread() {
            boolean result;

            @Override
            public void run(){
                try {
                    result = authHandler.isServerDown();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            public boolean getResult(){
                return result;
            }
        };

        httpHeartbeatThread.start();
        try {
            httpHeartbeatThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Todo: Move to Login Activity
        Thread getinfo = new Thread(){
            @Override
            public void run() {
                try {
                    MyApplication.currentUser = MyApplication.authHandler.getMyInfo();
                    ImageView profilePicture = findViewById(R.id.profilePicture);
                    profilePicture.setImageBitmap(MyApplication.currentUser.profilePictureBitmap);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        getinfo.start();

        try {
            getinfo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 */
    }




    @Override
    protected void onStart() {
        super.onStart();

    }




    @Override
    protected void onResume() {

        super.onResume();
    }




    @Override
    protected void onPause() {

        super.onPause();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (CamStreamer.isRunning()) {
            final Intent captureStopIntent = new Intent(this, CamStreamer.class);
            stopService(captureStopIntent);
        }
    }
}
