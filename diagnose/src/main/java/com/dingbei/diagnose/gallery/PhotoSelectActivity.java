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

package com.dingbei.diagnose.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.adapter.FolderListAdapter;
import com.dingbei.diagnose.gallery.adapter.PhotoListAdapter;
import com.dingbei.diagnose.gallery.model.PhotoFolderInfo;
import com.dingbei.diagnose.gallery.model.PhotoInfo;
import com.dingbei.diagnose.gallery.utils.PhotoTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.finalteam.toolsfinal.DeviceUtils;
import cn.finalteam.toolsfinal.FileUtils;
import cn.finalteam.toolsfinal.StringUtils;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Desction:图片选择器
 * Author:pengjianbo
 * Date:15/10/10 下午3:54
 */
public class PhotoSelectActivity extends PhotoBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private final int HANLDER_TAKE_PHOTO_EVENT = 1000;
    private final int HANDLER_REFRESH_LIST_EVENT = 1002;

    private GridView mGvPhotoList;
    private ListView mLvFolderList;
    private LinearLayout mLlFolderPanel;
    private ImageView mIvTakePhoto;
    private ImageView mIvBack;
    private TextView mTvChooseCount;
    private TextView mTvSubTitle;
    private LinearLayout mLlTitle;
    private TextView mTvEmptyView;
    private RelativeLayout mTitlebar;
    private ImageView mIvFolderArrow;

    private List<PhotoFolderInfo> mAllPhotoFolderList;
    private FolderListAdapter mFolderListAdapter;

    private ArrayList<PhotoInfo> mCurPhotoList;
    private PhotoListAdapter mPhotoListAdapter;

    private FunctionConfig mFunctionConfig;
    private ThemeConfig mThemeConfig;
    //用于第一次选了4张后继续进来选变(0/max)重置的BUG
    public static int selected_Photo;
    //是否需要刷新相册
    private boolean mHasRefreshGallery = false;
    private ArrayList<PhotoInfo> mSelectPhotoList = new ArrayList<>();

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( msg.what == HANLDER_TAKE_PHOTO_EVENT ) {
                PhotoInfo photoInfo = (PhotoInfo) msg.obj;
                takeRefreshGallery(photoInfo);
                refreshSelectCount();
            } else if ( msg.what == HANDLER_REFRESH_LIST_EVENT ){
                refreshSelectCount();
                mPhotoListAdapter.notifyDataSetChanged();
                mFolderListAdapter.notifyDataSetChanged();
                if (mAllPhotoFolderList.get(0).getPhotoList() == null ||
                        mAllPhotoFolderList.get(0).getPhotoList().size() == 0) {
                    mTvEmptyView.setText(R.string.no_photo);
                }

                mGvPhotoList.setEnabled(true);
                mLlTitle.setEnabled(true);
                mIvTakePhoto.setEnabled(true);
            }
        }
    };
    private TextView mTvPreview;
    private TextView mTvClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFunctionConfig = GalleryFinal.getFunctionConfig();
        mThemeConfig = GalleryFinal.getGalleryTheme();

        if ( mFunctionConfig == null || mThemeConfig == null) {
            resultFailure(getString(R.string.please_reopen_gf), true);
        } else {
            setContentView(R.layout.dingbei_gf_activity_photo_select);
            mPhotoTargetFolder = null;

            findViews();
            setListener();

            mAllPhotoFolderList = new ArrayList<>();
            mFolderListAdapter = new FolderListAdapter(this, mAllPhotoFolderList, mFunctionConfig);
            mLvFolderList.setAdapter(mFolderListAdapter);

            int column = 3;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                //如果是横屏
                column = 6;
            }
            mGvPhotoList.setNumColumns(column);

            mCurPhotoList = new ArrayList<>();
            mPhotoListAdapter = new PhotoListAdapter(this, mCurPhotoList, mSelectPhotoList, mScreenWidth, column);
            mPhotoListAdapter.setOnCheckClickListener(new PhotoListAdapter.OnCheckClickListener() {
                @Override
                public void onCheckClick(PhotoListAdapter.PhotoViewHolder holder, int position) {
                    checkClick(holder, position);
                }
            });
            mGvPhotoList.setAdapter(mPhotoListAdapter);

            setTheme();
            mGvPhotoList.setEmptyView(mTvEmptyView);

            // TODO: 返回图片bug
