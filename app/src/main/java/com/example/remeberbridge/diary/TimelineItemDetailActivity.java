package com.example.remeberbridge.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.remeberbridge.R;
import com.example.remeberbridge.utils.AlertDialogHelper;

/**
 * TimelineItemDetailActivity.java
 * @desc : 타임라인 상세 게시글화면 - 조회/수정/삭제 기능
 * 
 * @author : wkimdev
 * @created : 1/28/24
 * @version 1.0
 * @modifyed 
 **/
public class TimelineItemDetailActivity extends AppCompatActivity {

    private Button btn_timeline_delete;
    private Button btn_timeline_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_item);


        btn_timeline_update = findViewById(R.id.btn_timeline_update);
        btn_timeline_delete = findViewById(R.id.btn_timeline_delete);


        //상세 컨텐츠를 조회하는 키값이 무엇인지 찾아본다.(ex: dirary_id)
        //해당 키값을 통해 api 를 요청해 해당 게시글에 상세 내용을 리턴받는다.
        //리턴받는 값을 ui에 바인딩 한다.

        //타이틀이나 내용을 수정한 뒤 업데이트 가능한 API를 호출한다.
        //API 요청/응답이 정상인지 확인한다.
        //응답이 절상일 경우 수정이 완료되었습니다 토스트 팝업이 뜨고 이전 화면으로 돌아간다.
        //일반화면과 편집화면의 UI가 다르다.
        //근데 굳이 화면을 나눠야 하나?
        //쑥쑥찰칵은 어떻게 했는데? (조회화면과 편집화면이 나눠짐) 이게 더 편해보임.


    }

    @Override
    protected void onResume() {
        super.onResume();


        //수정하기 버튼
        btn_timeline_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //수정하기
            }
        });

        //삭제하기 버튼
        btn_timeline_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    } // {end} onResume


    private void showDeleteConfirmationDialog() {
        DialogInterface.OnClickListener deleteClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code to delete something
                Toast.makeText(TimelineItemDetailActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        };

        DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        };

        AlertDialogHelper.showAlertDialog(
                TimelineItemDetailActivity.this,
                "삭제",
                "내가 쓴글을 삭제하시겠어요?\n삭제하시면 이 글을 더 이상 볼 수 없어요",
                "삭제",
                deleteClickListener,
                "취소",
                cancelClickListener
        );

    }


}