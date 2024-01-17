package com.example.remeberbridge.diary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remeberbridge.R;
import com.example.remeberbridge.home.HomeMyDogListAdapter;
import com.example.remeberbridge.model.diary.DiaryInfoResult;

import java.util.ArrayList;

/**
 * TimeLineListAdapter.java
 * @desc : 추억공간 화면의 타임라인 리사이클러뷰
 *
 * @author : wkimdev
 * @created : 2024/01/04
 * @version 1.0
 * @modifyed
 **/
public class TimeLineListAdapter extends RecyclerView.Adapter<TimeLineListAdapter.TimeLineViewHolder> {

    private String TAG = this.getClass().getSimpleName();

    //data list
    private ArrayList<DiaryInfoResult> diaryInfoResultArrayList;
    private int isLoad;
    private Context context;

    //백그라운드 데이터의 로딩을 나타내는 구분자
    public int getIsLoad() {

        if (diaryInfoResultArrayList.size()<= 0) {
            return isLoad = -1; //로딩중
        } else {
            return isLoad = 1; //로딩중 아님
        }
    }

    public void setIsLoad(int isLoad) {
        this.isLoad = isLoad;
    }

    public TimeLineListAdapter(ArrayList<DiaryInfoResult> diaryInfoResultArrayList, Context context) {
        this.diaryInfoResultArrayList = diaryInfoResultArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.timeline_single_item, parent, false);
        return new TimeLineListAdapter.TimeLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineListAdapter.TimeLineViewHolder holder, int position) {
        DiaryInfoResult item = diaryInfoResultArrayList.get(position);
        holder.timeline_txt_content.setText(item.getDiaryContent());
        // Set more data to your views...

        // 해당 아이템이 로딩 아이템일 경우 숫자 표시 view 제거 및 로딩 view 표시
        /*if(getIsLoad() == -1){
            holder.getTimeline_ll().setVisibility(View.GONE);
            //holder.getTextView().setVisibility(View.GONE);
            holder.getProgressBar().setVisibility(View.VISIBLE);
        }else{
            // 해당 아이템이 숫자 아이템일 경우 숫자 view 표시 및 로딩 view 제거
            //holder.getTextView().setVisibility(View.VISIBLE);
            holder.getTimeline_ll().setVisibility(View.VISIBLE);
            holder.getProgressBar().setVisibility(View.GONE);

            //holder.getTextView().setText(String.valueOf(numberlist.get(position)));
            holder.timeline_txt_content.setText(item.getDiaryContent());
        }*/
    }

    @Override
    public int getItemCount() {
        int count = diaryInfoResultArrayList != null ? diaryInfoResultArrayList.size() : 0;
        return count;
    }



    // ################ 신규추가


    // 로딩 아이템 추가 메소드
    // 로딩항목 == 새항목을 의미한다.
    //addLoadingData()가 호출되면 목록에 -1을 추가하고 로딩 항목이 될 새 항목이 삽입되었음을 어댑터에 알립니다.
    // 내가 이해한 것 => 실제로 데이터를 넣는준비를 한다.
    // (-1)은 백그라운드에서 데이터를 불러오는 중임을 나타내는 플래그 값 같은거다.
    public void addLoadingData() {

        Log.e(TAG, "addLoadingData: 데이터를 넣을 준비!!");
        /*numberlist.add(-1);
        notifyItemInserted(numberlist.size());*/

        setIsLoad(-1);
        notifyItemInserted(diaryInfoResultArrayList.size());
    }

    /*
     로딩 아이템 제거 및 텍스트와 이미지 데이터 추가 메소드
     로딩항목 == 새항목을 의미한다.
     실제 데이터항목이 추가되기 전에, 로딩항목을 제거하고(-1)
     어뎁터에 항목이 제거되었음을 알린다.
     내가 이해한 것 => 실제로 데이터를 넣는다.*/
    public void addItems(ArrayList<DiaryInfoResult> diaryInfoResultArrayList) {

        Log.e(TAG, "addItems: 데이터를 넣는다!!");

        // 새 데이터를 추가하기 전 로딩아이템 제거
        //numberlist.remove(numberlist.size()-1);
        diaryInfoResultArrayList.remove(diaryInfoResultArrayList.size()-1);

        /*등록된 관찰자에게 이전 위치에 있던 항목이 데이터 세트에서 제거되었음을 알립니다.
        이전 위치 및 이후 위치에 있던 항목은 이제 이전 위치 - 1에서 찾을 수 있습니다.
        이것은 구조적 변화 이벤트입니다.
        데이터 세트에 있는 다른 기존 항목의 표현은 여전히 최신 상태로 간주되며, 위치가 변경될 수 있지만 반등하지는 않습니다.
                매개 변수:
        position – 제거된 아이템의 위치*/
        notifyItemRemoved(diaryInfoResultArrayList.size());

        // 데이터 추가
        for(int i = 0; i < diaryInfoResultArrayList.size(); i++){
            //여기서 계속 out of memory error
            diaryInfoResultArrayList.add(diaryInfoResultArrayList.get(i));
            //notifyItemInserted(diaryInfoResultArrayList.size()-1);
            //notifyDataSetChanged();
        }

    }


    // 현재 데이터를 불러오는 중인지 확인하기 위해
    // 마지막 아이템의 값을 반환 (-1일 경우 로딩 아이템)
    public int getLastItem(){
        Log.e(TAG, "getLastItem: 마지막 데이터인지 확인한다!!");
        //만약 size가 0이라면 -1이 된다.
        //size가 0일 경우, -1를 리턴하게 만들면된다.
        //return numberlist.get(numberlist.size()-1);
        return getIsLoad();
    }





    // Add an update method inside your TimeLineListAdapter
    public void updateData(ArrayList<DiaryInfoResult> diaryInfoResults) {
        diaryInfoResultArrayList.clear();
        diaryInfoResultArrayList.addAll(diaryInfoResults);
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout timeline_ll;

        private ImageView timeline_rc_img_profile; //솜이이미지
        private TextView timeline_txt_content; //타임라인내용

        private ProgressBar progressBar;

        public TimeLineViewHolder(@NonNull View itemView) {
            super(itemView);
            this.timeline_ll = itemView.findViewById(R.id.timeline_ll);
            this.timeline_rc_img_profile = itemView.findViewById(R.id.timeline_rc_img_profile);
            this.timeline_txt_content = itemView.findViewById(R.id.timeline_txt_content);
            this.progressBar = itemView.findViewById(R.id.progressBar);
        }

        public ProgressBar getProgressBar(){
            return progressBar;
        }

        public LinearLayout getTimeline_ll() { return timeline_ll; }
    }


}

