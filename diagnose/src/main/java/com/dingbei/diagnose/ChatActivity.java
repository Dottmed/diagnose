package com.dingbei.diagnose;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.bean.ChatErrorBean;
import com.dingbei.diagnose.bean.ChatMessageBean;
import com.dingbei.diagnose.bean.ChatMessageListBean;
import com.dingbei.diagnose.bean.RTCRoomUsersBean;
import com.dingbei.diagnose.bean.RtcRoomBean;
import com.dingbei.diagnose.bean.UserInfoBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpParams;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.message.MessageEvent;
import com.dingbei.diagnose.message.MessageType;
import com.dingbei.diagnose.rtc.activity.RTCRequestActivity;
import com.dingbei.diagnose.rtc.activity.RTCRoomActivity;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.DataConvertUtil;
import com.dingbei.diagnose.utils.DiagnoseUtil;
import com.dingbei.diagnose.utils.FileUtil;
import com.dingbei.diagnose.view.DialogPop;
import com.dingbei.diagnose.view.keyboard.KeyboardFunctionView;
import com.dingbei.diagnose.view.keyboard.SimpleCommonUtils;
import com.dingbei.diagnose.view.keyboard.XhsEmoticonsKeyBoard;
import com.dingbei.diagnose.view.recyc.CommonAdapter;
import com.dingbei.diagnose.view.recyc.HeaderAndFooterWrapper;
import com.dingbei.diagnose.view.recyc.ViewHolder;
import com.dingbei.diagnose.websocket.ErrorResponse;
import com.dingbei.diagnose.websocket.IWebSocketPage;
import com.dingbei.diagnose.websocket.Response;
import com.dingbei.diagnose.websocket.WebSocketServiceConnectManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;

/**
 * @author Dayo
 */

public class ChatActivity extends BasePostActivity implements IWebSocketPage, FuncLayout.OnFuncKeyBoardListener {

    protected final int REQUEST_RTC = 1004;

    private final String BIZ_LOGIN = "login";
    private final String BIZ_LOGOUT = "logout";
    private final String BIZ_CHAT = "chat";
    private final String BIZ_OPINION = "opinion";
    private final String BIZ_PRESCRIPTION = "prescription";
    private final String BIZ_RTC = "rtc";
    private final String BIZ_RTC_REJECT = "rtc_reject";
    private final String BIZ_RTC_CANCEL = "rtc_cancel";
    private final String BIZ_RTC_CLOSE = "rtc_close";
    private final String BIZ_HI = "hi";
    private final String BIZ_PLAIN = "plain";
    private final String BIZ_PLAIN_RTC = "plain_rtc";
    private final String BIZ_ZHUANZHEN_YES = "zhuanzhen_yes";
    private final String BIZ_ZHUANZHEN_NO = "zhuanzhen_no";
    private final String BIZ_ECG = "duote_ecg";

    private final String TYPE_TEXT = "text";
    private final String TYPE_IMAGE = "image";
    private final String TYPE_AUDIO = "audio";
    private final String TYPE_VIDEO = "video";

    private RecyclerView mRecycler;
    private HeaderAndFooterWrapper mAdapter;
    private ArrayList<ChatMessageBean> mList;
    private WebSocketServiceConnectManager mConnectManager;
    private String mRoom;
    private String mReception;
    private XhsEmoticonsKeyBoard ekBar;
    private View mLy_volume;
    private ImageView mImg_volume;
    private AnimationDrawable mVolumeAnim;
    private String mNextUrl;
    private LinearLayoutManager mLinearLayoutManager;
    private Handler mRecordHandler;
    private int mRecordDuration;
    private Runnable mRecordTask;
    private TextView mTx_voice_duration;
    private ArrayList<String> mChatImages;
    private RtcRoomBean mRtcRoomBean;
    private DialogPop mRtcPop;
    private boolean mIsRTC;
    private TextView mTx_to_rtc;
    private long mid = 0;
    private LongSparseArray<ChatMessageBean> mUnsendMap;
    private UserInfoBean mUserInfoBean;
    private EmoticonsEditText mEtChat;
    private ImageView mBtnMultimedia;
    private Button mBtnVoice;
    private Button mBtnSend;

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_chat;
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        mConnectManager = new WebSocketServiceConnectManager(this, this);
        mConnectManager.onCreate();

