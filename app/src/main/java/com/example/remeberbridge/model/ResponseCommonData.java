package com.example.remeberbridge.model;

import com.example.remeberbridge.model.auth.UserLoginInfoResult;
import com.example.remeberbridge.model.auth.UserSpaceInfoResult;
import com.example.remeberbridge.model.diary.DiaryInfoResult;
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

    @SerializedName("diary_info")
    private ArrayList<DiaryInfoResult> diaryInfo;

    @SerializedName("space_id")
    private int spaceid;

    @SerializedName("dog_id")
    private int dogId;

    @SerializedName("diary_id")
    private int diaryId;



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

    public ArrayList<DiaryInfoResult> getDiaryInfo() {
        return diaryInfo;
    }

    public void setDiaryInfo(ArrayList<DiaryInfoResult> diaryInfo) {
        this.diaryInfo = diaryInfo;
    }

    public int getSpaceid() {
        return spaceid;
    }

    public void setSpaceid(int spaceid) {
        this.spaceid = spaceid;
    }

    public int getDogId() {
        return dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public int getDiaryId() {
        return diaryId;
    }
}
