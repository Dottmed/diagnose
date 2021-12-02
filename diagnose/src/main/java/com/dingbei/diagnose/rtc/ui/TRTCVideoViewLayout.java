package com.dingbei.diagnose.rtc.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.bean.DoctorBean;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.ImageLoadUtil;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.List;


/**
 * Module:   TRTCVideoViewLayout
 *
 * Function: 用于计算每个视频画面的位置排布和大小尺寸
 *
 */
public class TRTCVideoViewLayout extends RelativeLayout {
    private final static String TAG     = "TRTCVideoViewLayout";
    public static final int MAX_USER    = 9;
    private Context mContext;
    private ArrayList<TXCloudVideoView> mVideoViewList;
    private RelativeLayout mLayout;

    private String mSelfUserId;
    private int mScreenWidth;
    private int mScreenHeight;
    private ArrayList<TXCloudVideoView> mMemberVideoList;

    public TRTCVideoViewLayout(Context context) {
        super(context);
        initView(context);
    }

    public TRTCVideoViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TRTCVideoViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setUserId(String userId) {
        mSelfUserId = userId;
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.dingbei_item_rtc, this);
        mLayout = findViewById(R.id.ll_mainview);
        mScreenWidth = getScreenWidth(mContext);
        mScreenHeight = getScreenHeight(mContext);

