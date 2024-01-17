package com.example.remeberbridge.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.remeberbridge.mypage.AddDogActivity;
import com.example.remeberbridge.R;

/**
 * NoTimeLineFragment.java
 * @desc : 등록한 반려견이 없을때 프레그먼트 화면
 *
 * @author : wkimdev
 * @created : 1/15/24
 * @version 1.0
 * @modifyed
 **/
public class NoTimeLineFragment extends Fragment {

    private Button timelint_add_dog_Btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.diary_tab1_no_dog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timelint_add_dog_Btn = view.findViewById(R.id.timelint_add_dog_Btn);


        timelint_add_dog_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //반려견 등록 액티비티 이동
                Intent intent = new Intent(getContext(), AddDogActivity.class);
                startActivity(intent);
            }
        });

    }
}
