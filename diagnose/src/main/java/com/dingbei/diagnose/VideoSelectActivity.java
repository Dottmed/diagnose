package com.dingbei.diagnose;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.dingbei.diagnose.bean.VideoInfoBean;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.ImageLoadUtil;
import com.dingbei.diagnose.utils.PhoneUtil;
import com.dingbei.diagnose.utils.ToastUtil;
import com.dingbei.diagnose.view.recyc.CommonAdapter;
import com.dingbei.diagnose.view.recyc.ViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.toolsfinal.DeviceUtils;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * 选择视频页面
 */

public class VideoSelectActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private List<VideoInfoBean> mListData = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CommonAdapter<VideoInfoBean> mAdapter;
    private int mItemHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dingbei_activity_video_select);
        initView();
    }

    private void setStatusBarColor(int color){
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

    private void initView() {
        setStatusBarColor(getResources().getColor(R.color.status_bar));

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        int column = 3;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //如果是横屏
            column = 6;
        }
        DisplayMetrics dm = DeviceUtils.getScreenPix(this);
        mItemHeight = dm.widthPixels / column;

        GridLayoutManager layoutManager = new GridLayoutManager(this, column);
        int padding = (int) (PhoneUtil.getScreenRate(this) * 4);
        mRecyclerView.addItemDecoration(new VideoItemDecoration(padding, column));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mListData.add(new VideoInfoBean());//默认第一个为拍摄

        requestStoragePermission();
    }

    public void getExternalVideoList(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        int vidsCount = 0;
        if (c != null) {
            vidsCount = c.getCount();

            while (c.moveToNext()) {
                AppLogger.i("VIDEO" + vidsCount);
                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

//                if (duration < 11000){ //只能选择10s内的视频
                    mListData.add(new VideoInfoBean(id, duration, path));
//                }
            }

            c.close();
        }

        setData();
    }

    private void setData() {
        mAdapter = new CommonAdapter<VideoInfoBean>(this, R.layout.dingbei_item_select_video, mListData) {
            @Override
            protected void convert(ViewHolder holder, final VideoInfoBean videoInfoBean, final int position) {
                ImageView imageView = holder.getView(R.id.img);
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.height = mItemHeight;

                if (position == 0) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    holder.setImageResource(R.id.img, R.drawable.ic_video);
                    holder.setVisible(R.id.text_duration, false);
                } else {
                    holder.setVisible(R.id.text_duration, true);
                    holder.setText(R.id.text_duration, millisToMMSS(videoInfoBean.getDuration()));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ImageLoadUtil.setImageUrl(VideoSelectActivity.this, videoInfoBean.getPath(), imageView);
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0) {
                            requestVideoPermission();
                        } else {
                            String path = videoInfoBean.getPath();
                            if (new File(path).exists()) {
                                startActivityForResult(new Intent(VideoSelectActivity.this, VideoPlayActivity.class).putExtra("path", path)
                                        .putExtra("canDownload", false), 11);
                            } else {
                                ToastUtil.show("无法播放", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    private void requestStoragePermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getExternalVideoList(this);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_storage),
                    2000, perms);
        }
    }

    private void requestVideoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            toRecord();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_media),
                    2001, perms);
        }
    }

    private String millisToMMSS(long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(millis);
    }

    private void toRecord() {
        if (Camera.getNumberOfCameras() > 0) {
            startActivityForResult(new Intent(VideoSelectActivity.this, VideoRecordActivity.class), 11);
        }else {
            ToastUtil.show("此设备无可用摄像头", Toast.LENGTH_SHORT);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, new Intent().putExtra("path", data.getStringExtra("path")).putExtra("duration", data.getStringExtra("duration")));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case 2000 :
                getExternalVideoList(this);
                break;
            case 2001 :
                toRecord();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtil.show(getString(R.string.permission_denied), Toast.LENGTH_SHORT);
    }


    public static class VideoItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private int count;

        public VideoItemDecoration(int space, int count) {
            this.space = space;
            this.count = count;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if (position % count == 0) {
                outRect.left = space;
                outRect.right = space / 2;
            }
            else if (position % count == count - 1) {
                outRect.left = space / 2;
                outRect.right = space;
            }
            else {
                outRect.left = space / 2;
                outRect.right = space / 2;
            }
            outRect.bottom = space;
        }
    }
}
