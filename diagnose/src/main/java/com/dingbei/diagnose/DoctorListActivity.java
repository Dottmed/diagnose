package com.dingbei.diagnose;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.bean.ConfigBean;
import com.dingbei.diagnose.bean.DepartmentBean;
import com.dingbei.diagnose.bean.HospitalBean;
import com.dingbei.diagnose.bean.SignBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.message.MessageEvent;
import com.dingbei.diagnose.message.MessageType;
import com.dingbei.diagnose.view.HospitalPop;
import com.dingbei.diagnose.view.SlidingTabLayout;
import com.dingbei.diagnose.view.recyc.CommonAdapter;
import com.dingbei.diagnose.view.recyc.ViewHolder;
import com.dingbei.diagnose.view.viewpager.TabFragmentPagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Dayo
 * @desc 选择全科医生页面
 */

public class DoctorListActivity extends BaseAutoActivity {

    private String mPatientId;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabs;
    private String mInquiryNo;
    private SignBean mSign;
    private boolean mIsLand;
    private RecyclerView mRecycler_tag;
    private ArrayList<DepartmentBean> mTagList;
    private CommonAdapter<DepartmentBean> mTagAdapter;
    private int mSelectedTag;
    private DoctorListFragment mFragment;
    private LinearLayoutManager tagLayoutManager;
    private View mImgBack;
    private TextView mTx_hospital;
    private String mHospital;
    private String mDepartment;
    private HospitalPop mHospitalPop;

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_doctor_list;
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        //判断是否横屏
        mIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mPatientId = getIntent().getStringExtra("patient_id");
        mInquiryNo = getIntent().getStringExtra("inquiry_no");
        mSign = (SignBean) getIntent().getSerializableExtra("sign");

        initView();
        getTags();
    }

    private void initView() {
        setBack();
        setTitle("选择医生");

        mTx_hospital = findViewById(R.id.tx_hospital);
        mTx_hospital.setOnClickListener(v -> {
            mHospitalPop.showAtLocation(mTx_hospital);
        });

        mHospitalPop = new HospitalPop(this, (name, id) -> {
            mTx_hospital.setText(name);
            mHospital = id;
            if(mIsLand) {
                mFragment.filter(mHospital, mDepartment);
            }else {
                MessageEvent event = new MessageEvent(MessageType.REFRESH_HOSPITAL);
                event.setExtra(mHospital);
                EventBus.getDefault().post(event);
            }
        });

        if(mIsLand) {
            mImgBack = findViewById(R.id.img_back);
            mRecycler_tag = findViewById(R.id.recycler_tag);
            tagLayoutManager = new LinearLayoutManager(this);
            tagLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecycler_tag.setLayoutManager(tagLayoutManager);
            mRecycler_tag.setHasFixedSize(false);
            setTagAdapter();

        } else {
            mViewPager = findViewById(R.id.viewpager);
            mSlidingTabs = findViewById(R.id.sliding_tabs);
        }
    }

    private void setTagAdapter() {
        mTagList = new ArrayList<>();
        mTagAdapter = new CommonAdapter<DepartmentBean>(this, R.layout.dingbei_item_tags, mTagList) {
            @Override
            protected void convert(ViewHolder holder, final DepartmentBean bean, final int position) {
                TextView tx_name = holder.getView(R.id.tx_name);
                tx_name.setText(bean.getName());

                View ly_item = holder.getView(R.id.ly_item);
                ly_item.setSelected(mSelectedTag == position);
                ly_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedTag = position;
                        notifyDataSetChanged();

                        mDepartment = bean.getId();
                        mFragment.filter(mHospital, mDepartment);
                    }
                });
            }
        };
        mRecycler_tag.setAdapter(mTagAdapter);
    }

    private void getTags() {
        HttpUtil.get("sys/config/", null, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                ConfigBean bean = JSONObject.parseObject(json, ConfigBean.class);

                List<HospitalBean> hospitals = bean.getAll_hospital();
                if(hospitals != null && hospitals.size() > 0) {
                    HospitalBean hos = hospitals.get(0);
                    mTx_hospital.setText(hos.getName());
                    mHospital = hos.getId();
                    mHospitalPop.setData(hospitals);
                }

                List<DepartmentBean> departments = bean.getDepartments();
                DepartmentBean all = new DepartmentBean();
                all.setName("全部");
                all.setId("0");
                departments.add(0, all);

                if(mIsLand) {
                    initTagsLand(departments);
                } else {
                    initTags(departments);
                }
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

    private void initTagsLand(List<DepartmentBean> departments) {
        mTagList.clear();
        mTagList.addAll(departments);
        mTagAdapter.notifyDataSetChanged();
        mRecycler_tag.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshFocus();
            }
        });

        mFragment = new DoctorListFragment();
        mFragment.setArguments(getBundle("0"));
        getSupportFragmentManager().beginTransaction().add(R.id.ly_container, mFragment, "doctors")
                .commitAllowingStateLoss();
    }

    private void initTags(List<DepartmentBean> tags) {
        TabFragmentPagerAdapter pagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < tags.size(); i++) {
            DepartmentBean tag = tags.get(i);
            pagerAdapter.addTab(tag.getName(), DoctorListFragment.class, getBundle(tag.getId()));
        }
        mViewPager.setAdapter(pagerAdapter);
        //设置预加载页数，不然滑动会卡顿
        mViewPager.setOffscreenPageLimit(3);

        mSlidingTabs.setCustomTabView(R.layout.dingbei_item_sliding_tab, R.id.tx_tab);
        mSlidingTabs.setViewPager(mViewPager);
        //分隔线不显示
        mSlidingTabs.setDividerColors(getResources().getColor(android.R.color.transparent));
        //指示器
        mSlidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.theme));
        //通过DividerPadding控制Indicator的长短
        mSlidingTabs.setDividerPadding(40, 40);
    }

    private Bundle getBundle(String tag) {
        Bundle bundle = new Bundle();
        bundle.putString("hospital", mHospital);
        bundle.putString("department", tag);
        bundle.putString("patient", mPatientId);
        bundle.putString("inquiry_no", mInquiryNo);
        bundle.putSerializable("sign", mSign);
        return bundle;
    }

    private void refreshFocus() {
        if (getItemView(tagLayoutManager, mSelectedTag) != null) {
            getItemView(tagLayoutManager, mSelectedTag).requestFocus();
        }
    }

    public View getItemView(RecyclerView.LayoutManager layoutManager, int position){
        return layoutManager.findViewByPosition(position);
    }

}
