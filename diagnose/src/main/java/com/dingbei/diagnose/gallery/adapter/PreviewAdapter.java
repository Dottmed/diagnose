package com.dingbei.diagnose.gallery.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.GalleryFinal;
import com.dingbei.diagnose.gallery.model.PhotoInfo;
import com.dingbei.diagnose.gallery.widget.zoonview.PhotoView;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.toolsfinal.DeviceUtils;

/**
 * @author Dayo
 * @time 2017/6/24 14:53
 * @desc 预览页面adapter
 */

public class PreviewAdapter extends PagerAdapter {

    private final List<PhotoInfo> mList;
    private Activity mActivity;
    private DisplayMetrics mDisplayMetrics;
    private ArrayList<PhotoView> mPhotos;
    private final Drawable mDefaultDrawable;

    public PreviewAdapter(Activity activity, List<PhotoInfo> list) {
        this.mActivity = activity;
        this.mDisplayMetrics = DeviceUtils.getScreenPix(mActivity);
        this.mList = list;
        mDefaultDrawable = mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo);
        mPhotos = new ArrayList();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        PhotoView photo = (PhotoView)object;
        container.removeView(photo);
        //用作缓存
        mPhotos.add(photo);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photo;
        if(mPhotos.size() > 0) {
            //有缓存则从缓存中取出
            photo = mPhotos.get(0);
            mPhotos.remove(0);
        }else {
            photo = new PhotoView(mActivity);
        }

        PhotoInfo photoInfo = mList.get(position);
        String path = photoInfo.getPhotoPath();
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(mActivity, path, photo,
                mDefaultDrawable, mDisplayMetrics.widthPixels/2, mDisplayMetrics.heightPixels/2);

        container.addView(photo, 0);
        return photo;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
