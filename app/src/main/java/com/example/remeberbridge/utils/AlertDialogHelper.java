package com.example.remeberbridge.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


/**
 * AlertDialogHelper.java
 * @desc : 공통 팝업 클래스
 *
 * @author : wkimdev
 * @created : 2023/12/11
 * @version 1.0
 * @modifyed
 **/
public class AlertDialogHelper {


    public static void showAlertDialog(Context context, String title, String message,
                                       String positiveButtonText, DialogInterface.OnClickListener positiveListener,
                                       String negativeButtonText, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, positiveListener);
        builder.setNegativeButton(negativeButtonText, negativeListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void showAlertDialogOnlyConfrim(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // "확인" 버튼을 클릭했을 때의 동작
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
