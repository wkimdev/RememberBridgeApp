package com.example.remeberbridge;

import static com.example.remeberbridge.utils.CommonUtils.getCurrentTime;
import static com.example.remeberbridge.utils.CommonUtils.logError;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.auth.LoginResponse;
import com.example.remeberbridge.model.auth.RequestEmailLogin;
import com.example.remeberbridge.model.auth.UserLoginInfoResult;
import com.example.remeberbridge.service.MemberService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * EmailLoginActivity.java
 * @desc : 이메일로 로그인하는 화면
 *
 * @author : wkimdev
 * @created : 2023/12/11
 * @version 1.0
 * @modifyed
 **/
public class EmailLoginActivity extends AppCompatActivity {


    private TextInputLayout login_textInput_email;
    private TextInputLayout login_textInput_pwd;

    private TextInputEditText login_textInputEdit_email; //이메일 작성 인풋폼
    private TextInputEditText login_textInputEdit_pwd; //비밀번호 작성 인풋폼

    private Button loginBtn;

    private CheckBox emailLogin_checkBox_rememberUser;

    private boolean isValidEmail; //이메일유효성체크
    private boolean isValidPwd; //비밀번호유효성체크

    //로그인 액션을 위한 레트로핏 서비스 API 객체 생성
    private MemberService service = RetrofitClientInstance.getRetrofitInstance().create(MemberService.class);
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        //@fixme - 고민해본것
        //이메일 유효성은 어떻게 체크하지?
        //비밀번호 현재 규칙이 어떻게 되어있지?

        login_textInput_email = findViewById(R.id.login_textInput_email);
        login_textInput_pwd = findViewById(R.id.login_textInput_pwd);
        login_textInputEdit_email = findViewById(R.id.login_textInputEdit_email);
        login_textInputEdit_pwd = findViewById(R.id.login_textInputEdit_pwd);


