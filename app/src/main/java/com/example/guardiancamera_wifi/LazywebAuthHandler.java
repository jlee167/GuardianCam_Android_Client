package com.example.guardiancamera_wifi;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;


public class LazywebAuthHandler {

    public final static int AUTHENTICATOR_KAKAO = 0;
    public final static int AUTHENTICATOR_GOOGLE = 1;

    private int authenticator;

    // Caller activity context & passed intent
    private Context appContext;

    // Login Information
    private GoogleSignInAccount googleAccount;
    private UserAccount kakaoAccount;
    private String socialAuthProvider;

    // Objects for auth server connection (http)
    private URL authServerUrl, streamingServerUrl;
    private HttpURLConnection authServerConnection;
    private boolean lastConnStatus;

    private OutputStream streamToAuthServer;
    private InputStream streamFromAuthServer;


    /**
     * Default Constructor.
     * Get the owner's environments and copy to the local variables.
     *
     * @param applicationContext
     *      Current application's context
     *
     * @throws MalformedURLException
     */
    LazywebAuthHandler (final Context applicationContext, final int auth) throws IOException {
        appContext = applicationContext;
        authenticator = auth;
    }


    /**
     * @return
     *      Current account authentiator
     */
    public int getAuthenticator() {
        return this.authenticator;
    }

    /**
     * @return
     *      Kakao Access Token in string
     */
    public String getKakaoAccessToken() {
        return Session.getCurrentSession().getTokenInfo().getAccessToken();
    }


    /**
     * @return
     *      Google Access Token in string
     */
    public String getGoogleAccessToken() {
        return googleAccount.getIdToken();
    }

    /**
     * Check if the user is signed in with Google Account
     *
     * @return
     *      True if user signed in with Google Account
     */
    public boolean isSignedWithGoogle(){
        //return socialAuthProvider.equals(appContext.getResources().getString(R.string.LOGIN_GOOGLE));
        return authenticator == AUTHENTICATOR_GOOGLE;
    }


    /**
     *  Check if the user is signed in with Kakao Account
     *
     * @return
     *      True if user signed in with Kakao Account
     */
    public boolean isSignedWithKakao() {
        //return socialAuthProvider.equals(appContext.getResources().getString(R.string.LOGIN_KAKAO));
        return authenticator == AUTHENTICATOR_KAKAO;
    }


