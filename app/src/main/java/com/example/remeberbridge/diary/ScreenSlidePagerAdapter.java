package com.example.remeberbridge.diary;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * ScreenSlidePagerAdapter.java
 * @desc : viewpager2로 변환
 *
 * @author : wkimdev
 * @created : 2/8/24
 * @version 1.0
 * @modifyed
 **/
public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    private static boolean isTimeLineValueExist;

    public ScreenSlidePagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int).
        if (position == 0) {
            return isTimeLineValueExist ? new TimeLineFragment2() : new NoTimeLineFragment();
        } else {
            return new AIStoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // The number of pages
    }

}
