package com.example.remeberbridge.model.dog;

/**
 * Dog.java
 * @desc : 내가 추억으로 생성한 강아지 모델
 *
 * @author : wkimdev
 * @created : 2023/12/23
 * @version 1.0
 * @modifyed
 **/
public class Dog {

    private String profileImg;

    private String dogName;

    //@todo - delete 미사용값
    private String birthday;

    private String createdDay;


    public Dog(String profileImg, String dogName, String createdDay) {
        this.profileImg = profileImg;
        this.dogName = dogName;
        this.createdDay = createdDay;
    }

    public Dog(String profileImg, String dogName, String birthday, String createdDay) {
        this.profileImg = profileImg;
        this.dogName = dogName;
        this.birthday = birthday;
        this.createdDay = createdDay;
    }
    public Dog(String dogName, String birthday) {
        this.dogName = dogName;
        this.birthday = birthday;
    }


    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreatedDay() {
        return createdDay;
    }

    public void setCreatedDay(String createdDay) {
        this.createdDay = createdDay;
    }
}
