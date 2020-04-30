package com.example.guardiancamera_wifi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class MainMenuActivity extends AppCompatActivity {

    // Views for control buttons
    TextView captureServiceBtn;
    TextView viewVideoBtn;
    TextView peerListBtn;
    TextView settingBtn;

    // Status flags for camera and server
    boolean camRecordingStat;
    boolean camConnectionStat;
    boolean serverConnectionStat;

    /**
     *  Create activity intents and initialize control buttons' UI.
     *  Connect respective buttons to corresponding services and activities.
     */
    private void initButtons(){

        final Intent captureIntent = new Intent(this, CamStreamer.class);
        captureIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        captureServiceBtn = findViewById(R.id.captureStartBtn);
        captureServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CamStreamer.isRunning())
                    startService(captureIntent);
            }
        });

        final Intent videoViewIntent = new Intent(this, VideoViewActivity.class);
        videoViewIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        viewVideoBtn = (TextView) findViewById(R.id.videoViewBtn);
        viewVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(videoViewIntent);
            }
        });

        final Intent peerListIntent = new Intent(this, PeerListActivity.class);
        peerListIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        peerListBtn = (TextView) findViewById(R.id.peerListBtn);
        peerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(peerListIntent);
            }
        });

        final Intent settingIntent = new Intent(this, SettingActivity.class);
        settingIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        settingBtn = (TextView) findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(settingIntent);
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Initialize status flags
        camRecordingStat = false;
        camConnectionStat = false;
        serverConnectionStat = false;

        // Initialize Control Buttons
        initButtons();

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

        final Intent captureStopIntent = new Intent(this, CamStreamer.class);
        if (CamStreamer.isRunning())
            stopService(captureStopIntent);
    }
}
