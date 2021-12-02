package com.dingbei.diagnose.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dingbei.diagnose.R;


/**
 * 拍摄视频进度条
 */

public class RecorderProgress extends View {


    private Paint mPaint = new Paint();

    private volatile State mState = State.PAUSE;

    private int maxRecorderTime = 10000;

    private int minRecorderTime = 2000;

    private int afProgressColor = 0xFF00FF00;

    private int bfProgressColor = 0xFFFC2828;

    private long startTime;

    private Context mContext;

    public RecorderProgress(Context context) {
        super(context, null);
    }


    public RecorderProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecorderProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecorderProgress);
        afProgressColor = a.getColor(R.styleable.RecorderProgress_af_progress_color, afProgressColor);
        bfProgressColor = a.getColor(R.styleable.RecorderProgress_bf_progress_color, bfProgressColor);
        maxRecorderTime = a.getInteger(R.styleable.RecorderProgress_max_recorder_time, maxRecorderTime);
        minRecorderTime = a.getInt(R.styleable.RecorderProgress_min_recorder_time, minRecorderTime);

        init(context);
    }


    private void init(Context context) {
        this.mContext = context;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bfProgressColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long currTime = System.currentTimeMillis();

        if (mState == State.START) {
            int measuredWidth = getMeasuredWidth();

            float mSpeed = measuredWidth / 2.0f / maxRecorderTime; //速度   = 甲的距离 ／ 总时间

            float druTime = (currTime - startTime);   // 时间

            if (druTime >= minRecorderTime) {
                mPaint.setColor(afProgressColor);
            }


            float dist = mSpeed * druTime; //甲 在druTime 行走的距离

            if (dist < measuredWidth / 2.0f) {  //判断是否到达终点

                canvas.drawRect(dist, 0.0f, measuredWidth - dist, getMeasuredHeight(), mPaint);//绘制进度条
                invalidate();

            }
        } else {
            return;
        }
        canvas.drawRect(0.0f, 0.0f, 0.0f, getMeasuredHeight(), mPaint);
    }


    public void startAnimation() {
        if (mState != State.START) {
            mState = State.START;
            this.startTime = System.currentTimeMillis();
            invalidate();
            setVisibility(VISIBLE);
            mPaint.setColor(bfProgressColor);
        }
    }

    public void stopAnimation() {
        if (mState != State.PAUSE) {
            mState = State.PAUSE;
            setVisibility(INVISIBLE);
        }
    }


    enum State {
        START(1, "开始"),
        PAUSE(2, "暂停");

        State(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
