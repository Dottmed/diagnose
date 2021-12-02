package com.dingbei.diagnose.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;


/**
 * @author Dayo
 * @desc 图片加载工具
 */
public class ImageLoadUtil {

    public static void setImageUrl(Context context, String url, ImageView view) {
        Glide.with(context).load(url).into(view);
    }

    public static void setImageUrlOverride(Context context, String url, ImageView view, int width, int height) {
        RequestOptions options = new RequestOptions().override(width, height);
        Glide.with(context).load(url).apply(options).into(view);
    }

    public static void setImageUrlWithPlaceHolder(Context context, String url, ImageView view, int placeHolder) {
        RequestOptions options = new RequestOptions().placeholder(placeHolder).error(placeHolder);
        Glide.with(context).load(url).apply(options).into(view);
    }

    public static void setImageResource(Context context, int imageResId, ImageView view) {
        Glide.with(context).load(imageResId).into(view);
    }

    /**
     * 灰度处理
     */
    public static void setGrayScale(Context context, String url, ImageView view) {
        RequestOptions options = new RequestOptions().transform(new GrayscaleTransformation());
        Glide.with(context).load(url)
                .apply(options)
                .into(view);
    }

    /**
     * 模糊处理
     */
    public static void setBlurImage(Context context, String url, ImageView view) {
        RequestOptions options = new RequestOptions().transform(new BlurTransformation());
        Glide.with(context).load(url)
                .apply(options)
                .into(view);
    }
}