package com.example.guardiancamera_wifi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

// Todo: Move to another file
class UserInterfaceHandler {

    /**
     * Create activity intents and initialize control buttons' UI.
     * Connect respective buttons to corresponding services and activities.
     */
    static void initButtonsUI(final AppCompatActivity activity) {
        // Views for control buttons
        final TextView captureServiceBtn;
        TextView viewVideoBtn;
        TextView peerListBtn;
        TextView settingBtn;

        final Intent captureIntent = new Intent(activity, CamStreamer.class);
        captureIntent.putExtras(Objects.requireNonNull(activity.getIntent().getExtras()));
        captureServiceBtn = activity.findViewById(R.id.captureStartBtn);

        captureServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CamStreamer.isRunning()) {
                    activity.startService(captureIntent);
                    captureServiceBtn.setText(R.string.MENU_STOP_CAPTURE);
                } else {
                    activity.stopService(captureIntent);
                    captureServiceBtn.setText(R.string.MENU_START_CAPTURE);
                }
            }
        });

        final Intent videoViewIntent = new Intent(activity, VideoViewActivity.class);
        videoViewIntent.putExtras(Objects.requireNonNull(activity.getIntent().getExtras()));
        viewVideoBtn = (TextView) activity.findViewById(R.id.videoViewBtn);
        viewVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(videoViewIntent);
            }
        });

        final Intent peerListIntent = new Intent(activity, PeersActivity.class);
        peerListIntent.putExtras(Objects.requireNonNull(activity.getIntent().getExtras()));
        peerListBtn = (TextView) activity.findViewById(R.id.peerListBtn);
        peerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(peerListIntent);
            }
        });

        final Intent settingIntent = new Intent(activity, SettingActivity.class);
        settingIntent.putExtras(Objects.requireNonNull(activity.getIntent().getExtras()));
        settingBtn = (TextView) activity.findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(settingIntent);
            }
        });
    }
}


public class MainMenuActivity extends AppCompatActivity {

    // Views for control buttons
    TextView captureServiceBtn;
    TextView viewVideoBtn;
    TextView peerListBtn;
    TextView settingBtn;

    // Status flags for camera and server
    boolean statCamRecording;
    boolean statCamConnection;
    boolean statServerConnection;


    /**
     *  Create activity intents and initialize control buttons' UI.
     *  Connect respective buttons to corresponding services and activities.
     */
    private void initButtonsUI(){
        final Intent captureIntent = new Intent(this, CamStreamer.class);
        captureIntent.putExtras(Objects.requireNonNull(this.getIntent().getExtras()));
        captureServiceBtn = findViewById(R.id.captureStartBtn);
        /*
        if (!CamStreamer.isRunning()) {
            startService(captureIntent);
            captureServiceBtn.setText(R.string.MENU_STOP_CAPTURE);
        }
        */
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
        statCamRecording = false;
        statCamConnection = false;
        statServerConnection = false;

        // Initialize Control Buttons
        UserInterfaceHandler.initButtonsUI(this);
        /* Todo: Uncomment
        try {
            MyApplication.authHandler.httpHeartbeat();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        */

        /*
        try {
            String serverMessage = MyApplication.authHandler.getUserInfo();
            TextView serverLog = findViewById(R.id.serverLogView);
            if (serverMessage == null)
                serverLog.setText("No response from server. Could not retrieve user information");
            else {
                MyApplication.currentUser.setWithJSON(new JSONObject(serverMessage));
                serverLog.setText(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
