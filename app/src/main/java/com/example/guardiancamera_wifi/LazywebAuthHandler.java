package com.example.guardiancamera_wifi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LazywebAuthHandler {

    // Caller activity context & passed intent
    Context callerContext;
    Intent callerIntent;

    // Login Information
    GoogleSignInAccount googleAccount;
    String socialAuthProvider;

    // Objects for auth server connection (http)
    URL authServerUrl, streamingServerUrl;
    HttpURLConnection authServerConnection;


    /**
     * Default Constructor.
     * Get the owner's environments and copy to the local variables.
     *
     * @param context  Owner's context
     * @param intent   Owner's intents from its parent activity
     * @throws MalformedURLException
     */
    LazywebAuthHandler (Context context, Intent intent) throws MalformedURLException {
        callerContext = context;
        callerIntent = intent;
        authServerUrl = new URL(callerContext.getString(R.string.AUTH_SERVER_ADDRESS));
    }


    /**
     * @return
     *      Kakao Token in string
     */
    public String getKakaoToken() {
        return Session.getCurrentSession().getTokenInfo().getAccessToken();
    }



    /**
     * Connect to the authentication server (Http protocol)
     * Pass Google or Kakao authentication token to the server for validation.
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        authServerConnection = (HttpURLConnection) authServerUrl.openConnection();
        authServerConnection.setDoOutput(true);
        authServerConnection.setChunkedStreamingMode(0);
        OutputStream outputStream = new BufferedOutputStream(authServerConnection.getOutputStream());
        InputStream inputStream = new BufferedInputStream(authServerConnection.getInputStream());
        if (isSignedWithGoogle()) {
            String token_packet = "GoogleIdToken=" +  googleAccount.getIdToken();
            outputStream.write(token_packet.getBytes());
        }
        else if (isSignedWithKakao()) {

        }
    }


    /**
     * Check if the user is signed in with Google Account
     *
     * @return
     *      True if user signed in with Google Account
     */
    public boolean isSignedWithGoogle(){
        return socialAuthProvider.equals(callerContext.getResources().getString(R.string.LOGIN_GOOGLE));
    }


    /**
     *  Check if the user is signed in with Kakao Account
     *
     * @return
     *      True if user signed in with Kakao Account
     */
    public boolean isSignedWithKakao() {
        return socialAuthProvider.equals(callerContext.getResources().getString(R.string.LOGIN_KAKAO));
    }


    /**
     * Get user's social login account
     *
     * @return
     *      True if signed in with Kakao or Google
     */
    public boolean getAccount() {
        /**
         *  Handle login results from either Kakao or Google
         */

        // Get the user's social authentication provider from the owner activity's intent.
        socialAuthProvider = callerIntent.getStringExtra(callerContext.getResources().getString(R.string.INDEX_LOGIN_METHOD));

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
            googleAccount = GoogleSignIn.getLastSignedInAccount(callerContext);
            if (googleAccount == null)
                return false;
        }

        // If signed in with neither Google or Kakao, something is wrong. Handle accordingly
        else {
            return false;
        }

        return true;
    }





}