        initTXCloudVideoView();
    }

    public void initTXCloudVideoView() {
        mVideoViewList = new ArrayList<TXCloudVideoView>();
        mMemberVideoList = new ArrayList<TXCloudVideoView>();

        mLayout.removeAllViews();
        for (int i = 0; i < MAX_USER; i++) {
            final TXCloudVideoView cloudVideoView = new TXCloudVideoView(mContext);
            cloudVideoView.setVisibility(GONE);
            cloudVideoView.setId(1000 + i);
            cloudVideoView.setClickable(true);
            cloudVideoView.setEnabled(false);
            cloudVideoView.setTag(R.string.str_tag_pos, i);
            cloudVideoView.setBackgroundColor(Color.BLACK);
            cloudVideoView.setFocusable(true);
            cloudVideoView.setForeground(ContextCompat.getDrawable(cloudVideoView.getContext(), R.drawable.sel_tv_focusable));
            cloudVideoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean selected = cloudVideoView.isSelected();
                    if(selected) {
                        toggleToMultiUsersUI();
                    }else {
                        int pos = (int) cloudVideoView.getTag(R.string.str_tag_pos);
                        LayoutParams params = new LayoutParams(mScreenWidth, mScreenWidth);
                        cloudVideoView.setLayoutParams(params);

                        for(int j = 0; j < mMemberVideoList.size(); j++) {
                            TXCloudVideoView videoView = mMemberVideoList.get(j);
                            int tag = (int) videoView.getTag(R.string.str_tag_pos);
                            if(tag != pos) {
                                videoView.setVisibility(GONE);
                            }
                        }
                    }

                    cloudVideoView.setSelected(!selected);
                }
            });

            mVideoViewList.add(i, cloudVideoView);
            mLayout.addView(cloudVideoView);
        }
    }

    public TXCloudVideoView getLocalVideoView() {
        if(mMemberVideoList != null && mMemberVideoList.size() > 0) {
            return mMemberVideoList.get(0);
        }
        return null;
    }

    public TXCloudVideoView getCloudVideoViewByUserId(String userId) {
        if (TextUtils.isEmpty(userId)) return null;

        for (int i = 0; i < mMemberVideoList.size(); i++) {
            TXCloudVideoView renderView = mMemberVideoList.get(i);
            if (renderView != null) {
                String vUserId = renderView.getUserId();
                if (!TextUtils.isEmpty(vUserId) && userId.contains(vUserId)){
                    return renderView;
                }
            }
        }

        return null;
    }

    public void appendEventMessage(String userId, String message) {
        for (int i=0; i<mVideoViewList.size(); i++){
            if (userId.equalsIgnoreCase(mVideoViewList.get(i).getUserId())) {
                mVideoViewList.get(i).appendEventInfo(message);
                break;
            }
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void showDebugView(int type) {
        for (int i = 0; i < mVideoViewList.size(); i++) {
            TXCloudVideoView renderView = mVideoViewList.get(i);
            if (renderView != null) {
                String vUserId = renderView.getUserId();
                if (!TextUtils.isEmpty(vUserId)){
                    renderView.showVideoDebugLog(type);
                }

            }
        }
    }

    private void setTargetWindowParams(int targetPos, TXCloudVideoView targetWindow) {
        //判断是否横屏
        boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        switch (mMemberVideoList.size()) {
            case 1:
                if(isLand) {
                    updateLayoutParams(targetWindow, 0, mScreenWidth / 3, mScreenWidth / 3, 0, 0, RelativeLayout.CENTER_HORIZONTAL);
                }else {
                    updateLayoutParams(targetWindow, 0, mScreenWidth, mScreenWidth, 0, 0, -1);
                }
            case 2:
                if(isLand) {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, mScreenWidth / 6, 0, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, 0, mScreenWidth / 6, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    }
                }else {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, mScreenWidth / 4, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, mScreenWidth / 4, RelativeLayout.ALIGN_PARENT_RIGHT);
                    }
                }
                break;
            case 3:
                if(isLand) {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, 0, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, 0, RelativeLayout.CENTER_HORIZONTAL);
                    } else {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    }
                }else {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, 0, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, mScreenWidth / 2, RelativeLayout.CENTER_HORIZONTAL);
                    }
                }
                break;
            case 4:
                if(isLand) {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 4, mScreenWidth / 4, 0, 0, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 4, mScreenWidth / 4, mScreenWidth / 4, 0, -1);
                    } else if (targetPos == 2) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 4, mScreenWidth / 4, 0, 0, mScreenWidth / 4, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 4, mScreenWidth / 4, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    }
                }else {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, 0, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else if (targetPos == 2) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, mScreenWidth / 2, RelativeLayout.ALIGN_PARENT_LEFT);
                    } else {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 2, mScreenWidth / 2, 0, mScreenWidth / 2, RelativeLayout.ALIGN_PARENT_RIGHT);
                    }
                }
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                if(isLand) {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, 0, 0, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, mScreenWidth / 6, 0, -1);
                    } else if (targetPos == 2) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, mScreenWidth / 3, 0, -1);
                    } else if (targetPos == 3) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, 0, 0, mScreenWidth / 3, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else if (targetPos == 4) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, 0, 0, mScreenWidth / 6, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else if (targetPos == 5) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else if (targetPos == 6) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, 0, mScreenWidth / 6, -1);
                    } else if (targetPos == 7) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, mScreenWidth / 6, mScreenWidth / 6, -1);
                    } else if (targetPos == 8) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 6, mScreenWidth / 6, mScreenWidth / 3, mScreenWidth / 6, -1);
                    }
                }else {
                    if (targetPos == 0) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, 0, -1);
                    } else if (targetPos == 1) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, 0, RelativeLayout.CENTER_HORIZONTAL);
                    } else if (targetPos == 2) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else if (targetPos == 3) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, mScreenWidth / 3, -1);
                    } else if (targetPos == 4) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, mScreenWidth / 3, RelativeLayout.CENTER_HORIZONTAL);
                    } else if (targetPos == 5) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, mScreenWidth / 3, RelativeLayout.ALIGN_PARENT_RIGHT);
                    } else if (targetPos == 6) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, mScreenWidth * 2 / 3, -1);
                    } else if (targetPos == 7) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, mScreenWidth * 2 / 3, RelativeLayout.CENTER_HORIZONTAL);
                    } else if (targetPos == 8) {
                        updateLayoutParams(targetWindow, targetPos, mScreenWidth / 3, mScreenWidth / 3, 0, mScreenWidth * 2 / 3, RelativeLayout.ALIGN_PARENT_RIGHT);
                    }
                }
                break;
        }
    }

    private void updateLayoutParams(TXCloudVideoView targetView, int targetPos, int width, int height, int marginStart, int marginTop, int gravity) {
        updateLayoutParams(targetView, targetPos, width, height, marginStart, marginTop, 0, 0, gravity);
    }

    private void updateLayoutParams(TXCloudVideoView targetView, int targetPos, int width, int height, int marginStart, int marginTop, int marginEnd, int marginBottom, int gravity) {
        //若要复用RelativeLayout.LayoutParams 要移除之前的rule
        LayoutParams lp = new LayoutParams(width, height);
        lp.topMargin = marginTop;
        lp.bottomMargin = marginBottom;
        if(gravity != -1) {
            lp.addRule(gravity);
        }
        lp.setMarginStart(marginStart);
        lp.setMarginEnd(marginEnd);
        targetView.setLayoutParams(lp);
        targetView.setVisibility(TextUtils.isEmpty(targetView.getUserId()) ? GONE : VISIBLE);
    }

    private void toggleToMultiUsersUI() {
        for (int i = 0; i < mMemberVideoList.size(); i++) {
            setTargetWindowParams(i, mMemberVideoList.get(i));
        }
    }

    public void setMembers(List<DoctorBean> members) {
        int size = members.size();
        int index = -1;
        for(int i = 0; i < size; i++) {
            String id = members.get(i).getId();
            if(!TextUtils.isEmpty(id) && id.equalsIgnoreCase(mSelfUserId)) {
                index = i;
                break;
            }
        }
        if(index != -1) {
            // 将自己放在第一位
            DoctorBean self = members.remove(index);
            members.add(0, self);
        }

        size = size > MAX_USER ? MAX_USER : size;
        for(int i = 0; i < size; i++) {
            ImageView avatar = new ImageView(mContext);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            avatar.setLayoutParams(params);
            avatar.setScaleType(ImageView.ScaleType.FIT_XY);
            //取出未用过的
            TXCloudVideoView videoView = mVideoViewList.remove(0);
            videoView.addView(avatar);
            //显示在最前
            videoView.bringChildToFront(avatar);
            DoctorBean member = members.get(i);
            videoView.setUserId(member.getId());
            ImageLoadUtil.setImageUrl(mContext, member.getAvatar(), avatar);
            mMemberVideoList.add(videoView);
        }
    }


    /**
     * 更新进入房间人数，4个人以下用四宫格，4个人以上用9宫格
     */
    public TXCloudVideoView onMemberEnter(String userId) {
        AppLogger.e("rtc 进房：" + userId);
        TXCloudVideoView view = getCloudVideoViewByUserId(userId);
        if(view != null) {
            view.setEnabled(true);
            return view;
        }

        //有新人拉进来时 取出未用过的
        TXCloudVideoView videoView = mVideoViewList.remove(0);
        videoView.setUserId(userId);
        videoView.setEnabled(true);
        mMemberVideoList.add(videoView);

        if (mMemberVideoList.size() <= 5) {
            toggleToMultiUsersUI();
        } else {
            //从第6个开始布局不变，只是在第5个基础上添加1个
            setTargetWindowParams(mMemberVideoList.size() - 1, videoView);
        }

        return videoView;
    }

    public void onMemberLeave(String userId) {
        AppLogger.e("rtc 退出：" + userId);
        int index = -1;
        for (int i = 0; i < mMemberVideoList.size(); i++) {
            TXCloudVideoView renderView = mMemberVideoList.get(i);
            if (renderView != null && null != renderView.getUserId()) {
                if (userId.contains(renderView.getUserId())) {
                    renderView.setUserId(null);
                    renderView.setVisibility(View.GONE);
                    index = i;
                }
            }
        }

        if(index != -1) {
            //回收用过的
            mVideoViewList.add(mMemberVideoList.remove(index));
        }

        toggleToMultiUsersUI();
    }

    /**
     * 超时未接入提示（根据isEnabled）
     */
    public void timeout() {
        for(int i = 0; i < mMemberVideoList.size(); i++) {
            TXCloudVideoView videoView = mMemberVideoList.get(i);
            if(!videoView.isEnabled()) {
                TextView txt = new TextView(mContext);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                txt.setLayoutParams(params);
                txt.setBackgroundResource(R.color.black_tran50);
                txt.setTextColor(0xffff1e19);
                txt.setTextSize(14);
                txt.setText("未接入...");
                txt.setGravity(Gravity.CENTER);
                videoView.addView(txt);
                //显示在最前
                videoView.bringChildToFront(txt);
            }
        }
    }

    public int getCount() {
        return mMemberVideoList != null ? mMemberVideoList.size() : 0;
    }

    public void onRoomEnter() {
        toggleToMultiUsersUI();
    }

    public int getScreenWidth(Context context) {
        if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public int getScreenHeight(Context context) {
        if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public int getStatusBarHeight(Context context) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }
}
