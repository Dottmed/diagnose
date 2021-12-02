package com.dingbei.diagnose.gallery.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.dingbei.diagnose.gallery.widget.zoonview.PhotoView;
import com.dingbei.diagnose.gallery.widget.zoonview.PhotoViewAttacher;
import com.dingbei.diagnose.utils.ImageLoadUtil;

import java.util.ArrayList;
import java.util.List;


public class HackyPagerAdapter extends PagerAdapter {

    private List<PhotoView> mViews;
    private Activity mActivity;
    private ArrayList<String> list;

    public HackyPagerAdapter(Activity activity) {
        mActivity = activity;
        mViews = new ArrayList<>();
    }

    public void setDate(ArrayList<String> list) {
        if (list != null) {
            this.list = list;
            for (int i = 0; i < list.size(); i++) {
                PhotoView photoView = new PhotoView(mActivity);
                photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
//                        mActivity.finish();
                    }
                });
                mViews.add(photoView);
            }
        }
    }

    public void setOnItemLongClickListener(View.OnLongClickListener listener) {
        for (PhotoView view : mViews) {
            view.setOnLongClickListener(listener);
        }
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position), -1, -1);
        ImageLoadUtil.setImageUrl(mActivity, list.get(position), mViews.get(position));
        return mViews.get(position);
    }
}
