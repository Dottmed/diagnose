package com.dingbei.diagnose.rtc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.dingbei.diagnose.BaseAutoActivity;
import com.dingbei.diagnose.R;
import com.dingbei.diagnose.bean.RtcRoomBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpParams;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.message.MessageEvent;
import com.dingbei.diagnose.message.MessageType;
import com.dingbei.diagnose.rtc.ui.TRTCVideoViewLayout;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;



/**
 * Module:   RTCRoomActivity
 *
 * Function: 使用TRTC SDK完成 1v1 和 1vn 的视频通话功能
 *
 *    1. 支持九宫格平铺和前后叠加两种不同的视频画面布局方式，该部分由 TRTCVideoViewLayout 来计算每个视频画面的位置排布和大小尺寸
 *
 *    2. 支持对视频通话的分辨率、帧率和流畅模式进行调整，该部分由 TRTCSettingDialog 来实现
 *
 *    3. 创建或者加入某一个通话房间，需要先指定 roomId 和 userId，这部分由 TRTCNewActivity 来实现
 */
public class RTCRoomActivity extends BaseAutoActivity implements View.OnClickListener {

    public static boolean isRunning = false;

    private final static String TAG = "RTCRoomActivity";

    private boolean bFrontCamera = true, bMicMute = false, bLoudspeakerMute = false, bCameraMute = false;

    private ImageView ivSwitch, ivMic;
    private TRTCVideoViewLayout mVideoViewLayout;

    private TRTCCloudDef.TRTCParams trtcParams; /// TRTC SDK 视频通话房间进入所必须的参数
    private TRTCCloud trtcCloud;                /// TRTC SDK 实例对象
    private TRTCCloudListener trtcListener;     /// TRTC SDK 回调监听
    private ImageView ivLoudspeaker;
    private ImageView ivCamera;

    public static final String EXTRA_ROOM = "ROOM";
    public static final String EXTRA_ROOM_ID = "ROOM_ID";
    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_LAUNCH = "LAUNCH"; //是否自己发起的
    private RtcRoomBean mRtcRoomBean;
    private boolean mLaunch; //是否自己发起的
    private boolean mHasStart; //是否已开始（其他人已连接过）
    private boolean mIsFloating; //悬浮窗是否在显示
    private String mRoomID;
    private String mSelfUserId;
    private TXCloudVideoView mLocalVideoView;
    private Chronometer mTimer;
    private Handler mHandler;

