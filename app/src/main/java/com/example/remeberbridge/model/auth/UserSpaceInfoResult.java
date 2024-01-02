package com.example.remeberbridge.model.auth;


import com.google.gson.annotations.SerializedName;

/**
 * UserSpaceInfo.java
 * @desc : 사용자의 반려견 추억정보 생성화면 DTO 클래스
 *
 * @author : wkimdev
 * @created : 2024/01/01
 * @version 1.0
 * @modifyed
 **/
public class UserSpaceInfoResult {

    //GSON에 포함된 어노테이션
    //@SerializedName("속성명")으로
    //문자열로 넘겨주는 속성명은 해당 객체가 JSON으로 바뀔때 사용하는 값으로,
    // 변수이름과 속성명을 똑같이 준다면 생략가능하다
    @SerializedName("space_id")
    private int spaceId;

    @SerializedName("dog_prof_img")
    private String dogProfImg;

    @SerializedName("dog_name")
    private String dogName;

    @SerializedName("create_at")
    private String createAt;

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public String getDogProfImg() {
        return dogProfImg;
    }

    public void setDogProfImg(String dogProfImg) {
        this.dogProfImg = dogProfImg;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}