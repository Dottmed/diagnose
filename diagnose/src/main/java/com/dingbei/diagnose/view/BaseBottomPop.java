package com.dingbei.diagnose.view;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.utils.PhoneUtil;


/**
 * @author Dayo
 * @time 2018/5/11 11:31
 * @desc 从底部弹出
 */

public abstract class BaseBottomPop extends BasePop {

    public BaseBottomPop(Context context) {
        super(context);
    }

    @Override
    protected void initAnim() {
        mAnimIn = AnimationUtils.loadAnimation(mContext, R.anim.dialog_enter);
        mAnimOut = AnimationUtils.loadAnimation(mContext, R.anim.dialog_exit);
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

    protected void setBottomMargin() {
        int navigationBarHeight = PhoneUtil.getNavigationBarHeight(mContext);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLy_pop.getLayoutParams();
        //注意：在xml和代码都设置了margin时，使用xml中的值
        layoutParams.setMargins(0, 0, 0, navigationBarHeight);
        mLy_pop.requestLayout();
    }
}
