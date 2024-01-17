package com.example.remeberbridge.diary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.R;
import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.diary.DiaryInfoResult;
import com.example.remeberbridge.service.DiaryService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.PreferenceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TabFragment1.java
 * @desc : 타임라인 리스트 프레그먼트, 스크롤 적용
 *
 * @author : wkimdev
 * @created : 2024/01/04
 * @version 1.0
 * @modifyed
 **/
public class TimeLineFragment2 extends Fragment {

    private DiaryService diaryService = RetrofitClientInstance.getRetrofitInstance().create(DiaryService.class);
    TimeLineListAdapter2 timeLineListAdapter2;
    RecyclerView recyclerView;

    private int page = 1; //최초 페이지 호출
    private int limit = 15; //페이지 사이즈 지정
    private String TAG = this.getClass().getSimpleName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.diary_tab1_fragment_4, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 숫자 목록을 담을 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.recy);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // 초기 입력 데이터 준비
        ArrayList<DiaryInfoResult> newList = new ArrayList<>();

        //페이징과 함께 아이템을 업데이하도록 요청하는 메소드
        callItemWithPaging();

        /*for(int i = 0; i < 30; i++){
            newList.add(i);
        }*/

        // 리사이클러뷰 어댑터 초기화 및 리사이클러뷰에 어댑터 설정
        timeLineListAdapter2 = new TimeLineListAdapter2(newList);
        recyclerView.setAdapter(timeLineListAdapter2);


        // 리사이클러뷰 스크롤리스너 설정
        // (스크롤 상태가 변경 되었을 때 호출)
        //스크롤 상태가 변경될때마다 호출됨
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 현재 리사이클러뷰 아이템 리스트에 로딩 아이템이 있는지 확인, 없다면 내부 코드 실행
                // numberlist의 사이즈값을 의미함.
                if (timeLineListAdapter2.isLastItemLoading()) {
                    Log.e(TAG, "isLastItemLoading: CALL!! ");

                    // 현재 리사이클러뷰의 최하단이여서 더 이상 스크롤 할 수 없을 경우 내부 코드 실행
                    if(!recyclerView.canScrollVertically(1)) {
                        Log.e(TAG, "onScrollStateChanged: 스크롤 최하단 이다!!!");

                        // 리사이클러뷰 아이템 리스트에 로딩 아이템 추가
                        // -1를 넣어서 숫자 아이템 로딩이 재시작 됨을 알린다.
                        timeLineListAdapter2.addLoadingData();

                        //더이상 호출할 데이터가 없으면 스크롤 이벤트는 발생하더라도, 서버에 데이터요청은 하지 않도록 처리
                        page++;
                        callItemWithPaging();
                    }
                }
            }
        });
    }

    private void callItemWithPaging() {

        int userId = PreferenceManager.getInt(getContext(), "user_id");
        //Log.e(TAG, "callItemWithPaging: user_id: " +  userId);
        Log.e(TAG, "callItemWithPaging: page 값 확인: " +  page);

        diaryService.getDiaryByUserId(userId, page, limit).enqueue(new Callback<ResponseWrapper>() {
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ResponseWrapper responseWrapper = response.body();
                    ResponseCommonData responseCommonData = responseWrapper.getResult();
                    String code = responseCommonData.getCode();
                    String message = responseCommonData.getMessage();

                    if ("2000".equals(code)) { //인증성공

                        generateDataList(responseCommonData.getDiaryInfo());

                    } else {
                        //인증실패 오류팝업을 노출시킨다.
                        AlertDialogHelper.showAlertDialogOnlyConfrim(
                                getContext(), // 현재 Activity의 Context
                                "", // 다이얼로그 제목
                                message); // 다이얼로그 메시지
                    }
                } else { //서버오류
                    //인증실패 오류팝업을 노출시킨다.
                    AlertDialogHelper.showAlertDialogOnlyConfrim(
                            getContext(), // 현재 Activity의 Context
                            "", // 다이얼로그 제목
                            "오류가 발생했습니다. \n잠시후 다시 시도해주세요."); // 다이얼로그 메시지
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper> call, Throwable t) {
                Log.e(TAG, "레트로핏 응답 실패 오류 발생 ", t);
                AlertDialogHelper.showAlertDialogOnlyConfrim(
                        getContext(), // 현재 Activity의 Context
                        "", // 다이얼로그 제목
                        "오류가 발생했습니다. \n잠시후 다시 시도해주세요." // 다이얼로그 메시지
                );
                t.printStackTrace();
            }
        });
    }

    private void generateDataList(ArrayList<DiaryInfoResult> diaryInfo) {
     
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeLineListAdapter2.addItems(diaryInfo);
            }
        });        
    }

}