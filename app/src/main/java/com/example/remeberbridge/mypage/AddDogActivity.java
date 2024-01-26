package com.example.remeberbridge.mypage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.remeberbridge.BuildConfig;
import com.example.remeberbridge.MainActivity;
import com.example.remeberbridge.R;
import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.diary.TimeLineFragment;
import com.example.remeberbridge.diary.TimeLineListAdapter2;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.diary.RequestAddNewDog;
import com.example.remeberbridge.service.DiaryService;
import com.example.remeberbridge.service.MemberService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.PreferenceManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


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
    File photoFile; //사진을 찍었거나, 가져왔거나 저장할 변수
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputLayout adddog_birthday;
    private TextInputEditText adddog_textInput_birthday; //반려견 생일
    private TextInputEditText adddog_textInput_edit_name; //반려견 이름
    
    private ImageView adddog_iv_photo;

    private RadioGroup adddog_radioGroup;

    private boolean isAlbumRequest; //프로필사진을 앨범에서 선택할 경우

    private File image; //이미지 디렉토리를 만들때 사용하는 변수
    private Uri mImageCaptureUri; //프로필이미지 처리를 위한 Uri전역 변수

    private Button adddog_btn;

    private CircleImageView mypage_dog_profile_img;

    private File profileImage; //이미지 디렉토리를 만들때 사용하는 변수

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

    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 100; //숫자 지정을 어떻게 하는걸까??
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 200; //숫자 지정을 어떻게 하는걸까??

    // 레트로핏으로 사용자의 추억공간 데이터를 호출함
    private DiaryService service = RetrofitClientInstance.getRetrofitInstance().create(DiaryService.class);
    private int dogBreedNum; //반려견 품종번호
    private String dogSex = "F"; //반려견 성별(디폴트여아)


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
        mypage_dog_profile_img = findViewById(R.id.mypage_dog_profile_img);
        adddog_textInput_edit_name = findViewById(R.id.test_textInput_edit); //반려견 이름


        // 사진 선택 다이알로그 팝업 발생
        adddog_iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseCameraAlbumDialog();
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
                dogBreedNum = position + 1; // ?
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
                        dogSex = "F";
                    break;
                    case R.id.radio_button_2:
                        //여아 선택
                        Log.e(TAG, "onCheckedChanged: 남아선택!!");
                        dogSex = "M";
                }
            }
        });


        adddog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //@fixme - 모든 필드에 값이 채워졌을 때 버튼 활성화
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


                String userEmail = PreferenceManager.getString(getApplicationContext(), "user_email");
                //@fixme 임시설정
                userEmail = "wkimdev@gmail.com";

                RequestAddNewDog requestAddNewDog =
                 new RequestAddNewDog(
                         userEmail,
                         adddog_textInput_edit_name.getText().toString(),
                         adddog_textInput_birthday.getText().toString(),
                         dogBreedNum,
                         dogSex,
                         profileImage
                 );

                Log.e(TAG, "onClick: requestr값 확인 >>>>> "
                        + userEmail + ", "
                        + adddog_textInput_edit_name.getText().toString() + ", "
                        + adddog_textInput_birthday.getText().toString() + ", "
                        + dogBreedNum + ", "
                        + dogSex + ", "
                        + profileImage+ ", "
                );

                // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
                RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), profileImage);

                // RequestBody로 Multipart.Part 객체 생성
                // 파라미터1) 서버에서 받는 키값(String)
                // 파라미터2) 파일이름(String)
                // 파라미터3) 파일 경로를 가지는 RequestBody 객체
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("dog_prof_img", profileImage.getName(), fileBody);

                //추가적인 요청 body값 생성
                Map<String, RequestBody> map = new HashMap<>();
                RequestBody emailValue = RequestBody.create(MediaType.parse("text/plain"), userEmail);
                RequestBody dogName = RequestBody.create(MediaType.parse("text/plain"), adddog_textInput_edit_name.getText().toString());
                RequestBody birthdayValue = RequestBody.create(MediaType.parse("text/plain"), adddog_textInput_birthday.getText().toString());
                RequestBody breedNumValue = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(dogBreedNum));
                RequestBody genderValue = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(dogSex));

                map.put("user_email", emailValue);
                map.put("dog_name", dogName);
                map.put("dog_birth", birthdayValue);
                map.put("dog_breed", breedNumValue);
                map.put("dog_sex", genderValue);

                //레트로핏 요청
                addDog(filePart, map);
            }
        });
    }


    private void addDog(MultipartBody.Part filePart, Map<String, RequestBody> map) {
        Log.e(TAG, "addDog: retfrofit call!!! ");
        service.addNewDog(filePart, map).enqueue(new Callback<ResponseWrapper>() {
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {
                Log.e(TAG, "addDog: retfrofit onResponse!!! ");
                //서버 응답성공 (200)
                if (response.isSuccessful() && response.body() != null) {

                    Log.e(TAG, "addDog: retfrofit onResponse 서버 응답성공 (200)!!! ");

                    ResponseWrapper responseWrapper = response.body();
                    ResponseCommonData responseCommonData = responseWrapper.getResult();
                    String code = responseCommonData.getCode();
                    String message = responseCommonData.getMessage();

                    //반려견 등록 성공
                    if ("2000".equals(code)) {

                        int spaceId = responseCommonData.getSpaceid();
                        int newDogId = responseCommonData.getDogId();

                        Log.e(TAG, "onResponse: 응답성공!!!" + spaceId + ". " + newDogId);

                        // TODO: 1/22/24 - 타임라인 화면으로 이동
                        //replaceFragment(myViewFragment);
                        Intent intent = new Intent(AddDogActivity.this, MainActivity.class);
                        intent.putExtra("fromOtherActivity", 1);
                        startActivity(intent);

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
                AlertDialogHelper.showAlertDialogOnlyConfrim(
                        AddDogActivity.this, // 현재 Activity의 Context
                        "", // 다이얼로그 제목
                        "오류가 발생했습니다. \n잠시후 다시 시도해주세요." // 다이얼로그 메시지
                );
                t.printStackTrace();
            }
        });
    }


    // 액티비티에서 프레그먼트로 이동시키기 위한 코드
    /*public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_notimeline, fragment);
        fragmentTransaction.commit();
    }*/


    // #################### 반려견등록 레트로핏요청 [START] ####################
    /*private void addDog(RequestAddNewDog requestAddNewDog) {
        service.addNewDog(requestAddNewDog).enqueue(new Callback<ResponseWrapper>() {
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {
                //서버 응답성공 (200)
                if (response.isSuccessful() && response.body() != null) {

                    ResponseWrapper responseWrapper = response.body();
                    ResponseCommonData responseCommonData = responseWrapper.getResult();
                    String code = responseCommonData.getCode();
                    String message = responseCommonData.getMessage();

                    //반려견 등록 성공
                    if ("2000".equals(code)) {

                        int spaceId = responseCommonData.getSpaceid();
                        int newDogId = responseCommonData.getDogId();

                        Log.e(TAG, "onResponse: 응답성공!!!" +
                                spaceId + ". " + newDogId);

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
                AlertDialogHelper.showAlertDialogOnlyConfrim(
                        getApplicationContext(), // 현재 Activity의 Context
                        "", // 다이얼로그 제목
                        "오류가 발생했습니다. \n잠시후 다시 시도해주세요." // 다이얼로그 메시지
                );
                t.printStackTrace();
            }
        });
    }*/
    // #################### 반려견등록 레트로핏요청 [END] ####################




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

    /**
     * 프로필의 사진을 변경하는 함수
     * 1. 사용자에게 카메라/앨범 권한 체크 먼저 할것
     * 2. 카메라로 사진찍고 or 앨범에서 선택하고 크롭할 것
     * 3. 크롭한 사진을 프로필이미지 뷰에 셋팅할 것
     */
    private void showChooseCameraAlbumDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddDogActivity.this);
        builder.setTitle("선택하세요.");

        // 다이아로그에 여러개 항목을 보여주게 가능한 함수
        builder.setItems(R.array.alert_permission_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { // 각각을 클릭했을때 이벤트를 작성
                // i : 누른게 무엇인지 알려줌
                if(i == 0){ // 첫번째 항목을 눌렀다면,
                    openCamera();
                }else if(i == 1){
                    openAlbum();
                }
            }
        });
        builder.show();
    }

    // 카메라 권한 체크 및 사진 활영 후 크롭 기능
    private void openAlbum() {
        if (!hasAllPermissions(getApplicationContext(), permissions())) {

            //onRequestPermissionsResult() 함수로 이동한다.
            ActivityCompat.requestPermissions(AddDogActivity.this,
                    permissions(), 500);
            return;
        }
        //앨범 원한을 이미 허용했을경우 호출되는 구문
        else {
            // 앨범 호출
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
            startActivityResultForAlbum.launch(intent);
        }//end else
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


    // 카메라 권한 체크 및 사진 활영 후 크롭 기능
    private void openCamera(){
        //카메라 권한체크 여부 확인
        int permissionCheck = ContextCompat.checkSelfPermission(
                getApplicationContext(), android.Manifest.permission.CAMERA);

        // 권한 허용이 안되어있으면, 권한체크를 다시 물어본다.
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddDogActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000); //1000을 어디에 쓰나~

            //Toast.makeText(MainActivity3.this, "카메라 권한 필요합니다.", Toast.LENGTH_SHORT).show();
            return;

        } else {// 허용이 되어 있다면,

            doCameraCapture();
        }
    }

    private void doCameraCapture() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = null; // 1
        try {
            file = getImageFile(); //currentPhotoPath 값을 가져오는 함수
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(AddDogActivity.this, BuildConfig.APPLICATION_ID.concat(".fileprovider"), file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }


    // 권한 관련해 통신할 때 사용되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, ">>>> 권한 관련해 통신할 때 사용되는 메서드 onRequestPermissionsResult: CALL !!!");

        switch (requestCode) {
            case 1000: { //카메라 권한이  설정안되어 있을때 1000번으로 보냈었음
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "카메라 권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();

                    //곧바로 카메라 촬영기능으로 화면 이동
                    doCameraCapture();

                } else {
                    Toast.makeText(getApplicationContext(), "아직 카메라 권한 승인하지 않았습니다. 카메라 권한을 허용해주세요",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: { //앨범 권한 설정

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허용클릭
                    Toast.makeText(getApplicationContext(), "앨범 권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityResultForAlbum.launch(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "앨범 권한 승인하지 않았습니다. 권한을 허용해주세요",
                            Toast.LENGTH_SHORT).show();
                }

                //@fixme- 의도한 대로 동작되지 않아서 주석처리함
                /*else if (checkPermissionDenyTwice()) {
                    //사용자가 권한을 2번 거절하면, 커스텀 다이알로그를 보여줌
                    //goSettingActivityAlertDialog();
                }*/
                break;
            }
        }
    }


    private boolean checkPermissionDenyTwice() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return shouldShowRequestPermissionRationale(p[0]) ||
                shouldShowRequestPermissionRationale(p[1]);
    }


    private void goSettingActivityAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 승인이 필요합니다.")
                .setMessage("앨범에 접근 하기 위한 권한이 필요합니다.\n권한 -> 저장공간 -> 허용")
                .setPositiveButton("허용하러 가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent goSettingPermission = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        goSettingPermission.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(goSettingPermission);
                        return;
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }


    private void showPermissionAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 승인이 필요합니다.")
                .setMessage("사진을 선택 하시려면 권한이 필요합니다.")
                .setPositiveButton("허용하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AddDogActivity.this,
                                permissions(), 500);
                        return;
                    }
                })
                .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }



    //카메라 권한 허용 후 촬영한 사진파일을 jpg 확장자로 저장하는 함수
    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    // 카메라/앨범 사진촬영 권한 허용 이후 resultCode를 받는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) { //카메라에서 선택한 이미지를 크롭하는 구문
            //Log.e(TAG, "onActivityResult: CROP 시작 전!!");
            Uri uri = Uri.parse(currentPhotoPath);
            //크롭실행
            openCropActivity(uri, uri);

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) { //크롭이후 크롭된 이미지를 뷰에 바인딩하는 구분
            //Log.e(TAG, "onActivityResult: CROP을 한 다음에 발생되는 구문 !!!");

            Uri uri = UCrop.getOutput(data);
            showImage(uri);

        } //앨범에서 이미지를 선택한 뒤 동작하는 메소드
        else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Log.e(TAG, "2) openImagesDocument: CALL !!!");

            Uri sourceUri = data.getData(); // 1

            File file = null; // 2
            try {
                file = getImageFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            Uri destinationUri = Uri.fromFile(file);  // 3
            openCropActivity(sourceUri, destinationUri);  // 4
        }
    }


    /**
     * 선택한 이미지를 크롭핑 후 프로필 뷰에 바인딩하는 코드
     */
    private void showImage(Uri imageUri) {
        Log.e(TAG, "3) showImage:  CALL!!!");
        File file = new File(FileUtils.getPath(getApplicationContext(), imageUri));

        //서버의 body값에 전달하기 위한 프로필사진 file값
        profileImage = file;


        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        mypage_dog_profile_img.setImageBitmap(bitmap);
    }


    // handle uCrop result.
    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        Log.e(TAG, "openCropActivity: sourceUri >>> " + sourceUri );
        Log.e(TAG, "openCropActivity: destinationUri >>> " + destinationUri );

        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(200, 200)
                .withAspectRatio(5f, 5f)
                .start(AddDogActivity.this);
    }


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


    //카메라에서 이미지 가져오기
    private void doTakePhotoAction() throws IOException {
        Log.e(TAG, "doTakePhotoAction 함수 호출 성공, 이미지 크롭 라이브러리 사용 결과 성공: CALL!!!");

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
        //registerForActivityResult(intent);


        /*CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(mImageCaptureUri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);*/

        //크롭
        /*CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(mImageCaptureUri, new CropImageOptions(true, true));
        cropImage.launch(cropImageContractOptions);*/

    }


    //이미지 크롭 라이브러리 사용
    /*ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), result -> {

        Log.e(TAG, "이미지 크롭 라이브러리 사용: CALL!!!");

        if (result.isSuccessful()) {
            Log.e(TAG, "이미지 크롭 라이브러리 사용 결과 성공: CALL!!!");
            // Handle the result
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));

        } else {
            // An error occurred.
            Exception exception = result.getError();
        }
    });*/


    /*private void startCrop() {
        // Start picker to get image for cropping and then use the image in cropping activity.
        cropImage.launch(
                new Options.Builder()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .build()
        );
        //이미지뷰에 크롭된 이미지 Uri를 넣는다
        circleImg_profile.setImageURI(result.getUri());
    }*/




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
                        //crop을 하기 위한 이미지를 가져와 crop activity에서 사용한다
                        Intent data = result.getData();
                        Uri sourceUri = data.getData(); //1

                        File file = null; // 2
                        try {
                            file = getImageFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Uri destinationUri = Uri.fromFile(file);  // 3
                        openCropActivity(sourceUri, destinationUri);  // 4
                    }
                }
            });


}
