package com.example.remeberbridge.diary;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.R;
import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.diary.DiaryInfoResult;
import com.example.remeberbridge.mypage.AddDogActivity;
import com.example.remeberbridge.service.DiaryService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TabFragment2.java
 * @desc : 타임라인 리스트 프레그먼트, 스크롤 적용
 *
 * @author : wkimdev
 * @created : 2024/01/04
 * @version 1.0
 * @modifyed
 **/
public class TimeLineFragment2 extends Fragment {

    private String TAG = this.getClass().getSimpleName();
    private DiaryService diaryService = RetrofitClientInstance.getRetrofitInstance().create(DiaryService.class);
    private TimeLineListAdapter2 timeLineListAdapter2;
    private RecyclerView recyclerView;
    private int page = 1; //최초 페이지 호출
    private int limit = 15; //페이지 사이즈 지정
    private Button timeline_btn_imageUpload;


    //api version에 따라 매니페스트 권한을 따로 준다
    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체


    //타임라인 게시글 클릭을 위한 인터페이스
    public interface TimelineInterfacer{
        void onCardClick(int diaryId);
    }

    TimelineInterfacer timelineInterfacer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.diary_tab1_fragment_4, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //타임라인 추가 버튼
        timeline_btn_imageUpload = view.findViewById(R.id.timeline_btn_imageUpload);

        //// >>>>>>>>>> 스크롤뷰 >>>>>>>>>>

        // 숫자 목록을 담을 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.recy);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // 초기 입력 데이터 준비
        //ArrayList<DiaryInfoResult> newList = new ArrayList<>();

        //페이징과 함께 아이템을 업데이하도록 요청하는 메소드
        callItemWithPaging(); //1페이지 15개 호출

        /*
        테스트코드
        for(int i = 0; i < 30; i++){
            newList.add(i);
        }

        // 리사이클러뷰 어댑터 초기화 및 리사이클러뷰에 어댑터 설정
        timeLineListAdapter2 = new TimeLineListAdapter2(newList);
        recyclerView.setAdapter(timeLineListAdapter2);
        */


