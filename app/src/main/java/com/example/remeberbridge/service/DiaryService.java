package com.example.remeberbridge.service;

import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.auth.RequestEmailLogin;
import com.example.remeberbridge.model.diary.RequestAddNewDog;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * DiaryService.java
 * @desc : 유저의 반려견 타임라인을 보여주도록 요청하는 API들
 *
 * @author : wkimdev
 * @created : 2024/01/02
 * @version 1.0
 * @modifyed
 **/
public interface DiaryService {

    @GET("/api/space/app/diary/{user_id}/{page}/{limit}")
    Call<ResponseWrapper> getDiaryByUserId(@Path("user_id") int userId
            , @Path("page") int page
            , @Path("limit") int limit);

    /**
    * 12. 추억공간생성
    */
    @POST("/api/space/")
    Call<ResponseWrapper> addNewDog(@Body RequestAddNewDog requestAddNewDog);

    /*@GET("/api/space/app/diary/{user_id}/{page}/{limit}")
    Call<ResponseWrapper> getDiaryByUserId(@Path("user_id") int userId, @Path("page") int page, @Path("limit") int limit);*/

}
