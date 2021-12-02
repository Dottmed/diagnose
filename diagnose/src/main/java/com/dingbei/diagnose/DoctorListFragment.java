package com.dingbei.diagnose;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.bean.DepartmentBean;
import com.dingbei.diagnose.bean.DoctorListBean;
import com.dingbei.diagnose.bean.HospitalBean;
import com.dingbei.diagnose.bean.SignBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpParams;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.utils.DiagnoseUtil;
import com.dingbei.diagnose.utils.ImageLoadUtil;
import com.dingbei.diagnose.view.recyc.CommonAdapter;
import com.dingbei.diagnose.view.recyc.EmptyWrapper;
import com.dingbei.diagnose.view.recyc.ViewHolder;

import java.util.ArrayList;


/**
 * @author Dayo
 * @time 2019/1/25 10:57
 * @desc 医生列表
 */
public class DoctorListFragment extends BaseFragment {

    private View mView;
    private String mDepartmentId;
    private HttpParams mParams;
    private ArrayList<DoctorListBean.ResultBean> mList;
    private String mNextUrl;
    private EmptyWrapper mAdapter;
    private String mPatientId;
    private RecyclerView mRecycler;
    private String mInquiryNo;
    private SignBean mSign;

    @Override
    protected View onCreatingView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null) {
            mDepartmentId = bundle.getString("department");
            mPatientId = bundle.getString("patient");
            mInquiryNo = bundle.getString("inquiry_no");
            mSign = (SignBean) bundle.getSerializable("sign");
        }
        mView = inflater.inflate(R.layout.dingbei_fragment_doctor_list, null);
        initView();

        mParams = new HttpParams();
        if(!"0".equals(mDepartmentId)) { //0是全部 不传
            mParams.put("department", mDepartmentId);
        }
        getData();

        return mView;
    }

    private void initView() {
        initRefreshLayout(mView);

        mRecycler = mView.findViewById(R.id.recycler);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setHasFixedSize(false);
        setAdapter();
    }

    private void setAdapter() {
        mList = new ArrayList<>();
        CommonAdapter<DoctorListBean.ResultBean> adapter = new CommonAdapter<DoctorListBean.ResultBean>(getContext(), R.layout.dingbei_item_doctors, mList) {
            @Override
            protected void convert(ViewHolder helper, final DoctorListBean.ResultBean item, int position) {
                helper.setText(R.id.tx_name, item.getName());
                String desc = "";
                HospitalBean hospital = item.getHospital();
                if(hospital != null) {
                    desc = hospital.getName();
                }
                desc = desc + item.getLevel();
                helper.setText(R.id.tx_desc, desc);
                helper.setText(R.id.tx_wait, item.getPending_count() + "人候诊");

                DepartmentBean department = item.getDepartment();
                if(department != null) {
                    helper.setText(R.id.tx_tag, department.getName());
                }

                if("1".equals(item.getOnline_status())) {
                    helper.setText(R.id.tx_status, "在线");
                    helper.getView(R.id.tx_status).setBackgroundResource(R.drawable.bg_theme_r8);
                    helper.setImageUrl(R.id.img_avatar, item.getAvatar());

                }else if("2".equals(item.getOnline_status())) {
                    helper.setText(R.id.tx_status, "在忙");
                    helper.getView(R.id.tx_status).setBackgroundResource(R.drawable.bg_red_r8);
                    helper.setImageUrl(R.id.img_avatar, item.getAvatar());

                }else {
                    helper.setText(R.id.tx_status, "离线");
                    helper.getView(R.id.tx_status).setBackgroundResource(R.drawable.bg_gray_light_r8);
                    ImageLoadUtil.setGrayScale(getContext(), item.getAvatar(), (ImageView) helper.getView(R.id.img_avatar));
                }

                helper.setOnClickListener(R.id.ly_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if("1".equals(item.getOnline_status())) {
                            getRoom(item);
                        }else {
                            showMsg("该医生离线或在忙，请选择在线的医生");
                        }
                    }
                });
            }
        };
        mAdapter = new EmptyWrapper(adapter);
        mRecycler.setAdapter(mAdapter);
    }

    private void getData() {
        String url = TextUtils.isEmpty(mNextUrl) ? "doctors/" : mNextUrl;
        HttpUtil.get(url, mParams, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                DoctorListBean bean = JSONObject.parseObject(json, DoctorListBean.class);
                if(TextUtils.isEmpty(mNextUrl)) {
                    mList.clear();
                }
                mList.addAll(bean.getResults());
                mAdapter.setEmptyView(R.layout.dingbei_empty_view);
                mAdapter.notifyDataSetChanged();
                mRecycler.scrollToPosition(0);

                mNextUrl = bean.getNext();
                closeRefresh();
                closeLoadmore();
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
                closeRefresh();
                closeLoadmore();
            }
        });
    }

    public void filter(String tag) {
        if("0".equals(tag)) {
            //0是全部 不传
            mParams.remove("department");
        }else {
            mParams.put("department", tag);
        }

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mNextUrl = null;
        getData();
    }

    @Override
    public void onLoadmore() {
        if(!TextUtils.isEmpty(mNextUrl)) {
            getData();
        }else {
            closeLoadmore();
        }
    }

    private void getRoom(final DoctorListBean.ResultBean doctor) {
        HttpParams params = new HttpParams();
        params.put("patient", mPatientId);
        params.put("doctor", doctor.getId());
        params.put("sn", mInquiryNo);
        params.put("sn_type", DiagnoseUtil.SN_TYPE);
        if(mSign != null) {
            params.put("height", mSign.getHeight());
            params.put("weight", mSign.getWeight());
            params.put("temperature", mSign.getTemperature());
            params.put("blood_oxygen", mSign.getBlood_oxygen());
            params.put("blood_pressure", mSign.getBlood_pressure());
            params.put("blood_sugar", mSign.getBlood_sugar());
            params.put("pulse_rate", mSign.getPulse_rate());
        }
        HttpUtil.post("diagnosis/new/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                JSONObject object = JSONObject.parseObject(json);
                String room = object.getString("room");
                startActivity(new Intent(getContext(), WaitingActivity.class)
                        .putExtra("room", room)
                        .putExtra("doctor", doctor)
                        .putExtra("reception", doctor.getId()));
                getActivity().finish();
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

}
