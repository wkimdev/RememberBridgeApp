package com.example.remeberbridge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * BoardFrag.java
 * @desc : 게시판 프레그먼트
 *
 * @author : wkimdev
 * @created : 2023/12/23
 * @version 1.0
 * @modifyed
 **/
public class BoardFrag extends Fragment {

    //프래그먼트에 맞는 UI를 그리기 위해 View를 반환하는 콜백메소드
    //프래그먼트의 레이아웃 루트이기 때문에 UI를 제공하지 않을 경우, null을 반환
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false);
    }


}
