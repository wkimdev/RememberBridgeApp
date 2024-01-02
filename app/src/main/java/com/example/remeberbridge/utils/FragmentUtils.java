package com.example.remeberbridge.utils;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * FragmentUtils.java
 * @desc : 프레그먼트를 공통으로 이동시키기 위한 유틸리티 함수
 *
 * @author : wkimdev
 * @created : 2023/12/31
 * @version 1.0
 * @modifyed
 **/
public class FragmentUtils {

    public static void replaceFragment(FragmentManager fragmentManager, @IdRes int containerId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commit();
    }
}
