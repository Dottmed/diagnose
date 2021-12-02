package com.dingbei.diagnose.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dingbei.diagnose.R;


/**
 * @author Dayo
 * @time 2017/8/1 11:09
 * @desc 提示对话框
 */

public class DialogPop extends BasePop implements View.OnClickListener {

    private OnDialogListener mListener;
    private TextView mTx_tip;
    private TextView mTx_cancel;
    private TextView mTx_content;
    private View mView;
    private TextView mTx_confirm;

    public DialogPop(Context context) {
        super(context);
    }

    @Override
    public View getContentView() {
        mView = View.inflate(mContext, R.layout.dingbei_pop_dialog, null);
        return mView;
    }

    @Override
    public void initView() {
        mTx_tip = mView.findViewById(R.id.tx_tip);
        mTx_confirm = mView.findViewById(R.id.tx_confirm);
        mTx_confirm.setOnClickListener(this);
        mTx_cancel = mView.findViewById(R.id.tx_cancel);
        mTx_cancel.setOnClickListener(this);

        mTx_content = mView.findViewById(R.id.tx_content);

        FrameLayout ly_outside = mView.findViewById(R.id.ly_outside);
        ly_outside.setOnClickListener(this);
    }


    /**
     * 设置提示信息
     * @param tip
     */
    public void setTip(String tip){
        mTx_tip.setText(tip);
    }

    /**
     * 设置确定按钮文案
     * @param confirm
     */
    public void setConfirm(String confirm){
        mTx_confirm.setText(confirm);
    }

    /**
     * 设置一般的监听
     * @param listener
     */
    public void setListener(OnDialogListener listener){
        mListener = listener;
    }

    /**
     * 设置提示框内容
     * @param content
     */
    public void setContent(String content){
        mTx_content.setVisibility(View.VISIBLE);
        mTx_content.setText(content);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tx_confirm) {
            mPop.dismiss();
            if(mListener != null) {
                mListener.onConfirm();
            }
        }
        else if(id == R.id.tx_cancel) {
            dismiss();
            if(mListener != null) {
                mListener.onCancel();
            }
        }
        else if(id == R.id.ly_outside) {
            dismiss();
        }
    }

    @Override
    public void showAsDropDown(View anchor){
        super.showAsDropDown(anchor);
        if (mTx_cancel != null) {
            mTx_cancel.requestFocus();
        }
    }

    @Override
    public void showAtLocation(View parent) {
        super.showAtLocation(parent);
        if (mTx_cancel != null) {
            mTx_cancel.requestFocus();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void showMsg(String msg) {
        super.showMsg(msg);
    }

    public interface OnDialogListener {
        void onConfirm();

        void onCancel();
    }

}
