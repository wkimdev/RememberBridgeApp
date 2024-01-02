package com.example.remeberbridge.utils;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * NavigationUtils.java
 * @desc : 프레그먼트 이동시 메뉴바도 같이 이동하는 유틸리티 코드
 *
 * @author : wkimdev
 * @created : 2023/12/31
 * @version 1.0
 * @modifyed
 **/
public class NavigationUtils {

    //@Learned - gg
    public static void setFragment(FragmentManager fragmentManager, @IdRes int containerId, Fragment fragment,
                                   BottomNavigationView bottomNavigationView, @IdRes int menuItemId) {

        //@Learned - gg
        //@Todo - 되는데
        // Perform the fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commit();

        // Set the bottom navigation bar item as selected
        Menu menu = bottomNavigationView.getMenu(); //이게 널이래.
        MenuItem item = menu.findItem(menuItemId);
        if (item != null) {
            item.setChecked(true);
        }
    }

}
