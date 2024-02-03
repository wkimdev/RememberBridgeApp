package com.example.remeberbridge.home;

import static com.example.remeberbridge.utils.CommonUtils.logError;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.board.BoardFrag;
import com.example.remeberbridge.R;
import com.example.remeberbridge.diary.RememberFrag;
import com.example.remeberbridge.configure.RetrofitClientInstance;
import com.example.remeberbridge.model.ResponseCommonData;
import com.example.remeberbridge.model.ResponseWrapper;
import com.example.remeberbridge.model.auth.UserSpaceInfoResult;
import com.example.remeberbridge.model.dog.Dog;
import com.example.remeberbridge.service.MemberService;
import com.example.remeberbridge.utils.AlertDialogHelper;
import com.example.remeberbridge.utils.NavigationUtils;
import com.example.remeberbridge.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HomeFrag.java
 * @desc : 추억공간을 생성하는 홈화면 프레그먼트
 *
 * @author : wkimdev
 * @created : 2023/12/23
 * @version 1.0
 * @modifyed
 **/
public class HomeFrag extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private boolean isFabOpen = false;

    //플로팅버튼 뷰바인딩을 위한 선언
    private FloatingActionButton home_floatingBtn_main, home_floatingBtn_photo,
            home_floatingBtn_board, home_floatingBtn_ai;

    //플로팅버튼을 감싸는 레이아웃 뷰바인딩을 위한 선언
    private LinearLayoutCompat home_ll_submenu_photo,
            home_ll_submenu_board, home_ll_submenu_ai;

    private RecyclerView home_rc_mydoglist; //추억공간 노출하는 리사이클러뷰
    private HomeMyDogListAdapter homeMyDogListAdapter; //어댑터 선언
    private ArrayList<Dog> dogArrayList; //강아지리스트

    // 레트로핏으로 사용자의 추억공간 데이터를 호출함
    private MemberService service = RetrofitClientInstance.getRetrofitInstance().create(MemberService.class);


    //프래그먼트에 맞는 UI를 그리기 위해 View를 반환하는 콜백메소드
    //프래그먼트의 레이아웃 루트이기 때문에 UI를 제공하지 않을 경우, null을 반환
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /** onCreateView가 호출 후 return한 뒤에 곧바로 실행되는 콜백 메소드
     View가 완전히 생성되었을때 호출된다.
     onCreateaView가 아닌, 아래 콜백에서 뷰바인딩을 하도록 권장*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //플로팅버튼 레이아웃 뷰바인딩
        home_floatingBtn_main = view.findViewById(R.id.home_floatingBtn_main);
        home_ll_submenu_photo = view.findViewById(R.id.home_ll_submenu_photo);
        home_ll_submenu_board = view.findViewById(R.id.home_ll_submenu_board);
        home_ll_submenu_ai = view.findViewById(R.id.home_ll_submenu_ai);

        //플로팅버튼 레이아웃내부 버튼뷰 뷰바인딩
        home_floatingBtn_photo = view.findViewById(R.id.home_floatingBtn_photo);
        home_floatingBtn_board = view.findViewById(R.id.home_floatingBtn_board);
        home_floatingBtn_ai = view.findViewById(R.id.home_floatingBtn_ai);

        //내 반려견과의 추억공간 리사이클러뷰
        home_rc_mydoglist = view.findViewById(R.id.home_rc_mydoglist);


        // 프레그먼트 이동을 공통화시켜서 사용하기 위함
        // In your Activity where you need to replace fragments
        // Inside a Fragment
        FragmentManager fm = getActivity().getSupportFragmentManager();


        //##################### 리사이클러뷰 처리 구문 #####################

        //강아지리스트 초기화
        dogArrayList = new ArrayList<>();

        // 추억공간 데이터를 가져오기 위한 회원정보 호출
        String userEmail = PreferenceManager.getString(getContext(), "user_email");
        getUserInfo(userEmail);


        //##################### 버튼이벤트 처리 구문 #####################
        home_floatingBtn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
            }
        });


        //추억공간 플로팅버튼 클릭
        home_floatingBtn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //@todo - Replace a fragment
                //@todo - 프레그먼트에서, 액티비티 뷰를 바이딩하려면 아래처럼 선언해야함
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.main_bottom_menu);
                NavigationUtils.setFragment(fm, R.id.main_frame, new RememberFrag(), bottomNavigationView, R.id.action_photos);
            }
        });

        //게시글생성 플로팅버튼 클릭
        home_floatingBtn_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //@todo - Replace a fragment
                //@todo - 프레그먼트에서, 액티비티 뷰를 바이딩하려면 아래처럼 선언해야함
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.main_bottom_menu);
                NavigationUtils.setFragment(fm, R.id.main_frame, new BoardFrag(), bottomNavigationView, R.id.action_board);
            }
        });

        //AIStory보드 플로팅버튼 클릭
        home_floatingBtn_ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "해당서비스는 아직 준비중입니다!🙏🏼", Toast.LENGTH_SHORT).show();
            }
        });

    } // [END] viewCreated 구문


    // 추억공간 데이터를 가져오기 위한 회원정보 호출
    private void getUserInfo(String userEmail) {
        //@todo -delete!
        //userEmail = "wkimdev2@gmail.com";

        //enqueue에 파라미터로 넘긴 콜백에서는 통신이 성공/실패 했을때 수행할 동작을 재정의 한다
        service.getUserInfo(userEmail).enqueue(new Callback<ResponseWrapper>() {
            @Override
            public void onResponse(Call<ResponseWrapper> call, Response<ResponseWrapper> response) {

                //@todo - 서버응답 성공하고, space_info 값이 있는지만 확인해서 Adapter생성시 view를 분기처리한다.
                //서버 통신 및 응답이 200상태
                if (response.isSuccessful() && response.body() != null) {

                    ResponseWrapper responseWrapper = response.body();
                    ResponseCommonData responseCommonData = responseWrapper.getResult();
                    String code = responseCommonData.getCode();
                    String message = responseCommonData.getMessage();

                    if ("2000".equals(code)) { //인증성공
                        //추억공간에 값이 있는 경우 리스트만드는 처리
                        if (responseCommonData.getSpaceInfo() != null
                                || responseCommonData.getSpaceInfo().size() > 0) { //[space_info가 있을 경우 start]

                            //사용자가 반려견 정보를 등록했는지 알려주는 구분값
                            PreferenceManager.setBoolean(getContext(), "isDogValue", true);

                            ArrayList<UserSpaceInfoResult> results = responseCommonData.getSpaceInfo();

                            for (int i = 0; i < results.size(); i++) {
                                // Dog 객체 생성
                                Dog dog = new Dog(results.get(i).getDogProfImg()
                                        ,results.get(i).getDogName()
                                        ,results.get(i).getCreateAt());

                                // ArrayList에 추가
                                dogArrayList.add(dog);
                            }

                            //@todo -for test deleted!
                            /*List<String> dogName = Arrays.asList("멍멍이", "솜이", "준이", "미미");
                            List<String> birthdays = Arrays.asList("2020-01-01", "2021-05-20", "2019-12-31", "2019-12-31");
                            List<String> createdDays = Arrays.asList("2020-01-01", "2021-05-20", "2019-12-31", "2019-12-31");
                            for (int i = 0; i < 100; i++) {
                                // Dog 객체 생성
                                Dog dog = new Dog(
                                        "프로필사진",
                                        dogName.get(i % dogName.size())
                                        ,birthdays.get(i % birthdays.size())
                                        ,createdDays.get(i % createdDays.size()));
                                // ArrayList에 추가
                                dogArrayList.add(dog);
                            }*/

                        } //[space_info가 있을 경우 end]


                        homeMyDogListAdapter = new HomeMyDogListAdapter(dogArrayList, getContext());
                        home_rc_mydoglist.setLayoutManager(new LinearLayoutManager(getContext()));
                        home_rc_mydoglist.addItemDecoration(new HomeMyDogItemDecoration(getContext()));
                        home_rc_mydoglist.setAdapter(homeMyDogListAdapter);

                    } else {
                        //인증실패 오류팝업을 노출시킨다.
                        AlertDialogHelper.showAlertDialogOnlyConfrim(
                                getContext(), // 현재 Activity의 Context
                                "", // 다이얼로그 제목
                                message); // 다이얼로그 메시지
                    }

                } else {//서버통신응답 자체가 200이 아닐경우

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
                            getContext(), // 현재 Activity의 Context
                            "", // 다이얼로그 제목
                            "오류가 발생했습니다. 잠시 후 다시 시도해주세요." // 다이얼로그 메시지
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper> call, Throwable t) {
                Log.e(TAG, "레트로핏 응답 실패 오류 발생 ");
                AlertDialogHelper.showAlertDialogOnlyConfrim(
                        getContext(), // 현재 Activity의 Context
                        "", // 다이얼로그 제목
                        "오류가 발생했습니다. \n잠시후 다시 시도해주세요." // 다이얼로그 메시지
                );

                t.printStackTrace();
            }
        });
    }


    /** 플로팅버튼 클릭시 애니메이션 처리 */
    private void toggleFab() {
        //Toast.makeText(getContext(), "메인 플로팅 버튼 클릭 : " + isFabOpen, Toast.LENGTH_SHORT).show();

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) { //플로팅버튼을 클릭했을 때 위에, 3개의 버튼이 노출된다.


            home_ll_submenu_photo.setVisibility(View.INVISIBLE);
            home_ll_submenu_photo.animate().translationY(0).start();

            home_ll_submenu_board.setVisibility(View.INVISIBLE);
            home_ll_submenu_board.animate().translationY(0).start();

            home_ll_submenu_ai.setVisibility(View.INVISIBLE);
            home_ll_submenu_ai.animate().translationY(0).start();

            home_floatingBtn_main.setImageResource(R.drawable.icon_add);  // [+] 표시로 변환하기

        } else {
            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅

            //Y축을 따라 일정 거리(수직으로) 이동하여 보이도록 애니메이션을 적용합니다. 구체적으로 fabCamera는 200단위 위로 이동하고 fabEdit는 400단위 위로 이동하여 사용자가 볼 수 있도록 합니다.
            home_ll_submenu_photo.setVisibility(View.VISIBLE);
            home_ll_submenu_photo.animate().translationY(-200f).start();

            home_ll_submenu_board.setVisibility(View.VISIBLE);
            home_ll_submenu_board.animate().translationY(-400f).start();

            home_ll_submenu_ai.setVisibility(View.VISIBLE);
            home_ll_submenu_ai.animate().translationY(-600f).start();

            home_floatingBtn_main.setImageResource(R.drawable.icon_close); // [x] 표시로 변환하기
        }
        isFabOpen = !isFabOpen;
    }
}
