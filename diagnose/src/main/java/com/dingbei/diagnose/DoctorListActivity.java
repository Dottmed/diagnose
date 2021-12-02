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
import com.dingbei.diagnose.bean.DepartmentListBean;
import com.dingbei.diagnose.bean.DoctorListBean;
import com.dingbei.diagnose.bean.SignBean;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.view.SlidingTabLayout;
import com.dingbei.diagnose.view.recyc.CommonAdapter;
import com.dingbei.diagnose.view.recyc.EmptyWrapper;
import com.dingbei.diagnose.view.recyc.ViewHolder;
import com.dingbei.diagnose.view.viewpager.TabFragmentPagerAdapter;

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
    private ArrayList<DepartmentListBean.ResultsBean> mTagList;
    private CommonAdapter<DepartmentListBean.ResultsBean> mTagAdapter;
    private int mSelectedTag;
    private RecyclerView mRecycler;
    private ArrayList<DoctorListBean.ResultBean> mList;
    private EmptyWrapper mAdapter;
    private DoctorListFragment mFragment;
    private LinearLayoutManager tagLayoutManager;
    private View mImgBack;

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

        if(mIsLand) {
            mImgBack = findViewById(R.id.img_back);
            mRecycler_tag = findViewById(R.id.recycler_tag);
            tagLayoutManager = new LinearLayoutManager(this);
            tagLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecycler_tag.setLayoutManager(tagLayoutManager);
            mRecycler_tag.setHasFixedSize(false);
            setTagAdapter();

            mFragment = new DoctorListFragment();
            mFragment.setArguments(getBundle("0"));
            getSupportFragmentManager().beginTransaction().add(R.id.ly_container, mFragment, "doctors")
                    .commitAllowingStateLoss();

        } else {
            mViewPager = findViewById(R.id.viewpager);
            mSlidingTabs = findViewById(R.id.sliding_tabs);
        }
    }

    private void setTagAdapter() {
        mTagList = new ArrayList<>();
        mTagAdapter = new CommonAdapter<DepartmentListBean.ResultsBean>(this, R.layout.dingbei_item_tags, mTagList) {
            @Override
            protected void convert(ViewHolder holder, final DepartmentListBean.ResultsBean bean, final int position) {
                TextView tx_name = holder.getView(R.id.tx_name);
                tx_name.setText(bean.getName());

                View ly_item = holder.getView(R.id.ly_item);
                ly_item.setSelected(mSelectedTag == position);
                ly_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedTag = position;
                        notifyDataSetChanged();
                        mFragment.filter(bean.getId());
                    }
                });
            }
        };
        mRecycler_tag.setAdapter(mTagAdapter);
    }

    private void getTags() {
        HttpUtil.get("refferal/get_quanke_doctors/", null, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                DepartmentListBean bean = JSONObject.parseObject(json, DepartmentListBean.class);
                List<DepartmentListBean.ResultsBean> results = bean.getResults();
                DepartmentListBean.ResultsBean all = new DepartmentListBean.ResultsBean();
                all.setName("全部");
                all.setId("0");
                results.add(0, all);

                if(mIsLand) {
                    mTagList.clear();
                    mTagList.addAll(results);
                    mTagAdapter.notifyDataSetChanged();
                    mRecycler_tag.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            refreshFocus();
                        }
                    });
                } else {
                    initTags(results);
                }
            }

            @Override
            public void onError(ErrorBean error) {
                showError(error);
            }
        });
    }

    private void initTags(List<DepartmentListBean.ResultsBean> tags) {
        TabFragmentPagerAdapter pagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), this);
        for (int i = 0; i < tags.size(); i++) {
            DepartmentListBean.ResultsBean tag = tags.get(i);
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
