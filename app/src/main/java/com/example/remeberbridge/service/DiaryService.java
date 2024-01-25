package com.example.remeberbridge.service;

import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.auth.RequestEmailLogin;
import com.example.remeberbridge.model.diary.RequestAddNewDog;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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
     단일파일을 전송할때는 아래와 같이 사용한다.
     안드로이드에서 retrofit으로 이미지나 영상등을 보낼 때에는 Mutipart를 사용
     */
    @Multipart
    @POST("/api/space/")
    Call<ResponseWrapper> addNewDog(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    /*@GET("/api/space/app/diary/{user_id}/{page}/{limit}")
    Call<ResponseWrapper> getDiaryByUserId(@Path("user_id") int userId, @Path("page") int page, @Path("limit") int limit);*/

}
