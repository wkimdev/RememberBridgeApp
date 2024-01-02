package com.example.remeberbridge;

import static com.example.remeberbridge.utils.CommonUtils.logError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.remeberbridge.member.LoginActivity;
import com.example.remeberbridge.utils.CommonUtils;
import com.example.remeberbridge.utils.PreferenceManager;


/**
 * LoadingActivity.java
 * @desc : 로딩화면 노출화면
 *  - 로그인 0 상태 => 메인화면 이동
 *  - 로그인 X 상태 => 로그인화면 이동
 *
 * @author : wkimdev
 * @created : 2023/12/31
 * @version 1.0
 * @modifyed
 **/
public class LoadingActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        //@todo - shared에 저장된 값으로 로그아웃이 된 상태인지 판단해서, 화면이동 분기처리를 한다.
        //  이때 스레드를 통해 로딩애니메이션을 주는게 좋겠다.
        String user_email = PreferenceManager.getString(getApplicationContext(), "user_email");
        logError(TAG, "user_meail: " + user_email);

        Intent intent;
        if (CommonUtils.isStringEmpty(user_email)) {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
