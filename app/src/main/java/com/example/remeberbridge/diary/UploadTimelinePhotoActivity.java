package com.example.remeberbridge.diary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.text.TextLayoutInput;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.remeberbridge.MainActivity;
import com.example.remeberbridge.R;
import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.diary.RequestAddNewDog;
import com.example.remeberbridge.model.diary.RequestAddNewTimeline;
import com.example.remeberbridge.mypage.AddDogActivity;
import com.example.remeberbridge.service.DiaryService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.PreferenceManager;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * UploadTimelinePhotoActivity.java
 * @desc : 타임라인에 사진을 업로드하는 액티비티
 *
 * @author : wkimdev
 * @created : 1/25/24
 * @version 1.0
 * @modifyed
 **/
public class UploadTimelinePhotoActivity extends AppCompatActivity implements ImageSelectionListener {

    private Button timeline_btn_imageUpload;
    private TextInputLayout upload_txtinput_title;
    private TextInputEditText upload_txtinput_edit;

    RecyclerView rc_upload_images;  // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터
    private String TAG = this.getClass().getSimpleName();

    private ImageView upload_iv_imageTitle;

    private File images; //이미지 디렉토리를 만들때 사용하는 변수

    private ArrayList<Uri> receivedUriList;

    private List<MultipartBody.Part> parts; //다중업로드 이미지 처리를 위한 multipart 선언

    // 레트로핏으로 사용자의 추억공간 데이터를 호출함
    private DiaryService service = RetrofitClientInstance.getRetrofitInstance().create(DiaryService.class);

