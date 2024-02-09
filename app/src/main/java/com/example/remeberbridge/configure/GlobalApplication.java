package com.example.remeberbridge.configure;

import android.app.Application;
import android.util.Log;

import com.example.remeberbridge.R;
import com.kakao.sdk.common.KakaoSdk;
import com.navercorp.nid.NaverIdLoginSDK;

/**
 * GlobalApplication.java
 * @desc : 카카오 소셜 로그인을 위한 SDK 초기화
 *
 * @author : wkimdev
 * @created : 2023/12/18
 * @version 1.0
 * @modifyed
 **/
public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Other initialization code
        Log.e("GlobalApplication", "onCreate: CALL!!!");

        // Initialize Kakao SDK
        //String mystring = getResources().getString(R.string.mystring);
        KakaoSdk.init(this, getResources().getString(R.string.KAKAO_NATIVE_APP_KEY)); // Replace "NATIVE_APP_KEY" with your actual app key
    }
}
