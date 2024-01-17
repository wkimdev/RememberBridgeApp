package com.example.remeberbridge.model.diary;

import com.google.gson.annotations.SerializedName;

public class RequestAddNewDog {


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
    String dogProfImg;

}
