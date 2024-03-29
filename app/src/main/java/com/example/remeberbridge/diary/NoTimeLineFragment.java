package com.example.remeberbridge.diary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.remeberbridge.R;

/**
 * NoTimeLineFragment.java
 * @desc : 사용자가 반려견은 등록했으나, 해당 반려견에 대해 추억은 생성하지 않는경우(타임라인이 없는경우) 화면
 *
 * @author : wkimdev
 * @created : 1/23/24
 * @version 1.0
 * @modifyed
 **/
public class NoTimeLineFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_notimeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //View 바인딩
        Button noTimeline_btn_add = view.findViewById(R.id.noTimeline_btn_add);

        noTimeline_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: 타임라인생성 버튼!!!");
            }
        });

    }
}
