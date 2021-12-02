package com.dingbei.diagnose.gallery.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.dingbei.diagnose.gallery.FunctionConfig;
import com.dingbei.diagnose.gallery.GalleryFinal;
import com.dingbei.diagnose.gallery.PhotoSelectActivity;
import com.dingbei.diagnose.gallery.model.PhotoInfo;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * @author Dayo
 * @time 2017/6/2 14:58
 * @desc 相册工具类
 */

public class GalleryUtil {
    private int maxNum;
    private OnResultCallback mCallback;

    public GalleryUtil() {

    }

    /**
     * 选择多张图片
     * @param imgs 当前已选的图片
     * @param maxNum 最大可选数
     * @param callback 回调
     */
    public void selectPhotos(List<String> imgs, int maxNum, OnResultCallback callback) {
        this.maxNum = maxNum;
        mCallback = callback;

        int size = imgs.size();
        //移除空数据
        if (size > 0 && TextUtils.isEmpty(imgs.get(0))) { //刚开始时第一个数据为null
            imgs.remove(0);
            size = imgs.size();
        }
        if (size > 0 && TextUtils.isEmpty(imgs.get(size - 1))) { //最后一个空数据
            imgs.remove(size - 1);
            size = imgs.size();
        }

        if (size < maxNum) {
            //更新已选照片显示数
            PhotoSelectActivity.selected_Photo = size;

            //参数： 1返回状态码是不是选择了图片   2加载相册配置    3相册返回回调
            GalleryFinal.openGalleryMuti(1000,
                    new FunctionConfig.Builder().setMutiSelectMaxSize(maxNum - size).build(),
                    mOnHandlerResultCallback);
        } else {
            ToastUtil.show("最多只能选择" + maxNum + "张", Toast.LENGTH_SHORT);
        }
    }

    public void selectPhotos(int currentSize, int maxNum, OnResultCallback callback) {
        this.maxNum = maxNum;
        mCallback = callback;

        if (currentSize < maxNum) {
            //更新已选照片显示数
            PhotoSelectActivity.selected_Photo = currentSize;

            //参数： 1返回状态码是不是选择了图片   2加载相册配置    3相册返回回调
            GalleryFinal.openGalleryMuti(1000,
                    new FunctionConfig.Builder().setMutiSelectMaxSize(maxNum - currentSize).build(),
                    mOnHandlerResultCallback);
        }
    }

    //调用库的相册的回调
    private GalleryFinal.OnHanlderResultCallback mOnHandlerResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null && reqeustCode == 1000) {
                addPicsFromGallery(resultList);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            mCallback.onFailure(errorMsg);
        }
    };

    private void addPicsFromGallery(final List<PhotoInfo> resultList) {
//        new AsyncTask<Void, Void, List<String>>(){
//            @Override
//            protected List<String> doInBackground(Void... params) { //子线程
//                ArrayList<String> list = new ArrayList<>();
//                int size = resultList.size();
//                for(int i = 0; i < size; i++) {
//                    PhotoInfo photoInfo = resultList.get(i);
//                    String path = zipImage(photoInfo.getPhotoPath()).getPath();
//                    list.add(path);
//                }
//                return list;
//            }
//
//            @Override
//            protected void onPostExecute(List<String> list) { //主线程
//                mCallback.onSuccess(list);
//            }
//        }.execute();

        final ArrayList<String> list = new ArrayList<>();
        Observable.just(resultList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<PhotoInfo>, Observable<PhotoInfo>>() {
                    @Override
                    public Observable<PhotoInfo> apply(List<PhotoInfo> photoInfos) throws Exception {
                        PhotoInfo[] p = new PhotoInfo[photoInfos.size()];
                        return Observable.fromArray(photoInfos.toArray(p));
                    }
                })
                .map(new Function<PhotoInfo, String>() {
                    @Override
                    public String apply(PhotoInfo photoInfo) throws Exception {
                        String s = zipImage(photoInfo.getPhotoPath()).getPath();
                        list.add(s);
                        return s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mCallback.onSuccess(list);
                    }
                });
    }

    /**
     * 压缩图片
     * @param path
     * @return
     */
    public static File zipImage(String path) {
        PhotoOperator operater = PhotoOperator.getInstance();
        File file = null;
        try {
            file = operater.scale(path, 300, 50);
        } catch (Exception e) {
            AppLogger.e(e.getMessage());
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 选择单张图片
     * @param callback
     */
    public void selectSinglePhoto(final OnResultCallback callback){
        GalleryFinal.openGalleryMuti(11, 1, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                addSinglePicsFromGallery(resultList, callback);
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                callback.onFailure(errorMsg);
            }
        });
    }

    private void addSinglePicsFromGallery(final List<PhotoInfo> resultList, final OnResultCallback callback) {
        // TODO: 用rxjava
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) { //子线程
                PhotoInfo photoInfo = resultList.get(0);
                String path = zipImage(photoInfo.getPhotoPath()).getPath();
                return path;
            }

            @Override
            protected void onPostExecute(String path) { //主线程
                ArrayList<String> list = new ArrayList<>();
                list.add(path);
                callback.onSuccess(list);
            }
        }.execute();
    }

    /**
     * 释放资源，防止内存泄漏
     */
    public void release(){
        //GalleryFinal 的 OnHanlderResultCallback 是静态变量, 而OnHanlderResultCallback持有OnResultCallback（即Activity），造成内存泄漏
        GalleryFinal.release();
    }

    /**
     * 回调
     */
    public interface OnResultCallback{
        void onSuccess(List<String> list);

        void onFailure(String errorMsg);
    }

}