    private Sprite fadingCircle; //노출되는 로딩바 애니메이션 선언
    private ProgressBar progressBar; //로그아웃시 로딩바호출


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_timeline_photo);

        timeline_btn_imageUpload = findViewById(R.id.timeline_btn_imageUpload);
        upload_txtinput_title = findViewById(R.id.upload_txtinput_title);
        upload_txtinput_edit = findViewById(R.id.upload_txtinput_edit);
        rc_upload_images = findViewById(R.id.rc_upload_images);
        //대표이미지
        upload_iv_imageTitle = findViewById(R.id.upload_iv_imageTitle);

        progressBar = findViewById(R.id.spin_kit);; //로그아웃시 로딩바 노출
        fadingCircle = new FadingCircle(); //노출되는 로딩바 애니메이션 스타일

        //사용법
        //로딩애니메이션 노출
        /*progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminateDrawable(fadingCircle); //진행바 모양을 그리는데 사용되는 드로어블 정의*/

        receivedUriList = getIntent().getParcelableArrayListExtra("uriList");

        Log.e(TAG, "onCreate 전달된 이미지데이터 갯수:"+ receivedUriList.size());

        //값이 없다면 오류 리턴
        //이미지가 1개의 경우 1개만 노출, 이미지가 여러개인 경우 여러개를 리사이클러뷰에 노출

        if (receivedUriList.size() <= 0 ) {
            AlertDialogHelper.showAlertDialogOnlyConfrim(UploadTimelinePhotoActivity.this,
                    "오류가 발생했습니다", "다시 시도해 주세요!");
        } else {
            //어뎁터 초기화
            adapter = new MultiImageAdapter(receivedUriList, getApplicationContext(), this);
            rc_upload_images.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
            rc_upload_images.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));     // 리사이클러뷰 수평 스크롤 적용
            showTitleImage(receivedUriList.get(0));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        upload_txtinput_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트가 변경되기 전에 호출
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트가 변경되는 동안 호출
                if (s.length() > 121) {
                    upload_txtinput_title.setError("No More!!!");
                    timeline_btn_imageUpload.setEnabled(false);
                } else if (1 < s.length() && s.length() <= 120) {
                    upload_txtinput_title.setError(null);
                    timeline_btn_imageUpload.setEnabled(true);
                } else {
                    timeline_btn_imageUpload.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트가 변경된 후 호출
                //timeline_btn_imageUpload.setEnabled(true);
            }
        });


        /**
         * 타임라인 내용을 서버에 업로드 하는 코드
         */
        timeline_btn_imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //로딩애니메이션 노출
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminateDrawable(fadingCircle); //진행바 모양을 그리는데 사용되는 드로어블 정의
                timeline_btn_imageUpload.setEnabled(false);


                int userId = PreferenceManager.getInt(getApplicationContext(), "user_id");

                //@fixme 임시설정
                userId = 208; //김위
                int spaceId = 91; //db값 보고 하드코딩함.
                String selectedDate = "2024-01-01";
                String content = upload_txtinput_edit.getText().toString();


                //space_id는 반려견 한마리당 한개로 할당되는 걸로 추측함.
                RequestAddNewTimeline requestAddNewTimeline =
                        new RequestAddNewTimeline(
                                spaceId,
                                userId,
                                selectedDate,
                                0, //미사용값 0으로 고정
                                content
                        );

                // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
                parts = new ArrayList<>();
                for (Uri imageUri : receivedUriList) {
                    // new File(uri.getPath()).
                    //File file = new File(imageUri.getPath());
                    File file = new File(FileUtils.getPath(UploadTimelinePhotoActivity.this, imageUri)); // Convert Uri to File
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("diary_imgs", file.getName(), requestBody);
                    parts.add(part);
                }

                // Convert list to array
                //todo - 왜 0일까??
                MultipartBody.Part[] imagePartsArray = parts.toArray(new MultipartBody.Part[0]);

                //추가적인 요청 body값 생성
                Map<String, RequestBody> map = new HashMap<>();
                RequestBody spaceIdValue = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(spaceId));
                RequestBody userIdValue = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
                RequestBody selectedDateValue = RequestBody.create(MediaType.parse("text/plain"), selectedDate);
                RequestBody emotionValue = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
                RequestBody contentValue = RequestBody.create(MediaType.parse("text/plain"), content);

                map.put("space_id", spaceIdValue);
                map.put("user_id", userIdValue);
                map.put("select_date", selectedDateValue);
                map.put("emotion", emotionValue);
                map.put("diary_content", contentValue);

                //레트로핏 요청
                uploadTimeLineItem(map, imagePartsArray);
            }
        });

    }


    /**
     * 레트로핏을 통해 서버에 타임라인 데이터를 업로드 한다.
     * - 사진, 내용
     */
    private void uploadTimeLineItem(Map<String, RequestBody> map, MultipartBody.Part... parts) {

        service.addNewDiary(map, parts).enqueue(new Callback<ResponseWrapper>() {

            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {

                Log.e(TAG, "addNewTimeline: retfrofit onResponse!!! ");
                //서버 응답성공 (200)
                if (response.isSuccessful() && response.body() != null) {

                    Log.e(TAG, "addNewTimeline: retfrofit onResponse 서버 응답성공 (200)!!! ");

                    ResponseWrapper responseWrapper = response.body();
                    ResponseCommonData responseCommonData = responseWrapper.getResult();
                    String code = responseCommonData.getCode();
                    String message = responseCommonData.getMessage();

                    //리사이클러뷰 초기화
                    adapter.clear();

                    //타임라인 등록 성공
                    if ("2000".equals(code)) {

                        int diaryId = responseCommonData.getDiaryId();

                        Log.e(TAG, "onResponse: 응답성공!!!" + diaryId);
                        Log.e(TAG, "onResponse: 응답성공!!!" + message);

                        // TODO: 1/22/24 - 타임라인 화면으로 이동
                        Intent intent = new Intent(UploadTimelinePhotoActivity.this, MainActivity.class);
                        intent.putExtra("fromOtherActivity", 1);
                        startActivity(intent);
                        finish();

                    } else {
                        // 오류 응답 처리
                        ResponseWrapper errorResponseWrapper = null;
                        if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson();
                                // ResponseWrapper 클래스로 파싱
                                errorResponseWrapper = gson.fromJson(response.errorBody().charStream(), ResponseWrapper.class);

                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: 오류 메시지를 읽는데 실패했습니다.", e);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper> call, Throwable t) {
                Log.e(TAG, "레트로핏 네트워크 오류 또는 요청 실패");
                //이전화면으로 이동시키기
                showErrorDialog(UploadTimelinePhotoActivity.this, // 현재 Activity의 Context
                        "", // 다이얼로그 제목
                        "오류가 발생했습니다. \n잠시후 다시 시도해주세요.");
                t.printStackTrace();
            }
        });

    }


    /**
     * 선택한 첫번째 이미지를 이미지뷰에 바인딩하는 코드
     */
    private void showTitleImage(Uri imageUri) {
        File file = new File(FileUtils.getPath(getApplicationContext(), imageUri));

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        upload_iv_imageTitle.setImageBitmap(bitmap);
    }


    /*이전 타임라인 페이지로 이동*/


    /*이미지 업로드 오류 발생시 팝업창*/
    public void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // "확인" 버튼을 클릭했을 때의 동작
                dialog.dismiss();

                //리사이클러뷰 초기화
                adapter.clear();
                Toast.makeText(context, "타임라인조회 화면으로 이동합니다!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UploadTimelinePhotoActivity.this, MainActivity.class);
                intent.putExtra("fromOtherActivity", 1);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    } //end code


    @Override
    protected void onPause() {
        super.onPause();

        adapter.clear();
        receivedUriList.clear();
        //Log.e(TAG, "onPause: CALL!!! 이미지 데이터갯수 확인 : " + receivedUriList.size());
    }


    /**
     * 다중이미지를 갤러리에서 생성
     * - 리사이클러뷰에서 사용자가 선택한 이미지를 모두 삭제했을 경우 실행되는 메소드
     */
    @Override
    public void onImageSelectionRequested() {
        selectMultiImage();
    }

    @Override
    public void onTimelineItemClick(int diaryId) {

    }

    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

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

        Log.e(TAG, "onActivityResult: 갤러리화면 진입하고 완료된 이후에 호출되는 구문!");

        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                Intent intent = new Intent(getApplicationContext(), UploadTimelinePhotoActivity.class);
                intent.putParcelableArrayListExtra("uriList", uriList);
                Log.e(TAG, "onActivityResult: urlList lenth" + uriList.size() );
                startActivity(intent);
                finish();

            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 5){   // 선택한 이미지가 6장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
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

                    Intent intent = new Intent(getApplicationContext(), UploadTimelinePhotoActivity.class);
                    intent.putParcelableArrayListExtra("uriList", uriList);
                    Log.e(TAG, "onActivityResult: urlList lenth" + uriList.size() );
                    startActivity(intent);
                    finish();
                }
            }

            //타임라인 이미지와 내용을 등록하는 화면에 이미지 URI를 넘긴다.
            /*Intent intent = new Intent(getActivity(), UploadTimelinePhotoActivity.class);
            intent.putParcelableArrayListExtra("uriList", uriList);
            Log.e(TAG, "onActivityResult: urlList lenth" + uriList.size() );
            startActivity(intent);*/
        }
    }

}