        Intent intent = getIntent();
        mRoom = intent.getStringExtra("room");
        mReception = intent.getStringExtra("reception");

        init();
        initView();

        getUserInfo();
        getChatHistory(true);
        getRoomUsers();

        ekBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEtChat = ekBar.getEtChat();
                mBtnVoice = ekBar.getBtnVoice();
                mBtnMultimedia = ekBar.getBtnMultimedia();
                mBtnSend = ekBar.getBtnSend();
                setKeycodeListener(new KeycodeListener() {
                    @Override
                    public void onCenter() {

                    }

                    @Override
                    public void onUp() {

                    }

                    @Override
                    public void onDown() {

                    }

                    @Override
                    public void onLeft() {
                        if (mEtChat.getVisibility() == View.VISIBLE && mBtnMultimedia.getVisibility() == View.VISIBLE && mBtnMultimedia.isFocused()) {
                            mEtChat.requestFocus();
                        } else if (mEtChat.getVisibility() == View.VISIBLE && mBtnSend.getVisibility() == View.VISIBLE && mBtnSend.isFocused()) {
                            mEtChat.requestFocus();
                        } else if (mBtnVoice.getVisibility() == View.VISIBLE && mBtnMultimedia.getVisibility() == View.VISIBLE && mBtnMultimedia.isFocused()) {
                            mBtnVoice.requestFocus();
                        } else if (mBtnVoice.getVisibility() == View.VISIBLE && mBtnSend.getVisibility() == View.VISIBLE && mBtnSend.isFocused()) {
                            mBtnVoice.requestFocus();
                        }
                    }

                    @Override
                    public void onRight() {
                        if (mEtChat.getVisibility() == View.VISIBLE && mBtnMultimedia.getVisibility() == View.VISIBLE && mEtChat.isFocused()) {
                            mBtnMultimedia.requestFocus();
                        } else if (mEtChat.getVisibility() == View.VISIBLE && mBtnSend.getVisibility() == View.VISIBLE && mEtChat.isFocused()) {
                            mBtnSend.requestFocus();
                        } else if (mBtnVoice.getVisibility() == View.VISIBLE && mBtnMultimedia.getVisibility() == View.VISIBLE && mBtnVoice.isFocused()) {
                            mBtnMultimedia.requestFocus();
                        } else if (mBtnVoice.getVisibility() == View.VISIBLE && mBtnSend.getVisibility() == View.VISIBLE && mBtnVoice.isFocused()) {
                            mBtnSend.requestFocus();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setBack();
        setTitle("医生会诊");
        if(!TextUtils.isEmpty(DiagnoseUtil.historyUrl)) {
            setRightText("查看病历").setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(DiagnoseUtil.historyUrl)) {
                        startActivity(new Intent(ChatActivity.this, H5Activity.class)
                                .putExtra(H5Activity.EX_TITLE, "病历详情")
                                .putExtra(H5Activity.EX_URL, DiagnoseUtil.historyUrl));
                    }
                }
            });
        }

