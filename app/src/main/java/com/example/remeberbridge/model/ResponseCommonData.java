package com.example.remeberbridge.model;

import com.example.remeberbridge.model.auth.UserLoginInfoResult;
import com.example.remeberbridge.model.auth.UserSpaceInfoResult;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// 공통 응답 DTO 모델 클래스
public class ResponseCommonData {

    //응답상태코드
    @SerializedName("code")
    private String code;

    //응답상태 메세지
    @SerializedName("message")
    private String message;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("userInfo")
    private UserLoginInfoResult userInfo;

    @SerializedName("space_info")
    private ArrayList<UserSpaceInfoResult> spaceInfo;


    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserLoginInfoResult getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserLoginInfoResult userInfo) {
        this.userInfo = userInfo;
    }

    public ArrayList<UserSpaceInfoResult> getSpaceInfo() {
        return spaceInfo;
    }

    public void setSpaceInfo(ArrayList<UserSpaceInfoResult> spaceInfo) {
        this.spaceInfo = spaceInfo;
    }
}
