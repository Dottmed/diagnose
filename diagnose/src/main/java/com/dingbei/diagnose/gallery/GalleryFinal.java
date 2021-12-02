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

import android.content.Intent;
import android.widget.Toast;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.model.PhotoInfo;
import com.dingbei.diagnose.gallery.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.toolsfinal.DeviceUtils;
import cn.finalteam.toolsfinal.FileUtils;
import cn.finalteam.toolsfinal.Logger;
import cn.finalteam.toolsfinal.StringUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/12/2 上午11:05
 */
public class GalleryFinal {
    static final int TAKE_REQUEST_CODE = 1001;

    static final int PERMISSIONS_CODE_CAMERA = 2000;
    static final int PERMISSIONS_CODE_GALLERY = 2001;

    private static FunctionConfig sCurrentFunctionConfig;
    private static FunctionConfig sGlobalFunctionConfig;
    private static ThemeConfig sThemeConfig;
    private static CoreConfig sCoreConfig;

    private static OnHanlderResultCallback sCallback;
    private static int sRequestCode;

    public static void init(CoreConfig coreConfig) {
        sThemeConfig = coreConfig.getThemeConfig();
        sCoreConfig = coreConfig;
        sGlobalFunctionConfig = coreConfig.getFunctionConfig();
        Logger.init("galleryfinal", coreConfig.isDebug());
    }

    public static FunctionConfig copyGlobalFuncationConfig() {
        if ( sGlobalFunctionConfig != null ) {
            return sGlobalFunctionConfig.clone();
        }
        return null;
    }

    public static CoreConfig getCoreConfig() {
        return sCoreConfig;
    }

    public static FunctionConfig getFunctionConfig() {
        return sCurrentFunctionConfig;
    }

    public static ThemeConfig getGalleryTheme() {
        if (sThemeConfig == null) {
            //使用默认配置
            sThemeConfig = ThemeConfig.DEFAULT;
        }
        return sThemeConfig;
    }

    /**
     * 打开Gallery-单选
     * @param requestCode
     * @param callback
     */
    public static void openGallerySingle(int requestCode, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openGallerySingle(requestCode, config, callback);
        } else {
            if(callback != null) {
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            Logger.e("FunctionConfig null");
        }
    }

