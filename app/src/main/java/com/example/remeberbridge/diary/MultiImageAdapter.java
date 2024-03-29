package com.example.remeberbridge.diary;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.remeberbridge.R;

import java.util.ArrayList;

/**
 * MultiImageAdapter.java
 * @desc : 갤러리에서 선택한 이미지 리스트를 보여주는 라시아클러뷰
 *
 * @author : wkimdev
 * @created : 1/25/24
 * @version 1.0
 * @modifyed
 **/
public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder>{

    private ArrayList<Uri> mData = null ;
    private Context mContext = null ;

    private ImageSelectionListener listener;

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    MultiImageAdapter(ArrayList<Uri> list, Context context, ImageSelectionListener listener) {
        mData = list ;
        mContext = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.timeline_item2, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        MultiImageAdapter.ViewHolder vh = new MultiImageAdapter.ViewHolder(view) ;

        return vh ;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri image_uri = mData.get(position) ;

        Glide.with(mContext)
                .load(image_uri)
                .into(holder.multi_image);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    /*리사이클러뷰의 이미지 데이터를 초기화 하는 함수*/
    public void clear() {
        int size = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, size);
    }


    /*이미지의 x버튼을 클릭햇을때 사진이 없어지도록 하는 함수*/
    // position of your data in the list.
    private void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);

        //계속 아이템값을 체크한다.
        //그리고 아이템갯수가 0이하가 되면
        if (getItemCount() <= 0) {
            Toast.makeText(mContext, "사진을 1개이상 선택해주세요", Toast.LENGTH_SHORT).show();
            //갤러리화면 이동시킨다. -> 이걸 어떻게 하 ? selectMultiImage()를 실행시키면 되지 않나?
            //인터페이스를 생성한다.
            if (listener != null) {
                listener.onImageSelectionRequested();
            }
        }
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView multi_image;
        ImageView upload_iv_delete;
        private String TAG = this.getClass().getSimpleName();

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조.
            multi_image = itemView.findViewById(R.id.multi_image);
            upload_iv_delete = itemView.findViewById(R.id.upload_iv_delete);

            upload_iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition(); // This will give you the position
                    Log.e(TAG, "postion value !!!!" + position);
                    removeItem(position);
                }
            });

        }

    }

}
