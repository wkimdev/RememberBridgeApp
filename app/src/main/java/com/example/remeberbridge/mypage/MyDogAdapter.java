package com.example.remeberbridge.mypage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.R;
import com.example.remeberbridge.model.dog.Dog;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * MyDogAdapter.java
 * @desc : 마이페이지에서, 내 반려견 정보 리스트를 (가로로) 보여주는 카드뷰
 *
 * @author : wkimdev
 * @created : 2023/12/23
 * @version 1.0
 * @modifyed
 **/
public class MyDogAdapter extends RecyclerView.Adapter<MyDogAdapter.DogViewHolder> {

    private String TAG = this.getClass().getSimpleName();
    private ArrayList<Dog> dogList;
    private Context context;

    public MyDogAdapter(ArrayList<Dog> dogList, Context context) {
        this.dogList = dogList;
        this.context = context;
    }

    /** 3-1) LayoutInflater를 통해 뷰 객체를 만들고,
     뷰홀더 객체를 생성하여 해당 뷰를 리턴*/
    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder 호출. 뷰객체 생성 !!!");

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.mydog_info, parent, false);

        return new DogViewHolder(view);
    }

    /** 3-2) 뷰홀더를 재활용해, 새로운 데이터로 객체를 바인딩한다.*/
    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {

        //@todo - 이미지 가져오는 법을 모르겠음!
        //holder.rc_img_profile.setBackground();
        holder.rc_txt_name.setText(dogList.get(position).getDogName());
        holder.rc_txt_birthday.setText(dogList.get(position).getBirthday());

        holder.itemView.setTag(position);
    }

    /** 3-3) 생성자를 통해 전달받은 전체 데이터 갯수 리턴 */
    @Override
    public int getItemCount() {
        return dogList != null ? dogList.size() : 0;
    }

    //뷰홀더 생성
    public class DogViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView rc_img_profile;
        private TextView rc_txt_name;
        private TextView rc_txt_birthday;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);

            this.rc_img_profile = itemView.findViewById(R.id.rc_img_profile);
            this.rc_txt_name = itemView.findViewById(R.id.rc_txt_name);
            this.rc_txt_birthday = itemView.findViewById(R.id.rc_txt_birthday);

        }
    }
}
