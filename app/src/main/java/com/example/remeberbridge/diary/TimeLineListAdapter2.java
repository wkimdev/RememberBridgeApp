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
public class TimeLineListAdapter2 extends RecyclerView.Adapter<TimeLineListAdapter2.ViewHolder> {

    ArrayList<DiaryInfoResult> diarylist;

    public TimeLineListAdapter2(ArrayList<DiaryInfoResult> diarylist){
        this.diarylist = diarylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.e("adapter", "onBindViewHolder position value: " + diarylist.get(position));
        DiaryInfoResult item = diarylist.get(position);

        //로딩 상태일때
        if (item == null) { // Assuming a null item indicates a loading state
            holder.getTextView().setVisibility(View.GONE);
            holder.getImageView().setVisibility(View.GONE);
            holder.getProgressBar().setVisibility(View.VISIBLE);
        } else {
            holder.getTextView().setVisibility(View.VISIBLE);
            holder.getImageView().setVisibility(View.VISIBLE);
            holder.getProgressBar().setVisibility(View.GONE);

            holder.getTextView().setText(item.getDiaryContent()); // Replace with actual text getter from DiaryInfoResult
        }
    }

    @Override
    public int getItemCount() {
        return diarylist.size();
    }

    // 로딩 아이템 제거 및 숫자 데이터 추가 메소드
    public void addItems(ArrayList<DiaryInfoResult> newList) {
        // Remove loading indicator
        if (!diarylist.isEmpty() && diarylist.get(diarylist.size() - 1) == null) {
            Log.e("adapter", "addItems: "+"로딩 아이템 제거");
            diarylist.remove(diarylist.size() - 1);
            notifyItemRemoved(diarylist.size());
        }

        // Add new data
        int startInsertIndex = diarylist.size();
        diarylist.addAll(newList);
        notifyItemRangeInserted(startInsertIndex, newList.size());
    }


    // 로딩 아이템 추가 메소드
    public void addLoadingData() {
        diarylist.add(null);
        notifyItemInserted(diarylist.size() - 1);
    }

    // 현재 데이터를 불러오는 중인지 확인하기 위해
    // 마지막 아이템의 값을 반환 (-1일 경우 로딩 아이템)

    //목록의 마지막일때 true를 만환한다.
    //diarylist.get(diarylist.size() - 1) == null: 목록의 마지막 항목이 null인지 확인합니다. 여기서 null 값은 로딩 상태(데이터를 가져오는 중이지만 아직 도착하지 않은 경우의 자리 표시자)를 나타내기 위해 사용됩니다.
    public boolean isLastItemLoading() {
        Log.e("adapter", "isLastItemLoading1 : " +  !diarylist.isEmpty());
        Log.e("adapter", "isLastItemLoading2-1 : " +  (diarylist.size() - 1));
        Log.e("adapter", "isLastItemLoading2-2 : " +  (diarylist.get(diarylist.size() - 1)));
        return !diarylist.isEmpty() && (diarylist.get(diarylist.size() - 1) != null);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        TextView textView;

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public ProgressBar getProgressBar(){
            return progressBar;
        }

        public TextView getTextView(){
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

}

