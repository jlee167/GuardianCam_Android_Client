package com.example.guardiancamera_wifi;

public class GuardianCamConfigs {

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

    public void setFrameSize(int [] newSize) {
        frameSize = newSize;
    }

    public void setMode(int newMode) {
        outputMethod = newMode;
    }

    public void setFormat(int newFormat) {
        format = newFormat;
    }
}
