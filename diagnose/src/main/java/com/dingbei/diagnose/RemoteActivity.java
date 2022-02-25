package com.dingbei.diagnose;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dingbei.diagnose.http.BaseHttp;
import com.dingbei.diagnose.utils.PreferencesUtil;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


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
        checkPermission();
    }

    private void initView() {
        View layout_setting = findViewById(R.id.layout_setting);
        EditText ed_address = findViewById(R.id.ed_address);
        EditText ed_pwd = findViewById(R.id.ed_pwd);

        findViewById(R.id.img_setting).setOnClickListener(v -> {
            layout_setting.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.txt_clear_cache).setOnClickListener(v -> {
            mFragment.clearCache();
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
//        String url = "https://trtc-1252463788.file.myqcloud.com/web/demo/official-demo/index.html";
//        bundle.putString(H5Fragment.EX_URL, url);
        bundle.putString(H5Fragment.EX_URL, BaseHttp.API + mUrl);
        bundle.putBoolean(H5Fragment.HAS_TITLE, true);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, mFragment, "h5")
                .commitAllowingStateLoss ();
    }

    protected void checkPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //不做操作
        } else {
            EasyPermissions.requestPermissions(this, "App需要您开启摄像头及存储权限", 1001, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        //不做操作
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        showMsg("您没有授予相应权限");
    }

}
