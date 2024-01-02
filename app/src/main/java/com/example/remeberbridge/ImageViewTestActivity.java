package com.example.remeberbridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.translation.ViewTranslationCallback;
import android.widget.ImageView;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator2;

public class ImageViewTestActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_test);

        // Java
        ImageCarousel carousel = findViewById(R.id.carousel);

        // If you need custom indicator, use the CircleIndicator2 from CircleIndicator (https://github.com/ongakuer/CircleIndicator).
        // Then pass the view to the ImageCarousel.
        CircleIndicator2 indicator = findViewById(R.id.custom_indicator);

        //carousel.setIndicator(indicator);

        // Touch to pause autoPlay.
        carousel.setTouchToPause(true);

        // Infinite scroll for the carousel.
        carousel.setInfiniteCarousel(true);

        // Scroll listener
        carousel.setOnScrollListener(new CarouselOnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy, int position, @Nullable CarouselItem carouselItem) {
                // ...
                Log.e(TAG, "onScrolled: CALL...!!!");
            }

            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState, int position, @Nullable CarouselItem carouselItem) {
                // ...
                Log.e(TAG, "onScrollStateChangeded: CALL...!!!");
            }
        });


        // Goto next slide/item
        carousel.next();

// Goto previous slide/item
        carousel.previous();

// Start auto play
        carousel.start();

        // Stop auto play
        carousel.stop();


        // Register lifecycle. For activity this will be lifecycle/getLifecycle() and for fragments it will be viewLifecycleOwner/getViewLifecycleOwner().
        carousel.registerLifecycle(getLifecycle());

        List<CarouselItem> list = new ArrayList<>();

// Image URL with caption
        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080",
                        "Photo by Aaron Wu on Unsplash"
                )
        );

// Just image URL
        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"
                )
        );

// Image URL with header
        Map<String, String> headers = new HashMap<>();
        headers.put("header_key", "header_value");

        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080",
                        headers
                )
        );

// Image drawable with caption
        list.add(
                new CarouselItem(
                        R.drawable.som_1,
                        "Photo by Kimiya Oveisi on Unsplash"
                )
        );

// Just image drawable
        list.add(
                new CarouselItem(
                        R.drawable.som_2
                )
        );

// ...

        carousel.setData(list);



        //indicator 클릭이벤트 텟트트
        // Carousel listener
        // 여긴 동작을 안하네...
        carousel.setCarouselListener(new CarouselListener() {
            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup parent) {
                // ...
                return null;
            }

            @Override
            public void onLongClick(int position, @NotNull CarouselItem dataObject) {
                // ...
                Log.e(TAG, "onLongClick: CALL...!!!");
            }

            @Override
            public void onClick(int position, @NotNull CarouselItem carouselItem) {
                // ...
                Log.e(TAG, "onClick: CALL...!!!");
                // Cast the binding to the returned view binding class of the onCreateViewHolder() method.
                // ItemCustomFixedSizeLayout1Binding currentBinding = (ItemCustomFixedSizeLayout1Binding) binding;


                // Do the bindings...
                //currentBinding.imageView.setScaleType(imageScaleType);

                // setImage() is an extension function to load image to an ImageView using CarouselItem object.
                // We need to provide current CarouselItem data and the place holder Drawable or drawable resource id to the function.
                // placeholder parameter is optional.

                // setImage()는 CarouselItem 개체를 사용하여 ImageView에 이미지를 로드하는 확장 기능입니다.
                // 현재 CarouselItem 데이터와 플레이스 홀더 Drawable 또는 Drawable Resource ID를 해당 기능에 제공해야 합니다.
                // 자리 표시자 매개 변수는 선택 사항입니다.
                // Utils.setImage(currentBinding.imageView, carouselItem, R.drawable.ic_wb_cloudy_with_padding);
            }
        });
    }
}