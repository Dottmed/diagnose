package com.dingbei.diagnose;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;



/**
 * @author Dayo
 * @desc H5页面，有以下name可选：
 */

public class H5Activity extends BaseAutoActivity {

    public static final String EX_TITLE = "title";
    public static final String EX_URL = "url";
    public static final String EX_RIGHT = "right"; //右上角类型

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_h5;
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(EX_TITLE);
        String url = intent.getStringExtra(EX_URL);
        String right = intent.getStringExtra(EX_RIGHT);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View ly_content = findViewById(R.id.ly_content);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ly_content.getLayoutParams();
            layoutParams.width = dm.widthPixels / 2;
        }

        ImageView img_back = setBack();
        img_back.setImageResource(R.drawable.ic_finish);

        H5Fragment fragment = new H5Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(H5Fragment.EX_URL, url);
        if(!TextUtils.isEmpty(title)) {
            setTitle(title);
            bundle.putBoolean(H5Fragment.HAS_TITLE, true);
        }
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, fragment, "h5")
                .commitAllowingStateLoss ();
    }

}