        loginBtn = findViewById(R.id.emailLogin_btn);
        emailLogin_checkBox_rememberUser = findViewById(R.id.emailLogin_checkBox_rememberUser);

    }

    @Override
    protected void onResume() {
        super.onResume();


        //만약 계정정보 저장을 체크해놨다면 이메일 값을 미리 불러와 셋팅한다.
        if (PreferenceManager.getBoolean(getApplicationContext(), "isRemeberEmail")) {
            String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");
            login_textInputEdit_email.setText(userEmail);
        } else {
            login_textInputEdit_email.setText("");
        }


        //@todo
        // 1. 단순히 이메일 형식이 맞는지
        // 2. 존재하는 이메일 도메인지까지는 검증이 되야 초록색 체크박스를 표시할 수 있따.
        //참고: 이 코드는 클라이언트 사이드에서 이메일 형식만을 검증합니다. 실제로 도메인이 존재하고 활성화된 이메일 주소인지 확인하려면, 이메일 검증 서비스를 사용하거나 백엔드에서 해당 기능을 구현해야 합니다.

        // 실제로 이메일이 존재하는지 도메인 유효성 자체를 검사하려면 서버단에서 하는게 맞는 것 같다.
        // 클라에선 패턴만 인증해서 보내는게 맞을 것 같다.
        // 현재 백엔드에선 정확히 이메일의 뭘 검증하는걸까???
        login_textInputEdit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Before text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When text is changing
                // 텍스트가 변경되는 동안 호출
                //자바에서 제공하는 Patterns 클래스를 사용해 이메일 형식이 유효한지 검사한다.
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    // Set error message if email is not valid
                    // NOTE - 레이아웃에 에러설정을 해야 함.
                    login_textInput_email.setError("이메일 형식에 맞춰 주세요");
                    login_textInput_email.setEndIconVisible(false);
                    isValidEmail = false;
                } else {
                    // Clear error message
                    login_textInput_email.setError(null);
                    isValidEmail = true;
                    //login_textInput_email.setEndIconVisible(true); // Show check icon
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                String email = s.toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // Set error message if email is not valid
                    login_textInput_email.setError("이메일 형식에 맞춰 주세요");
                    login_textInput_email.setEndIconVisible(false);
                    isValidEmail = false;
                } else {
                    // Clear error message and show check icon if email is valid
                    login_textInput_email.setError(null);
                    login_textInput_email.setEndIconVisible(true); // Show check icon
                    login_textInput_email.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.teal_700)));
                    isValidEmail = true;
                }
                checkAllInput();
            }
        });


       //패스워드 체크
        login_textInputEdit_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //@fixme - 패스워드 체크 기준을 어떻게 잡아야 할지 몰라서 아래처럼 유효성 체크해놓음
                isValidPwd = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //@fixme - 패스워드 체크 기준을 어떻게 잡아야 할지 몰라서 아래처럼 유효성 체크해놓음
                isValidPwd = true;
                checkAllInput();
            }
        });



       //로그인 버튼
       loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //todo) 유효성 체크는 기획에 맞게 수정되어야 함.
                /** @desc 이메일과 비밀번호 유효성체크가 정상일때 로그인을 수행한다.*/
                if (isValidEmail && login_textInputEdit_email.getText().length() > 1) {

                    RequestEmailLogin requestEmailLogin = new RequestEmailLogin(
                            login_textInputEdit_email.getText().toString().trim(),
                            login_textInputEdit_pwd.getText().toString().trim()
                    );
                    //이메일 로그인 레트로핏 요청
                    login(requestEmailLogin);

                } else {
                    // 실패한 경우 사용자에게 오류 메시지를 보여주는 대화상자를 띄웁니다.
                    AlertDialogHelper.showAlertDialogOnlyConfrim(
                            EmailLoginActivity.this, // 현재 Activity의 Context
                            "", // 다이얼로그 제목
                            "이메일 또는 비밀번호값을 입력해주세요!"); // 다이얼로그 메시지
                }
            }
        });
    }

    //모든 인풋값들의 유효성 확인
    public void checkAllInput() {
        if (isValidEmail && isValidPwd) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
    };

    //이메일 로그인 레트로핏 요청
    private void login(RequestEmailLogin requestEmailLogin) {
        //enqueue에 파라미터로 넘긴 콜백에서는 통신이 성공/실패 했을때 수행할 동작을 재정의 한다
        service.login(requestEmailLogin).enqueue(new Callback<ResponseWrapper>() {

            //성공했을 경우
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {

                //서버 응답성공 (200)
                if (response.isSuccessful() && response.body() != null) {
                    ResponseCommonData commonData = response.body().getResult();
                    UserLoginInfoResult userLoginInfoResult = commonData.getUserInfo();

                    //로그인 인증 성공
                    if ("2000".equals(commonData.getCode())) {

                        //shared에 사용자 로그인정보를 저장
                        PreferenceManager.setString(getApplicationContext(), "user_email", userLoginInfoResult.getUserEmail());
                        PreferenceManager.setString(getApplicationContext(), "user_name", userLoginInfoResult.getUserName());
                        PreferenceManager.setString(getApplicationContext(), "user_prof_img", userLoginInfoResult.getUserProfImg());
                        PreferenceManager.setString(getApplicationContext(), "user_sns_type", userLoginInfoResult.getLoginSnsType());

                        if (emailLogin_checkBox_rememberUser.isChecked()) {
                            logError(TAG, "계정정보 저장 체크해둠 >>>>");
                            PreferenceManager.setBoolean(getApplicationContext(), "isRememberEmail", true);

                        } else {
                            logError(TAG, "계정정보 저장 체크해지함 쉐어드의 이메일정도 삭제 >>>>");
                            PreferenceManager.setBoolean(getApplicationContext(), "isRememberEmail", false);
                        }

                        /** 로그인성공시 메인화면으로 이동 */
                        Toast.makeText(EmailLoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else { //인증실패
                        AlertDialogHelper.showAlertDialogOnlyConfrim(
                                EmailLoginActivity.this, // 현재 Activity의 Context
                                "", // 다이얼로그 제목
                                "로그인에 실패했습니다. \n다시 한번 시도해주세요" // 다이얼로그 메시지
                        );
                    }
                } else { //서버 통신 및 응답이 200외 상태

                    //@fixme - 서버응답에 의문이 있음, 로그인 비밀번호, 아이디 검증 실패인데 500응답을 내려주고 있음

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

                    if (errorResponseWrapper != null && errorResponseWrapper.getResult() != null) {
                        ResponseCommonData errorResponse = errorResponseWrapper.getResult();
                        logError(TAG, errorResponse.getMessage());
                        logError(TAG, errorResponse.getCode());

                        // 실패한 경우 사용자에게 오류 메시지를 보여주는 대화상자를 띄웁니다.
                        // 비밀번호가 틀렸을 경우에 http 500 상태값 오류를 내려줘서... 응답메세지를 직관적으로 변경함.
                        String errMsg = "로그인 실패했습니다 \n다시 한번 시도해주세요.";
                        AlertDialogHelper.showAlertDialogOnlyConfrim(
                                EmailLoginActivity.this, // 현재 Activity의 Context
                                "", // 다이얼로그 제목
                                //errorResponse.getMessage()
                                errMsg
                        ); // 다이얼로그 메시지
                    } else {
                        // 오류 응답 파싱 실패 처리
                        Log.e(TAG, "onResponse: 오류 메시지를 읽는데 실패했습니다.");
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
    }
}