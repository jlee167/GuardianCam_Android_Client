package com.example.guardiancamera_wifi;

public class GuardianCamConfigs {

    // Frame Size macros
    final static int [] FRAME_SIZE_QCIF = {320, 240};
    final static int [] FRAME_SIZE_VGA = {640, 480};
    final static int [] FRMAE_SIZE_SVGA = {800, 600};
    final static int [] FRMAE_SIZE_HD = {1280, 720};
    final static int [] FRAME_SIZE_FHD = {1920, 1080};

    // Output Method Macros
    final static int INPUT_EXTERNAL_CAM = 0;
    final static int INPUT_PHONE_CAM = 1;

    // Output Formats
    final static int FMT_MJPEG = 0;
    final static int FMT_RGB565 = 1;

    public int inputSource;
    public int [] inputSize;
    public int inputFormat;


    /**
     *      Constructor with default settings
     *
     *      Todo: Restrict Framesize when RGB565 format is selected
     */
    GuardianCamConfigs() {
        inputSource = INPUT_EXTERNAL_CAM;
        inputSize = FRAME_SIZE_QCIF;
        inputFormat = FMT_MJPEG;
    }


    public void setInputSize(int [] newSize) {
        inputSize = newSize;
    }


    public void setMode(int newMode) {
        inputSource = newMode;
    }


    public void setInputFormat(int newFormat) {
        inputFormat = newFormat;
    }
}
