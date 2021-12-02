package com.dingbei.diagnose.view;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingbei.diagnose.R;


/**
 * @author Dayo
 * @desc 录音弹窗
 */

public class RecordPop extends BaseBottomPop {

    private OnRecordListener mListener;
    private View mView;
    private View mImg_record;
    private TextView mTx_record_status;
    private TextView mTx_record_second;
    private ImageView mImg_wave1;
    private ImageView mImg_wave2;
    private ImageView mImg_wave3;
    private Animation mAnim_wave1;
    private Animation mAnim_wave2;
    private Animation mAnim_wave3;

    public RecordPop(Context context, OnRecordListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    public View getContentView() {
        mView = View.inflate(mContext, R.layout.dingbei_pop_record, null);
        return mView;
    }

    @Override
    public void initView() {
        setBottomMargin();

        mImg_record = mView.findViewById(R.id.img_record);
        mImg_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mListener.onStart();
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    mListener.onStop(duration);
                }
                return true;
            }
        });
        mTx_record_status = mView.findViewById(R.id.tx_record_status);
        mTx_record_second = mView.findViewById(R.id.tx_record_second);
        //录音动画
        mImg_wave1 = mView.findViewById(R.id.img_wave1);
        mImg_wave2 = mView.findViewById(R.id.img_wave2);
        mImg_wave3 = mView.findViewById(R.id.img_wave3);
        mAnim_wave1 = AnimationUtils.loadAnimation(mContext, R.anim.wave);
        mAnim_wave2 = AnimationUtils.loadAnimation(mContext, R.anim.wave);
        mAnim_wave3 = AnimationUtils.loadAnimation(mContext, R.anim.wave);

        mView.findViewById(R.id.ly_outside).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void showAtLocation(View parent) {
        mTx_record_second.setText("0''");
        mTx_record_status.setText("按住说话");
        super.showAtLocation(parent);
    }

    private int duration = 0;
    private Handler mHandler = new Handler();
    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            duration += 1;
            mTx_record_second.setText(duration + "''");
            mHandler.postDelayed(timer, 1000);
        }
    };

    public void start() {
        mImg_wave1.startAnimation(mAnim_wave1);
        mImg_wave2.postDelayed(new Runnable() {
            @Override
            public void run() {
                mImg_wave2.startAnimation(mAnim_wave2);
            }
        }, 1000);
        mImg_wave3.postDelayed(new Runnable() {
            @Override
            public void run() {
                mImg_wave3.startAnimation(mAnim_wave3);
            }
        }, 2000);

        mTx_record_status.setText("可以说话");

        //计时
        duration = 0;
        mHandler.postDelayed(timer, 1000);
    }

    public void stop() {
        mImg_wave1.clearAnimation();
        mImg_wave2.clearAnimation();
        mImg_wave3.clearAnimation();
        mHandler.removeCallbacks(timer);
        dismiss();
    }

    public interface OnRecordListener {
        void onStart();

        void onStop(int duration);
    }
}
