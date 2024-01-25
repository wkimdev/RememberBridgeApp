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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.R;
import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.auth.UserSpaceInfoResult;
import com.example.remeberbridge.model.diary.Diary;
import com.example.remeberbridge.model.diary.DiaryInfoResult;
import com.example.remeberbridge.model.dog.Dog;
import com.example.remeberbridge.service.DiaryService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

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
public class TimeLineFragment extends Fragment {

    //레트로핏 서비스 API 객체 생성
    private DiaryService diaryService = RetrofitClientInstance.getRetrofitInstance().create(DiaryService.class);

    private RecyclerView diary_rc;
    private ProgressBar progress_bar;
    private TimeLineListAdapter timeLineListAdapter;

    private int page = 1; //최초 페이지 호출
    private int limit = 15; //페이지 사이즈 지정

    // 진행바
    private ProgressDialog progressDoalog;
    private String TAG = this.getClass().getSimpleName();

    private NestedScrollView nestedScrollView; //스크롤뷰

    private int totalCont; //전체 다이어 갯수

    private ArrayList<DiaryInfoResult> diaryInfoResultArrayList;

    private TextView txt_no_timeline;
    private LinearLayoutCompat ll_title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.diary_tab1_fragment_2, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(TAG, "onViewCreated: CALL...!!!");


        ll_title = view.findViewById(R.id.ll_title);
        progress_bar = view.findViewById(R.id.progress_bar);
        nestedScrollView = view.findViewById(R.id.timeline_scroll_view); //스크롤뷰
        txt_no_timeline = view.findViewById(R.id.timeline_txt_no_timeline); //데이터가 없을때 노출함


        //@TODO: 2024/01/11 user_id가 있는지 예외처리 필요
        ////////////// NEW 스크롤 처리 //////////////


        page = 1; //호출페이지 초기화


        //기본로딩바 노출
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();


        // 숫자 목록을 담을 리사이클러뷰 초기화
        diary_rc = view.findViewById(R.id.timeline_diary_rc);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        diary_rc.setLayoutManager(linearLayoutManager);

        // 초기 입력 데이터 준비
        diaryInfoResultArrayList = new ArrayList<>(); //타임라인(일기) 리스트 초기화

        //페이징과 함께 아이템을 업데이하도록 요청하는 메소드
        //최초화면 진입시, 1페이지 15개 아이템을 요청한다

        // TODO: 1/25/24 임시 주석처리
        //callItemWithPaging();

        // 리사이클러뷰 어댑터 초기화 및 리사이클러뷰에 어댑터 설정
        timeLineListAdapter = new TimeLineListAdapter(diaryInfoResultArrayList, getContext());
        diary_rc.setAdapter(timeLineListAdapter);
        diary_rc.addItemDecoration(new DiaryItemDecoration(getContext()));


        // 리사이클러뷰 스크롤리스너 설정
        //스크롤 상태가 변경 될 때마다 호출됨
       /* diary_rc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                Log.e(TAG, "onScrollStateChanged: CALL!! ");

                // 현재 리사이클러뷰 아이템 리스트에 로딩 아이템이 있는지 확인, 없다면 내부 코드 실행
                // numberlist의 사이즈값을 의미함.
                //if (timeLineListAdapter.getLastItem() != -1 ) {

                    // 현재 리사이클러뷰의 최하단이여서 더 이상 스크롤 할 수 없을 경우 내부 코드 실행
                    if(!recyclerView.canScrollVertically(1)) { // [리사이클러뷰 최하단 if구문 start]

                        // 리사이클러뷰 아이템 리스트에 로딩 아이템 추가
                        // -1를 넣어서 숫자 아이템 로딩이 재시작 됨을 알린다.
                        //timeLineListAdapter.addLoadingData();

                        //더이상 호출할 데이터가 없으면 스크롤 이벤트는 발생하더라도, 서버에 데이터요청은 하지 않도록 처리
                        if (timeLineListAdapter.getItemCount() < totalCont) {
                            page++;
                            //progress_bar.setVisibility(View.VISIBLE);
                            callItemWithPaging();
                        }

                    }// [리사이클러뷰 최하단 if구문 end]
                //} // [어뎁터 if구문 end]


            }
        });*/






        ////////////// 스크롤 처리 //////////////

        page = 1; //호출페이지 초기화
        diaryInfoResultArrayList = new ArrayList<>(); //타임라인(일기) 리스트 초기화

