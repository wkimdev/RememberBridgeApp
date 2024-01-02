package com.example.remeberbridge.mypage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.remeberbridge.member.LoginActivity;
import com.example.remeberbridge.R;
import com.example.remeberbridge.utils.PreferenceManager;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;

/**
 * SettingActivity.java
 * @desc : 설정화면
 *
 * @author : wkimdev
 * @created : 2023/12/25
 * @version 1.0
 * @modifyed
 **/
public class SettingActivity extends Activity {

    private Button setting_btn_logout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting_btn_logout = findViewById(R.id.setting_btn_logout);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //로그아웃 버튼 실행
        setting_btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //@todo - sns_type을 제외한 shared에 저장된 유저정보삭제
                PreferenceManager.setString(getApplicationContext(), "user_email", "");
                PreferenceManager.setString(getApplicationContext(), "user_name", "");
                PreferenceManager.setString(getApplicationContext(), "user_prof_img", "");
                Toast.makeText(SettingActivity.this, "로그아웃 완료!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                /**
                 * 새로운 액티비티를 실행하고, 현재 액티비티를 stack에서 제거하기 위해 아래 옵션들을 줌
                 *
                 * FLAG_ACTIVITY_CLEAR_TOP:
                 *  - 호출하려는 액티비티가 이미 스택에 쌓여있을 때, 새로 인스턴스를 생성하지 않고 기존의 액티비티를 포그라운드로 가져옵니다.
                 *  - 그리고 액티비티스택의 최상단부터 포그라운드로 가져올 액티비티까지의 모든 액티비티를 삭제합니다.
                 * FLAG_ACTIVITY_CLEAR_TASK:
                 *  - 활동이 시작되기 전에 활동과 연관된 기존 작업이 지워지도록 하는 플래그.
                 *  - FLAG_ACTIVITY_NEW_TASK와 같이 사용되어야 함.
                 * FLAG_ACTIVITY_NEW_TASK:
                 *  - 기존에 해당 태스크가 없을 때 새로운 task 를 만들면서 launch 시킨다.
                 *  @참고 -> https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=estern&logNo=220012629594
                 **/
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 네이버 로그아웃
     */
    private void startNaverLogout() {
        NaverIdLoginSDK.INSTANCE.logout();
//        setLayoutState(false);
        Toast.makeText(SettingActivity.this, "네이버 아이디 로그아웃 성공!", Toast.LENGTH_SHORT).show();
    }

    /**
     * 연동해제
     */
    private void startNaverDeleteToken() {
        new NidOAuthLogin().callDeleteTokenApi(new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SettingActivity.this, "네이버 아이디 토큰삭제 성공!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int httpStatus, String message) {
                Log.d("naver", "errorCode: " + NaverIdLoginSDK.INSTANCE.getLastErrorCode().getCode());
                Log.d("naver", "errorDesc: " + NaverIdLoginSDK.INSTANCE.getLastErrorDescription());
            }

            @Override
            public void onError(int errorCode, String message) {
                onFailure(errorCode, message);
            }
        });
    }

}
