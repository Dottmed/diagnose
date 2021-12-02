package com.dingbei.diagnose.gallery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.adapter.PhotoPreviewAdapter;
import com.dingbei.diagnose.gallery.adapter.PreviewAdapter;
import com.dingbei.diagnose.gallery.model.PhotoInfo;
import com.dingbei.diagnose.gallery.widget.GFViewPager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Desction:
 * Author:pengjianbo
 * Date:2015/12/29 0029 14:43
 */
public class PhotoPreviewActivity extends PhotoBaseActivity implements ViewPager.OnPageChangeListener{

    public static final String PHOTO_LIST = "photo_list";
    public static final String ALL_PHOTOS = "all_photos";
    public static final String CURRENT_POSITION = "current_position";
    public static final String RESULT = "result";
    public static final int RESULT_CODE_BACK = 0;
    public static final int RESULT_CODE_SEND = 1;
    public static final String MAX_COUNT = "max";

    private RelativeLayout mTitleBar;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvIndicator;

    private GFViewPager mVpPager;
    private List<PhotoInfo> mPhotoList;
    private ArrayList<PhotoInfo> mSelectedList;
    private PhotoPreviewAdapter mPhotoPreviewAdapter;

    private ThemeConfig mThemeConfig;
    private TextView mTvSelect;
    private TextView mTvChooseCount;
    private int mMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeConfig = GalleryFinal.getGalleryTheme();

        if ( mThemeConfig == null) {
            resultFailure(getString(R.string.please_reopen_gf), true);
        } else {
            setContentView(R.layout.dingbei_gf_activity_photo_preview);
            findViews();
            setListener();
            setTheme();
            setData();
        }
    }

    private void setData() {
        mPhotoList = new ArrayList<>();

        Intent intent = getIntent();
        List<PhotoInfo> allList = (List<PhotoInfo>) intent.getSerializableExtra(ALL_PHOTOS);
        mSelectedList = (ArrayList<PhotoInfo>) intent.getSerializableExtra(PHOTO_LIST);
        mMax = intent.getIntExtra(MAX_COUNT, 0);

        int position = intent.getIntExtra(CURRENT_POSITION, 0);

        if(allList == null || allList.size() == 0) {
            mPhotoList.addAll(mSelectedList);
        }else {
            mPhotoList.addAll(allList);
        }

        //mPhotoPreviewAdapter = new PhotoPreviewAdapter(this, mPhotoList);
        //mVpPager.setAdapter(mPhotoPreviewAdapter);

        //修改：换了一个adapter
        PreviewAdapter previewAdapter = new PreviewAdapter(this, mPhotoList);
        mVpPager.setAdapter(previewAdapter);
        mVpPager.setCurrentItem(position);

        mTvChooseCount.setText(getString(R.string.send) + "(" + mSelectedList.size() + "/" + mMax + ")");
        mTvChooseCount.setSelected(mSelectedList.size() > 0);
    }

    private void findViews() {
        mTitleBar = (RelativeLayout) findViewById(R.id.titlebar);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvIndicator = (TextView) findViewById(R.id.tv_indicator);
        mTvSelect = (TextView) findViewById(R.id.tv_select);

        mVpPager = (GFViewPager) findViewById(R.id.vp_pager);
        mTvChooseCount = (TextView) findViewById(R.id.tv_choose_count);
    }

    private void setListener() {
        mVpPager.addOnPageChangeListener(this);
        mIvBack.setOnClickListener(mBackListener);
        mTvSelect.setOnClickListener(mSelectListener);
        mTvChooseCount.setOnClickListener(mSendListener);
    }

    private void setTheme() {
        mIvBack.setImageResource(mThemeConfig.getIconBack());
        if (mThemeConfig.getIconBack() == R.drawable.ic_gf_back) {
            mIvBack.setColorFilter(mThemeConfig.getTitleBarIconColor());
        }

        setStatusBarColor(mThemeConfig.getTitleBarBgColor());
        mTitleBar.setBackgroundColor(mThemeConfig.getTitleBarBgColor());
        mTvTitle.setTextColor(mThemeConfig.getTitleBarTextColor());
        if(mThemeConfig.getPreviewBg() != null) {
            mVpPager.setBackgroundDrawable(mThemeConfig.getPreviewBg());
        }
    }

    /**
     * 改变状态栏颜色
     * @param color
     */
    public void setStatusBarColor(int color){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean contains(int photoId){
        for (PhotoInfo info : mSelectedList) {
            if(info.getPhotoId() == photoId) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAndRemove(int photoId){
        try {
            Iterator<PhotoInfo> iterator = mSelectedList.iterator();
            while (iterator.hasNext()) {
                PhotoInfo info = iterator.next();
                if (info != null && info.getPhotoId() == photoId) {
                    iterator.remove();
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void takeResult(PhotoInfo info) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTvIndicator.setText((position + 1) + "/" + mPhotoList.size());
        int photoId = mPhotoList.get(position).getPhotoId();
        mTvSelect.setSelected(contains(photoId));
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_CODE_BACK, new Intent().putExtra(RESULT, mSelectedList));
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        setResult(RESULT_CODE_BACK, new Intent().putExtra(RESULT, mSelectedList));
        super.onBackPressed();
    }

    private View.OnClickListener mSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentItem = mVpPager.getCurrentItem();
            PhotoInfo photoInfo = mPhotoList.get(currentItem);
            if(containsAndRemove(photoInfo.getPhotoId())) {
                mTvSelect.setSelected(false);
            }else {
                if(mSelectedList.size() == mMax) {
                    toast(getString(R.string.select_max_tips));
                    return;
                }
                mSelectedList.add(photoInfo);
                mTvSelect.setSelected(true);
            }

            mTvChooseCount.setText(getString(R.string.send) + "(" + mSelectedList.size() + "/" + mMax + ")");
            if(mSelectedList.size() > 0) {
                mTvChooseCount.setSelected(true);
                mTvChooseCount.setClickable(true);
            }else {
                mTvChooseCount.setSelected(false);
                mTvChooseCount.setClickable(false);
            }
        }
    };

    private View.OnClickListener mSendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_CODE_SEND, new Intent().putExtra(RESULT, mSelectedList));
            finish();
        }
    };

}
