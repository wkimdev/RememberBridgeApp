package com.example.remeberbridge.configure;

import android.app.Application;

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

        // Initialize Kakao SDK
        KakaoSdk.init(this, String.valueOf(R.string.KAKAO_NATIVE_APP_KEY)); // Replace "NATIVE_APP_KEY" with your actual app key
    }
}
