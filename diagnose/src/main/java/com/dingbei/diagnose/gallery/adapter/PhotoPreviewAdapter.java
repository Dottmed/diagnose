package com.dingbei.diagnose.gallery.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.GalleryFinal;
import com.dingbei.diagnose.gallery.model.PhotoInfo;
import com.dingbei.diagnose.gallery.widget.zoonview.PhotoView;

import java.util.List;

import cn.finalteam.toolsfinal.DeviceUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2015/12/29 0029 15:53
 */
public class PhotoPreviewAdapter extends ViewHolderRecyclingPagerAdapter<PhotoPreviewAdapter.PreviewViewHolder, PhotoInfo> {

    private Activity mActivity;
    private DisplayMetrics mDisplayMetrics;

    public PhotoPreviewAdapter(Activity activity, List<PhotoInfo> list) {
        super(activity, list);
        this.mActivity = activity;
        this.mDisplayMetrics = DeviceUtils.getScreenPix(mActivity);
    }

    @Override
    public PreviewViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = getLayoutInflater().inflate(R.layout.dingbei_gf_adapter_preview_viewpgaer_item, null);
        PhotoView imageView = (PhotoView)view.findViewById(R.id.photoView);
        return new PreviewViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(PreviewViewHolder holder, int position) {
        PhotoInfo photoInfo = getDatas().get(position);
        String path = "";
        if (photoInfo != null) {
            path = photoInfo.getPhotoPath();
        }
        holder.mImageView.setImageResource(R.drawable.ic_gf_default_photo);
        Drawable defaultDrawable = mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo);
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(mActivity, path, holder.mImageView, defaultDrawable, mDisplayMetrics.widthPixels/2, mDisplayMetrics.heightPixels/2);
    }

    static class PreviewViewHolder extends ViewHolderRecyclingPagerAdapter.ViewHolder{
        PhotoView mImageView;

        public PreviewViewHolder(View view) {
            super(view);
            mImageView = (PhotoView) view;
        }
    }
}
