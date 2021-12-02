/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dingbei.diagnose.gallery.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.GalleryFinal;
import com.dingbei.diagnose.gallery.model.PhotoInfo;
import com.dingbei.diagnose.gallery.widget.GFImageView;

import java.util.List;

import cn.finalteam.toolsfinal.adapter.ViewHolderAdapter;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/10 下午4:59
 */
public class PhotoListAdapter extends ViewHolderAdapter<PhotoListAdapter.PhotoViewHolder, PhotoInfo> {

    private List<PhotoInfo> mSelectList;
    private int mScreenWidth;
    private int mRowWidth;

    private Activity mActivity;
    private OnCheckClickListener mListener;

    public PhotoListAdapter(Activity activity, List<PhotoInfo> list, List<PhotoInfo> selectList, int screenWidth, int column) {
        super(activity, list);
        this.mSelectList = selectList;
        this.mScreenWidth = screenWidth;
        this.mRowWidth = mScreenWidth / column;
        this.mActivity = activity;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflate(R.layout.dingbei_gf_adapter_photo_list_item, parent);
        setHeight(view);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        PhotoInfo photoInfo = getDatas().get(position);

        String path = "";
        if (photoInfo != null) {
            path = photoInfo.getPhotoPath();
        }

        holder.mIvThumb.setImageResource(R.drawable.ic_default);
        Drawable defaultDrawable = mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo);
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(mActivity, path, holder.mIvThumb, defaultDrawable, mRowWidth, mRowWidth);

//        holder.mView.setAnimation(null);
//        if (GalleryFinal.getCoreConfig().getAnimation() > 0) {
//            holder.mView.setAnimation(AnimationUtils.loadAnimation(mActivity, GalleryFinal.getCoreConfig().getAnimation()));
//        }

        if ( GalleryFinal.getFunctionConfig().isMutiSelect() ) {
            holder.mIvCheck.setVisibility(View.VISIBLE);
            holder.mIvBg.setVisibility(View.VISIBLE);

            if (contains(photoInfo.getPhotoId())) {
                holder.mIvCheck.setImageResource(R.drawable.ic_gf_done_tick);
                holder.mIvBg.setSelected(true);
            } else {
                holder.mIvCheck.setImageResource(R.drawable.ic_gf_done);
                holder.mIvBg.setSelected(false);
            }

            holder.mIvCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCheckClick(holder, position);
                }
            });

        } else {
            holder.mIvCheck.setVisibility(View.GONE);
            holder.mIvBg.setVisibility(View.GONE);
        }
    }

    private boolean contains(int id){
        for (PhotoInfo info : mSelectList) {
            if(info.getPhotoId() == id) {
                return true;
            }
        }
        return false;
    }

    private void setHeight(final View convertView) {
        int height = mRowWidth - 8;
        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }

    public static class PhotoViewHolder extends ViewHolderAdapter.ViewHolder {

        public GFImageView mIvThumb;
        public ImageView mIvBg;
        public ImageView mIvCheck;
        View mView;

        public PhotoViewHolder(View view) {
            super(view);
            mView = view;
            mIvThumb = (GFImageView) view.findViewById(R.id.iv_thumb);
            mIvBg = (ImageView) view.findViewById(R.id.iv_bg);
            mIvCheck = (ImageView) view.findViewById(R.id.iv_check);
        }
    }

    public interface OnCheckClickListener{
        void onCheckClick(PhotoViewHolder holder, int position);
    }

    public void setOnCheckClickListener(OnCheckClickListener listener){
        this.mListener = listener;
    }
}
