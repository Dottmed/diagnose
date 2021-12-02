package com.dingbei.diagnose;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingbei.diagnose.utils.ImageLoadUtil;


/**
 * @author Dayo
 * @time 2019/9/30 10:22
 * @desc 开始会话
 */
public class StartChatActivity extends BaseAutoActivity implements View.OnClickListener {

    private ImageView mImg_avatar;
    private TextView mTx_name;
    private TextView mTx_level;
    private TextView mTx_hospital;
    private Intent mIntent;
    private ImageView mImageClose;
    private ImageView mImgStart;

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_start_chat;
    }

    @Override
    public void setStatusBarColor(int color) {
        View decorView = getWindow().getDecorView();
        //使用状态栏沉浸式
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);

        //设置透明状态栏
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //透明底部导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        mIntent = getIntent();
        initView();
    }

    private void initView() {
        mImg_avatar = findViewById(R.id.img_avatar);
        mTx_name = findViewById(R.id.tx_name);
        mTx_level = findViewById(R.id.tx_level);
        mTx_hospital = findViewById(R.id.tx_hospital);

        ImageLoadUtil.setImageUrl(this, mIntent.getStringExtra("avatar"), mImg_avatar);
        mTx_name.setText(mIntent.getStringExtra("name"));
        mTx_level.setText(mIntent.getStringExtra("level"));
        mTx_hospital.setText(mIntent.getStringExtra("hospital"));

        mImageClose = findViewById(R.id.img_close);
        mImageClose.setOnClickListener(this);

        mImg_avatar.setOnClickListener(this);
        mTx_name.setOnClickListener(this);
        mTx_level.setOnClickListener(this);
        mTx_hospital.setOnClickListener(this);
        findViewById(R.id.tx_title).setOnClickListener(this);
        mImgStart = findViewById(R.id.img_start);
        mImgStart.setOnClickListener(this);
        findViewById(R.id.tx_start).setOnClickListener(this);

        mImgStart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mImgStart.requestFocus();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.img_close) {
            finish();
        }
        else if(id == R.id.img_avatar || id == R.id.tx_hospital || id == R.id.tx_name ||
                id == R.id.tx_level || id == R.id.tx_title || id == R.id.img_start || id == R.id.tx_start) {
            Intent toChat = new Intent(StartChatActivity.this, ChatActivity.class);
            toChat.putExtra("room", mIntent.getStringExtra("room"));
            toChat.putExtra("reception", mIntent.getStringExtra("reception"));
            startActivity(toChat);
            finish();
        }
    }

}
