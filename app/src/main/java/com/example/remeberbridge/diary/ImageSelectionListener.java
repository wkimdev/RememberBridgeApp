package com.example.remeberbridge.diary;

/**
 * ImageSelectionListener.java
 * @desc : 사진선택 인터페이스
 *
 * @author : wkimdev
 * @created : 1/29/24
 * @version 1.0
 * @modifyed
 **/
public interface ImageSelectionListener {
    void onImageSelectionRequested();

    void onTimelineItemClick(int diaryId);
}
