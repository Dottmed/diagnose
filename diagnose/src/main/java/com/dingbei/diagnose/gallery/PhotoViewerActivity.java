package com.dingbei.diagnose.gallery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.adapter.HackyPagerAdapter;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.ToastUtil;
import com.dingbei.diagnose.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class PhotoViewerActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private HackyViewPager mViewpager;
    private HackyPagerAdapter adapter;
    private ImageView mImgDownload;
    private int mCurrentItem;
    private ArrayList<String> list;
    private ArrayList<ImageView> mDots;
    private TextView mTx_indicator;
    private ImageView mImgBack;
    private ImageView mImgDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dingbei_activity_photo_preview);
        initView();
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

    public static void startViewer(Context context, ArrayList<String> list, int currentItem) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoViewerActivity.class);
        intent.putExtra("list", list);
        intent.putExtra("currentItem", currentItem);
        context.startActivity(intent);
    }

    protected void initView() {
        setStatusBarColor(GalleryFinal.getGalleryTheme().getTitleBarBgColor());

        Intent intent = getIntent();
        list = (ArrayList<String>) intent.getSerializableExtra("list");

        if (list != null && list.size() > 0) {
            mViewpager = (HackyViewPager) this.findViewById(R.id.viewpager);
            adapter = new HackyPagerAdapter(this);
            adapter.setDate(list);
            mViewpager.setAdapter(adapter);
            mCurrentItem = intent.getIntExtra("currentItem", 0);
            mViewpager.setCurrentItem(mCurrentItem);

            mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mCurrentItem = position;
//                    changeIndicator();
                    mTx_indicator.setText(String.format("%d / %d", mCurrentItem + 1, list.size()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            mImgDownload = (ImageView) findViewById(R.id.img_download);
            mImgDownload.setOnClickListener(this);

            mTx_indicator = findViewById(R.id.tx_indicator);
            mTx_indicator.setText(String.format("%d / %d", mCurrentItem + 1, list.size()));

//            mDots = new ArrayList<>();
//            LinearLayout layout_indicator = (LinearLayout) findViewById(R.id.layout_indicator);
//            int size = list.size();
//            for(int i = 0; i < size; i++) {
//                ImageView dot = new ImageView(this);
//                dot.setImageResource(i == mCurrentItem ? R.drawable.ic_indicator_sel : R.drawable.ic_indicator);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.setMargins(12, 12, 12, 12);
//                dot.setLayoutParams(layoutParams);
//                layout_indicator.addView(dot);
//                mDots.add(dot);
//            }
        }

        mImgBack = (ImageView) findViewById(R.id.img_back);
        mImgBack.setOnClickListener(this);

        mImgDelete = findViewById(R.id.img_delete);

    }

    private void changeIndicator() {
        int size = mDots.size();
        for(int i = 0; i < size; i++) {
            ImageView dot = mDots.get(i);
            dot.setImageResource(i == mCurrentItem ? R.drawable.ic_indicator_sel : R.drawable.ic_indicator);
        }
    }

    public void showMsg(String msg) {
        ToastUtil.show(msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.img_back) {
            finish();
        }
        else if(id == R.id.img_download) {
            requestStoragePermission();
        }
    }

    private void downloadImage() {
//        FileUtil.downloadImage(this, list.get(mCurrentItem));
    }

    private void requestStoragePermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            downloadImage();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_storage), 11, perms);
        }
    }

    /**
     * 处理权限 具体操作由子类实现onPermissionsGranted，onPermissionsDenied方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode == 11) {
            downloadImage();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        showMsg(getString(R.string.permission_denied));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        return handleKeyEvent(action, keyCode) || super.dispatchKeyEvent(event);
    }

    private boolean handleKeyEvent(int action, int keyCode) {
        if (action != KeyEvent.ACTION_DOWN) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //确定键enter
                AppLogger.e("确定键enter");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //向下键
                AppLogger.e("向下键");
                setFocusable(true);
                if (mImgBack.isFocused()) {
                    mImgDownload.requestFocus();
                } else if (mImgDownload.isFocused()){
                    mImgDelete.requestFocus();
                } else if (mImgDelete.isFocused()){
                    mImgBack.requestFocus();
                } else {
                    mImgBack.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                //向上键
                AppLogger.e("向上键");
                setFocusable(true);
                if (mImgBack.isFocused()) {
                    mImgDelete.requestFocus();
                } else if (mImgDownload.isFocused()){
                    mImgBack.requestFocus();
                } else if (mImgDelete.isFocused()){
                    mImgDownload.requestFocus();
                } else {
                    mImgBack.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //向左键
                AppLogger.e("向左键");
                setFocusable(false);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //向右键
                AppLogger.e("向右键");
                setFocusable(false);
                break;
            default:
                break;
        }
        return false;
    }

    private void setFocusable(boolean b) {
        mImgBack.setFocusable(b);
        mImgDownload.setFocusable(b);
        mImgDelete.setFocusable(b);
    }

}
