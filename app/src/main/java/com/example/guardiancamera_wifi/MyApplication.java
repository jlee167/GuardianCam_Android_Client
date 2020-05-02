package com.example.guardiancamera_wifi;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class MyApplication extends Application {

    // Current user using this application
    static LazyWebUserInfo currentUser;

    // AuthHandler for fetching peer list
    static LazywebAuthHandler authHandler;

    public static void setCurrentUser(LazyWebUserInfo userinfo) {
        currentUser = userinfo;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // SDK 초기화
        KakaoSDK.init(new KakaoAdapter() {

            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return MyApplication.this;
                    }
                };
            }
        });
    }
}
