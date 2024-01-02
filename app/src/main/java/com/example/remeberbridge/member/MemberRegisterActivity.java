package com.example.remeberbridge.member;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.remeberbridge.R;

/**
 * MemberRegisterActivity.java
 * @desc : 일반 이메일 회원가입 화면
 *
 * @author : wkimdev
 * @created : 2023/12/11
 * @version 1.0
 * @modifyed
 **/
public class MemberRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_register);
    }
}