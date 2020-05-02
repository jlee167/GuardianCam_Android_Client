package com.example.guardiancamera_wifi;

import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *      Container class for user information.
 */
public class LazyWebUserInfo {

    private ImageView profilePicture;
    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private String authProvider;
    private String streamAddress;
    private String userStatus;
    private String cameraID;


    public LazyWebUserInfo() {

    }


    public LazyWebUserInfo(JSONObject jsonUserInfo) throws JSONException {
            this.uid = (String) jsonUserInfo.get("UID");
            this.name = (String) jsonUserInfo.get("Name");
            this.email = (String) jsonUserInfo.get("Email");
            this.phoneNumber = (String) jsonUserInfo.get("Phone Number");
            this.authProvider = (String) jsonUserInfo.get("Authenticator");
            this.streamAddress = (String) jsonUserInfo.get("Stream Address");
            this.userStatus = (String) jsonUserInfo.get("Status");
            this.cameraID = (String) jsonUserInfo.get("Camera ID");
    }


    public boolean setWithJSON(JSONObject jsonUserInfo) {
        try {
            this.uid = (String) jsonUserInfo.get("UID");
            this.name = (String) jsonUserInfo.get("Name");
            this.email = (String) jsonUserInfo.get("Email");
            this.phoneNumber = (String) jsonUserInfo.get("Phone Number");
            this.authProvider = (String) jsonUserInfo.get("Authenticator");
            this.streamAddress = (String) jsonUserInfo.get("Stream Address");
            this.userStatus = (String) jsonUserInfo.get("Status");
            this.cameraID = (String) jsonUserInfo.get("Camera ID");

            return true;
        }
        catch (JSONException e) {
            return false;
        }
    }
}
