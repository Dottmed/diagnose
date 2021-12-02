package com.dingbei.diagnose.view.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TabFragmentPagerAdapter extends CustomFragmentPagerAdapter{

    private Context mContext;
    private ArrayList<TabInfo> mTabs = new ArrayList<>();

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(String tag, Class<?> clss, Bundle args) {
            this.tag = tag;
            this.clss = clss;
            this.args = args;
        }
    }

    public void addTab(String title, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(title, clss, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    public TabFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).tag;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