    @Override
    protected int setContentViewID() {
        //应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return R.layout.dingbei_activity_rtc_room;
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        //获取前一个页面得到的进房参数
        Intent intent = getIntent();
        mRtcRoomBean = (RtcRoomBean) intent.getSerializableExtra(EXTRA_ROOM);
        mRoomID = intent.getStringExtra(EXTRA_ROOM_ID); //rtc的room_id是int，没有hz
        mSelfUserId = intent.getStringExtra(EXTRA_USER_ID);
        mLaunch = intent.getBooleanExtra(EXTRA_LAUNCH, false);

        //初始化 UI 控件
        initView();

        //获取 TRTC SDK 单例
        trtcListener = new TRTCCloudListenerImpl(this);
        trtcCloud = TRTCCloud.sharedInstance(this);
        trtcCloud.setListener(trtcListener);
        //关闭打印log
        TRTCCloud.setConsoleEnabled(false);

        //开始进入视频通话房间
        enterRoom();

        //20s后不接入提示
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoViewLayout.timeout();
            }
        }, 20000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        mHandler.removeCallbacksAndMessages(null);

        if (trtcCloud != null) {
            //取消SDK回调
            trtcCloud.setListener(null);
        }
        trtcCloud = null;
        TRTCCloud.destroySharedInstance();

        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent message) {
        //其他人拒绝rtc
        if (MessageType.RTC_REJECT.equals(message.getMessage())){
            //移除窗体
            mVideoViewLayout.onMemberLeave(message.getExtra());
            autoLeave();
        }
    }

    @Override
    public void onBackPressed() {
        exitRoom();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.iv_switch) {
            onSwitchCamera();
        }
        else if(id == R.id.iv_camera) {
            onMuteCamera();
        }
        else if(id == R.id.iv_mic) {
            onMuteAudio();
        }
        else if(id == R.id.iv_loudspeaker) {
            onMuteLoudspeaker();
        }
        else if(id == R.id.iv_hang_up) {
            exitRoom();
        }
        else if(id == R.id.iv_float) {
            startFloatingVideoService();
        }
    }

    public void startFloatingVideoService() {
//        if (FloatingWindowService.isStarted) {
//            return;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            showMsg(getString(R.string.permission_alert_window));
//            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 2);
//        } else {
//            startVideoService();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//                showMsg(getString(R.string.permission_denied));
//            } else {
//                showMsg(getString(R.string.permission_granted));
//            }
        }
    }

    private void startVideoService() {
//        //开启服务显示悬浮框
//        Intent intent = new Intent(this, FloatingWindowService.class);
//        startService(intent);
//        mIsFloating = true;
//
//        //最小化Activity
//        moveTaskToBack(true);
    }

    private void stopVideoService() {
//        //回到前台时关闭服务
//        Intent intent = new Intent(this, FloatingWindowService.class);
//        stopService(intent);
//        mIsFloating = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stopVideoService();
    }

    private void finishActivity() {
        finish();
    }

    /**
     * 设置视频通话的视频参数：需要 TRTCSettingDialog 提供的分辨率、帧率和流畅模式等参数
     */
    private void setTRTCCloudParam() {
        // 大画面的编码器参数设置
        // 设置视频编码参数，包括分辨率、帧率、码率等等
        // 注意（1）：不要在码率很低的情况下设置很高的分辨率，会出现较大的马赛克
        // 注意（2）：不要设置超过25FPS以上的帧率，因为电影才使用24FPS，我们一般推荐15FPS，这样能将更多的码率分配给画质
        TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
//        encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
        encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_480_480;
        encParam.videoFps = 15;
        encParam.videoBitrate = 450;
        encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        trtcCloud.setVideoEncoderParam(encParam);

        TRTCCloudDef.TRTCNetworkQosParam qosParam = new TRTCCloudDef.TRTCNetworkQosParam();
        qosParam.controlMode = TRTCCloudDef.VIDEO_QOS_CONTROL_SERVER;
        qosParam.preference = TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_SMOOTH;
        trtcCloud.setNetworkQosParam(qosParam);

        //小画面的编码器参数设置
        //TRTC SDK 支持大小两路画面的同时编码和传输，这样网速不理想的用户可以选择观看小画面
        //注意：iPhone & Android 不要开启大小双路画面，非常浪费流量，大小路画面适合 Windows 和 MAC 这样的有线网络环境
        TRTCCloudDef.TRTCVideoEncParam smallParam = new TRTCCloudDef.TRTCVideoEncParam();
        smallParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_160_90;
        smallParam.videoFps = 15;
        smallParam.videoBitrate = 100;
        smallParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        trtcCloud.enableEncSmallVideoStream(false, smallParam);

        trtcCloud.setPriorRemoteVideoStreamType(TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
    }

    /**
     * 加入视频房间：需要 TRTCNewViewActivity 提供的  TRTCParams 函数
     */
    private void enterRoom() {
        trtcParams = new TRTCCloudDef.TRTCParams(mRtcRoomBean.getAppid(), mSelfUserId,
                mRtcRoomBean.getToken(), mRtcRoomBean.getRoom_id(), "", "");

        // 预览前配置默认参数
        setTRTCCloudParam();

        // 开启视频采集预览
        mLocalVideoView = mVideoViewLayout.getLocalVideoView();
        mLocalVideoView.setUserId(trtcParams.userId);
        mLocalVideoView.setVisibility(View.VISIBLE);
        mLocalVideoView.setEnabled(true);
        trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
        trtcCloud.startLocalPreview(true, mLocalVideoView);
        trtcCloud.startLocalAudio();

        // 进房
        trtcCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
    }

    /**
     * 退出视频房间
     */
    private void exitRoom() {
        if(mLaunch && !mHasStart) {
            //自己发起且其他人还没接入时，可取消
            rtcCancel();
        }else {
            //关闭rtc
            rtcClose();
        }
    }

    private void autoLeave() {
        if(mVideoViewLayout.getCount() <= 1) {
            if(mIsFloating) {
                //关闭悬浮窗
                stopVideoService();
            }
            exitRoom();
        }
    }

    private void disconnect() {
        if (trtcCloud != null) {
            trtcCloud.exitRoom();
        }
        finish();
    }

    private void rtcClose() {
        HttpParams params = new HttpParams();
        params.put("room", mRoomID);
        HttpUtil.get("rtc/close/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                disconnect();
            }

            @Override
            public void onError(ErrorBean error) {
                disconnect();
            }
        });
    }

    private void rtcCancel() {
        HttpParams params = new HttpParams();
        params.put("room", mRoomID);
        HttpUtil.get("rtc/cancel/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                disconnect();
            }

            @Override
            public void onError(ErrorBean error) {
                disconnect();
            }
        });
    }

    /**
     * 初始化界面控件，包括主要的视频显示View，以及底部的一排功能按钮
     */
    private void initView() {
        mVideoViewLayout = findViewById(R.id.ll_mainview);
        mVideoViewLayout.setUserId(mSelfUserId);
        mVideoViewLayout.setMembers(mRtcRoomBean.getUsers());
        ivSwitch = findViewById(R.id.iv_switch);
        ivSwitch.setOnClickListener(this);
        ivMic = findViewById(R.id.iv_mic);
        ivMic.setOnClickListener(this);
        findViewById(R.id.iv_hang_up).setOnClickListener(this);
        ivLoudspeaker = findViewById(R.id.iv_loudspeaker);
        ivLoudspeaker.setOnClickListener(this);
        ivCamera = findViewById(R.id.iv_camera);
        ivCamera.setOnClickListener(this);
        mTimer = findViewById(R.id.timer);

        findViewById(R.id.iv_float).setOnClickListener(this);
    }

    private void startTimer() {
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.start();
    }

    private void stopTimer() {
        mTimer.stop();
    }

    /**
     * 点击切换前后置摄像头
     */
    private void onSwitchCamera() {
        bFrontCamera = !bFrontCamera;
        trtcCloud.switchCamera();
        ivSwitch.setImageResource(bFrontCamera ? R.drawable.ic_rtc_switch_front : R.drawable.ic_rtc_switch_back);
    }

    /**
     * 关闭摄像头
     */
    private void onMuteCamera() {
        bCameraMute = !bCameraMute;
        trtcCloud.muteLocalVideo(bCameraMute);
        mLocalVideoView.getGLSurfaceView().setVisibility(bCameraMute ? View.INVISIBLE : View.VISIBLE);
        ivCamera.setImageResource(bCameraMute ? R.drawable.ic_rtc_camera_mute : R.drawable.ic_rtc_camera);
    }

    /**
     * 点击关闭或者打开本地的麦克风采集
     */
    private void onMuteAudio() {
        bMicMute = !bMicMute;
        trtcCloud.muteLocalAudio(bMicMute);
        ivMic.setImageResource(bMicMute ? R.drawable.ic_rtc_audio_mute : R.drawable.ic_rtc_audio);
    }

    /**
     * 关闭所有远端声音
     */
    private void onMuteLoudspeaker() {
        bLoudspeakerMute = !bLoudspeakerMute;
        trtcCloud.muteAllRemoteAudio(bLoudspeakerMute);
        ivLoudspeaker.setImageResource(bLoudspeakerMute ? R.drawable.ic_rtc_speaker_mute : R.drawable.ic_rtc_speaker);
    }

    /**
     * SDK内部状态回调
     */
    static class TRTCCloudListenerImpl extends TRTCCloudListener {

        private WeakReference<RTCRoomActivity> mContext;

        public TRTCCloudListenerImpl(RTCRoomActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        /**
         * 加入房间
         */
        @Override
        public void onEnterRoom(long elapsed) {
            RTCRoomActivity activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "加入房间成功", Toast.LENGTH_SHORT).show();
                activity.mVideoViewLayout.onRoomEnter();
                activity.startTimer();
            }
        }

        /**
         * 离开房间
         */
        @Override
        public void onExitRoom(int reason) {
            RTCRoomActivity activity = mContext.get();
            if (activity != null) {
                activity.stopTimer();
                activity.finishActivity();
            }
        }

        /**
         * ERROR 大多是不可恢复的错误，需要通过 UI 提示用户
         */
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Log.d(TAG, "sdk callback onError");
            RTCRoomActivity activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode+ "]" , Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    activity.exitRoom();
                }
            }
        }

        /**
         * WARNING 大多是一些可以忽略的事件通知，SDK内部会启动一定的补救机制
         */
        @Override
        public void onWarning(int warningCode, String warningMsg, Bundle extraInfo) {
            Log.d(TAG, "sdk callback onWarning");
        }

        /**
         * 有新的用户加入了当前视频房间
         */
        @Override
        public void onUserEnter(final String userId) {
            RTCRoomActivity activity = mContext.get();
            if(activity != null) {
                activity.mHasStart = true;

                final TXCloudVideoView renderView = activity.mVideoViewLayout.onMemberEnter(userId);
                if (renderView != null) {
                    // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                    activity.trtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                    activity.trtcCloud.setDebugViewMargin(userId, new TRTCCloud.TRTCViewMargin(0.0f, 0.0f, 0.1f, 0.0f));
                    activity.trtcCloud.startRemoteView(userId, renderView);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            renderView.setUserId(userId);
                        }
                    });
                }
            }
        }

        /**
         * 有用户离开了当前视频房间
         */
        @Override
        public void onUserExit(String userId, int reason) {
            RTCRoomActivity activity = mContext.get();
            if (activity != null) {
                activity.trtcCloud.stopRemoteView(userId);
                activity.trtcCloud.stopRemoteSubStreamView(userId);
                activity.mVideoViewLayout.onMemberLeave(userId);

                activity.autoLeave();
            }
        }

        /**
         * 有用户屏蔽了画面
         */
        @Override
        public void onUserVideoAvailable(final String userId, boolean available){
            RTCRoomActivity activity = mContext.get();
            if (activity != null) {
                TXCloudVideoView renderView = activity.mVideoViewLayout.getCloudVideoViewByUserId(userId);
                if(renderView != null) {
                    TextureView videoView = renderView.getVideoView();
                    if(videoView != null) {
                        videoView.setVisibility(available ? View.VISIBLE : View.GONE);
                    }
                }
            }
        }

        /**
         * 有用户屏蔽了声音
         */
        @Override
        public void onUserAudioAvailable(String userId, boolean available){
            Log.d(TAG, "sdk callback onUserAudioAvailable " +available);
            // TODO: 声音icon
        }

        /**
         * userid 对应的远端辅路（屏幕分享等）画面的状态通知
         * @param userId 用户标识
         * @param available true：屏幕分享可播放，false：屏幕分享被关闭
         */
        public void onUserSubStreamAvailable(final String userId, boolean available){
            RTCRoomActivity activity = mContext.get();
            if (activity != null) {
                if (available) {
                    final TXCloudVideoView renderView = activity.mVideoViewLayout.onMemberEnter(userId);
                    if (renderView != null) {
                        // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                        activity.trtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                        activity.trtcCloud.startRemoteSubStreamView(userId, renderView);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                renderView.setUserId(userId);
                            }
                        });
                    }

                } else {
                    activity.trtcCloud.stopRemoteSubStreamView(userId);
                    activity.mVideoViewLayout.onMemberLeave(userId);
                }
            }
        }
    }
}
