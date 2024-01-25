package com.example.remeberbridge.model.diary;

import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * RequestAddNewTimeline.java
 * @desc : 신규타임라인을 작성하는 DTO 클래스
 *
 * @author : wkimdev
 * @created : 1/25/24
 * @version 1.0
 * @modifyed
 **/
public class RequestAddNewTimeline {

    public RequestAddNewTimeline(int spaceId, int userId, String selectDate, int emotion, String diaryContent) {
        this.spaceId = spaceId;
        this.userId = userId;
        this.selectDate = selectDate;
        this.emotion = emotion;
        this.diaryContent = diaryContent;
    }

    public RequestAddNewTimeline(int spaceId, int userId, String selectDate, int emotion, String diaryContent, File diaryImgs) {
        this.spaceId = spaceId;
        this.userId = userId;
        this.selectDate = selectDate;
        this.emotion = emotion;
        this.diaryContent = diaryContent;
        this.diaryImgs = diaryImgs;
    }

    @SerializedName("space_id")
    int spaceId;

    @SerializedName("user_id")
    int userId;

    //"YYYY-MM-DD" 형식의 문자열
    @SerializedName("select_date")
    String selectDate;

    //감정표현
    @SerializedName("emotion")
    int emotion;

    @SerializedName("diary_content")
    String diaryContent;

    @SerializedName("diary_imgs")
    File diaryImgs;

}
