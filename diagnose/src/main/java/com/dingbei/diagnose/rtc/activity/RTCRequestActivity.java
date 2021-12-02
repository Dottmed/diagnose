package com.dingbei.diagnose.rtc.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.BaseAutoActivity;
import com.dingbei.diagnose.R;
import com.dingbei.diagnose.bean.RtcRoomBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.BaseHttp;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.message.MessageEvent;
import com.dingbei.diagnose.message.MessageType;
import com.dingbei.diagnose.utils.ImageLoadUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Dayo
 * @time 2018/8/30 9:59
 * @desc 请求会话页
 */

public class RTCRequestActivity extends BaseAutoActivity implements View.OnClickListener {

    private String mRoom;
    private String mPatientId;
    private String mOwnerAvatar;
    private String mOwnerName;
    private MediaPlayer mPlayer;
    private RtcRoomBean mRtcRoomBean;
    private Handler mHandler;

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_rtc_request;
    }

    @Override
    public void setStatusBarColor(int color) {
        View decorView = getWindow().getDecorView();
        //使用状态栏沉浸式
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);

        //设置透明状态栏
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //透明底部导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        mRoom = intent.getStringExtra("room");
        mPatientId = intent.getStringExtra("patient");
        mOwnerAvatar = intent.getStringExtra("owner_avatar");
        mOwnerName = intent.getStringExtra("owner_name");

        initView();
        playSound();

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //20s不接入自动挂断
                rejectRTC();
            }
        }, 20000);
    }

    private void initView() {
        ImageView img_avatar = findViewById(R.id.img_avatar);
        ImageLoadUtil.setImageUrl(this, mOwnerAvatar, img_avatar);
        TextView tx_name = findViewById(R.id.tx_name);
        tx_name.setText(mOwnerName);

        findViewById(R.id.tx_reject).setOnClickListener(this);
        findViewById(R.id.tx_accept).setOnClickListener(this);
    }

    private void playSound() {
        mPlayer = MediaPlayer.create(this, R.raw.videocall); //播放本地的声音
        mPlayer.setLooping(true); //循环播放
        mPlayer.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tx_reject) {
            rejectRTC();
        }
        else if(id == R.id.tx_accept) {
            getToken();
        }
    }

    private void rejectRTC() {
        String url = BaseHttp.HOST + "rtc/reject/?room=" + mRoom;
        HttpUtil.get(url, null, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                finish();
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

    private void getToken() {
        String url = BaseHttp.HOST + "rtc/get_token/?room=" + mRoom;
        HttpUtil.get(url, null, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                mRtcRoomBean = JSONObject.parseObject(json, RtcRoomBean.class);
                requestPermission();
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
                finish();
            }
        });
    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            joinRoom();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_audio), 0x101, perms);
        }
    }

    private void joinRoom() {
        Intent intent = new Intent(this, RTCRoomActivity.class);
        intent.putExtra(RTCRoomActivity.EXTRA_ROOM, mRtcRoomBean);
        intent.putExtra(RTCRoomActivity.EXTRA_ROOM_ID, mRoom);
        intent.putExtra(RTCRoomActivity.EXTRA_USER_ID, mPatientId);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        joinRoom();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        showMsg(getString(R.string.permission_denied));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent message) {
        //退出rtc
        if (MessageType.RTC_CANCEL_BY_LAUNCHER.equals(message.getMessage())){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        mHandler.removeCallbacksAndMessages(null);

        //释放资源
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        super.onDestroy();
    }
}