    /**
     * 打开Gallery-单选
     * @param requestCode
     * @param config
     * @param callback
     */
    public static void openGallerySingle(int requestCode, FunctionConfig config, OnHanlderResultCallback callback) {
        if ( sCoreConfig.getImageLoader() == null ) {
            Logger.e("Please init GalleryFinal.");
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if ( config == null && sGlobalFunctionConfig == null) {
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if (!DeviceUtils.existSDCard()) {
            Toast.makeText(sCoreConfig.getContext(), R.string.empty_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }
        config.mutiSelect = false;
        sRequestCode = requestCode;
        sCallback = callback;
        sCurrentFunctionConfig = config;

        Intent intent = new Intent(sCoreConfig.getContext(), PhotoSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sCoreConfig.getContext().startActivity(intent);
    }

    /**
     * 打开Gallery-
     * @param requestCode
     * @param maxSize
     * @param callback
     */
    public static void openGalleryMuti(int requestCode, int maxSize, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            config.maxSize = maxSize;
            openGalleryMuti(requestCode, config, callback);
        } else {
            if(callback != null) {
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            Logger.e("Please init GalleryFinal.");
        }
    }

    /**
     * 打开Gallery-多选
     * @param requestCode
     * @param config
     * @param callback
     */
    public static void openGalleryMuti(int requestCode, FunctionConfig config, OnHanlderResultCallback callback) {
        if ( sCoreConfig.getImageLoader() == null ) {
            Logger.e("Please init GalleryFinal.");
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if ( config == null && sGlobalFunctionConfig == null) {
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if (!DeviceUtils.existSDCard()) {
            Toast.makeText(sCoreConfig.getContext(), R.string.empty_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }
        sRequestCode = requestCode;
        sCallback = callback;
        sCurrentFunctionConfig = config;

        config.mutiSelect = true;

        Intent intent = new Intent(sCoreConfig.getContext(), PhotoSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sCoreConfig.getContext().startActivity(intent);
    }


    /**
     * 打开相机
     * @param requestCode
     * @param callback
     */
    public static void openCamera(int requestCode, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openCamera(requestCode, config, callback);
        } else {
            if(callback != null) {
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            Logger.e("Please init GalleryFinal.");
        }
    }

    /**
     * 打开相机
     * @param config
     * @param callback
     */
    public static void openCamera(int requestCode, FunctionConfig config, OnHanlderResultCallback callback) {
        if ( sCoreConfig.getImageLoader() == null ) {
            Logger.e("Please init GalleryFinal.");
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if ( config == null && sGlobalFunctionConfig == null) {
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if (!DeviceUtils.existSDCard()) {
            Toast.makeText(sCoreConfig.getContext(), R.string.empty_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }
        sRequestCode = requestCode;
        sCallback = callback;

        config.mutiSelect = false;//拍照为单选
        sCurrentFunctionConfig = config;

        Intent intent = new Intent(sCoreConfig.getContext(), PhotoEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PhotoEditActivity.TAKE_PHOTO_ACTION, true);
        sCoreConfig.getContext().startActivity(intent);
    }

    /**
     * 打开裁剪
     * @param requestCode
     * @param photoPath
     * @param callback
     */
    public static void openCrop(int requestCode, String photoPath, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openCrop(requestCode, config, photoPath, callback);
        } else {
            if(callback != null) {
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            Logger.e("Please init GalleryFinal.");
        }
    }

    /**
     * 打开裁剪
     * @param requestCode
     * @param config
     * @param photoPath
     * @param callback
     */
    public static void openCrop(int requestCode, FunctionConfig config, String photoPath, OnHanlderResultCallback callback) {
        if ( sCoreConfig.getImageLoader() == null ) {
            Logger.e("Please init GalleryFinal.");
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if ( config == null && sGlobalFunctionConfig == null) {
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if (!DeviceUtils.existSDCard()) {
            Toast.makeText(sCoreConfig.getContext(), R.string.empty_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }

        if ( config == null || StringUtils.isEmpty(photoPath) || !new File(photoPath).exists()) {
            Logger.d("config为空或文件不存在");
            return;
        }
        sRequestCode = requestCode;
        sCallback = callback;

        //必须设置这个三个选项
        config.mutiSelect = false;//拍照为单选
        config.editPhoto = true;
        config.crop = true;

        sCurrentFunctionConfig = config;
        HashMap<String, PhotoInfo> map = new HashMap<>();
        PhotoInfo photoInfo = new PhotoInfo();
        photoInfo.setPhotoPath(photoPath);
        photoInfo.setPhotoId(Utils.getRandom(10000, 99999));
        map.put(photoPath, photoInfo);
        Intent intent = new Intent(sCoreConfig.getContext(), PhotoEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PhotoEditActivity.CROP_PHOTO_ACTION, true);
        intent.putExtra(PhotoEditActivity.SELECT_MAP, map);
        sCoreConfig.getContext().startActivity(intent);
    }

    /**
     * 打开编辑
     * @param requestCode
     * @param photoPath
     * @param callback
     */
    public static void openEdit(int requestCode, String photoPath, OnHanlderResultCallback callback) {
        FunctionConfig config = copyGlobalFuncationConfig();
        if (config != null) {
            openEdit(requestCode, config, photoPath, callback);
        } else {
            if(callback != null) {
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            Logger.e("Please init GalleryFinal.");
        }
    }

    /**
     * 打开编辑
     * @param requestCode
     * @param config
     * @param photoPath
     * @param callback
     */
    public static void openEdit(int requestCode, FunctionConfig config, String photoPath, OnHanlderResultCallback callback) {
        if ( sCoreConfig.getImageLoader() == null ) {
            Logger.e("Please init GalleryFinal.");
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if ( config == null && sGlobalFunctionConfig == null) {
            if(callback != null){
                callback.onHanlderFailure(requestCode, sCoreConfig.getContext().getString(R.string.open_gallery_fail));
            }
            return;
        }

        if (!DeviceUtils.existSDCard()) {
            Toast.makeText(sCoreConfig.getContext(), R.string.empty_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }

        if ( config == null || StringUtils.isEmpty(photoPath) || !new File(photoPath).exists()) {
            Logger.d("config为空或文件不存在");
            return;
        }
        sRequestCode = requestCode;
        sCallback = callback;

        config.mutiSelect = false;//拍照为单选

        sCurrentFunctionConfig = config;
        HashMap<String, PhotoInfo> map = new HashMap<>();
        PhotoInfo photoInfo = new PhotoInfo();
        photoInfo.setPhotoPath(photoPath);
        photoInfo.setPhotoId(Utils.getRandom(10000, 99999));
        map.put(photoPath, photoInfo);
        Intent intent = new Intent(sCoreConfig.getContext(), PhotoEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PhotoEditActivity.EDIT_PHOTO_ACTION, true);
        intent.putExtra(PhotoEditActivity.SELECT_MAP, map);
        sCoreConfig.getContext().startActivity(intent);
    }

    /**
     * 清楚缓存文件
     */
    public static void cleanCacheFile() {
        if (sCurrentFunctionConfig != null && sCoreConfig.getEditPhotoCacheFolder() != null) {
            //清楚裁剪冗余图片
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    FileUtils.deleteFile(sCoreConfig.getEditPhotoCacheFolder());
                }
            }.start();
        }
    }

    public static int getRequestCode() {
        return sRequestCode;
    }

    public static OnHanlderResultCallback getCallback() {
        return sCallback;
    }

    public static void release(){
        sCallback = null;
    }

    /**
     * 处理结果
     */
    public interface OnHanlderResultCallback {
        /**
         * 处理成功
         * @param reqeustCode
         * @param resultList
         */
        void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList);

        /**
         * 处理失败或异常
         * @param requestCode
         * @param errorMsg
         */
        void onHanlderFailure(int requestCode, String errorMsg);
    }
}
