package com.example.remeberbridge.model;

import com.google.gson.annotations.SerializedName;

/**
 * ResponseWrapper.java
 * @desc : 최상위 응답을 위한 감싸는 클래스
 * 
 * @author : wkimdev
 * @created : 2023/12/14
 * @version 1.0
 * @modifyed 
 **/
public class ResponseWrapper {

    @SerializedName("result")
    private ResponseCommonData result;

    public ResponseCommonData getResult() {
        return result;
    }
}
