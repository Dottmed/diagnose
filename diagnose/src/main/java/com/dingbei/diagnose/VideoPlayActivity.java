package com.dingbei.diagnose;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingbei.diagnose.utils.ImageLoadUtil;
import com.dingbei.diagnose.utils.ToastUtil;
import com.dingbei.diagnose.view.MyVideoPlayer;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Dayo
 * @time 2017/11/3 16:55
 * @desc 播放视频页
 */

public class VideoPlayActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private MyVideoPlayer mVideoPlayer;
    private String mVideoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dingbei_activity_video_play);
        setStatusBarColor(getResources().getColor(R.color.status_bar));
        initView();
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

    private void initView() {
        ImageView back = findViewById(R.id.img_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //录制视频后播放视频，不需要显示下载按钮，右上角显示确认用于返回结果
        boolean canDownload = getIntent().getBooleanExtra("canDownload", true);

        TextView tx_confirm = findViewById(R.id.txt_right);
        tx_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("path", mVideoPath)
                        .putExtra("duration", String.valueOf(mVideoPlayer.getDuration())));
                finish();
            }
        });
        tx_confirm.setVisibility(canDownload ? View.GONE : View.VISIBLE);

        mVideoPath = getIntent().getStringExtra("path");
        mVideoPlayer = findViewById(R.id.video_player);
        mVideoPlayer.setDownloadEnable(this, canDownload); //普通的播放视频需要显示下载按钮
        mVideoPlayer.setUp(mVideoPath, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, "");
        ImageLoadUtil.setImageUrl(this, mVideoPath, mVideoPlayer.thumbImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.startVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) { //释放资源
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    private void downloadVideo() {
//        FileUtil.downloadVideo(this, mVideoPath);
    }

    public void requestStoragePermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            downloadVideo();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_storage), 11, perms);
        }
    }

    /**
     * 处理权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode == 11) {
            downloadVideo();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtil.show(getString(R.string.permission_denied), Toast.LENGTH_SHORT);
    }

}
