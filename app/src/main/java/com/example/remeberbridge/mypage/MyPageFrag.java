package com.example.remeberbridge.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.R;
import com.example.remeberbridge.model.dog.Dog;
import com.example.remeberbridge.utils.PreferenceManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MyPageFrag.java
 * @desc : 마이페이지 프레그먼트
 *
 * @author : wkimdev
 * @created : 2023/12/23
 * @version 1.0
 * @modifyed
 **/
public class MyPageFrag extends Fragment {

    private Button mypage_button_setting;
    private MaterialCardView mypage_card_profile;
    private MaterialCardView mypage_card_dogs;

    private TextView mypage_txt_myEmail;

    //내 강아지리스트 데이터를 담을 리사이클러뷰 선언
    private RecyclerView mypage_rc_doglist;
    private MyDogAdapter myDogAdapter; //어뎁터 선언
    private ArrayList<Dog> dogArrayList; //강아지리스트
    private String TAG = this.getClass().getSimpleName();


    /** 프래그먼트에 맞는 UI를 그리기 위해 View를 반환하는 콜백메소드
    프래그먼트의 레이아웃 루트이기 때문에 UI를 제공하지 않을 경우, null을 반환*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false);
    }

    /** onCreateView가 호출 후 return한 뒤에 곧바로 실행되는 콜백 메소드
    View가 완전히 생성되었을때 호출된다.
    onCreateaView가 아닌, 아래 콜백에서 뷰바인딩을 하도록 권장*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: 호출!!! ");
        //프래그먼트 레아아웃의 루트view를 가져와 findViewById를 실행
        mypage_card_profile = view.findViewById(R.id.mypage_card_profile);
        mypage_card_dogs = view.findViewById(R.id.mypage_card_dogs);
        mypage_rc_doglist = view.findViewById(R.id.mypage_rc_doglist);
        mypage_txt_myEmail = view.findViewById(R.id.mypage_txt_myEmail);

        //서비스 설정화면 이동
        mypage_button_setting = view.findViewById(R.id.mypage_button_setting);

    }


    @Override
    public void onResume() {
        super.onResume();


        String email = PreferenceManager.getString(getActivity(), "user_email");
        //로그인한 내 계정 이메일노출
        mypage_txt_myEmail.setText(email);

        //강아지리스트 초기화
        dogArrayList = new ArrayList<>();

        // Dog 객체 정보 더미 리스트 생성
        List<String> dogName = Arrays.asList("멍멍이", "솜이", "준이", "미미");
        List<String> birthdays = Arrays.asList("2020-01-01", "2021-05-20", "2019-12-31", "2019-12-31");
        for (int i = 0; i < 100; i++) {
            // Dog 객체 생성
            Dog dog = new Dog(dogName.get(i % dogName.size()), birthdays.get(i % birthdays.size()));
            // ArrayList에 추가
            dogArrayList.add(dog);
        }


        //화면최초 진입시 리사이클러뷰로 강아지 리스트 노출
        myDogAdapter = new MyDogAdapter(dogArrayList, getContext());
        //가로로 레이아웃을 만든다
        mypage_rc_doglist.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        mypage_rc_doglist.addItemDecoration(new MyDogItemDecoration(getContext()));
        mypage_rc_doglist.setAdapter(myDogAdapter);


        mypage_button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

    }
}
