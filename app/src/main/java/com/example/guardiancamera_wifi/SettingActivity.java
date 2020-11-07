package com.example.guardiancamera_wifi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;




public class SettingActivity extends AppCompatActivity {

    GuardianCamConfigs app_configs;
    RadioGroup resolutions, formats, modes;
    Button saveConfigBtn;

    private void updateWidgets() {
        if (app_configs.inputSize == GuardianCamConfigs.FRAME_SIZE_QCIF) {
            resolutions.check(R.id.qcif);
        } else if (app_configs.inputSize == GuardianCamConfigs.FRAME_SIZE_VGA) {
            resolutions.check(R.id.vga);
        } else if (app_configs.inputSize == GuardianCamConfigs.FRMAE_SIZE_SVGA) {
            resolutions.check(R.id.svga);
        }

        switch (app_configs.inputSource) {
            case GuardianCamConfigs.INPUT_EXTERNAL_CAM:
                modes.check(R.id.externalCamera);
                break;
            case GuardianCamConfigs.INPUT_PHONE_CAM:
                modes.check(R.id.phone);
                break;
        }

        switch (app_configs.inputFormat) {
            case GuardianCamConfigs.FMT_MJPEG:
                formats.check(R.id.mjpeg);
                break;
            case GuardianCamConfigs.FMT_RGB565:
                formats.check(R.id.rgb565);
                break;
        }
    }

    private void configChangeHandler() {
        switch (resolutions.getCheckedRadioButtonId()) {
            case R.id.qcif:
                app_configs.setInputSize(GuardianCamConfigs.FRAME_SIZE_QCIF);
                MyApplication.applicationLog("Capture Resolution: 320x240\n");
                break;
            case R.id.vga:
                app_configs.setInputSize(GuardianCamConfigs.FRAME_SIZE_VGA);
                MyApplication.applicationLog("Capture Resolution: 640x480\n");
                break;
            case R.id.svga:
                app_configs.setInputSize(GuardianCamConfigs.FRMAE_SIZE_SVGA);
                MyApplication.applicationLog("Capture Resolution: 800x600\n");
                break;
        }

        switch (formats.getCheckedRadioButtonId()) {
            case R.id.mjpeg:
                app_configs.setInputFormat(GuardianCamConfigs.FMT_MJPEG);
                MyApplication.applicationLog("Capture Format: MJPEG\n");
                break;
            case R.id.rgb565:
                app_configs.setInputFormat(GuardianCamConfigs.FMT_RGB565);
                MyApplication.applicationLog("Capture Format: RGB565\n");
                break;
        }

        switch (modes.getCheckedRadioButtonId()) {
            case R.id.externalCamera:
                app_configs.setMode(GuardianCamConfigs.INPUT_EXTERNAL_CAM);
                MyApplication.applicationLog("Capture Mode: Camera Module\n");
                break;
            case R.id.phone:
                app_configs.setMode(GuardianCamConfigs.INPUT_PHONE_CAM);
                MyApplication.applicationLog("Capture Mode: Phone Camera\n");
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        app_configs = MyApplication.configs;

        resolutions = findViewById(R.id.resolutions);
        formats = findViewById(R.id.formats);
        modes = findViewById(R.id.modes);
        saveConfigBtn = findViewById(R.id.saveButton);

        saveConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configChangeHandler();
            }
        });

        UserInterfaceHandler.initButtonsUI(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        updateWidgets();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
