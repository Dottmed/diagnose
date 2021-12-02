package com.dingbei.diagnose.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.dingbei.diagnose.R;
import com.dingbei.diagnose.gallery.widget.zoonview.PhotoView;
import com.dingbei.diagnose.utils.ImageLoadUtil;


/**
 * @author Dayo
 * @time 2018/10/24 16:28
 * @desc 查看单张大图片
 */
public class BigImageViewerActivity extends AppCompatActivity {

    public static void startViewer(Context context, String path) {
        Intent intent = new Intent();
        intent.setClass(context, BigImageViewerActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dingbei_activity_big_image);
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

    private void initView() {
        setStatusBarColor(getResources().getColor(R.color.status_bar));

        PhotoView img_picture = findViewById(R.id.img_picture);
        String path = getIntent().getStringExtra("path");
        ImageLoadUtil.setImageUrlOverride(this, path, img_picture, 3000, 2000);
    }
}
