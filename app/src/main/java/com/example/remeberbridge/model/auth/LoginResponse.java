package com.example.remeberbridge.model.auth;


import com.example.remeberbridge.model.ResponseCommonData;
import com.google.gson.annotations.SerializedName;

/**
 * LoginResponse.java
 * @desc :
 *
 * @author : wkimdev
 * @created : 2023/12/11
 * @version 1.0
 * @modifyed
 **/
public class LoginResponse extends ResponseCommonData {

    //GSON에 포함된 어노테이션
    //@SerializedName("속성명")으로
    //문자열로 넘겨주는 속성명은 해당 객체가 JSON으로 바뀔때 사용하는 값으로, 변수이름과 속성명을 똑같이 준다면 생략가능하다
    @SerializedName("access_token")
    private int accessToken;


    @SerializedName("refresh_token")
    private int refreshToken;

    //응답 데이터 모델
    private UserLoginInfoResult userInfo;


//    public int getAccessToken() {
//        return accessToken;
//    }

    public void setAccessToken(int accessToken) {
        this.accessToken = accessToken;
    }

//    public int getRefreshToken() {
//        return refreshToken;
//    }

    public void setRefreshToken(int refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserLoginInfoResult getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserLoginInfoResult userInfo) {
        this.userInfo = userInfo;
    }
}
