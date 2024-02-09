package com.example.remeberbridge.diary;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.remeberbridge.board.BoardTabFragment;
import com.example.remeberbridge.board.ChatTabFragment;

/**
 * ScreenSlidePagerAdapter.java
 * @desc : viewpager2로 변환
 *
 * @author : wkimdev
 * @created : 2/8/24
 * @version 1.0
 * @modifyed
 **/
public class ScreenSlidePagerAdapter2 extends FragmentStateAdapter {

    private static boolean isTimeLineValueExist;

    public ScreenSlidePagerAdapter2(Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int).
        if (position == 0) {
            return isTimeLineValueExist ? new BoardTabFragment() : new ChatTabFragment();
        } else {
            return new AIStoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // The number of pages
    }

}