//            if (mFunctionConfig.isCamera()) {
//                mIvTakePhoto.setVisibility(View.VISIBLE);
//            } else {
                mIvTakePhoto.setVisibility(View.GONE);
//            }

            refreshSelectCount();
            requestGalleryPermission();
        }
    }

    private void setTheme() {
        mIvBack.setImageResource(mThemeConfig.getIconBack());
        if (mThemeConfig.getIconBack() == R.drawable.ic_gf_back) {
            mIvBack.setColorFilter(mThemeConfig.getTitleBarIconColor());
        }

        mIvFolderArrow.setImageResource(mThemeConfig.getIconFolderArrow());
        if (mThemeConfig.getIconFolderArrow() == R.drawable.ic_gf_triangle_arrow) {
            mIvFolderArrow.setColorFilter(mThemeConfig.getTitleBarIconColor());
        }

        mIvTakePhoto.setImageResource(mThemeConfig.getIconCamera());
        if (mThemeConfig.getIconCamera() == R.drawable.ic_gf_camera) {
            mIvTakePhoto.setColorFilter(mThemeConfig.getTitleBarIconColor());
        }

        setStatusBarColor(mThemeConfig.getTitleBarBgColor());
        mTitlebar.setBackgroundColor(mThemeConfig.getTitleBarBgColor());
        mTvSubTitle.setTextColor(mThemeConfig.getTitleBarTextColor());
        mTvChooseCount.setTextColor(mThemeConfig.getTitleBarTextColor());
    }

    private void findViews() {
        mGvPhotoList = (GridView) findViewById(R.id.gv_photo_list);
        mLvFolderList = (ListView) findViewById(R.id.lv_folder_list);
        mTvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        mLlFolderPanel = (LinearLayout) findViewById(R.id.ll_folder_panel);
        mIvTakePhoto = (ImageView) findViewById(R.id.iv_take_photo);
        mTvChooseCount = (TextView) findViewById(R.id.tv_choose_count);
        mIvBack = (ImageView) findViewById(R.id.iv_back);

        mTvEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);

        mTvClear = (TextView) findViewById(R.id.tv_clear);
        mTitlebar = (RelativeLayout) findViewById(R.id.titlebar);
        mIvFolderArrow = (ImageView) findViewById(R.id.iv_folder_arrow);

        mTvPreview = (TextView) findViewById(R.id.tv_preview);
    }

    /**
     * 改变状态栏颜色
     * @param color
     */
    public void setStatusBarColor(int color){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        mLlTitle.setOnClickListener(this);
        mIvTakePhoto.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mIvFolderArrow.setOnClickListener(this);

        mLvFolderList.setOnItemClickListener(this);
        mGvPhotoList.setOnItemClickListener(this);

        mTvClear.setOnClickListener(this);

        mTvPreview.setOnClickListener(this);

        mTvChooseCount.setOnClickListener(this);
    }

    protected void deleteSelect(int photoId) {
        try {
            Iterator<PhotoInfo> iterator = mSelectPhotoList.iterator();
            while (iterator.hasNext()) {
                PhotoInfo info = iterator.next();
                if (info != null && info.getPhotoId() == photoId) {
                    iterator.remove();
                }
            }
        } catch (Exception e){}

        refreshAdapter();
    }

    private void refreshAdapter() {
        mHanlder.sendEmptyMessageDelayed(HANDLER_REFRESH_LIST_EVENT, 100);
    }

    protected void takeRefreshGallery(PhotoInfo photoInfo, boolean selected) {
        if (isFinishing() || photoInfo == null) {
            return;
        }

        Message message = mHanlder.obtainMessage();
        message.obj = photoInfo;
        message.what = HANLDER_TAKE_PHOTO_EVENT;
        mSelectPhotoList.add(photoInfo);
        mHanlder.sendMessageDelayed(message, 100);
    }

    /**
     * 解决在5.0手机上刷新Gallery问题，从startActivityForResult回到Activity把数据添加到集合中然后理解跳转到下一个页面，
     * adapter的getCount与list.size不一致，所以我这里用了延迟刷新数据
     * @param photoInfo
     */
    private void takeRefreshGallery(PhotoInfo photoInfo) {
        mCurPhotoList.add(0, photoInfo);
        mPhotoListAdapter.notifyDataSetChanged();

        //添加到集合中
        List<PhotoInfo> photoInfoList = mAllPhotoFolderList.get(0).getPhotoList();
        if (photoInfoList == null) {
            photoInfoList = new ArrayList<>();
        }
        photoInfoList.add(0, photoInfo);
        mAllPhotoFolderList.get(0).setPhotoList(photoInfoList);

        if ( mFolderListAdapter.getSelectFolder() != null ) {
            PhotoFolderInfo photoFolderInfo = mFolderListAdapter.getSelectFolder();
            List<PhotoInfo> list = photoFolderInfo.getPhotoList();
            if ( list == null ) {
                list = new ArrayList<>();
            }
            list.add(0, photoInfo);
            if ( list.size() == 1 ) {
                photoFolderInfo.setCoverPhoto(photoInfo);
            }
            mFolderListAdapter.getSelectFolder().setPhotoList(list);
        } else {
            String folderA = new File(photoInfo.getPhotoPath()).getParent();
            for (int i = 1; i < mAllPhotoFolderList.size(); i++) {
                PhotoFolderInfo folderInfo = mAllPhotoFolderList.get(i);
                String folderB = null;
                if (!StringUtils.isEmpty(photoInfo.getPhotoPath())) {
                    folderB = new File(photoInfo.getPhotoPath()).getParent();
                }
                if (TextUtils.equals(folderA, folderB)) {
                    List<PhotoInfo> list = folderInfo.getPhotoList();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(0, photoInfo);
                    folderInfo.setPhotoList(list);
                    if ( list.size() == 1 ) {
                        folderInfo.setCoverPhoto(photoInfo);
                    }
                }
            }
        }

        mFolderListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void takeResult(PhotoInfo photoInfo) {

        Message message = mHanlder.obtainMessage();
        message.obj = photoInfo;
        message.what = HANLDER_TAKE_PHOTO_EVENT;

        if ( !mFunctionConfig.isMutiSelect() ) { //单选
            mSelectPhotoList.clear();
            mSelectPhotoList.add(photoInfo);

            if ( mFunctionConfig.isEditPhoto() ) {//裁剪
                mHasRefreshGallery = true;
                toPhotoEdit();
            } else {
                ArrayList<PhotoInfo> list = new ArrayList<>();
                list.add(photoInfo);
                resultData(list);
            }

            mHanlder.sendMessageDelayed(message, 100);
        } else {//多选
            mSelectPhotoList.add(photoInfo);
            mHanlder.sendMessageDelayed(message, 100);
        }
    }

    /**
     * 执行裁剪
     */
    protected void toPhotoEdit() {
        Intent intent = new Intent(this, PhotoEditActivity.class);
        intent.putExtra(PhotoEditActivity.SELECT_MAP, mSelectPhotoList);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if ( id == R.id.ll_title || id == R.id.iv_folder_arrow) {
            if ( mLlFolderPanel.getVisibility() == View.VISIBLE ) {
                mLlFolderPanel.setVisibility(View.GONE);
                mLlFolderPanel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.gf_flip_horizontal_out));
            } else {
                mLlFolderPanel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.gf_flip_horizontal_in));
                mLlFolderPanel.setVisibility(View.VISIBLE);
            }
        } else if ( id == R.id.iv_take_photo ) {
            requestCameraPermission();
        } else if ( id == R.id.iv_back ) {
            if ( mLlFolderPanel.getVisibility() == View.VISIBLE ) {
                mLlTitle.performClick();
            } else {
                finish();
            }
        } else if ( id == R.id.tv_choose_count ) {
            if(mSelectPhotoList.size() > 0) {
                if (!mFunctionConfig.isEditPhoto()) {
                    resultData(mSelectPhotoList);
                } else {
                    toPhotoEdit();
                }
            }

        } else if ( id == R.id.tv_clear ) {
            mSelectPhotoList.clear();
            mPhotoListAdapter.notifyDataSetChanged();
            refreshSelectCount();
        } else if ( id == R.id.tv_preview ) {
            Intent intent = new Intent(this, PhotoPreviewActivity.class);
            intent.putExtra(PhotoPreviewActivity.PHOTO_LIST, mSelectPhotoList);
            intent.putExtra(PhotoPreviewActivity.MAX_COUNT, mFunctionConfig.getMaxSize());
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int parentId = parent.getId();
        if ( parentId == R.id.lv_folder_list ) {
            folderItemClick(position);
        } else {
            Intent intent = new Intent(this, PhotoPreviewActivity.class);
            intent.putExtra(PhotoPreviewActivity.PHOTO_LIST, mSelectPhotoList);
            intent.putExtra(PhotoPreviewActivity.ALL_PHOTOS, mCurPhotoList);
            intent.putExtra(PhotoPreviewActivity.CURRENT_POSITION, position);
            intent.putExtra(PhotoPreviewActivity.MAX_COUNT, mFunctionConfig.getMaxSize());
            startActivityForResult(intent, 0);
        }
    }

    private void folderItemClick(int position) {
        mLlFolderPanel.setVisibility(View.GONE);
        mCurPhotoList.clear();
        PhotoFolderInfo photoFolderInfo = mAllPhotoFolderList.get(position);
        if ( photoFolderInfo.getPhotoList() != null ) {
            mCurPhotoList.addAll(photoFolderInfo.getPhotoList());
        }
        mPhotoListAdapter.notifyDataSetChanged();

        if (position == 0) {
            mPhotoTargetFolder = null;
        } else {
            PhotoInfo photoInfo = photoFolderInfo.getCoverPhoto();
            if (photoInfo != null && !StringUtils.isEmpty(photoInfo.getPhotoPath())) {
                mPhotoTargetFolder = new File(photoInfo.getPhotoPath()).getParent();
            } else {
                mPhotoTargetFolder = null;
            }
        }
        mTvSubTitle.setText(photoFolderInfo.getFolderName());
        mFolderListAdapter.setSelectFolder(photoFolderInfo);
        mFolderListAdapter.notifyDataSetChanged();

        if (mCurPhotoList.size() == 0) {
            mTvEmptyView.setText(R.string.no_photo);
        }
    }

    private void checkClick(PhotoListAdapter.PhotoViewHolder holder, int position) {
        PhotoInfo info = mCurPhotoList.get(position);
        if (!mFunctionConfig.isMutiSelect()) {
            mSelectPhotoList.clear();
            mSelectPhotoList.add(info);
            String ext = FileUtils.getFileExtension(info.getPhotoPath());
            if (mFunctionConfig.isEditPhoto() && (ext.equalsIgnoreCase("png")
                    || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
                toPhotoEdit();
            } else {
                ArrayList<PhotoInfo> list = new ArrayList<>();
                list.add(info);
                resultData(list);
            }
            return;
        }
        boolean checked = false;
        if (containsAndRemove(info.getPhotoId())) {
            checked = false;
        } else {
            if (mFunctionConfig.isMutiSelect() && mSelectPhotoList.size() == mFunctionConfig.getMaxSize()) {
                toast(getString(R.string.select_max_tips));
                return;
            } else {
                mSelectPhotoList.add(info);
                checked = true;
            }
        }
        refreshSelectCount();

        if (holder != null) {
            if (checked) {
                holder.mIvCheck.setImageResource(R.drawable.ic_gf_done_tick);
                holder.mIvBg.setSelected(true);
            } else {
                holder.mIvCheck.setImageResource(R.drawable.ic_gf_done);
                holder.mIvBg.setSelected(false);
            }
        } else {
            mPhotoListAdapter.notifyDataSetChanged();
        }
    }

    private boolean containsAndRemove(int photoId){
        try {
            Iterator<PhotoInfo> iterator = mSelectPhotoList.iterator();
            while (iterator.hasNext()) {
                PhotoInfo info = iterator.next();
                if (info != null && info.getPhotoId() == photoId) {
                    iterator.remove();
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public void refreshSelectCount() {
        mTvChooseCount.setText(getString(R.string.send) + "(" + mSelectPhotoList.size() + "/" + mFunctionConfig.getMaxSize() + ")");
        mTvPreview.setText(getString(R.string.preview_photo) + "("+ mSelectPhotoList.size() +")");

        if(mSelectPhotoList.size() > 0) {
            mTvChooseCount.setBackgroundResource(R.drawable.bg_btn_theme_common);
            mTvChooseCount.setClickable(true);
            mTvPreview.setClickable(true);
        }else {
            mTvChooseCount.setBackgroundResource(R.drawable.bg_btn_theme);
            mTvChooseCount.setClickable(false);
            mTvPreview.setClickable(false);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode == GalleryFinal.PERMISSIONS_CODE_GALLERY) {
            getPhotos();
        }
        else if(requestCode == 1001) {
            takePhoto();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        mTvEmptyView.setText(R.string.permissions_denied_tips);
        mIvTakePhoto.setVisibility(View.GONE);
    }

    private void requestCameraPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            takePhoto();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera), 1001, perms);
        }
    }

    private void takePhoto() {
        //判断是否达到多选最大数量
        if (mFunctionConfig.isMutiSelect() && mSelectPhotoList.size() == mFunctionConfig.getMaxSize()) {
            toast(getString(R.string.select_max_tips));
            return;
        }

        if (!DeviceUtils.existSDCard()) {
            toast(getString(R.string.empty_sdcard));
            return;
        }

        takePhotoAction();
    }

    /**
     * 获取所有图片
     */
    private void requestGalleryPermission() {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getPhotos();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.permissions_tips_gallery),
                    GalleryFinal.PERMISSIONS_CODE_GALLERY, perms);
        }
    }

    private void getPhotos() {
        mTvEmptyView.setText(R.string.waiting);
        mGvPhotoList.setEnabled(false);
        mLlTitle.setEnabled(false);
        mIvTakePhoto.setEnabled(false);
        new Thread() {
            @Override
            public void run() {
                super.run();

                mAllPhotoFolderList.clear();
                List<PhotoFolderInfo> allFolderList = PhotoTools.getAllPhotoFolder(PhotoSelectActivity.this, mSelectPhotoList);
                mAllPhotoFolderList.addAll(allFolderList);

                mCurPhotoList.clear();
                if ( allFolderList.size() > 0 ) {
                    if ( allFolderList.get(0).getPhotoList() != null ) {
                        mCurPhotoList.addAll(allFolderList.get(0).getPhotoList());
                    }
                }

                refreshAdapter();
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) {
            if ( mLlFolderPanel.getVisibility() == View.VISIBLE ) {
                mLlTitle.performClick();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( mHasRefreshGallery) {
            mHasRefreshGallery = false;
            requestGalleryPermission();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if ( GalleryFinal.getCoreConfig().getImageLoader() != null ) {
            GalleryFinal.getCoreConfig().getImageLoader().clearMemoryCache();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoTargetFolder = null;
        mSelectPhotoList.clear();
        System.gc();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            mSelectPhotoList.clear();
            mSelectPhotoList.addAll((ArrayList<PhotoInfo>) data.getSerializableExtra(PhotoPreviewActivity.RESULT));
            refreshSelectCount();
            mPhotoListAdapter.notifyDataSetChanged();

            if(resultCode == PhotoPreviewActivity.RESULT_CODE_SEND) {
                if(mSelectPhotoList.size() > 0) {
                    if (!mFunctionConfig.isEditPhoto()) {
                        resultData(mSelectPhotoList);
                    } else {
                        toPhotoEdit();
                    }
                }
            }
        }
    }
}
