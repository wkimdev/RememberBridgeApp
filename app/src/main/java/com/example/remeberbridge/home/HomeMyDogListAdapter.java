package com.example.remeberbridge.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.R;
import com.example.remeberbridge.model.dog.Dog;
import com.example.remeberbridge.utils.CommonUtils;
import java.util.ArrayList;


/**
 * HomeMyDogListAdapter.java
 * @desc :
 *  홈화면 내가 생성한 강아지 추억리스트 노출
 *  - 사용자의 추억공간이 있냐, 없냐에 따라 뷰를 구분해서 노출한다.
 *
 * @author : wkimdev
 * @created : 2024/01/01
 * @version 1.0
 * @modifyed
 **/
public class HomeMyDogListAdapter extends RecyclerView.Adapter<HomeMyDogListAdapter.DogViewHolder> {

    private String TAG = this.getClass().getSimpleName();
    private ArrayList<Dog> dogList;
    //private List<ChatData> mChatData;
    private Context context;

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_ITEM = 1;



    public HomeMyDogListAdapter(ArrayList<Dog> dogList, Context context) {
        this.dogList = dogList;
        this.context = context;
    }

    /** 3-1) LayoutInflater를 통해 뷰 객체를 만들고,
     뷰홀더 객체를 생성하여 해당 뷰를 리턴*/
    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰 타입에 따라 다른 레이아웃을 인플레이트
        View view;
        Log.e(TAG, "onCreateViewHolder: VIEW_TYPE 확인 " + viewType);

        if (viewType == VIEW_TYPE_EMPTY) {
            Log.e(TAG, "view 타입 빈값이다!!!!!");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_mydog_no_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_mydog_item, parent, false);
        }
        return new HomeMyDogListAdapter.DogViewHolder(view);
    }

    /** 3-2) 뷰홀더를 재활용해, 새로운 데이터로 객체를 바인딩한다.*/
    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {


        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_ITEM) {
            //Dog dogList = dogList.get(position);
            // Bind your views here

            //@todo - 이미지 가져오는 법을 모르겠음!
            //holder.rc_img_profile.setBackground();
            holder.home_rc_txt_name.setText(dogList.get(position).getDogName() + "와의 추억");
            holder.home_rc_txt_birthday.setText("생일: " + dogList.get(position).getBirthday());
            holder.home_rc_txt_createdDay.setText("추억생성일: " + dogList.get(position).getCreatedDay());

            holder.itemView.setTag(position);
        } else {
            // For VIEW_TYPE_EMPTY, you don't need to bind anything.
        }



        //ChatData chat = mChatData.get(position);
//        Dog dog = dogList.get(position);
//
//        //@todo - 값이 있을때만 viewholder를 그리도록 해라
//        if (!CommonUtils.isStringEmpty(dog.getDogName())) { // data가 있을경우만 view에 데이터 바인딩을 한다.
//            //@todo - 이미지 가져오는 법을 모르겠음!
//            //holder.rc_img_profile.setBackground();
//            holder.home_rc_txt_name.setText(dogList.get(position).getDogName() + "와의 추억");
//            holder.home_rc_txt_birthday.setText(dogList.get(position).getBirthday());
//            holder.home_rc_txt_createdDay.setText(dogList.get(position).getCreatedDay());
//        }
//        holder.itemView.setTag(position);
    }

    /** 3-3) 생성자를 통해 전달받은 전체 데이터 갯수 리턴 */
    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount: !!!! 값 화긴 : " + (dogList != null && !dogList.isEmpty()));
        ///true가 나오면 안돼
        //return ( dogList != null || dogList.size() > 0 ) ? dogList.size() : 1;
        // Return the actual size of the list or 0 if the list is null or empty
        //return (dogList != null && !dogList.isEmpty()) ? dogList.size() : 0;

        // If list is empty, we still need to return 1 to display the empty view.
        return (dogList == null || dogList.isEmpty()) ? 1 : dogList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        Log.e(TAG, "getItemViewType: CALL~!!!!!");
//        int result = (dogList != null && !dogList.isEmpty()) ? VIEW_TYPE_ITEM : VIEW_TYPE_EMPTY;
//        Log.e(TAG, "getItemViewType: !!!! 값 화긴 : " + result);
//        return result;

        // If list is empty, return the empty view type.
        return (dogList == null || dogList.isEmpty()) ? VIEW_TYPE_EMPTY : VIEW_TYPE_ITEM;
    }

    public class DogViewHolder extends RecyclerView.ViewHolder {

        private ImageView home_rc_img_profile; //솜이이미지
        private TextView home_rc_txt_name; //솜이
        private TextView home_rc_txt_birthday; //솜이 생일
        private TextView home_rc_txt_createdDay; //솜이 계정 생성일

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);

            this.home_rc_img_profile = itemView.findViewById(R.id.home_rc_img_profile);
            this.home_rc_txt_name = itemView.findViewById(R.id.home_rc_txt_name);
            this.home_rc_txt_birthday = itemView.findViewById(R.id.home_rc_txt_birthday);
            this.home_rc_txt_createdDay = itemView.findViewById(R.id.home_rc_txt_createdDay);
        }
    }

}
