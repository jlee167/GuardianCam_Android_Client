package com.example.guardiancamera_wifi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;




class GuardianCamConfigs {

    // Frame Size macros
    final static int [] FRAME_SIZE_QCIF = {320, 240};
    final static int [] FRAME_SIZE_VGA = {640, 480};
    final static int [] FRMAE_SIZE_SVGA = {800, 600};

    // Output Method Macros
    final static int OUTPUT_EXTERNAL_CAM = 0;
    final static int OUTPUT_PHONE_CAM = 1;

    // Output Formats
    final static int FMT_MJPEG = 0;
    final static int FMT_RGB565 = 1;

    public int outputMethod;
    public int [] frameSize;
    public int format;


    /**
     *      Constructor with default settings
     *
     *      Todo: Restrict Framesize when RGB565 format is selected
     */
    GuardianCamConfigs() {
        outputMethod = OUTPUT_EXTERNAL_CAM;
        frameSize = FRAME_SIZE_QCIF;
        format = FMT_MJPEG;
    }
}




public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        UserInterfaceHandler.initButtonsUI(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
