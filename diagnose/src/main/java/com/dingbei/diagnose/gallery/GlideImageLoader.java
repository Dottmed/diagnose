package com.dingbei.diagnose.gallery;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dingbei.diagnose.gallery.widget.GFImageView;


/**
 * 相册框架的关于图片加载的重写
 */
public class GlideImageLoader implements ImageLoader {

    public GlideImageLoader() {
    }

    @Override
    public void displayImage(Activity activity, String path, GFImageView imageView, Drawable defaultDrawable, int width, int height) {
        RequestOptions options = new RequestOptions()
                .error(defaultDrawable)
                .override(width, height)
                .skipMemoryCache(true) //不缓存到内存
                .diskCacheStrategy(DiskCacheStrategy.NONE); //不缓存到SD卡

        Glide.with(activity)
                .load("file://" + path)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}
