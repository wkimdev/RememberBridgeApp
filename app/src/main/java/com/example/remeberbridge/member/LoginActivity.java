package com.example.remeberbridge.member;

import static com.example.remeberbridge.utils.CommonUtils.logError;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remeberbridge.EmailLoginActivity;
import com.example.remeberbridge.MainActivity;
import com.example.remeberbridge.R;
import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.auth.RequestLoginData;
import com.example.remeberbridge.model.auth.UserLoginInfoResult;
import com.example.remeberbridge.model.auth.UserSpaceInfoResult;
import com.example.remeberbridge.service.MemberService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.common.model.KakaoSdkError;
import com.kakao.sdk.user.UserApiClient;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginActivity.java
 *
 * @author : wkimdev
 * @version 1.0
 * @desc : 소셜 및 일반로그인 화면
 * @created : 2023/12/08
 * @modifyed
 **/
public class LoginActivity extends AppCompatActivity {


    private  final String TAG = getClass().getSimpleName();

    //private static final int BACK_PRESS_THRESHOLD = 2000; // 2초 내에 뒤로 가기 버튼을 누르면 앱 종료

    //2초 이내로 백키를 누르면 앱을 종료한다는 의미
    private static final long DOUBLE_BACK_PRESS_THRESHOLD = 2000; // 백키 누름 허용 간격 (2초)
    private long lastBackPressedTime;
    private Toast backToast; //이게 뭘까?



    private long backPressedTime;

    /** Android API 레벨 33에서 onBackPressed() 메서드가 deprecated되어서 이걸 사용함 */
    //private OnBackPressedCallback onBackPressedCallback;

    ImageButton naverLoginBtn;
    ImageButton kakaoLoginBtn;
    ImageButton googleLoginBtn;
    Button emailLoginBtn; //이메일 로그인화면 이동 버튼
    TextView registerTxt; //회원가입 화면 이동
    TextView fnqTxt; //문의하기 화면 이동

    //로그인 액션을 위한 레트로핏 서비스 API 객체 생성
    private MemberService service = RetrofitClientInstance.getRetrofitInstance().create(MemberService.class);
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1000;

    // 클래스 멤버 변수
    //private GoogleSignInClient client;
    /* 구글 로그인 요청 코드 할당 */;
    //private static final int GOOGLE_LOGIN = 1000;

    //최근로그인이력 말풍선 뷰
    private TextView login_txt_recent_login_g;
    private TextView login_txt_recent_login_k;
    private TextView login_txt_recent_login_n;
    //네이버 토큰 초기화
    final String[] naverToken = {null};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 여기에 onBackPressedCallback 로직 추가
        /** 하드웨어 백키클릭시 앱 종료하는 처리 콜백 코드 */
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                Log.e(TAG, "handleOnBackPressed: CALL!!!!");

                //내가 현재에 백키를 누른시간이 2초 이내라면 앱을 종료한다.
                if (System.currentTimeMillis() - lastBackPressedTime < DOUBLE_BACK_PRESS_THRESHOLD) {
                    // 두 번째 백키 누름이 허용 간격 이내일 경우 앱 종료
                    Log.e(TAG, "handleOnBackPressed: 2222 CALL!!!!");

                    //현재 보여지고 있는 Toast 메시지를 즉시 숨깁니다. 만약 Toast가 화면에 보여지고 있지 않으면, 아무런 효과가 없습니다.
                    //이 코드는 사용자가 빠르게 두 번 백 버튼을 눌러 앱을 종료하려 할 때,
                    // 첫 번째 누름에 표시된 Toast 메시지를 취소하기 위해 사용됩니다.
                    // 사용자가 백 버튼을 한 번만 누르고, 그 후에 다른 작업을 하여
                    // Toast 메시지가 더 이상 유효하지 않은 경우에도 Toast 메시지를 숨기기 위해 사용됩니다.
                    /*즉, 이 로직은 사용자가 백 버튼을 두 번 눌러 앱을 종료하려 할 때
                    두 번째 누름에서 Toast가 다시 표시되지 않도록 하기 위해 있습니다.
                    Toast가 이미 화면에 표시된 상태에서 다시 표시되려고 하면,
                    이전 Toast를 취소하고 새로운 Toast를 표시하거나, Toast 표시가 불필요하다면 취소하는 것입니다.*/
                    if (backToast != null) {
                        backToast.cancel();
                    }
                    finish(); // 앱 종료
                    return;
                }
                // 첫 번째 백키 누름, Toast 메시지 표시

