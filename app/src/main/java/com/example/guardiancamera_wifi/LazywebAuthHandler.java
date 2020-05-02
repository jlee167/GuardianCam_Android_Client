package com.example.guardiancamera_wifi;

import android.content.Context;
import android.util.Base64;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class LazywebAuthHandler {

    public final static int AUTHENTICATOR_KAKAO = 0;
    public final static int AUTHENTICATOR_GOOGLE = 1;

    private int authenticator;

    // Caller activity context & passed intent
    private Context appContext;

    // Login Information
    private GoogleSignInAccount googleAccount;
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
    LazywebAuthHandler (Context applicationContext, int auth) throws IOException {
        appContext = applicationContext;
        authenticator = auth;
        authServerUrl = new URL(appContext.getString(R.string.AUTH_SERVER_ADDRESS));
        authServerConnection = (HttpURLConnection) authServerUrl.openConnection();

        // Use POST method
        authServerConnection.setDoOutput(true);
        authServerConnection.setChunkedStreamingMode(0);
        authServerConnection.setRequestProperty("Content-Type", "application/json; utf-8");


        // Get IO streams from HTTP connection object
        streamToAuthServer = new BufferedOutputStream(authServerConnection.getOutputStream());
        streamFromAuthServer = new BufferedInputStream(authServerConnection.getInputStream());
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
        return socialAuthProvider.equals(appContext.getResources().getString(R.string.LOGIN_GOOGLE));
    }


    /**
     *  Check if the user is signed in with Kakao Account
     *
     * @return
     *      True if user signed in with Kakao Account
     */
    public boolean isSignedWithKakao() {
        return socialAuthProvider.equals(appContext.getResources().getString(R.string.LOGIN_KAKAO));
    }


    /**
     * Get user's social login account
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

                    UserAccount kakaoAccount = result.getKakaoAccount();
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
     * Connect to the authentication server (Http protocol)
     * Pass Google or Kakao authentication token to the server for validation.
     *
     * @throws IOException
     *
     * Todo:
     *      Add Timeout Routine for HTTP response
     */
    public LazyWebUserInfo getUserInfo() throws IOException, JSONException {
        byte [] serverMessageBytes;
        String serverMessage;


        if (!isSignedWithKakao() && !isSignedWithGoogle())
            return null;

        // Check for any pending operation
        // Todo: Make custom exception for pending operations
        if (streamFromAuthServer.available() > 0)
            throw new IOException();
        else
            streamToAuthServer.flush();


        /*
         *       Send Access Token information to Auth Server to retrieve
         *       relevant data about current user
         */
        JSONObject tokens_json = new JSONObject();
        if (isSignedWithGoogle()){
            tokens_json.put("AccessToken", getGoogleAccessToken());
            tokens_json.put("Authenticator", "Google");
        }
        else if (isSignedWithKakao()){
            tokens_json.put("AccessToken", getKakaoAccessToken());
            tokens_json.put("Authenticator", "Kakao");
        }
        else {
            /* Todo: Make custom Exception for authentication error */
        }
        streamToAuthServer.write(tokens_json.toString().getBytes(StandardCharsets.UTF_8));
        Log.i("AUTH_SERVER_INIT", "HTTP Access Requested" + tokens_json.toString());


        /*
         *      Wait for HTTP response from server.
         *      The response should contain JSON packet containing user information.
         */
        /* Todo: Insert waiting (wiith timeout) routine for HTTP response */
        if (streamFromAuthServer.available() > 0) {
            serverMessageBytes = new byte[streamFromAuthServer.available()];
            streamFromAuthServer.read(serverMessageBytes);
            serverMessage = Arrays.toString(serverMessageBytes);

            LazyWebUserInfo user = new LazyWebUserInfo();
            user.setWithJSON(new JSONObject(serverMessage));
            return user;
        }

        return null;
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
    public LazyWebUserInfo [] getPeers() throws IOException, JSONException {
        byte [] serverMessageBytes;
        String serverMessage;


        if (!isSignedWithKakao() && !isSignedWithGoogle())
            return null;

        // Check for any pending operation
        // Todo: Make custom exception for pending operations
        if (streamFromAuthServer.available() > 0)
            throw new IOException();
        else
            streamToAuthServer.flush();


        /*
         *       Send Access Token information to Auth Server to retrieve
         *       relevant data about current user
         */
        JSONObject tokens_json = new JSONObject();
        if (isSignedWithGoogle()){
            tokens_json.put("AccessToken", getGoogleAccessToken());
            tokens_json.put("Authenticator", "Google");
        }
        else if (isSignedWithKakao()){
            tokens_json.put("AccessToken", getKakaoAccessToken());
            tokens_json.put("Authenticator", "Kakao");
        }
        else {
            /* Todo: Make custom Exception for authentication error */
        }
        streamToAuthServer.write(tokens_json.toString().getBytes(StandardCharsets.UTF_8));
        Log.i("AUTH_SERVER_INIT", "HTTP Access Requested" + tokens_json.toString());


        /*
         *      Wait for HTTP response from server.
         *      The response should contain JSON packet containing user information.
         */
        /* Todo: Insert waiting (wiith timeout) routine for HTTP response */
        if (streamFromAuthServer.available() > 0) {
            serverMessageBytes = new byte[streamFromAuthServer.available()];
            streamFromAuthServer.read(serverMessageBytes);
            serverMessage = Arrays.toString(serverMessageBytes);

            LazyWebUserInfo [] user = new LazyWebUserInfo [10];
            user[0].setWithJSON(new JSONObject(serverMessage));
            return user;
        }

        return null;
    }

}