        // 리사이클러뷰 스크롤리스너 설정
        // (스크롤 상태가 변경 되었을 때 호출)
        //스크롤 상태가 변경될때마다 호출됨
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 현재 리사이클러뷰 아이템 리스트에 로딩 아이템이 있는지 확인, 없다면 내부 코드 실행
                // numberlist의 사이즈값을 의미함.
                if (timeLineListAdapter2.isLastItemLoading()) {
                    //Log.e(TAG, "isLastItemLoading: CALL!! ");

                    // 현재 리사이클러뷰의 최하단이여서 더 이상 스크롤 할 수 없을 경우 내부 코드 실행
                    if(!recyclerView.canScrollVertically(1)) {
                        //Log.e(TAG, "onScrollStateChanged: 스크롤 최하단 이다!!!");

                        // 리사이클러뷰 아이템 리스트에 로딩 아이템 추가
                        // -1를 넣어서 숫자 아이템 로딩이 재시작 됨을 알린다.
                        timeLineListAdapter2.addLoadingData();

                        //더이상 호출할 데이터가 없으면 스크롤 이벤트는 발생하더라도, 서버에 데이터요청은 하지 않도록 처리
                        page++;
                        callItemWithPaging();
                    }
                }
            }
        });


        /**
        * 갤러리 사진 업로드 버튼 클릭
        */
        timeline_btn_imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPhoto();
            }
        });
    }


    //타임라인에 업로드할 사진 선택 및 게시글 작성
    private void uploadPhoto() {
        //앨범 권한 체크 먼저
        if (!hasAllPermissions(getContext(), permissions())) {

            //onRequestPermissionsResult() 함수로 이동한다.
            requestPermissions(permissions(), 500);
            return;
        }
        //앨범 원한을 이미 허용했을경우 호출되는 구문
        else {
            // 앨범 호출
            // 다중이미지를 선택하도록 설정하는 코드
            selectMultiImage();
        }//end else
    }


    // 다중이미지를 선택하도록 설정하는 코드
    private void selectMultiImage() {
        //초기화
        uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2222);
    }


    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                /*adapter = new MultiImageAdapter(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));*/

                Intent intent = new Intent(getActivity(), UploadTimelinePhotoActivity.class);
                intent.putParcelableArrayListExtra("uriList", uriList);
                Log.e(TAG, "onActivityResult: urlList lenth" + uriList.size() );
                startActivity(intent);

            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 5){   // 선택한 이미지가 6장 이상인 경우
                    Toast.makeText(getActivity(), "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }

                    /*adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용*/

                    Intent intent = new Intent(getActivity(), UploadTimelinePhotoActivity.class);
                    intent.putParcelableArrayListExtra("uriList", uriList);
                    Log.e(TAG, "onActivityResult: urlList lenth" + uriList.size() );
                    startActivity(intent);
                }
            }

            //타임라인 이미지와 내용을 등록하는 화면에 이미지 URI를 넘긴다.
            /*Intent intent = new Intent(getActivity(), UploadTimelinePhotoActivity.class);
            intent.putParcelableArrayListExtra("uriList", uriList);
            Log.e(TAG, "onActivityResult: urlList lenth" + uriList.size() );
            startActivity(intent);*/
        }
    }


    // #################### 권한 처리 ####################
    /**
     * 빌드 버전별 퍼미션 구분
     * @NOTE api level 33이후부턴 앨범권한 허용 매니페스가 변경됨
     */
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    //앨범권한체크
    public boolean hasAllPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    // 앨범에서 이미지 선택 후 결과값을 받는 처리
    // 앨범에서 선택한 이미지 결과값을 받아, CROP을 시킨다
    ActivityResultLauncher<Intent> startActivityResultForAlbum = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) { //성공
                        //crop을 하기 위한 이미지를 가져와 crop activity에서 사용한다
                        Intent data = result.getData();
                        //source – Uri for image to crop
                        Uri sourceUri = data.getData(); //1


                        File file = null; // 2
                        try {
                            file = getImageFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //destination – Uri for saving the cropped image
                        Uri destinationUri = Uri.fromFile(file);  // 3

                        // TODO: 1/25/24 UploadPhoto Activity로 이동

                        //openCropActivity(sourceUri, destinationUri);  // 4

                        //갤러리에서 선택한 사진을 전달해야 한다.
                        //사진은 다중선택이 가능해야 한다.
                        //사용자가 선택한 사진 중, 삭제할 수도 있다

                    }
                }
            });


    //creates a new File in the external storage directory.
    //외부 저장소 디렉토리에 새 파일을 생성한다.
    String currentPhotoPath = "";
    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }


    // 권한 관련해 통신할 때 사용되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, ">>>> 권한 관련해 통신할 때 사용되는 메서드 onRequestPermissionsResult: CALL !!!");

        switch (requestCode) {
            case 500: { //앨범 권한 설정

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허용클릭
                    Toast.makeText(getContext(), "앨범 권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityResultForAlbum.launch(intent);

                } else {
                    Toast.makeText(getContext(), "앨범 권한 승인하지 않았습니다. 권한을 허용해주세요",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }



    private void callItemWithPaging() {

        //@fixme - 하드코딩
        int userId = PreferenceManager.getInt(getContext(), "user_id");
        userId = 208;
        //Log.e(TAG, "callItemWithPaging: user_id: " +  userId);
        Log.e(TAG, "callItemWithPaging: page 값 확인: " +  page);

        diaryService.getDiaryByUserId(userId, page, limit).enqueue(new Callback<ResponseWrapper>() {
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ResponseWrapper responseWrapper = response.body();
                    ResponseCommonData responseCommonData = responseWrapper.getResult();
                    String code = responseCommonData.getCode();
                    String message = responseCommonData.getMessage();

                    if ("2000".equals(code)) { //인증성공

                        generateDataList(responseCommonData.getDiaryInfo(),
                                timelineInterfacer);

                    } else {
                        //인증실패 오류팝업을 노출시킨다.
                        AlertDialogHelper.showAlertDialogOnlyConfrim(
                                getContext(), // 현재 Activity의 Context
                                "", // 다이얼로그 제목
                                message); // 다이얼로그 메시지
                    }
                } else { //서버오류
                    //인증실패 오류팝업을 노출시킨다.
                    AlertDialogHelper.showAlertDialogOnlyConfrim(
                            getContext(), // 현재 Activity의 Context
                            "", // 다이얼로그 제목
                            "오류가 발생했습니다. \n잠시후 다시 시도해주세요."); // 다이얼로그 메시지
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper> call, Throwable t) {
                Log.e(TAG, "레트로핏 응답 실패 오류 발생 ", t);
                AlertDialogHelper.showAlertDialogOnlyConfrim(
                        getContext(), // 현재 Activity의 Context
                        "", // 다이얼로그 제목
                        "오류가 발생했습니다. \n잠시후 다시 시도해주세요." // 다이얼로그 메시지
                );
                t.printStackTrace();
            }
        });
    }

    private void generateDataList(ArrayList<DiaryInfoResult> diaryInfo,
                                  TimelineInterfacer timelineInterfacer) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (page == 1) {
                    timeLineListAdapter2 = new TimeLineListAdapter2(diaryInfo, timelineInterfacer, getActivity());
                    recyclerView.setAdapter(timeLineListAdapter2);
                } else {
                    timeLineListAdapter2.addItems(diaryInfo);
                }//end
            }
        });        
    }

}