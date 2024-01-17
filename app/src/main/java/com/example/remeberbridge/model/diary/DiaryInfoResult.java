package com.example.remeberbridge.model.diary;

import com.google.gson.annotations.SerializedName;

/**
 * DIARY.java
 * @desc : 강아지 타임라인을 보여줄 DTO 모델
 *
 * @author : wkimdev
 * @created : 2024/01/02
 * @version 1.0
 * @modifyed
 **/
public class DiaryInfoResult {

    public DiaryInfoResult(String diaryContent) {
        this.diaryContent = diaryContent;
    }

    @SerializedName("diary_id")
    int diaryId;

    @SerializedName("space_id")
    int spaceId;

    @SerializedName("user_id")
    int userId;

    //이게 뭐임?
    @SerializedName("select_date")
    String selectDate;

    @SerializedName("emotion")
    int emotion;

    @SerializedName("diary_content")
    String diaryContent;

    @SerializedName("create_at")
    String createAt;

    @SerializedName("update_at")
    String updateAt;


    @SerializedName("total_count")
    private int totalCount;


    //스크롤뷰터에서 값을 구분하기 위한 구분값
    private int isLoad = 1;

    public int getIsLoad() {
        return isLoad;
    }

    public DiaryInfoResult setIsLoad(int isLoad) {
        this.isLoad = isLoad;
        return null;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId = diaryId;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(String selectDate) {
        this.selectDate = selectDate;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public String getDiaryContent() {
        return diaryContent;
    }

    public void setDiaryContent(String diaryContent) {
        this.diaryContent = diaryContent;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
