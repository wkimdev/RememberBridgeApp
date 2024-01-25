package com.example.remeberbridge.board;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.remeberbridge.R;
import com.google.android.material.tabs.TabLayout;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager pager = view.findViewById(R.id.pager);
        TabLayout tabLayout = view.findViewById(R.id.board_tab_layout);

        pager.setOffscreenPageLimit(2); //현재 페이지의 양쪽에 보유해야하는 페이지 수를 설정 (상황에 맞게 사용하시면 됩니다.)
        tabLayout.setupWithViewPager(pager); //텝레이아웃과 뷰페이저를 연결
        pager.setAdapter(new PageAdapter(getActivity().getSupportFragmentManager(),getContext())); //뷰페이저 어뎁터 설정 연결
    }

    static class PageAdapter extends FragmentStatePagerAdapter { //뷰 페이저 어뎁터


        PageAdapter(FragmentManager fm, Context context) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) { //프래그먼트 사용 포지션 설정 0 이 첫탭
                return new BoardTabFragment(); //타임라인
            } else {
                return new ChatTabFragment();  //AI스토리
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) { //텝 레이아웃의 타이틀 설정
                return "게시글";
            } else {
                return "채팅";
            }
        }
    }


}
