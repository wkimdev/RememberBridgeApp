package com.example.remeberbridge.model.auth;

import com.google.gson.annotations.SerializedName;

/**
 * RequestEmailLogin.java
 * @desc : 이메일로그인 요청 DTO
 *
 * @author : wkimdev
 * @created : 2023/12/11
 * @version 1.0
 * @modifyed
 **/
public class RequestEmailLogin {

    @SerializedName("user_email")
    String userEmail;

    @SerializedName("user_pw")
    String userPw;

    public RequestEmailLogin(String userEmail, String userPw) {
        this.userEmail = userEmail;
        this.userPw = userPw;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }
}
