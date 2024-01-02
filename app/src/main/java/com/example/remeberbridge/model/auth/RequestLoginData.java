package com.example.remeberbridge.model.auth;

import com.google.gson.annotations.SerializedName;

/**
 * RequestLoginData.java
 * @desc : 로그인 DTO 클래스, SNS 로그인의 경우 회원가입도 해당 API로 수행함
 *
 * @author : wkimdev
 * @created : 2023/12/11
 * @version 1.0
 * @modifyed
 **/
public class RequestLoginData {

    @SerializedName("user_email")
    String userEmail;

    @SerializedName("user_name")
    String userName;

    @SerializedName("user_prof_img")
    String userProfImg;

    @SerializedName("login_sns_type")
    String loginSnsType;

    public RequestLoginData(String userEmail, String userName, String userProfImg, String loginSnsType) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userProfImg = userProfImg;
        this.loginSnsType = loginSnsType;
    }

    public String getLoginSnsType() {
        return loginSnsType;
    }

    public void setLoginSnsType(String loginSnsType) {
        this.loginSnsType = loginSnsType;
    }
}
