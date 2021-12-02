package com.dingbei.diagnose;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.bean.DepartmentBean;
import com.dingbei.diagnose.bean.DoctorListBean;
import com.dingbei.diagnose.bean.HospitalBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpParams;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.utils.ImageLoadUtil;
import com.dingbei.diagnose.view.DialogPop;

/**
 * @author Dayo
 * @time 2019/9/29 15:41
 * @desc 等待接诊
 */
public class WaitingActivity extends BaseAutoActivity {

    private DoctorListBean.ResultBean mDoctor;
    private ImageView mImg_waiting;
    private TextView mTx_before;
    private ImageView mImg_avatar;
    private TextView mTx_name;
    private TextView mTx_level;
    private TextView mTx_hospital;
    private TextView mTx_merit;
    private TextView mTx_desc;
    private Runnable mRunnable;
    private HttpParams mParams;
    private String mRoom;
    private String mReception;
    private TextView mTx_tip1;
    private TextView mTx_tip2;
    private DialogPop mCancelPop;
    private boolean mHasDone;

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_waiting;
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        mRoom = intent.getStringExtra("room");
        mReception = intent.getStringExtra("reception");
        mDoctor = (DoctorListBean.ResultBean) intent.getSerializableExtra("doctor");

        mParams = new HttpParams();
        mParams.put("room", mRoom);
        initView();
        setData();
    }

    @Override
    public void onBackPressed() {
        if(mHasDone) {
            super.onBackPressed();
        }else {
            mCancelPop.showAtLocation(mImg_waiting);
        }
    }

    private void initView() {
        setBack();
        setTitle("等待问诊");

        mImg_waiting = findViewById(R.id.img_waiting);
        AnimationDrawable anim = (AnimationDrawable) mImg_waiting.getDrawable();
        anim.start();

        mTx_tip1 = findViewById(R.id.tx_tip1);
        mTx_tip2 = findViewById(R.id.tx_tip2);
        mTx_before = findViewById(R.id.tx_before);
        mImg_avatar = findViewById(R.id.img_avatar);
        mTx_name = findViewById(R.id.tx_name);
        mTx_level = findViewById(R.id.tx_level);
        mTx_hospital = findViewById(R.id.tx_hospital);
        mTx_merit = findViewById(R.id.tx_merit);
        mTx_desc = findViewById(R.id.tx_desc);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                getData();
                mTx_before.postDelayed(mRunnable, 10000);
            }
        };
        mTx_before.post(mRunnable);

        mCancelPop = new DialogPop(this);
        mCancelPop.setTip("确定取消等待？");
        mCancelPop.setListener(new DialogPop.OnDialogListener() {
            @Override
            public void onConfirm() {
                cancel();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void setData() {
        if(mDoctor != null) {
            ImageLoadUtil.setImageUrl(this, mDoctor.getAvatar(), mImg_avatar);
            mTx_name.setText(mDoctor.getName());
            mTx_level.setText(mDoctor.getLevel());

            HospitalBean hospital = mDoctor.getHospital();
            DepartmentBean department = mDoctor.getDepartment();
            if(hospital != null && department != null) {
                mTx_hospital.setText(hospital.getName() + "    " + department.getName());
            }
            mTx_merit.setText(mDoctor.getMerit());
            mTx_desc.setText(mDoctor.getDesc());
        }
    }

    private void getData() {
        HttpUtil.get("refferal/huizhen_number/", mParams, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                String number = JSONObject.parseObject(json).getString("number");
                mTx_before.setText(number);
                if("-1".equals(number)) {
                    mHasDone = true;
                    mTx_before.removeCallbacks(mRunnable);
                    mTx_tip1.setText("已过期，请重新排队");
                    mTx_tip2.setText("");
                    mTx_before.setText("");
                    mTx_tip1.setTextColor(0xFFFF4B4D);
                }
                else if("0".equals(number)) {
                    Intent intent = new Intent(WaitingActivity.this, StartChatActivity.class);
                    intent.putExtra("room", mRoom);
                    intent.putExtra("reception", mReception);

                    if(mDoctor != null) {
                        intent.putExtra("avatar", mDoctor.getAvatar());
                        intent.putExtra("name", mDoctor.getName());
                        intent.putExtra("level", mDoctor.getLevel());
                        HospitalBean hospital = mDoctor.getHospital();
                        DepartmentBean department = mDoctor.getDepartment();
                        if(hospital != null && department != null) {
                            intent.putExtra("hospital", hospital.getName() + "    " + department.getName());
                        }
                    }

                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

    private void cancel() {
        HttpParams params = new HttpParams();
        params.put("room", mRoom);
        HttpUtil.get("refferal/cancel_huizhen/", params, new BaseCallback() {
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

    @Override
    protected void onDestroy() {
        if(mTx_before != null && mRunnable != null) {
            mTx_before.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }
}
