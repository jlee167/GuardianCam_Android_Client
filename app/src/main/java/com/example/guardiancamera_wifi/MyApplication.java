package com.example.guardiancamera_wifi;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MyApplication extends Application {

    // Current user using this application
    static LazyWebUser currentUser;

    // AuthHandler for fetching peer list
    static LazyWebAuthHandler authHandler;

    // Application & Peripheral Configuration
    static GuardianCamConfigs configs;

    static ConcurrentLinkedDeque<String> appLogs;

    public static void setCurrentUser(LazyWebUser userinfo) {
        currentUser = userinfo;
    }
    public static MutableLiveData<ConcurrentLinkedDeque<String>> applicationLogLiveData = new MutableLiveData<ConcurrentLinkedDeque<String>>();


    public static void applicationLog(String msg) {
        if (appLogs.size() >= 50)
            appLogs.removeFirst();

        appLogs.add("[" + Calendar.getInstance().getTime().toString().split("GMT")[0] + "]" +msg);
        applicationLogLiveData.setValue(appLogs);
    }

    public static void postApplicationLog(String msg) {
        if (appLogs.size() >= 50)
            appLogs.removeFirst();

        appLogs.add("[" + Calendar.getInstance().getTime().toString().split("GMT")[0] + "]" +msg);
        applicationLogLiveData.postValue(appLogs);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        configs = new GuardianCamConfigs();
        appLogs = new ConcurrentLinkedDeque<String>();

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
