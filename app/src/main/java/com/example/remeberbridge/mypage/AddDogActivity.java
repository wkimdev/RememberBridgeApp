package com.example.remeberbridge.mypage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;

import com.example.remeberbridge.R;
import com.example.remeberbridge.diary.TimeLineListAdapter2;
import com.example.remeberbridge.utils.PreferenceManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * AddDogActivity.java
 * @desc : 반려견 등록 페이지
 *
 * @author : wkimdev
 * @created : 1/15/24
 * @version 1.0
 * @modifyed
 **/
public class AddDogActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private AutoCompleteTextView autoCompleteTextView;
    private TextInputLayout adddog_birthday;
    private TextInputEditText adddog_textInput_birthday;
    
    private ImageView adddog_iv_photo;

    private RadioGroup adddog_radioGroup;

    private boolean isAlbumRequest; //프로필사진을 앨범에서 선택할 경우

    private File image; //이미지 디렉토리를 만들때 사용하는 변수
    private Uri mImageCaptureUri; //프로필이미지 처리를 위한 Uri전역 변수

    private Button adddog_btn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog_2);

        Log.e(TAG, "onCreate: CALL!!!");

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView_2);
        adddog_birthday = findViewById(R.id.adddog_birthday);
        adddog_textInput_birthday = findViewById(R.id.adddog_textInput_birthday_2);
        adddog_radioGroup = findViewById(R.id.adddog_radioGroup);
        adddog_iv_photo = findViewById(R.id.adddog_iv_photo); //프로필사진 이미지 변경 버튼
        adddog_btn = findViewById(R.id.adddog_btn); //반려견 등록 버튼


        // 사진 선택 다이알로그 팝업 발생
        adddog_iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePickOption();
            }
        });


        //품종선택로직
        Resources resources = getResources();
        String[] dogArray = resources.getStringArray(R.array.dog_variety);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddDogActivity.this, R.layout.dropdown_item, dogArray);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e(TAG, "onItemClick: !!!"+ position);
                //선택한 아이템의 인덱스를 가져온다.
                String dogValue = dogArray[position];
                Log.e(TAG, "onItemSelected: !!!"+ dogValue);
            }
        });


        //동물 생일 선택 datepicker로직
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        // In your activity
        adddog_birthday.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePicker.show(getSupportFragmentManager(), "TAG~");

            }
        });

        //datepicker 이벤트 발생시키기
        // Set up the positive button click listener
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Use the Calendar instance to convert the selected date (in milliseconds) to year, month, and day
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);

                int year = calendar.get(Calendar.YEAR);
                // Note that Calendar.MONTH is zero-based
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Format the date to a string or set it to a text field
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
                // Example: set the date to a TextView or EditText
                adddog_textInput_birthday.setText(selectedDate);
            }
        });

        // Set the negative button click listener if needed
        datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the negative button click
            }
        });


        //라디오그룹 체크
        adddog_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Log.e(TAG, "onCheckedChanged: checkedId!!" + checkedId );
                RadioButton radioButton = radioGroup.findViewById(checkedId);
                switch (checkedId) {
                    case R.id.radio_button_1:
                        //여아 선택
                        Log.e(TAG, "onCheckedChanged: 여아선택!!");
                    break;
                    case R.id.radio_button_2:
                        //여아 선택
                        Log.e(TAG, "onCheckedChanged: 남아선택!!");
                }
            }
        });




        adddog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //모든 필드에 값이 채워졌을 때 버튼 활성화
                //버튼 클릭시 api 요청
                //요청 응답 성공시,
                //1. StoryFragment 화면으로 이동
                //2. 메뉴도 타임라인으로 활성화 되어야 함.
                //2. mypage에 등록된 반려견 노출됨.

                /*button_main.setOnClickListener{
                    supportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout, fragment_main)
                            .commit()*/
                }

            }
        });

    }

    // 프로필사진변경
    private void openImagePickOption() {
        final CharSequence[] items = { "사진 찍기", "앨범에서 사진선택", "취소" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDogActivity.this);
        builder.setTitle("사진 선택");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("사진 찍기")) {
                    //카메라에서 이미지 가져오기
                    if (PreferenceManager.getBoolean(getApplicationContext(), "isCameraAlbumPermission")) {
                        try {
                            doTakePhotoAction();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //사용자의 권한허용 여부를 처리하는 메소드
                        tedPermission();
                    }

                } else if (items[item].equals("앨범에서 사진선택")) {

                    //카메라만 허용했는데, 같이 허용되고 있어 임시로 아래 처럼 처리
                    if (PreferenceManager.getBoolean(getApplicationContext(), "isCameraAlbumPermission")) {
                        try {
                            doTakeAlbumAction();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tedPermission();
                    }

                } else if (items[item].equals("취소")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //카메라에서 이미지 가져오기
    private void doTakePhotoAction() throws IOException {
        //MediaStore - Media Provider가 제공하는 파일들을 접근할 수 있도록 도와주는 API의 묶음
        //Media Provider - 단말에 저장된 이미지,동영상,오디오 파일의 정보를 제공하는 프로바이더
        //카메라 파일 공유방식으로 이미지를 로드하기 위한 인텐트 선언
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 카메라로 찍은 사진을 불러오기 위해선, 임시 파일 경로가 필요하기 때문에,
        // 임시로 사용할 파일의 경로를 생성한다

        //이미지가 저장될 폴더 이름 지정
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
        //디렉토리 생성
        if (!storageDir.exists()) storageDir.mkdirs();

        //지정된 디렉토리(storageDir)에 빈 파일 생성
        //prefix - 파일명. 그 뒤에 임의의 숫자가 더 붙는다. ex) prerix : test -> test1234578
        image = File.createTempFile("JPEG_", ".jpg", storageDir);

        //image로 부터 Uri를 만든다
        //file://이 아닌, Cotent:// 로 파일 Uri경로를 바꾸기 위해 FileProvider를 사용한다
        mImageCaptureUri = FileProvider.getUriForFile(this,
                "com.example.rememberbridge.fileprovider", image);

        //카메라에서 찍은 사진이 저장될 주소를 전달해준다
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        //카메라 촬영 후 결과값을 받는 코드
        //이 결과값으로 사진을 크롭한다.
        //startActivityResultForCamera.launch(intent);
        //cropImage.launch(mImageCaptureUri);

        //크롭
        /*CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(mImageCaptureUri, new CropImageOptions(true, true));
        cropImage.launch(cropImageContractOptions);*/

    }


    /*private ActivityResultLauncher<Uri> cropImage = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImage.ActivityResult>() {
        @Override
        public void onActivityResult(CropImage.ActivityResult result) {
            if (result.isSuccessful()) {
                // Use the returned uri.
                Uri uriContent = result.getUriContent();
                String uriFilePath = result.getUriFilePath(getApplicationContext()); // optional usage
            } else {
                // An error occurred.
                Exception exception = result.getError();
            }
        }
    });*/


    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction() throws IOException {
        isAlbumRequest = true;

        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityResultForAlbum.launch(intent);
    }


    // 카메라 촬영 후 결과값을 받는 처리
    // 촬영된 이미지 결과값으로 CROP을 시킨다
    ActivityResultLauncher<Intent> startActivityResultForCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) { //성공



                }
            }
        });


    // 앨범에서 이미지 선택 후 결과값을 받는 처리
    // 앨범에서 선택한 이미지 결과값을 받아, CROP을 시킨다
    ActivityResultLauncher<Intent> startActivityResultForAlbum = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) { //성공

                        Intent data = result.getData();
                        mImageCaptureUri = data.getData();

                        //crop을 하기 위한 이미지를 가져와 crop activity에서 사용한다
                        /*CropImage.activity(mImageCaptureUri).setGuidelines(CropImageView.Guidelines.ON)
                                .start(SettingActivity.this);*/

                    }
                }
            });





    //사용자의 권한허용 여부를 처리하는 메소드
    private void tedPermission() {

        //카메라, 앨범접근 권한 허용 리스너 등록
        PermissionListener cameraPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                PreferenceManager.setBoolean(getApplicationContext(), "isCameraAlbumPermission", true);
            }

            @Override
            public void onPermissionDenied(List<String> list) {
                // 권한 요청 실패
                PreferenceManager.setBoolean(getApplicationContext(), "isCameraAlbumPermission", false);
            }
        };

        //카메라 접근권한 체크
        TedPermission.create()
                .setPermissionListener(cameraPermissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_1)) //권한필요 문구 노출
                .setDeniedMessage(getResources().getString(R.string.permission_2)) //거부했을 경우 안내 문구 노출
                .setDeniedTitle("접근권한허용 거부")
                .setDeniedCloseButtonText("취소")
                .setRationaleConfirmText("확인")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

}