    /**
     * Get user's social login account
     * Store account instance to either 'googleAccount' or 'kakaoAccount' variable
     *
     * @return
     *      True if signed in with Kakao or Google
     */
    public boolean getAccount() {

        // Extract Kakao user information
        if (isSignedWithKakao()){
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                }

                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Log.i("KAKAO_API", "사용자 아이디: " + result.getId());

                    kakaoAccount = result.getKakaoAccount();
                    if (kakaoAccount != null) {


                        // 이메일
                        String email = kakaoAccount.getEmail();

                        if (email != null) {
                            Log.i("KAKAO_API", "email: " + email);

                        } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 이메일 획득 가능
                            // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                        } else {
                            // 이메일 획득 불가
                        }

                        // 프로필
                        Profile profile = kakaoAccount.getProfile();

                        if (profile != null) {
                            Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                            Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                            Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());

                        } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 프로필 정보 획득 가능

                        } else {
                            // 프로필 획득 불가
                        }
                    }
                }
            });
        }

        // Extract Google user information
        else if (isSignedWithGoogle()){
            googleAccount = GoogleSignIn.getLastSignedInAccount(appContext);
            return googleAccount != null;
        }

        // If signed in with neither Google or Kakao, something is wrong. Handle accordingly
        else {
            return false;
        }

        return true;
    }




    /**
     * Send Http connection test request to auth server.
     * This request returns simple response data confirming client-server connectivity.
     *
     * @return
     *      True if response is received
     *      False if no response comes back or connection error occurs
     *
     * Todo:
     *      Add more error codes
     */

    public boolean httpHeartbeat() throws JSONException, IOException {

        try {
            authServerUrl = new URL(appContext.getString(R.string.AUTH_SERVER_ADDRESS_IP));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            authServerConnection = (HttpURLConnection) authServerUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use POST method
        authServerConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        //authServerConnection.setConnectTimeout(10000);
        //authServerConnection.setReadTimeout(10000);
        authServerConnection.setRequestProperty("Accept", "application/json");
        try {
            authServerConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        authServerConnection.setDoOutput(true);
        authServerConnection.setDoInput(true);
        authServerConnection.connect();

        // Get IO streams from HTTP connection object
        try {
            streamToAuthServer = authServerConnection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject testPacket = new JSONObject();
        testPacket.put("Request", "Http Test");
        byte [] httpOutput = testPacket.toString().getBytes();
        streamToAuthServer.write(httpOutput);
        streamToAuthServer.flush();
        streamToAuthServer.close();
        int responseCode= authServerConnection.getResponseCode();
        //       streamToAuthServer.flush();

        streamFromAuthServer = authServerConnection.getInputStream();

            byte[] serverMessageBytes;
            String serverMessage;
            serverMessageBytes = new byte[30];
            streamFromAuthServer.read(serverMessageBytes);
            //         serverMessage = Arrays.toString(serverMessageBytes);
            String string_recv = new String(serverMessageBytes);
            JSONObject recv= new JSONObject(string_recv);
            if (recv.get("response").equals("OK"))
                return true;
            else
                return false;
    }




    /**
     * Connect to the authentication server (Http protocol)
     * Pass Google or Kakao authentication token to the server for validation.
     *
     * @throws IOException
     *
     */
    public LazyWebUserInfo getMyInfo() throws IOException, JSONException {
        LazyWebUserInfo currentUser = new LazyWebUserInfo();

        // Initialize server http url object
        try {
            authServerUrl = new URL(appContext.getString(R.string.AUTH_SERVER_ADDRESS_IP));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Make Http connection to the authentication server.
        // Data will be sent in JSON format, and POST will be used
        try {
            authServerConnection = (HttpURLConnection) authServerUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        authServerConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        authServerConnection.setRequestProperty("Accept", "application/json");
        try {
            authServerConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        authServerConnection.setDoOutput(true);
        authServerConnection.setDoInput(true);
        authServerConnection.connect();

        // Get Http output stream
        try {
            streamToAuthServer = authServerConnection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
         *       Send Access Token information to Auth Server to retrieve
         *       relevant data about current user
         */

        //Todo: Make some custom macros. TRy not to hardcode
        JSONObject tokens_json = new JSONObject();
        if (isSignedWithGoogle()){
            tokens_json.put("Access Token", getGoogleAccessToken());
            tokens_json.put("Authenticator", "Google");
        }
        else if (isSignedWithKakao()) {
            tokens_json.put("Access Token", getKakaoAccessToken());
            tokens_json.put("Authenticator", "Kakao");
        }
        else {
            /* Todo: Make custom Exception for authentication error */
        }
        tokens_json.put("Request", "My Info");

        //streamToAuthServer.write(tokens_json.toString().getBytes(StandardCharsets.UTF_8));
        Log.i("AUTH_SERVER_INIT", "HTTP Access Requested" + tokens_json.toString());


        // Send above request to the server
        byte [] httpOutput = tokens_json.toString().getBytes();
        streamToAuthServer.write(httpOutput);
        streamToAuthServer.flush();
        streamToAuthServer.close();
        int responseCode= authServerConnection.getResponseCode();
        streamFromAuthServer = authServerConnection.getInputStream();

        byte[] serverMessageBytes;
        String serverMessage;
        serverMessageBytes = new byte[1000];
        streamFromAuthServer.read(serverMessageBytes);
        String string_recv = new String(serverMessageBytes);
        JSONObject msg_recv = new JSONObject(string_recv);

        currentUser = new LazyWebUserInfo();
        currentUser.setWithJSON(msg_recv);
        MyApplication.postApplicationLog("User information Retrieved!\n");
        return currentUser;

        /*
         *      Wait for HTTP response from server.
         *      The response should contain JSON packet containing user information.
         */
        /* Todo: Insert waiting (wiith timeout) routine for HTTP response */
        /*
        if (streamFromAuthServer.available() > 0) {
            serverMessageBytes = new byte[streamFromAuthServer.available()];
            streamFromAuthServer.read(serverMessageBytes);
            serverMessage = Arrays.toString(serverMessageBytes);

            currentUser = new LazyWebUserInfo();
            currentUser.setWithJSON(new JSONObject(serverMessage));
            return currentUser;
        }

        return null;
        */
    }


    /**
     * Connect to the authentication server (Http protocol)
     * Pass Google or Kakao authentication token to the server for validation.
     *
     * @throws IOException
     *
     * Todo:
     *      Add Timeout Routine for HTTP response
     */
    public LazyWebPeerGroups getPeers() throws IOException, JSONException {

        // User information objects to fill.
        // User info arrays will be used to fill the peer groups object, which will be returned
        LazyWebPeerGroups peerGroups;
        LazyWebUserInfo [] protectees, guardians, request_protectees, request_guardians;

        // Initialize peer group object
        peerGroups = new LazyWebPeerGroups();

        // Initialize server http url object
        try {
            authServerUrl = new URL(appContext.getString(R.string.AUTH_SERVER_ADDRESS_IP));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Make Http connection to the authentication server.
        // Data will be sent in JSON format, and POST will be used
        try {
            authServerConnection = (HttpURLConnection) authServerUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        authServerConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        authServerConnection.setRequestProperty("Accept", "application/json");
        try {
            authServerConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        authServerConnection.setDoOutput(true);
        authServerConnection.setDoInput(true);
        authServerConnection.connect();

        // Get Http output stream
        try {
            streamToAuthServer = authServerConnection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prepare 'Peer List' request, which will return list of peers
        JSONObject testPacket = new JSONObject();
        testPacket.put("Request", "Peer List");
        if (isSignedWithGoogle()){
            testPacket.put("Access Token", getGoogleAccessToken());
            testPacket.put("Authenticator", "Google");
        }
        else if (isSignedWithKakao()) {
            testPacket.put("Access Token", getKakaoAccessToken());
            testPacket.put("Authenticator", "Kakao");
        }
        else {
            /* Todo: Make custom Exception for authentication error */
        }

        // Send above request to the server
        byte [] httpOutput = testPacket.toString().getBytes();
        streamToAuthServer.write(httpOutput);
        streamToAuthServer.flush();
        streamToAuthServer.close();
        int responseCode= authServerConnection.getResponseCode();
        streamFromAuthServer = authServerConnection.getInputStream();


        byte[] serverMessageBytes;
        String serverMessage;
        serverMessageBytes = new byte[1000];
        streamFromAuthServer.read(serverMessageBytes);
        //         serverMessage = Arrays.toString(serverMessageBytes);
        String string_recv = new String(serverMessageBytes);
        JSONObject msg_recv = new JSONObject(string_recv);

        // Different groups of peers will be returned in different JSON arrays
        JSONArray json_protectees = msg_recv.getJSONArray("protectees");
        JSONArray json_request_protectees = msg_recv.getJSONArray("request_protectee");
        JSONArray json_guardians = msg_recv.getJSONArray("guardians");
        JSONArray json_request_guardians = msg_recv.getJSONArray("request_guardian");

        // Storage for returned JSON arrays
        protectees = new LazyWebUserInfo[json_protectees.length()];
        guardians = new LazyWebUserInfo[json_guardians.length()];
        request_protectees = new LazyWebUserInfo[json_request_protectees.length()];
        request_guardians = new LazyWebUserInfo[json_request_guardians.length()];

        // Copy information in JSON return packet to userinfo objects
        for (int i = 0; i < json_protectees.length(); i++) {
            JSONObject peer = json_protectees.getJSONObject(i);
            protectees[i] = new LazyWebUserInfo();
            protectees[i].setAsPeer(peer);
        }
        for (int i = 0; i < json_request_protectees.length(); i++) {
            JSONObject peer = json_request_protectees.getJSONObject(i);
            request_protectees[i] = new LazyWebUserInfo();
            request_protectees[i].setAsPeer(peer);
        }
        for (int i = 0; i < json_guardians.length(); i++) {
            JSONObject peer = json_guardians.getJSONObject(i);
            guardians[i] = new LazyWebUserInfo();
            guardians[i].setAsPeer(peer);
        }
        for (int i = 0; i < json_request_guardians.length(); i++) {
            JSONObject peer = json_request_guardians.getJSONObject(i);
            request_guardians[i] = new LazyWebUserInfo();
            request_guardians[i].setAsPeer(peer);
        }


        // Pack userinfo objects into a single usergroup object and return it to caller
        peerGroups.setProtectees(protectees);
        peerGroups.setProtecteeRequests(request_protectees);
        peerGroups.setGuardians(guardians);
        peerGroups.setGuardianRequests(request_guardians);
        return peerGroups;
    }
}