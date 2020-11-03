package com.example.guardiancamera_wifi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;


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
        TextView homeBtn;
        Toolbar toolbar;

        toolbar = activity.findViewById(R.id.mainToolbar);

        toolbar.setTitle("Lazyboy's Blackbox");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.menu);
        //activity.setSupportActionBar(toolbar);


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

        final Intent MainMenuIntent = new Intent(activity, MainMenuActivity.class);
        MainMenuIntent.putExtras(Objects.requireNonNull(activity.getIntent().getExtras()));
        homeBtn = (TextView) activity.findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(MainMenuIntent);
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
        viewVideoBtn.setText("New Char");
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
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu
    }
*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Initialize status flags
        statCamRecording = false;
        statCamConnection = false;
        statServerConnection = false;

        // Applicationwide authentication handler object
        final LazywebAuthHandler authHandler = MyApplication.authHandler;

        // Initialize Control Buttons
        UserInterfaceHandler.initButtonsUI(this);

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
        MyApplication.applicationLogLiveData.observe(this,observer);
        MyApplication.applicationLog("Hello World!\n");

        Thread httpHeartbeatThread= new Thread() {
            boolean result;

            @Override
            public void run() {
                try {
                    result = authHandler.httpHeartbeat();
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
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
