package com.example.remeberbridge.model.diary;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class RequestAddNewDog {

    public RequestAddNewDog(String userEmail, String dogName, String dogBirth, int dogBreed, String dogSex, File dogProfImg) {
        this.userEmail = userEmail;
        this.dogName = dogName;
        this.dogBirth = dogBirth;
        this.dogBreed = dogBreed;
        this.dogSex = dogSex;
        this.dogProfImg = dogProfImg;
    }

    @SerializedName("user_email")
    String userEmail;

    @SerializedName("dog_name")
    String dogName;

    //"YYYY-MM-DD" 형식의 문자열
    @SerializedName("dog_birth")
    String dogBirth;

    //품종 index
    @SerializedName("dog_breed")
    int dogBreed;

    //F:여성, M:남성
    @SerializedName("dog_sex")
    String dogSex;

    @SerializedName("dog_prof_img")
    File dogProfImg;

}