        mRecycler = findViewById(R.id.recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mRecycler.setHasFixedSize(false);
        mRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    ekBar.reset();
                }
                return false;
            }
        });
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int first = mLinearLayoutManager.findFirstVisibleItemPosition();
                if(newState == RecyclerView.SCROLL_STATE_IDLE && first == 0 && !TextUtils.isEmpty(mNextUrl)) {
                    getChatHistory(false);
                }
            }
        });
        setAdapter();

        mLy_volume = findViewById(R.id.ly_volume);
        mImg_volume = findViewById(R.id.img_volume);
        mVolumeAnim = (AnimationDrawable) mImg_volume.getDrawable();

        ekBar = findViewById(R.id.ek_bar);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ekBar.getLayoutParams();
            layoutParams.width = dm.widthPixels / 2;
        }
        initEmoticonsKeyBoardBar();

        mTx_to_rtc = findViewById(R.id.tx_to_rtc);
        mTx_to_rtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken();
            }
        });

        mRtcPop = new DialogPop(this);
        mRtcPop.setTip("当前房间正在视频通话，是否加入？");
        mRtcPop.setListener(new DialogPop.OnDialogListener() {
            @Override
            public void onConfirm() {
                getToken();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void initEmoticonsKeyBoardBar() {
        SimpleCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
//        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);

        KeyboardFunctionView functionView = new KeyboardFunctionView(this);
        functionView.setFunctionListener(new KeyboardFunctionView.OnFunctionClickListener() {
            @Override
            public void onPicturesClick() {
                selectPictures(0, 9, new SelectPicturesCallback() {
                    @Override
                    public void callback(List<String> list) {
                        ekBar.reset();
                        for (String path : list) {
                            upload(TYPE_IMAGE, new File(path));
                        }
                    }
                });
            }

            @Override
            public void onVideoClick() {
                selectVideo(new ShootVideoCallback() {
                    @Override
                    public void callback(String path, String duration) {
                        ekBar.reset();
                        upload(TYPE_VIDEO, new File(path));
                    }
                });
            }

            @Override
            public void onRtcClick() {
                ekBar.reset();
                if(mIsRTC) {
                    mRtcPop.showAtLocation(mRecycler);
                }else {
                    sendMessage(BIZ_RTC, TYPE_TEXT, "发起视频会诊");
                    getToken();
                }
            }

            @Override
            public void onEcgClick() {
                ekBar.reset();
            }
        });
        ekBar.addFuncView(functionView);

        mTx_voice_duration = findViewById(R.id.tx_voice_duration);
        mRecordHandler = new Handler();
        mRecordTask = new Runnable() {
            @Override
            public void run() {
                mRecordDuration += 1;
                mTx_voice_duration.setText(DataConvertUtil.secondToTime(mRecordDuration));
                mRecordHandler.postDelayed(mRecordTask, 1000);
            }
        };

        ekBar.getBtnVoice().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    ekBar.getBtnVoice().setPressed(true);
                    requestAudioPermission();
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    ekBar.getBtnVoice().setPressed(false);
                    mVolumeAnim.stop();
                    mLy_volume.setVisibility(View.GONE);
                    mRecordHandler.removeCallbacksAndMessages(null);
                    stopRecorder(mRecordDuration);
                }
                return true;
            }
        });

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });

        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = ekBar.getEtChat().getText().toString();
                if(TextUtils.isEmpty(txt)) {
                    showMsg("消息不能为空");
                    return;
                }
                sendMessage(BIZ_CHAT, TYPE_TEXT, txt);
                ekBar.getEtChat().setText("");
            }
        });
    }

    protected void requestAudioPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mLy_volume.setVisibility(View.VISIBLE);
            mVolumeAnim.start();
            mRecordDuration = 0;
            mTx_voice_duration.setText(DataConvertUtil.secondToTime(mRecordDuration));
            mRecordHandler.postDelayed(mRecordTask, 1000);
            startRecord(new RecordCallback() {
                @Override
                public void callback(String path, int duration) {
                    if(duration > 0) {
                        upload(TYPE_AUDIO, new File(path));
                    }
                }
            });
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera),
                    REQUEST_VOICE, perms);
        }
    }

    @Override
    public void OnFuncPop(int height) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() {

    }

    private void setAdapter() {
        mList = new ArrayList<>(50);
        mChatImages = new ArrayList<>();
        mUnsendMap = new LongSparseArray<>();
        CommonAdapter adapter = new CommonAdapter<ChatMessageBean>(this, R.layout.dingbei_item_chat_message, mList) {
            @Override
            protected void convert(ViewHolder holder, ChatMessageBean bean, int position) {
                if(position == 0 || DataConvertUtil.calculateInterval(mList.get(position - 1).getTime(), bean.getTime()) / 1000 / 60 > 10) {
                    holder.setVisible(R.id.tx_time, true);
                    holder.setText(R.id.tx_time, DataConvertUtil.convertChatTime(bean.getTime(), "MM月dd日 HH:mm"));
                }else {
                    holder.setVisible(R.id.tx_time, false);
                }

                holder.setVisible(R.id.tx_other_msg, false);
                holder.getView(R.id.tx_other_msg).setFocusable(false);
                holder.setVisible(R.id.tx_end_msg, false);
                holder.setVisible(R.id.ly_chat, false);
                holder.setVisible(R.id.ly_opinion, false);
                holder.getView(R.id.ly_opinion).setFocusable(false);
                holder.setVisible(R.id.ly_prescription, false);
                holder.getView(R.id.ly_prescription).setFocusable(false);
                holder.setVisible(R.id.ly_result, false);
                holder.setVisible(R.id.ly_ecg, false);

                switch (bean.getBiz()) {
                    case BIZ_CHAT :
                        showChatMsg(holder, bean, position);
                        holder.setVisible(R.id.ly_chat, true);
                        break;
                    case BIZ_OPINION :
                        showOpinionMsg(holder, bean);
                        holder.setVisible(R.id.ly_opinion, true);
                        holder.getView(R.id.ly_opinion).setFocusable(true);
                        break;
                    case BIZ_PRESCRIPTION :
                        showPrescriptionMsg(holder, bean);
                        holder.setVisible(R.id.ly_prescription, true);
                        holder.getView(R.id.ly_prescription).setFocusable(true);
                        break;
                    case BIZ_PLAIN :
                        if("close_huizhen".equals(bean.getMsg().getLabel())) {
                            showEndMsg(holder, bean);
                            holder.setVisible(R.id.tx_end_msg, true);
                        }else {
                            showOtherMsg(holder, bean);
                            holder.setVisible(R.id.tx_other_msg, true);
                        }
                        break;
                    case BIZ_PLAIN_RTC :
                        showRTCMsg(holder, bean);
                        holder.setVisible(R.id.tx_other_msg, true);
                        holder.getView(R.id.tx_other_msg).setFocusable(true);
                        break;
                    case BIZ_ZHUANZHEN_YES :
                    case BIZ_ZHUANZHEN_NO :
                        showZhuanzhenMsg(holder, bean);
                        holder.setVisible(R.id.ly_result, true);
                        break;
                    case BIZ_ECG :
                        showEcgMsg(holder, bean);
                        holder.setVisible(R.id.ly_ecg, true);
                        break;
                }
            }

            private void showOtherMsg(ViewHolder holder, ChatMessageBean bean) {
                TextView tx_other_msg = holder.getView(R.id.tx_other_msg);
                tx_other_msg.setText(bean.getMsg().getContent());
                tx_other_msg.setSelected(false);
                tx_other_msg.setOnClickListener(null);
            }

            private void showEndMsg(ViewHolder holder, ChatMessageBean bean) {
                TextView tx_end_msg = holder.getView(R.id.tx_end_msg);
                tx_end_msg.setText(bean.getMsg().getContent());
            }

            private void showRTCMsg(ViewHolder holder, ChatMessageBean bean) {
                TextView tx_other_msg = holder.getView(R.id.tx_other_msg);
                tx_other_msg.setText(bean.getMsg().getContent());
                tx_other_msg.setSelected(true);
                tx_other_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getToken();
                    }
                });
            }

            private void showChatMsg(ViewHolder holder, ChatMessageBean bean, int position) {
                final ChatMessageBean.Message msg = bean.getMsg();
                ChatMessageBean.User user = bean.getUser();
                if(user == null || msg == null) {
                    holder.setVisible(R.id.ly_right, false);
                    holder.setVisible(R.id.ly_left, false);
                    return;
                }

                if(isMine(user.getId())) {
                    holder.setVisible(R.id.ly_right, true);
                    holder.setVisible(R.id.ly_left, false);

                    holder.setImageUrl(R.id.img_avatar_right, user.getAvatar());
                    String name = String.format("%s %s %s", user.getName(), user.getDepartment(), user.getLevel());
                    holder.setText(R.id.tx_name_right, name);

                    if(bean.getStatus() == ChatMessageBean.SENDED) {
                        holder.setInVisible(R.id.img_status_right);
                    }else {
                        holder.setImageResource(R.id.img_status_right, bean.getStatus() == ChatMessageBean.SENDING ?
                                R.drawable.ic_chat_loading : R.drawable.ic_chat_error);
                        holder.setVisible(R.id.img_status_right, true);
                    }

                    holder.setVisible(R.id.msg_txt_right, false);
                    holder.setVisible(R.id.msg_image_right, false);
                    holder.setVisible(R.id.msg_audio_right, false);
                    holder.setVisible(R.id.msg_video_right, false);
                    holder.getView(R.id.msg_txt_right).setFocusable(false);
                    holder.getView(R.id.msg_image_right).setFocusable(false);
                    holder.getView(R.id.msg_audio_right).setFocusable(false);
                    holder.getView(R.id.msg_video_right).setFocusable(false);
                    switch (msg.getType()) {
                        case TYPE_TEXT :
                            holder.setVisible(R.id.msg_txt_right, true);
                            holder.getView(R.id.msg_txt_right).setFocusable(true);
                            holder.setText(R.id.msg_txt_right, msg.getContent());
                            break;
                        case TYPE_IMAGE :
                            holder.setVisible(R.id.msg_image_right, true);
                            holder.getView(R.id.msg_image_right).setFocusable(true);
                            final String img = msg.getContent();
                            holder.setImageUrl(R.id.msg_image_right, img, R.drawable.ic_chat_image_placeholder);

                            if(!TextUtils.isEmpty(img) && bean.getStatus() == ChatMessageBean.SENDED
                                    && !mChatImages.contains(img)) {
                                mChatImages.add(img);
                            }

                            holder.setOnClickListener(R.id.msg_image_right, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewPictures(mChatImages, mChatImages.indexOf(img));
                                }
                            });
                            break;
                        case TYPE_AUDIO :
                            holder.setVisible(R.id.msg_audio_right, true);
                            holder.getView(R.id.msg_audio_right).setFocusable(true);
                            final ImageView img_voice_play_right = holder.getView(R.id.img_voice_play_right);
                            TextView tx_second_right = holder.getView(R.id.tx_second_right);
                            String duration = FileUtil.getFileInfo(msg.getContent());
                            if(!TextUtils.isEmpty(duration)) {
                                duration = String.format("%s''", duration);
                            }
                            tx_second_right.setText(duration);
                            holder.setOnClickListener(R.id.msg_audio_right, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playRecord(msg.getContent(), img_voice_play_right);
                                }
                            });
                            break;
                        case TYPE_VIDEO :
                            holder.setVisible(R.id.msg_video_right, true);
                            holder.getView(R.id.msg_video_right).setFocusable(true);
                            holder.setOnClickListener(R.id.msg_video_right, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playVideo(msg.getContent(), false);
                                }
                            });
                            break;
                    }

                }else {
                    holder.setVisible(R.id.ly_right, false);
                    holder.setVisible(R.id.ly_left, true);

                    holder.setImageUrl(R.id.img_avatar_left, user.getAvatar());
                    String name = String.format("%s %s %s", user.getName(), user.getDepartment(), user.getLevel());
                    holder.setText(R.id.tx_name_left, name);

                    holder.setVisible(R.id.msg_txt_left, false);
                    holder.setVisible(R.id.msg_image_left, false);
                    holder.setVisible(R.id.msg_audio_left, false);
                    holder.setVisible(R.id.msg_video_left, false);
                    holder.getView(R.id.msg_txt_left).setFocusable(false);
                    holder.getView(R.id.msg_image_left).setFocusable(false);
                    holder.getView(R.id.msg_audio_left).setFocusable(false);
                    holder.getView(R.id.msg_video_left).setFocusable(false);
                    switch (msg.getType()) {
                        case TYPE_TEXT :
                            holder.setVisible(R.id.msg_txt_left, true);
                            holder.getView(R.id.msg_txt_left).setFocusable(true);
                            holder.setText(R.id.msg_txt_left, msg.getContent());
                            break;
                        case TYPE_IMAGE :
                            holder.setVisible(R.id.msg_image_left, true);
                            holder.getView(R.id.msg_image_left).setFocusable(true);
                            final String img = msg.getContent();
                            holder.setImageUrl(R.id.msg_image_left, msg.getContent(), R.drawable.ic_chat_image_placeholder);

                            if(!TextUtils.isEmpty(img) && !mChatImages.contains(img)) {
                                mChatImages.add(img);
                            }

                            holder.setOnClickListener(R.id.msg_image_left, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewPictures(mChatImages, mChatImages.indexOf(img));
                                }
                            });
                            break;
                        case TYPE_AUDIO :
                            holder.setVisible(R.id.msg_audio_left, true);
                            holder.getView(R.id.msg_audio_left).setFocusable(true);
                            final ImageView img_voice_play_left = holder.getView(R.id.img_voice_play_left);
                            TextView tx_second_left = holder.getView(R.id.tx_second_left);
                            String duration = FileUtil.getFileInfo(msg.getContent());
                            if(!TextUtils.isEmpty(duration)) {
                                duration = String.format("%s''", duration);
                            }
                            tx_second_left.setText(duration);
                            holder.setOnClickListener(R.id.msg_audio_left, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playRecord(msg.getContent(), img_voice_play_left);
                                }
                            });
                            break;
                        case TYPE_VIDEO :
                            holder.setVisible(R.id.msg_video_left, true);
                            holder.getView(R.id.msg_video_left).setFocusable(true);
                            holder.setOnClickListener(R.id.msg_video_left, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playVideo(msg.getContent(), false);
                                }
                            });
                            break;
                    }
                }
            }

            private void showOpinionMsg(ViewHolder holder, ChatMessageBean bean) {
                final ChatMessageBean.Message msg = bean.getMsg();
                ChatMessageBean.User user = bean.getUser();

                holder.setText(R.id.tx_op_name, user.getName());
                holder.setText(R.id.tx_op_time, DataConvertUtil.convertChatTime(bean.getTime(), "yyyy-MM-dd HH:mm"));
                holder.setText(R.id.tx_op_desc, msg.getContent());
                holder.setOnClickListener(R.id.ly_opinion, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ChatActivity.this, OpinionActivity.class)
                                .putExtra("opinion_id", msg.getOpinion_id()));
                    }
                });
            }

            private void showPrescriptionMsg(ViewHolder holder, ChatMessageBean bean) {
                final ChatMessageBean.Message msg = bean.getMsg();
                ChatMessageBean.User user = bean.getUser();
                holder.setText(R.id.tx_rx_name, user.getName());
                holder.setText(R.id.tx_rx_desc, msg.getContent());
                holder.setText(R.id.tx_rx_time, DataConvertUtil.convertChatTime(bean.getTime(), "yyyy-MM-dd HH:mm"));
                holder.setOnClickListener(R.id.ly_prescription, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getUrl(msg.getPrescription_id());
                    }
                });
            }

            private void showZhuanzhenMsg(ViewHolder holder, ChatMessageBean bean) {
                final ChatMessageBean.Message msg = bean.getMsg();
                ChatMessageBean.User user = bean.getUser();

                holder.setText(R.id.tx_result_name, user.getName());
                holder.setText(R.id.tx_result_time, DataConvertUtil.convertChatTime(bean.getTime(), "yyyy-MM-dd HH:mm"));
                holder.setText(R.id.tx_result_desc, msg.getContent());
            }

            private void showEcgMsg(ViewHolder holder, ChatMessageBean bean) {
                final ChatMessageBean.Message msg = bean.getMsg();
                ChatMessageBean.User user = bean.getUser();

                holder.setText(R.id.tx_ecg_name, user.getName());
                holder.setText(R.id.tx_ecg_time, DataConvertUtil.convertChatTime(bean.getTime(), "yyyy-MM-dd HH:mm"));
                holder.setText(R.id.tx_ecg_desc, msg.getContent());
                holder.setOnClickListener(R.id.ly_ecg, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        startActivity(new Intent(ChatActivity.this, DottEcgActivity.class).putExtra("url", msg.getEcg_url()));
                    }
                });
            }
        };
        mAdapter = new HeaderAndFooterWrapper(adapter);
        mAdapter.addFootView(View.inflate(this, R.layout.dingbei_bottom_divider, null));
        mRecycler.setAdapter(mAdapter);
    }

    private boolean isMine(String id) {
        return !TextUtils.isEmpty(DiagnoseUtil.userId) && DiagnoseUtil.userId.equals(id);
    }

    private void scrollToBottom() {
        mRecycler.scrollToPosition(mList.size()); //有尾部
    }

    private void getUrl(String id) {
        HttpParams params = new HttpParams();
        params.put("prescription", id);
        HttpUtil.get("diagnosis/get_prescription_h5/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                String url = JSONObject.parseObject(json).getString("h5");
                startActivity(new Intent(ChatActivity.this, H5Activity.class)
                        .putExtra(H5Activity.EX_TITLE, "处方详情")
                        .putExtra(H5Activity.EX_URL, url));
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

    private void getUserInfo() {
        HttpUtil.get("account/mine/", null, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                mUserInfoBean = JSONObject.parseObject(json, UserInfoBean.class);
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }

        });
    }

    private void getChatHistory(final boolean isFirstLoad) {
        HttpParams params = new HttpParams();
        params.put("room", mRoom);
        String url = TextUtils.isEmpty(mNextUrl) ? "refferal/chats/" : mNextUrl;
        HttpUtil.get(url, params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                ChatMessageListBean listBean = JSONObject.parseObject(json, ChatMessageListBean.class);
                List<ChatMessageBean> results = listBean.getResults();
                Collections.reverse(results);
                int position = results.size() - 1;
                results.addAll(mList);
                mList.clear();
                mList.addAll(results);
                mAdapter.notifyDataSetChanged();

                if(isFirstLoad) {
                    scrollToBottom();
                }else {
                    mRecycler.scrollToPosition(position);
                }

                mNextUrl = listBean.getNext();
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }


    private void upload(final String type, File file) {
        final ChatMessageBean message = buildMessage(BIZ_CHAT, type, file.getPath());
        message.getMsg().setMid(mid);
        message.setStatus(ChatMessageBean.SENDING);
        refreshMessageList(message);
        mUnsendMap.put(mid, message);

        HttpParams params = new HttpParams();
        params.put("room", mRoom);
        params.put("type", type);
        params.put("mid", mid);
        mid += 1;

        HashMap<String, File> map = new HashMap<>();
        map.put("mfile", file);

        HttpUtil.postMultipart("refferal/upload_media/", params, map, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                JSONObject object = JSONObject.parseObject(json);
                message.getMsg().setContent(object.getString("url"));
                sendMessage(message);
            }

            @Override
            public void onError(ErrorBean error) {
                //showError(error);
                message.setStatus(ChatMessageBean.UNSEND);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onConnected() {
        AppLogger.e("socket connected");
        loginSocket();
    }

    private void loginSocket() {
        sendMessage(BIZ_LOGIN, TYPE_TEXT, "login");
        Message message = mHandler.obtainMessage();
        message.what = 0;
        mHandler.sendMessageDelayed(message, 10*1000);
    }

    @Override
    public void onConnectError(Throwable cause) {
        AppLogger.e("socket connect error: " + cause.getMessage());
    }

    @Override
    public void onDisconnected() {
        AppLogger.e("socket disconnect");
        mHandler.removeMessages(0);
    }

    @Override
    public void onMessageResponse(Response message) {
        String content = message.getResponseText();

        ChatErrorBean error = JSONObject.parseObject(content, ChatErrorBean.class);
        if(error != null && !TextUtils.isEmpty(error.getError())) {
            showMsg("Error" + error.getStatus() + ": " + error.getError());
            return;
        }

        ChatMessageBean bean = JSONObject.parseObject(content, ChatMessageBean.class);
        if(bean != null && mRoom.equals(bean.getRoom())) {
            String biz = bean.getBiz();

            if(BIZ_HI.equals(biz)) {
                return;
            }

            if(isMine(bean.getUser().getId()) && bean.getMsg() != null && !TYPE_TEXT.equals(bean.getMsg().getType())) {
                long mid = bean.getMsg().getMid();
                ChatMessageBean localMsg = mUnsendMap.get(mid);

                if(localMsg != null) {
                    localMsg.setStatus(ChatMessageBean.SENDED);
                    localMsg.getMsg().setContent(bean.getMsg().getContent());
                    mUnsendMap.remove(mid);
                    mAdapter.notifyDataSetChanged();
                }else {
                    refreshMessageList(bean);
                }
            }else {
                refreshMessageList(bean);
            }

            ChatMessageBean.User owner = bean.getUser();
            switch (biz) {
                case BIZ_RTC:
                    if (!RTCRoomActivity.isRunning) {
                        startActivity(new Intent(ChatActivity.this, RTCRequestActivity.class)
                                .putExtra("room", mRoom)
                                .putExtra("patient", DiagnoseUtil.userId)
                                .putExtra("owner_avatar", owner.getAvatar())
                                .putExtra("owner_name", owner.getName()));
                    }
                    break;
                case BIZ_RTC_REJECT :
                    MessageEvent event = new MessageEvent(MessageType.RTC_REJECT);
                    event.setExtra(owner.getId());
                    EventBus.getDefault().post(event);
                    getRoomUsers();
                    break;
                case BIZ_RTC_CANCEL :
                    if(!DiagnoseUtil.userId.equals(owner.getId())) {
                        EventBus.getDefault().post(new MessageEvent(MessageType.RTC_CANCEL_BY_LAUNCHER));
                    }
                    getRoomUsers();
                    break;
                case BIZ_RTC_CLOSE :
                    getRoomUsers();
                    break;
            }
        }
    }

    private void refreshMessageList(ChatMessageBean bean) {
        mList.add(bean);
        mAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    @Override
    public void onSendMessageError(ErrorResponse error) {
        AppLogger.e("send error: " + error.getDescription());

        switch (error.getErrorCode()) {
            case 1:
                showMsg("错误：未连接或连接已断开");
                break;
            case 2:
                showMsg("错误：服务未绑定或绑定失败");
                break;
            case 3:
                showMsg("错误：初始化未完成");
                break;
            case 11:
                showMsg("错误：解析失败");
                break;
            case 12:
                showMsg("错误：code值不正确");
                break;
        }
    }

    @Override
    public void onServiceBindSuccess() {
        AppLogger.e("service bind success");
    }

    @Override
    public void sendText(String text) {
        mConnectManager.sendText(text);
    }

    @Override
    public void reconnect() {
        AppLogger.e("socket reconnecting");
        mConnectManager.reconnect();
    }

    @Override
    protected void onDestroy() {
        sendMessage(BIZ_LOGOUT, TYPE_TEXT, "logout");
        mConnectManager.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private ChatMessageBean buildMessage(String biz, String type, String content) {
        ChatMessageBean bean = new ChatMessageBean();

        ChatMessageBean.User user = new ChatMessageBean.User();
        if(mUserInfoBean != null) {
            user.setId(mUserInfoBean.getId());
            user.setName(mUserInfoBean.getName());
            user.setAvatar(mUserInfoBean.getAvatar());
            UserInfoBean.DepartmentBean department = mUserInfoBean.getDepartment();
            if(department != null) {
                user.setDepartment(department.getName());
            }
            user.setLevel(mUserInfoBean.getLevel());
            bean.setUser(user);
        }

        if(!TextUtils.isEmpty(type)) {
            ChatMessageBean.Message message = new ChatMessageBean.Message();
            message.setType(type);
            message.setContent(content);
            bean.setMsg(message);
        }

        bean.setRoom(mRoom);
        bean.setReception(mReception);
        bean.setBiz(biz);
        bean.setSrc("capp");
        return bean;
    }

    private void sendMessage(String biz, String type, String content) {
        sendText(JSONObject.toJSONString(buildMessage(biz, type, content)));
    }

    private void sendMessage(String biz, ChatMessageBean.Message message) {
        ChatMessageBean bean = buildMessage(biz, "", "");
        bean.setMsg(message);
        sendText(JSONObject.toJSONString(bean));
    }

    private void sendMessage(ChatMessageBean bean) {
        sendText(JSONObject.toJSONString(bean));
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 0) {
                sendMessage(BIZ_HI, TYPE_TEXT, "hi");

                Message message = mHandler.obtainMessage();
                message.what = 0;
                mHandler.sendMessageDelayed(message, 10*1000); //10s发一次心跳
            }
            return false;
        }
    });

    private void getToken() {
        HttpParams params = new HttpParams();
        params.put("room", mRoom);
        HttpUtil.get("rtc/get_token/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                mRtcRoomBean = JSONObject.parseObject(json, RtcRoomBean.class);
                requestPermission(REQUEST_RTC);
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

    private void joinRoom() {
        Intent intent = new Intent(this, RTCRoomActivity.class);
        intent.putExtra(RTCRoomActivity.EXTRA_ROOM, mRtcRoomBean);
        intent.putExtra(RTCRoomActivity.EXTRA_ROOM_ID, mRoom);
        intent.putExtra(RTCRoomActivity.EXTRA_USER_ID, DiagnoseUtil.userId);
        intent.putExtra(RTCRoomActivity.EXTRA_LAUNCH, true);
        startActivity(intent);
    }

    @Override
    protected void handleRequest(int requestCode) {
        super.handleRequest(requestCode);
        if(requestCode == REQUEST_RTC) {
            joinRoom();
        }
    }

    private void getRoomUsers() {
        HttpParams params = new HttpParams();
        params.put("room", mRoom);
        HttpUtil.get("rtc/get_users/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                RTCRoomUsersBean bean = JSONObject.parseObject(json, RTCRoomUsersBean.class);
                List<RTCRoomUsersBean.UserBean> users = bean.getUsers();

                mIsRTC = users != null && users.size() > 1;
                if(mIsRTC) {
                    mTx_to_rtc.setText(String.format("%d人正在视频通话", users.size()));
                }
                mTx_to_rtc.setVisibility(mIsRTC ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }


}
