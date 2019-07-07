package com.tiva11.mtfoodrecipes;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;

public final class DataBindingAdapters {
    private static final String TAG = "DataBindingAdapters";
    @BindingAdapter("imageUrl") //No need to add app namespace, it is mandatory only for android namespace
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(
                    new RequestOptions().error(R.drawable.ic_launcher_background)
                )
                .into(view);
    }
    @BindingAdapter("imageCirle") //No need to add app namespace, it is mandatory only for android namespace
    public static void loadImageCircle(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(
                    new RequestOptions().circleCrop().error(R.drawable.ic_launcher_background)
                )
                .into(view);
    }
    @BindingAdapter("android:text")
    public static void setText(TextView view, double number) {
        view.setText(String.valueOf(number));
    }
    @BindingAdapter("android:text")
    public static void setText(TextView view, float number) {
        view.setText(String.valueOf(number));
    }
    @BindingAdapter("android:text")
    public static void setText(TextView view, long number) {
        view.setText(String.valueOf(number));
    }
    @BindingAdapter("android:text")
    public static void setText(TextView view, int number) {
        view.setText(String.valueOf(number));
    }
    @BindingAdapter("android:text")
    public static void setText(TextView view, Date time) {
        view.setText(String.valueOf(time));
    }

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }
}