                Log.e(TAG, "handleOnBackPressed: 3333 CALL!!!!");

                lastBackPressedTime = System.currentTimeMillis();
                if (backToast != null) {//이미 taost 객체가 있으면 객체 생성을 취소한다.
                    backToast.cancel();
                }
                //이 부분에서 공통으로 코드를 처리하기 때문
                backToast = Toast.makeText(LoginActivity.this, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
                backToast.show();
            }
        };

        //sns 로그인
        naverLoginBtn = findViewById(R.id.load_imgBtn_naver);
        kakaoLoginBtn = findViewById(R.id.load_imgBtn_kakao);
        googleLoginBtn = findViewById(R.id.load_imgBtn_google);
        emailLoginBtn = findViewById(R.id.login_btn_emailLogin);

        /*일반회원가입과 문의하기 영역*/
        /*registerTxt = findViewById(R.id.login_txt_register);
        fnqTxt = findViewById(R.id.login_txt_fnq);*/
        login_txt_recent_login_g = findViewById(R.id.login_txt_recent_login_g);
        login_txt_recent_login_k = findViewById(R.id.login_txt_recent_login_k);
        login_txt_recent_login_n = findViewById(R.id.login_txt_recent_login_n);


        /** Naver Login Module Initialize */
//        NaverIdLoginSDK.initialize(this, R.string.NAVER_CLIENT_ID, R.string.NAVER_CLIENT_SECRET, R.string.app_name);
        NaverIdLoginSDK.INSTANCE.initialize(this,
                getString(R.string.NAVER_CLIENT_ID),
                getString(R.string.NAVER_CLIENT_SECRET),
                getString(R.string.app_name));
        NaverIdLoginSDK.INSTANCE.showDevelopersLog(true);



        /** Google 로그인 및 GoogleSignInClient 객체 구성하기 */
        //앱에 필요한 사용자 데이터를 요청하도록 Google 로그인을 구성한다
        //DEFAULT_SIGN_IN 매개변수를 사용하여 GoogleSignInOptions 객체를 만든다.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // 백엔드서버와 인증정보를 주고받기 위해선 Create an OAuth 2.0 client ID for your backend server
        // web_client_id로 설정해야 한다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        // GoogleSignInClient 객체를 생성
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // 이미지 캐러셀 적용
        ImageCarousel carousel = findViewById(R.id.carousel);
        // 이미지 캐러셀은 라이프사이클을 인식하는 컴포넌트다.
        // 이미지 캐러샐을 자동재생할 경우, 앱이 백그라운드에 있을 때 재생/스크롤이 일시 중지되고 앱이 다시 시작되었을 때 다시 시작되도록 이미지 캐러샐을 라이프사이클에 등록하는게 좋다.
        // 또한 앱이 백그라운드에 있을 때 무한 캐러셀을 올바르게 초기화하는데도 사용된다.
        carousel.registerLifecycle(getLifecycle());

        //로그인화면의 이미지의 모서리를 둥글게 하기 위함
        carousel.setClipToOutline(true);
        //메인화면 이미지 캐러셀을 보여주는 함수
        showCarousel(carousel);




        /**1) callback 먼저 실행 */
        //getOnBackPressedDispatcher().addCallback(LoginActivity.this, callback);
        getOnBackPressedDispatcher().addCallback(this, callback);

    }


    //메인화면 이미지 캐러셀을 보여주는 함수
    private void showCarousel(ImageCarousel carousel) {
        List<CarouselItem> list = new ArrayList<>();

        // 로그인화면 이미지 캐러셀 적용
        // Image drawable with caption
        list.add(
                new CarouselItem(
                        R.drawable.som_1
                )
        );

        list.add(
                new CarouselItem(
                        R.drawable.som_2
                )
        );

        list.add(
                new CarouselItem(
                        R.drawable.som_3
                )
        );

        list.add(
                new CarouselItem(
                        R.drawable.som_4
                )
        );

        list.add(
                new CarouselItem(
                        R.drawable.som_5
                )
        );

        list.add(
                new CarouselItem(
                        R.drawable.som_6
                )
        );

        carousel.setData(list);
    }

    @Override
    protected void onStart() {
        super.onStart();


        //@todo - shared에 저장된 값으로 판단하기
        String user_sns_type = PreferenceManager.getString(getApplicationContext(), "user_sns_type");
        logError(TAG, "onStart() login_sns_type 이력내역  + " + user_sns_type);

        /** 소셜로그인의 경우만, 기존에 어떤 sns 계정으로 로그인했는지 말풍선을 노출해 보여준다 */
        if (!user_sns_type.isEmpty() && !("L".equals(user_sns_type))) {
            //보여줘라 말풍선!
            if ("G".equals(user_sns_type)) {
                login_txt_recent_login_g.setVisibility(View.VISIBLE);
            } else if ("K".equals(user_sns_type)) {
                login_txt_recent_login_k.setVisibility(View.VISIBLE);
            } else if ("N".equals(user_sns_type)) {
                login_txt_recent_login_n.setVisibility(View.VISIBLE);
            }
        } else { //일반로그인이면 말풍선 비노출
            login_txt_recent_login_g.setVisibility(View.INVISIBLE);
            login_txt_recent_login_k.setVisibility(View.INVISIBLE);
            login_txt_recent_login_n.setVisibility(View.INVISIBLE);
        }


        //공홈에서 소개하는 구글 로그인체크 방법
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
        //if(account != null) {
            //@todo -
            //logError(TAG, "구글에 로그인되어 있습니다");
        //}

    }

    @Override
    protected void onResume() {
        super.onResume();

        kakaoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Login with KakaoTalk
                checkKakaoTokenExist();
            }
        });


        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });


        naverLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snsLoginNaver();
            }
        });


        emailLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

