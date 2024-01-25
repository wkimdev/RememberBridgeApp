package com.example.remeberbridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.remeberbridge.board.BoardFrag;
import com.example.remeberbridge.diary.RememberFrag;
import com.example.remeberbridge.home.HomeFrag;
import com.example.remeberbridge.mypage.MyPageFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


/**
 * MainActivity.java
 * @desc : 로그인이후 진입하는 화면, 왜 이게 디폴트지??
 *  - 백버튼 2개 눌렀을시 종료처리
 *  - 메뉴 4개 노출
 *
 * @author : wkimdev
 * @created : 2023/12/14
 * @version 1.0
 * @modifyed
 **/

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    //하단메뉴바 선언
    private BottomNavigationView bottomNavi;

    //프래그먼트 사용 선언
    private FragmentManager fm;
    private FragmentTransaction ft;
    private HomeFrag homeFrag; //메인화면
    private RememberFrag rememberFrag; //추억공간

    private BoardFrag boardFrag; //게시판
    private MyPageFrag mypageFrag; //마이페이지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: CALL!!!");

        //뷰바인딩
        bottomNavi = findViewById(R.id.main_bottom_menu);

        //프래그먼트 객체 생성
        homeFrag = new HomeFrag();
        // TODO: 2024/01/09 임시 테스트
        rememberFrag = new RememberFrag();
        boardFrag = new BoardFrag();
        mypageFrag = new MyPageFrag();


        //여기서
        //Intent로 프래그먼트 이동 지정
        Intent i = getIntent();
        int pageValue = i.getIntExtra("fromNewAddDog", 0);
        if (pageValue > 0 ) {
            //지정된 프래그먼트 화면으로 이동
            setFrag(1);
        } else {
            //첫 프래그먼트 화면 지정
            setFrag(0);
        }



        //회의예약 추가 후 화면 이동
        /*if (intent.getBooleanExtra("fromAddReservation", false)) {
            //예약화면 프래그먼트 화면 지정
            setFrag(1);
            bottomNavi.setSelectedItemId(R.id.action_reservation);
        } else {
            //첫 프래그먼트 화면 지정
            setFrag(0);
        }*/

        //하단 메뉴바 리스너 등록
        SettingListener();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: CALL!!!");
    }


    //하단메뉴바 클릭 리스너 등록
    private void SettingListener() {

        //선택 리스너 등록
        bottomNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home: {
                        item.setChecked(true);
                        item.getSubMenu();
                        setFrag(0);
                        break;
                    }
                    case R.id.action_photos: { //추억공간 화면
                        item.setChecked(true);
                        setFrag(1);
                        break;
                    }
                    case R.id.action_board: { //게사판
                        item.setChecked(true);
                        setFrag(2);
                        break;
                    }
                    case R.id.action_setting: { //마이페이지
                        item.setChecked(true);
                        setFrag(3);
                        break;
                    }
                }
                return false;
            }
        });

    }

    // 프래그먼트 교체가 일어나는 실행문
    // 프래그먼트 트랜잭션을 수행하는 코드
    private void setFrag(int n) {
        //액티비티와 관련된 프래그먼트와 상호작용하기 위해 fragmentManager 반환
        fm = getSupportFragmentManager();
        //fragmentManager와 연결된 일련의 처리과정을 시작
        ft = fm.beginTransaction();

        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, homeFrag);
                ft.commit(); //commit을 호출해야 내용이 적용된다
                break;

            case 1:
                ft.replace(R.id.main_frame, rememberFrag);
                //item.setChecked(true);
                ft.commit(); //commit을 호출해야 내용이 적용된다
                break;

            case 2:
                ft.replace(R.id.main_frame, boardFrag);
                ft.commit(); //commit을 호출해야 내용이 적용된다
                break;

            case 3:
                ft.replace(R.id.main_frame, mypageFrag);
                ft.commit(); //commit을 호출해야 내용이 적용된다
                break;
        }
    }


}