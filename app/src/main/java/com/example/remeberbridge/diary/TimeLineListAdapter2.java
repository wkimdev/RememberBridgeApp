package com.example.remeberbridge.diary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.remeberbridge.R;
import com.example.remeberbridge.model.diary.DiaryInfoResult;
import com.example.remeberbridge.utils.CommonUtils;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

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

    private TimeLineFragment2.TimelineInterfacer listener;
    private Context context;

    ArrayList<DiaryInfoResult> diarylist;
    private String TAG = this.getClass().getSimpleName();

    public TimeLineListAdapter2(ArrayList<DiaryInfoResult> diarylist
            , TimeLineFragment2.TimelineInterfacer listener
            , Context context){
        this.diarylist = diarylist;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //테스트 -> itemview
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Log.e("adapter", "onBindViewHolder position value: " + diarylist.get(position));
        DiaryInfoResult item = diarylist.get(position);

        //로딩 상태일때
        if (item == null) { // Assuming a null item indicates a loading state
            holder.getTimeline2_upload_date().setVisibility(View.GONE);
            holder.getTimeline2_upload_content().setVisibility(View.GONE);
            holder.getProgressBar().setVisibility(View.VISIBLE);
        } else {
            holder.getTimeline2_upload_date().setVisibility(View.VISIBLE);
            holder.getTimeline2_upload_content().setVisibility(View.VISIBLE);
            holder.getProgressBar().setVisibility(View.GONE);

            // Replace with actual text getter from DiaryInfoResult
            //holder.getTextView().setText(item.getDiaryContent());

            holder.setDiaryId(item.getDiaryId());
            holder.getTimeline2_upload_date().setText(
                    CommonUtils.changeDateFormat(item.getSelectDate()));
            holder.getTimeline2_upload_content().setText(item.getDiaryContent());

            //@fixme - 디폴트이미지 임시방편 처리함
            String detaulthumnail = "https://ik.imagekit.io/demo/medium_cafe_B1iTdD0C.jpg";

            //photoUrls
            if (item.getPhotoUrls() != null) {
                String[] photoArr = item.getPhotoUrls().split(",");
                if (photoArr.length > 0) {
                    detaulthumnail = photoArr[0];
                }
            }

            Glide.with(context)
                    .load(detaulthumnail)
                    .into(holder.getTimeline2_iv_thumnail());
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

        MaterialCardView timeline_card;
        TextView timeline2_upload_date;
        TextView timeline2_upload_content;
        ImageView timeline2_iv_thumnail;
        ProgressBar progressBar;

        int diaryId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            timeline_card = itemView.findViewById(R.id.timeline_card);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            timeline2_upload_date = (TextView) itemView.findViewById(R.id.timeline2_upload_date);
            timeline2_upload_content = (TextView) itemView.findViewById(R.id.timeline2_upload_content);
            timeline2_iv_thumnail = itemView.findViewById(R.id.timeline2_iv_thumnail);


            //아이템뷰를 뷰홀더 객체에 갖고 있기 때문에
            //클릭이벤트를 뷰홀더에서 작성하도록 한다
            //뷰홀더가 만들어지는 시점에, 클릭이벤트 처리
            this.timeline_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition(); //어뎁터내 아이템의 위치를 알기 위해 사용

                    listener.onCardClick(diarylist.get(pos).getDiaryId());
                }
            });

        }


        public int getDiaryId() {
            return diaryId;
        }

        public void setDiaryId(int diaryId) {
            this.diaryId = diaryId;
        }

        public ProgressBar getProgressBar(){
            return progressBar;
        }

        public TextView getTimeline2_upload_date() {
            return timeline2_upload_date;
        }

        public TextView getTimeline2_upload_content() {
            return timeline2_upload_content;
        }

        public ImageView getTimeline2_iv_thumnail() {
            return timeline2_iv_thumnail;
        }
    }

}

