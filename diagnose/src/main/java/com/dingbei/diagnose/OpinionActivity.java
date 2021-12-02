package com.dingbei.diagnose;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.bean.OpinionDetailBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpParams;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.view.recyc.CommonAdapter;

import java.util.ArrayList;


/**
 * @author Dayo
 * @desc 远程会诊给诊断意见
 */

public class OpinionActivity extends BasePostActivity implements View.OnClickListener {

    private static final int MAX_PIC_SIZE = 9;
    private TextView mEd_desc;
    private View mLy_voice;
    private TextView mTx_second;
    private View mLy_pictures;
    private RecyclerView mRecycler_pictures;
    private View mLy_video;
    private ImageView mImg_video_cover;
    private ArrayList<String> mPicList;
    private CommonAdapter<String> mPicAdapter;
    private String mRecordPath;
    private String mVideoPath;
    private boolean mIsPosting;
    private String mOpinionId;
    private View mImg_delete_video;
    private View mImg_delete_voice;

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_opinion;
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        mOpinionId = intent.getStringExtra("opinion_id");

//        init();
        initView();
        getData();
    }

    private void initView() {
        setBack();
        setTitle("意见详情");

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View ly_content = findViewById(R.id.ly_content);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ly_content.getLayoutParams();
            layoutParams.width = dm.widthPixels / 2;
        }

        mEd_desc = findViewById(R.id.ed_desc);

//        //语音
//        mLy_voice = findViewById(R.id.ly_voice);
//        final ImageView img_play = findViewById(R.id.img_voice_play);
//        img_play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playRecord(mRecordPath, img_play);
//            }
//        });
//        mTx_second = findViewById(R.id.tx_second);
//        mImg_delete_voice = findViewById(R.id.img_delete_voice);
//        mImg_delete_voice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRecordPath = "";
//                mLy_voice.setVisibility(View.GONE);
//            }
//        });
//
//        //图片
//        mLy_pictures = findViewById(R.id.ly_pictures);
//        mRecycler_pictures = findViewById(R.id.recycler_pictures);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mRecycler_pictures.setLayoutManager(linearLayoutManager);
//        mRecycler_pictures.setHasFixedSize(false);
//        setPicAdapter();
//
//        //视频
//        mLy_video = findViewById(R.id.ly_video);
//        mImg_video_cover = findViewById(R.id.img_video_cover);
//        mImg_delete_video = findViewById(R.id.img_delete_video);
//        mImg_delete_video.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mVideoPath = "";
//                mLy_video.setVisibility(View.GONE);
//            }
//        });
//        findViewById(R.id.ly_video_play).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!TextUtils.isEmpty(mVideoPath)) {
//                    playVideo(mVideoPath, false);
//                }
//            }
//        });
//
//        findViewById(R.id.ly_media).setVisibility(mIsDetail ? View.GONE : View.VISIBLE);
//        findViewById(R.id.tx_pictures).setOnClickListener(this);
//        findViewById(R.id.tx_take_photo).setOnClickListener(this);
//        findViewById(R.id.tx_voice).setOnClickListener(this);
//        findViewById(R.id.tx_video).setOnClickListener(this);
    }

    private void setPicAdapter() {
//        mPicList = new ArrayList<>();
//        mPicAdapter = new CommonAdapter<String>(this, R.layout.item_add_file_pic, mPicList) {
//            @Override
//            protected void convert(ViewHolder holder, String path, final int position) {
//                holder.setImageUrl(R.id.img_pic, path);
//
//                holder.setVisible(R.id.img_delete, !mIsDetail);
//                holder.setOnClickListener(R.id.img_delete, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mPicList.remove(position);
//                        mPicAdapter.notifyDataSetChanged();
//                        mLy_pictures.setVisibility(mPicList.size() > 0 ? View.VISIBLE : View.GONE);
//                    }
//                });
//
//                holder.setOnClickListener(R.id.ly_item, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        viewPictures(mPicList, position);
//                    }
//                });
//            }
//        };
//        mRecycler_pictures.setAdapter(mPicAdapter);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.txt_right :
//                addOption();
//                break;
//            case R.id.tx_pictures : //相册
//                if(mPicList.size() >= MAX_PIC_SIZE) {
//                    showMsg(String.format(getString(R.string.max_pictures), MAX_PIC_SIZE));
//                }else {
//                    selectPictures(mPicList.size(), MAX_PIC_SIZE, new SelectPicturesCallback() {
//                        @Override
//                        public void callback(List<String> list) {
//                            if(list != null && list.size() > 0) {
//                                mPicList.addAll(list);
//                                mPicAdapter.notifyDataSetChanged();
//                                mLy_pictures.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    });
//                }
//                break;
//            case R.id.tx_take_photo : //拍照
//                if(mPicList.size() >= MAX_PIC_SIZE) {
//                    showMsg(String.format(getString(R.string.max_pictures), MAX_PIC_SIZE));
//                }else {
//                    takePhoto(new TakePhotoCallback() {
//                        @Override
//                        public void callback(String path) {
//                            if(!TextUtils.isEmpty(path)) {
//                                mPicList.add(path);
//                                mPicAdapter.notifyDataSetChanged();
//                                mLy_pictures.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    });
//                }
//                break;
//            case R.id.tx_voice : //语音
//                record(mLy_voice, new RecordCallback() {
//                    @Override
//                    public void onStart() {
//
//                    }
//
//                    @Override
//                    public void callback(String path, int duration) {
//                        if(!TextUtils.isEmpty(path)) {
//                            mRecordPath = path;
//                            mTx_second.setText(duration + "''");
//                            mLy_voice.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
//                break;
//            case R.id.tx_video : //视频
//                shootVideo(new ShootVideoCallback() {
//                    @Override
//                    public void callback(String path, String duration) {
//                        if(!TextUtils.isEmpty(path)) {
//                            mVideoPath = path;
//                            ImageLoadUtil.setImageUrl(RemoteOpinionActivity.this, path, mImg_video_cover);
//                            mLy_video.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
//                break;
//        }
    }

    private void getData() {
        HttpParams params = new HttpParams();
        params.put("id", mOpinionId);
        HttpUtil.get("diagnosis/detail_opinion/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                OpinionDetailBean bean = JSONObject.parseObject(json, OpinionDetailBean.class);
                setData(bean);
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

    private void setData(OpinionDetailBean bean) {
        mEd_desc.setText(bean.getDesc());

//        String audio = bean.getAudio();
//        if(!TextUtils.isEmpty(audio)) {
//            mRecordPath = audio;
//            mImg_delete_voice.setVisibility(View.GONE);
//            String duration = FileUtil.getFileInfo(audio);
//            if(!TextUtils.isEmpty(duration)) {
//                duration = String.format("%s''", duration);
//            }
//            mTx_second.setText(duration);
//            mLy_voice.setVisibility(View.VISIBLE);
//        }
//
//        List<String> images = bean.getImages();
//        if(images != null && images.size() > 0) {
//            mPicList.addAll(images);
//            mPicAdapter.notifyDataSetChanged();
//            mLy_pictures.setVisibility(View.VISIBLE);
//        }
//
//        String video = bean.getVideo();
//        if(!TextUtils.isEmpty(video)) {
//            mVideoPath = video;
//            mImg_delete_video.setVisibility(View.GONE);
//            ImageLoadUtil.setImageUrl(OpinionActivity.this, video, mImg_video_cover);
//            mLy_video.setVisibility(View.VISIBLE);
//        }
    }

}
