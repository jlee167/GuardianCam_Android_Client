package com.example.guardiancamera_wifi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.util.exception.KakaoException;

public class LoginActivity extends AppCompatActivity {

    // Google client object & Intent request code
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 7;


    /**
     *  Kakao session callback function.
     *  Handles Kakao login process.
     *
     *  On login success:
     *      Starts next activity and passes 'Kakao' as login method in the intent.
     *
     *  On login failure:
     *      Prints error message to log
     */
    private ISessionCallback sessionCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Log.i("KAKAO_SESSION", "로그인 성공");

            // Start main menu if Kakao authentication is successful.
            // Include Authentication provider in intent. (needed for auth server authentication)
            AuthService.getInstance()
                    .requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "토큰 정보 요청 실패: " + errorResult);
                        }

                        @Override
                        public void onSuccess(AccessTokenInfoResponse result) {
                            Log.i("KAKAO_API", "사용자 아이디: " + result.toString());
                            Log.i("KAKAO_API", "남은 시간 (ms): " + result.getExpiresInMillis());

                        }
                    });


            // Connect to Lazyweb authentication server and retrieve this user's personal information
            /* Todo: Include in the future
            try {
                MyApplication.authHandler = new LazywebAuthHandler(getApplicationContext(),
                                                            LazywebAuthHandler.AUTHENTICATOR_KAKAO);
                MyApplication.currentUser = MyApplication.authHandler.getUserInfo();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return;
            }
            */
            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            intent.putExtra(getResources().getString(R.string.INDEX_LOGIN_METHOD), getResources().
                                                                    getString(R.string.LOGIN_KAKAO));
            startActivity(intent);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("KAKAO_SESSION", "로그인 실패", exception);
        }
    };


    /**
     *  Get Google account info from signin task.
     *
     * @return
     *      True if signed in successfully. False if signin failed.
     */
    private boolean handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account == null)
                return false;
            else
                return true;

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Login Message", "signInResult:failed code=" + e.getStatusCode());
            return false;
        }
    }


    /**
     * Initialize social login environment.
     * Create Google sign-in object and add Kakao session callback.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Add Kakao signin session Callback
        Session.getCurrentSession().addCallback(sessionCallback);

        // Initialize Google signin object
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    /**
     *      Initialize Google Signin button with Google authentication event.
     *      Kakao button already contains onClick method without any initialization.
     */
    @Override
    protected void onStart() {
        super.onStart();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
                    break;
            }
            }
        });
    }


    /**
     *  Handle results from login attempt.
     *  If login attempt was successful, start Main Menu activity and pass authentication
     *  provider parameter (Key: "Login Method", Value: (Google or Kakao) in intent. This parameter
     *  will be used to determine which of the two services will be used for authentication.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Intent intent = new Intent(this, MainMenuActivity.class);

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        //if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
        //    return;
        //}

        // Call handleActivityResult method from Google API to determine if Google signin was successful
        // If successful, prepare intent. If not, return and wait for another login attempt.
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(handleGoogleSignInResult(task)) {
                intent.putExtra(getResources().getString(R.string.INDEX_LOGIN_METHOD), getResources().getString(R.string.LOGIN_GOOGLE));
            }
            else {
                return;
            }
        }

        // Invalid request.
        else {
            return;
        }

        // Connect to Lazyweb authentication server and retrieve this user's personal information
        /*  Todo: Include in the future
        try {
            MyApplication.authHandler = new LazywebAuthHandler(getApplicationContext(),
                                                        LazywebAuthHandler.AUTHENTICATOR_GOOGLE);
            MyApplication.currentUser = MyApplication.authHandler.getUserInfo();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return;
        }
        */

        // Start main menu activity with intent from above codes.
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(intent);
    }


    /**
     *  Close login sessions.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Kakao - Delete Callback
        Session.getCurrentSession().removeCallback(sessionCallback);
    }
}