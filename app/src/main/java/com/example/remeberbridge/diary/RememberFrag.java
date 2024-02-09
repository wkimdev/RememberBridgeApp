package com.example.remeberbridge.diary;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.remeberbridge.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * RememberFrag.java
 * @desc : 추억공간 프레그먼트
 *  - TabLayout와 ViewPager를 이용해서 Fragment를 전환한다.
 *
 * @author : wkimdev
 * @created : 2023/12/23
 * @version 1.0
 * @modifyed
 **/
public class RememberFrag extends Fragment {

    private String TAG = this.getClass().getSimpleName();
    private static boolean isDogValueExist;
    private static boolean isTimeLineValueExist;
    
    private ViewPager2 viewPager2;

    //프래그먼트에 맞는 UI를 그리기 위해 View를 반환하는 콜백메소드
    //프래그먼트의 레이아웃 루트이기 때문에 UI를 제공하지 않을 경우, null을 반환
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remember, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: CALL!!!");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: CALL...!!!");
        //setupViewPagerAndTabs();

        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);

        viewPager2 = getView().findViewById(R.id.remember_pager); // Ensure your layout uses ViewPager2
        //java.lang.IllegalStateException: Fragment no longer exists for key f#0: unique id c77b467f-b787-42c0-8b59-97995adda7dc
        viewPager2.setAdapter(new ScreenSlidePagerAdapter(RememberFrag.this));


        // TabLayout과 ViewPager2 연결
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("타임라인");
                    } else {
                        tab.setText("AI 스토리");
                    }
                }
        ).attach();
    }

    private void setupViewPagerAndTabs() {
        if (getView() == null) return; // Safety check

        ViewPager pager = getView().findViewById(R.id.remember_pager);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);

        pager.setOffscreenPageLimit(2); //현재 페이지의 양쪽에 보유해야하는 페이지 수를 설정 (상황에 맞게 사용하시면 됩니다.)
        tabLayout.setupWithViewPager(pager); //텝레이아웃과 뷰페이저를 연결

        //중첩된 조각(예: 다른 Fragment 내부의 ViewPager 내 Fragment)을 처리할 때 getActivity 대신 getChildFragmentManager()를 사용해야 합니다.
        pager.setAdapter(new PageAdapter(getChildFragmentManager(), getContext()));

        //TODO: 1/15/24
        // - 테스트를 위해 하드코딩 설정
        // - api를 호출해서, 해당값이 존재하는지 판단해야함.
        //isDogValueExist = PreferenceManager.getBoolean(getContext(), "isDogValueExist");
        isDogValueExist = false;
        isTimeLineValueExist = true;

    }

    //재 호출이 안됨.
    static class PageAdapter extends FragmentStatePagerAdapter {
        private String TAG = this.getClass().getSimpleName(); //뷰 페이저 어뎁터

        PageAdapter(FragmentManager fm, Context context) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) { //프래그먼트 사용 포지션 설정 0 이 첫탭
                //return new TimeLineFragment(); //타임라인
                //if (isDogValueExist){
                if (isTimeLineValueExist) {
                    Log.e(TAG, "getItem: CALL!!!");
                    return new TimeLineFragment2(); //타임라인
                } else {
                    //return new NoDogFragment(); //반려견 데이터가 없음
                    return new NoTimeLineFragment(); //타임라인 데이터가 없음.
                }

            } else {
                return new AIStoryFragment();  //AI스토리
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
                return "타임라인";
            } else {
                return "AI스토리";
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.e(TAG, "onDestroyView: CALL 뷰페이저 초기화!");

        //이 설정을 하는 이유는 아래와 같습니다.(정확히 이해한건 아님: 참고링크-> https://stackoverflow.com/questions/72156771/android-viewpager-2-fragment-no-longer-exists-for-key)
        //뷰페이저 생성시, 내부적으로 상태를 저장하도록 되어 있다(정확히 상태라고 표현하는지는 모르겠다)
        //- FragmentStateAdapters internal mFragmentMaxLifecycleEnforcer is causing a memory leak because it's holding a hard reference to the ViewPager2 instance.
        //- FragmentStateAdapters 내부의 mFragmentMaxLifecycleEnforcer가 ViewPager2 인스턴스에 대한 하드 참조를 보유하고 있기 때문에 메모리 누수가 발생하고 있습니다.

        //이에 대한 옵션명은 "saveEnabled"인데, 이는 기본값이 true이고, 이 기본값이 설정되어 있으면 ID도 할당되어 있어야 한다.
        //현재 프레그먼트에서 다른 프레그먼트로 이동 후, 다시 재진입할때 "이전에 저장된 뷰페이저값을 찾게 되는데" 이때 할당된 ID로 찾게된다.!!!
        //그래서 ID를 할당하지 않게되면 이 오류가 발생된다 : java.lang.IllegalStateException: Fragment no longer exists for key f#0: unique id c77b467f-b787-42c0-8b59-97995adda7dc
        //해당 오류를 발생시키지 않기 위해 뷰페이저 XML에 saveEnabled옵션을 false로 지정하고,
        //메모리누수를 방지하기 위해 아래처럼 초기화를 시켰다.
        viewPager2.setAdapter(null);
    }
}
