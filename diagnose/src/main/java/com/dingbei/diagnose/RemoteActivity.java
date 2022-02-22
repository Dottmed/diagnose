package com.dingbei.diagnose;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dingbei.diagnose.http.BaseHttp;
import com.dingbei.diagnose.utils.PreferencesUtil;


/**
 * @author Dayo
 * @desc 远程问诊
 */

public class RemoteActivity extends BaseAutoActivity {

    private H5Fragment mFragment;
    private String mUrl;

    @Override
    protected int setContentViewID() {
        return R.layout.dingbei_activity_remote;
    }

    @Override
    protected void onCreating(@Nullable Bundle savedInstanceState) {
        mUrl = getIntent().getStringExtra("url");
        initView();
    }

    private void initView() {
        View layout_setting = findViewById(R.id.layout_setting);
        EditText ed_address = findViewById(R.id.ed_address);
        EditText ed_pwd = findViewById(R.id.ed_pwd);

        findViewById(R.id.img_setting).setOnClickListener(v -> {
            layout_setting.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.txt_cancel).setOnClickListener(v -> {
            layout_setting.setVisibility(View.GONE);
        });

        findViewById(R.id.txt_confirm).setOnClickListener(v -> {
            String address = ed_address.getText().toString();
            String pwd = ed_pwd.getText().toString();

            if(TextUtils.isEmpty(address)) {
                showMsg("请输入地址");
                return;
            }

            if(TextUtils.isEmpty(pwd)) {
                showMsg("请输入密码");
                return;
            }

            if(!"dottmed888".equals(pwd)) {
                showMsg("密码错误");
                return;
            }

            PreferencesUtil.putString(PreferencesUtil.API_ADDRESS, address);
            BaseHttp.API = address;
            mFragment.reload(BaseHttp.API + mUrl);
            showMsg("设置成功");

            layout_setting.setVisibility(View.GONE);
        });

        mFragment = new H5Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(H5Fragment.EX_URL, BaseHttp.API + mUrl);
        bundle.putBoolean(H5Fragment.HAS_TITLE, true);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, mFragment, "h5")
                .commitAllowingStateLoss ();
    }

}
