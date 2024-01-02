package com.example.remeberbridge.model.auth;

import com.google.gson.annotations.SerializedName;

// 로그인 응답 Data DTO 모델 클래스
public class UserLoginInfoResult {

    //GSON에 포함된 어노테이션
    //@SerializedName("속성명")으로
    //문자열로 넘겨주는 속성명은 해당 객체가 JSON으로 바뀔때 사용하는 값으로,
    // 변수이름과 속성명을 똑같이 준다면 생략가능하다
    @SerializedName("user_id")
    private int userId;


    @SerializedName("user_name")
    private String userName;


    @SerializedName("user_prof_img")
    private String userProfImg;


    @SerializedName("login_sns_type")
    private String loginSnsType;


    @SerializedName("user_email")
    private String userEmail;


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserProfImg(String userProfImg) {
        this.userProfImg = userProfImg;
    }

    public void setLoginSnsType(String loginSnsType) {
        this.loginSnsType = loginSnsType;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserProfImg() {
        return userProfImg;
    }

    public String getLoginSnsType() {
        return loginSnsType;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
