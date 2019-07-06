package com.tiva11.mtfoodrecipes;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public final class DataBindingAdapters {
    private static final String TAG = "DataBindingAdapters";
    @BindingAdapter("imageUrl") //No need to add app namespace, it is mandatory only for android namespace
    public static void loadImage(ImageView view, String imageUrl) {
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

}
