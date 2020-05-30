package com.example.guardiancamera_wifi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 *      Container class for user information.
 */
public class LazyWebUserInfo {

    public String profilePicture;
    public Bitmap profilePictureBitmap;
    public String uid;
    public String name;
    public String email;
    public String phoneNumber;
    public String authProvider;
    public String streamAddress;
    public String userStatus;
    public String cameraID;


    public LazyWebUserInfo() {

    }


    public LazyWebUserInfo(JSONObject jsonUserInfo) throws JSONException {
            this.profilePicture = (String) jsonUserInfo.get("id_profile_picture");
            this.uid = (String) jsonUserInfo.get("uid");
            this.name = (String) jsonUserInfo.get("name");
            this.email = (String) jsonUserInfo.get("email");
            this.phoneNumber = (String) jsonUserInfo.get("phone");
            this.authProvider = (String) jsonUserInfo.get("authenticator");
            this.streamAddress = (String) jsonUserInfo.get("id_stream");
            this.userStatus = (String) jsonUserInfo.get("status");
            this.cameraID = (String) jsonUserInfo.get("id_camera");
    }


    public boolean setWithJSON(JSONObject jsonUserInfo) {
        try {
            this.profilePicture = (String) jsonUserInfo.get("id_profile_picture");
            this.uid = (String) jsonUserInfo.get("uid");
            this.name = (String) jsonUserInfo.get("name");
            this.email = (String) jsonUserInfo.get("email");
            this.phoneNumber = (String) jsonUserInfo.get("phone");
            this.authProvider = (String) jsonUserInfo.get("authenticator");
            this.streamAddress = (String) jsonUserInfo.get("id_stream");
            this.userStatus = (String) jsonUserInfo.get("status");
            this.cameraID = (String) jsonUserInfo.get("id_camera");

            return true;
        }
        catch (JSONException e) {
            return false;
        }
    }

    public boolean setAsPeer(JSONObject jsonUserInfo) {
        try {
            this.profilePicture = (String) jsonUserInfo.get("id_profile_picture");
            this.name = (String) jsonUserInfo.get("name");
            this.email = (String) jsonUserInfo.get("email");
            this.phoneNumber = (String) jsonUserInfo.get("phone");
            this.streamAddress = (String) jsonUserInfo.get("id_stream");
            this.userStatus = (String) jsonUserInfo.get("status");

        }
        catch (JSONException e) {
            return false;
        }

        try {
            this.profilePictureBitmap = getPictureBitmap(this.profilePicture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Bitmap getPictureBitmap(String imageUrl) throws IOException {
        Bitmap result;

        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.connect();

        InputStream in = conn.getInputStream();
        result = BitmapFactory.decodeStream(in);

        return result;
    }
}