        //기본로딩바 노출
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();

        //화면최초 진입시 리사이클러뷰로 회의리스트 노출
        timeLineListAdapter = new TimeLineListAdapter(diaryInfoResultArrayList, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());


        //화면에 새로 진입할때마다 이전 deco가 쌓여서 간격이 넓어지는 현상이 발생하기 때문에,
        //기존의 decoration을 remove() 시키고 새로 추가한다
        while (diary_rc.getItemDecorationCount() > 0) {
            diary_rc.removeItemDecorationAt(0);
        }
        diary_rc.addItemDecoration(new DiaryItemDecoration(getContext()));
        diary_rc.setLayoutManager(layoutManager);
        diary_rc.setAdapter(timeLineListAdapter);

        //페이징과 함께 아이템을 업데이하도록 요청하는 메소드
        // TODO: 1/25/24 임시주석처리
        //callItemWithPaging();

        //스크롤뷰 이벤트를 받아 다음페이징을 요청한다
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                //getChildAt - 그룹의 지정된 view의 높이를 나타낸다
                //getMeasuredHeight - view의 높이값 반환
                //스크롤뷰 높이값이 아이템리스트가 있는 뷰의 전체 높이값과 동일해지면 그때 다음 페이지를 호출한다\
                Log.e(TAG, "onScrollChange: scrollY: " + scrollY );
                Log.e(TAG, "onScrollChange: 스크롤뷰 계산값: " + (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) );

                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    Log.e(TAG, "timeLineListAdapter.getItemCount(): " + timeLineListAdapter.getItemCount());
                    Log.e(TAG, "totalCont: " + totalCont); //0

                    //더이상 호출할 데이터가 없으면 스크롤 이벤트는 발생하더라도, 서버에 데이터요청은 하지 않도록 처리
                    if (timeLineListAdapter.getItemCount() < totalCont) {
                        page++;
                        progress_bar.setVisibility(View.VISIBLE);
                        callItemWithPaging();
                    }
                }
            }
        });

    }


    /**
     * 페이징과 함께 아이템을 업데이하도록 요청하는 메소드
     *
     * 리사이클러뷰에서 페이징 할 때 필요한 것
     * 1) limit와 page 변수
     * 2) 쿼리문에 LIMIT limit(위의 변수) OFFSET (page-1)*limit를 붙인다
     * 3) recyclerView.addOnScrollListener() 함수와 findLastCompletelyVisibleItemPosition 함수를 이용하는 방법
     * 또는 스크롤뷰의 이벤트를 활용해 스크롤을 내리는 도중에 레트로핏을 호출 방법을 사용할 수 있다
     */
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

                        //회의생성 후 로딩바가 뜬 다음, 예약화면으로 이동
                        progressDoalog.dismiss();
                        //스크롤 로딩바
                        //progress_bar.setVisibility(View.GONE);

                        //전체회의아이템 갯수 응답
                        //테스트용
                        if (totalCont == 0) {
                            totalCont = responseCommonData.getDiaryInfo().get(0).getTotalCount();
                        }

                        //리스트값이 있을 경우, 타임라인 내용이 없다는 문구를 비노출한다
                        if (totalCont <= 0) {
                            txt_no_timeline.setVisibility(View.VISIBLE);
                        }  else {
                            txt_no_timeline.setVisibility(View.INVISIBLE);
                        }
                        //회의리스트를 담아 반복문을 돌면서 리스트에 아이템을 업데이트 시키고,
                        //adapter에 값이 변경됨을 noti한다
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


    private void generateDataList(ArrayList<DiaryInfoResult> diaryInfoResults) {
        if (timeLineListAdapter == null) {

            // Initialize your adapter only once
            timeLineListAdapter = new TimeLineListAdapter(diaryInfoResults, getContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            diary_rc.setLayoutManager(layoutManager);
            diary_rc.setAdapter(timeLineListAdapter);

            // Add decoration only once, not in a loop
            diary_rc.addItemDecoration(new DiaryItemDecoration(getContext()));
        }

        //데이터 신규 추가
        //timeLineListAdapter.addItems(diaryInfoResults);

        // Update the adapter's data set
        timeLineListAdapter.updateData(diaryInfoResults);

        // Notify the adapter that the data has changed
        timeLineListAdapter.notifyDataSetChanged();
    }

}