/*        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MemberRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
    }

    private void snsLoginKakao() {
        UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, (token, error) -> {
            if (error != null) {
                Log.e(TAG, "kakao 로그인 실패", error);

            } else if (token != null) {
                token.getIdToken();
                //access token 발급 완료!
                logError(TAG, "로그인 성공 " + token.getAccessToken());
                //@todo - new api 호출
                //유저정보를 호출받아, shared에 저장한다.
                //????

                //카카오 로그인 or 회원가입 처리
                //이메일 이런 정보 어디서 가져와ㅏ????
                token.getIdToken();




            }
            return null;
        });
    }


//    private void getKakaoLoginInfo() {
//        // 토큰 정보 보기
//        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//            if (error != null) {
//                Log.e(TAG, "토큰 정보 보기 실패", error)
//            }
//            else if (tokenInfo != null) {
//                Log.i(TAG, "토큰 정보 보기 성공" +
//                        "\n회원번호: ${tokenInfo.id}" +
//                        "\n만료시간: ${tokenInfo.expiresIn} 초")
//            }
//        }
//    }


    //앱 실행 시 사용자가 앞서 로그인을 통해 발급 받은 토큰이 있는지 확인
    private void checkKakaoTokenExist() {

        if (AuthApiClient.getInstance().hasToken()) { //토큰이 이미 있다면 다시 로그인을 안해도 된다.
            UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
                if (error != null) {
                    if (error instanceof KakaoSdkError && ((KakaoSdkError) error).isInvalidTokenError()) {
                        // 로그인 필요
                        //snsLoginKakao();
                        tokenInfo.getId(); //이메일이런 값들은 없네???
                    } else {
                        // 기타 에러
                        Log.e(TAG, "checkKakaoTokenExist: 오류가 발생했습니다. ");
                    }
                } else {
                    // 토큰 유효성 체크 성공 (필요 시 토큰 갱신됨)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return null;
            });
        } else {
            //토큰이 없다면 로그인 Or 회원가입처리를 해야 한다.
            snsLoginKakao();
        }
    }


    /**
     * 네이버 로그인 or 회원가입
     * authenticate() 메서드를 이용한 로그인
     */
    private void snsLoginNaver() {
        //3)
        //access token을 얻는데 성공한 경우 로직 처리
        NidProfileCallback<NidProfileResponse> profileCallback = new NidProfileCallback<NidProfileResponse>() {
            @Override
            public void onSuccess(NidProfileResponse response) {
                //@ todo- 아래 정보는 shared에 저장하고,db에 저장된 user_id를 가져오기 위해선 api를 호출해야 한다.
                logError(TAG, "ID: " + response.getProfile().getId());//네이버 고유 ID
                logError(TAG, "Email: " +response.getProfile().getEmail());
                logError(TAG, response.getProfile().getProfileImage());
                logError(TAG, response.getProfile().getName());

                String user_sns_id = response.getProfile().getId();
                String user_email = response.getProfile().getEmail();
                String user_name = response.getProfile().getName();
                String user_prof_img = response.getProfile().getProfileImage();
                String login_sns_type = "N";

                RequestLoginData requestLoginData =
                        new RequestLoginData(user_email, user_name,
                                user_prof_img, login_sns_type, user_sns_id);
                requestJoinOrLogin(requestLoginData);


                //회원가입 처리
                //서버에 조회해서,
                /*1) 최초회원가입 -> 해당 SNS 타입과 refresh 토큰이 둘다 없다 -> 회원가입처리
                2) 로그인 -> 해당 SNS타입과 refresh 토큰이 있음 -
                > 로그인처리, refresh token값이 변경되어을 경우 update 처리 (session 만료 처리를 이걸로 ..? ) 앱은 거의 로그인 되어 있지 않나.*/



            }

            @Override
            public void onFailure(int httpStatus, String message) {
                String errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode().getCode();
                String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                Toast.makeText(LoginActivity.this, "errorCode: " + errorCode + "\nerrorDescription: " + errorDescription, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String message) {
                onFailure(errorCode, message);
            }

        };

        //2)
        //refresh 토큰이 있으면 access 토큰의 갱신을 시도합니다.
        //갱신 토큰이 있으면 접근 토큰의 갱신을 시도합니다
        //갱신에 성공하면 아래코드로 결과를 확인할 수 있다.
        //갱신에 실패하면 로그인 창이 나타납니다.
        //* 참고 공홈 - https://developers.naver.com/docs/login/android/android.md#5-2--authenticate-%EB%A9%94%EC%84%9C%EB%93%9C%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8
        OAuthLoginCallback oauthLoginCallback = new OAuthLoginCallback() {

            //갱신 성공 - 갱신 토큰이 있다.
            @Override
            public void onSuccess() {
                // Convert the current time from milliseconds to seconds and log it
                long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                Long tokenExpiredTime = NaverIdLoginSDK.INSTANCE.getExpiresAt();

                /**
                //2-1) Check if the token is expired or not
                // 만료시간 - 현재시간 < 0 ==> 토큰이 유효하지 않음 */
                if (tokenExpiredTime - currentTimeInSeconds < 0) { //토큰이 유효하지 않음

                    logError(TAG, "네이버 access token이 유효하지 않음!!");

                    // If the token is expired, refresh it
                    new NidOAuthLogin().callRefreshAccessTokenApi(new OAuthLoginCallback() {
                        @Override
                        public void onSuccess() {
                            // Token was refreshed successfully
                            naverToken[0] = NaverIdLoginSDK.INSTANCE.getAccessToken();
                            logError(TAG, "[네아로] Refreshed access token: " + naverToken[0]);
                        }

                        @Override
                        public void onFailure(int i, @NonNull String s) {
                            // Handle the failure of token refreshing
                            String errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode().getCode();
                            String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                            Toast.makeText(LoginActivity.this, "errorCode: " + errorCode + "\nerrorDescription: " + errorDescription, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int i, @NonNull String s) {
                            // Handle errors during token refresh
                            onFailure(i, s);
                        }
                    });

                } else {
                    //2-2) 토큰이 유효함
                    logError(TAG, "네이버 access token이 유효함!!");
                    // The token is still valid, and you can make API calls
                    // Fetch user information
                    new NidOAuthLogin().callProfileApi(profileCallback);
                }
            }

            @Override
            public void onFailure(int httpStatus, String message) {
                String errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode().getCode();
                String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                Toast.makeText(LoginActivity.this, "errorCode: " + errorCode + "\nerrorDescription: " + errorDescription, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String message) {
                onFailure(errorCode, message);
            }
        };

        /** 1)
        authenticate() 함수를 사용해, 먼저 refresh token이 있는지 확인한다.*/
        NaverIdLoginSDK.INSTANCE.authenticate(this, oauthLoginCallback);

    }

    private void requestJoinOrLogin(RequestLoginData requestLoginData) {
        service.snsLogin(requestLoginData).enqueue(new Callback<ResponseWrapper>() {
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {
                //서버 통신 및 응답이 200상태
                if (response.isSuccessful() && response.body() != null) {

                    ResponseWrapper responseWrapper = response.body();
                    ResponseCommonData responseCommonData = responseWrapper.getResult();
                    String code = responseCommonData.getCode();
                    String message = responseCommonData.getMessage();

                    if ("2000".equals(code)) { //인증성공
                        //로그인이 정상이라면, userId값을 받아와 쉐어드에 담는다.
                        UserLoginInfoResult userLoginInfoResult = responseCommonData.getUserInfo();
                        logError(TAG, "회원가입한 사용자 ID: " + userLoginInfoResult.getUserId());
                        PreferenceManager.setInt(getApplicationContext(), "user_id", userLoginInfoResult.getUserId());


                        logError(TAG, "쉐어드에 저장된 userId 확인: " + PreferenceManager.getInt(getApplicationContext(), "user_id"));
                        logError(TAG, "쉐어드에 저장된 sns_type 확인: " + PreferenceManager.getString(getApplicationContext(), "user_sns_type"));
                        logError(TAG, "쉐어드에 저장된 user email 확인: " + PreferenceManager.getString(getApplicationContext(), "user_email"));

                        /** 로그인성공시 메인화면으로 이동 */
                        Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                        //@todo - 여기까지 확인!
                        //로그인 혹은 회원가입 성공했으니 메인화면으로 이동시킨다.
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else if ("1001".equals(code)) { //인증실패
                        //인증실패 오류팝업을 노출시킨다.
                        AlertDialogHelper.showAlertDialogOnlyConfrim(
                                LoginActivity.this, // 현재 Activity의 Context
                                "", // 다이얼로그 제목
                                message // 다이얼로그 메시지
                        );
                    }

                } else {
                    //서버통신응답 자체가 200이 아닐경우
                    // 오류 응답 처리
                    String errorMessage = "오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
                    try {
                        // errorBody()가 null이 아니라고 확신하는 경우에만 호출
                        if (response.errorBody() != null) {
                            String errorStr = response.errorBody().string();
                            // TODO: errorStr을 파싱하여 사용자에게 보여줄 메시지를 추출하세요.
                            // 예를 들어, JSON 형식의 오류 메시지를 파싱할 수 있습니다.
                            errorMessage = errorStr; // 이 부분을 실제 오류 메시지로 교체하세요.
                            //Log.e(TAG, "onResponse: 오류 메시지 >>> " + errorMessage);
                            logError(TAG, errorMessage);

                        }
                    } catch (IOException e) {
                        // errorBody를 읽는 데 실패한 경우
                        Log.e(TAG, "onResponse: 오류 메시지를 읽는데 실패했습니다.", e);
                    }

                    // 실패한 경우 사용자에게 오류 메시지를 보여주는 대화상자를 띄웁니다.
                    AlertDialogHelper.showAlertDialogOnlyConfrim(
                            LoginActivity.this, // 현재 Activity의 Context
                            "", // 다이얼로그 제목
                            "오류가 발생했습니다. 잠시 후 다시 시도해주세요." // 다이얼로그 메시지
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper> call, Throwable t) {
                Log.e(TAG, "레트로핏 응답 실패 오류 발생 ");

                AlertDialogHelper.showAlertDialogOnlyConfrim(
                        getApplicationContext(), // 현재 Activity의 Context
                        "", // 다이얼로그 제목
                        "오류가 발생했습니다. \n잠시후 다시 시도해주세요." // 다이얼로그 메시지
                );

                t.printStackTrace();
            }
        });

    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //사용자가 로그인한 후에는 활동의 onActivityResult 메서드에서 사용자의 GoogleSignInAccount 객체를 가져올 수 있습니다.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    //GoogleSignInAccount 객체에는 사용자 이름 등 로그인한 사용자에 대한 정보가 포함
    //구글에선 자동으로 토큰을 갱신해준다.
    //토큰이 유효하기 때문에, 로그인을 그대로 유지하면 된다.
    //토큰이 유효하지 않을경우만 로그인을 다시 유도하면서 db에서 user_id값을 가져온다.
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            //토큰 만료 정보
            logError(TAG, "토큰 만료시간 >>>> " + account.isExpired());

            String user_sns_id = account.getId();
            logError(TAG, "구글 user_sns_id >>>> " + user_sns_id);
            String user_email = account.getEmail();
            String user_name = account.getDisplayName();
            String user_prof_img = account.getPhotoUrl().toString();
            String login_sns_type = "G";

            PreferenceManager.setString(getApplicationContext(), "user_email", user_email);
            PreferenceManager.setString(getApplicationContext(), "user_name", user_name);
            PreferenceManager.setString(getApplicationContext(), "user_prof_img", user_prof_img);
            PreferenceManager.setString(getApplicationContext(), "user_sns_type", login_sns_type);

            RequestLoginData requestLoginData =
                    new RequestLoginData(user_email, user_name,
                            user_prof_img, login_sns_type, user_sns_id);
            requestJoinOrLogin(requestLoginData);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }


    //onDestroy()에서는 콜백을 제거하여 리소스 누수를 방지
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backToast != null) {
            backToast.cancel(); // 액티비티 종료 시 Toast 메시지도 취소
        }
    }

}