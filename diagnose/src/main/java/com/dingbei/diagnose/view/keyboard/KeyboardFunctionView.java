package com.dingbei.diagnose.view.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.dingbei.diagnose.R;


public class KeyboardFunctionView extends RelativeLayout {

    protected View view;
    private View mTx_pictures;
    private View mTx_video;
    private View mTx_rtc;
    private View mTx_ecg;

    public KeyboardFunctionView(Context context) {
        this(context, null);
    }

    public KeyboardFunctionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dingbei_view_keyboard_function, this);
        init();
    }

    protected void init(){
        mTx_pictures = view.findViewById(R.id.tx_pictures);
        mTx_video = view.findViewById(R.id.tx_video);
        mTx_rtc = view.findViewById(R.id.tx_rtc);
        mTx_ecg = view.findViewById(R.id.tx_ecg);

        mTx_pictures.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mTx_pictures.setFocusable(view.getVisibility() == View.VISIBLE);
        });

        mTx_video.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mTx_video.setFocusable(view.getVisibility() == View.VISIBLE);
        });

        mTx_rtc.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mTx_rtc.setFocusable(view.getVisibility() == View.VISIBLE);
        });

        mTx_ecg.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mTx_ecg.setFocusable(view.getVisibility() == View.VISIBLE);
        });

    }

    public void setRTCEnable(boolean enable) {
        mTx_rtc.setVisibility(enable ? VISIBLE : GONE);
    }

    public void setFunctionListener(final OnFunctionClickListener listener) {
        if(mTx_pictures != null) {
            mTx_pictures.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPicturesClick();
                }
            });
        }

        if(mTx_video != null) {
            mTx_video.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onVideoClick();
                }
            });
        }

        if(mTx_rtc != null) {
            mTx_rtc.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRtcClick();
                }
            });
        }

        if(mTx_ecg != null) {
            mTx_ecg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEcgClick();
                }
            });
        }
    }

    public interface OnFunctionClickListener {

        void onPicturesClick();

        void onVideoClick();

        void onRtcClick();

        void onEcgClick();
    }
}
