package com.dingbei.diagnose.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.utils.ToastUtil;


/**
 * @author Dayo
 * @time 2017/8/1 11:09
 * @desc 提示对话框
 */

public abstract class BasePop {

    protected Context mContext;
    protected PopupWindow mPop;
    protected Animation mAnimIn;
    protected Animation mAnimOut;
    protected View mLy_pop;

    public BasePop(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        View view = getContentView();
        mLy_pop = view.findViewById(R.id.ly_pop);
        //初始化界面
        initView();

        //初始化动画 可重写
        initAnim();

        //PopupWindow的设置
        initPopupWindow(view);
    }

    public abstract View getContentView();

    public abstract void initView();

    protected void initPopupWindow(View view) {
        mPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPop.setFocusable(true);
        mPop.setOutsideTouchable(true);
        mPop.setClippingEnabled(false); //允许弹出窗口超出屏幕范围,全屏显示,与设置软键盘冲突
        mPop.setBackgroundDrawable(new BitmapDrawable());
        mPop.update();
    }

    protected void initAnim() {
        mAnimIn = AnimationUtils.loadAnimation(mContext, R.anim.dialog_fade_scale_in);
        mAnimOut = AnimationUtils.loadAnimation(mContext, R.anim.dialog_fade_scale_out);
        //设置动画监听
        mAnimOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //播放完才消失
                mPop.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showAsDropDown(View anchor) {
        if (!mPop.isShowing()) {
            mPop.showAsDropDown(anchor);
            if(mLy_pop != null) {
                mLy_pop.startAnimation(mAnimIn);
            }
        }
    }

    public void showAtLocation(View parent) {
        if (!mPop.isShowing()) {
            mPop.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
            if(mLy_pop != null) {
                mLy_pop.startAnimation(mAnimIn);
            }
        }
    }

    public void dismiss() {
        if (mPop.isShowing()) {
            if(mLy_pop != null) {
                mLy_pop.startAnimation(mAnimOut);
            }
        }
    }

    protected void showMsg(String msg) {
        ToastUtil.show(msg, Toast.LENGTH_SHORT);
    }

}
