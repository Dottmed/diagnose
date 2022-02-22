package com.dottmed.demo;

import android.app.Application;

import com.dingbei.diagnose.utils.DiagnoseUtil;


/**
 * @author Dayo
 * @time 2019/7/5 16:22
 */
public class MyApplication extends Application {

    public static String SN_TYPE = ""; // TODO:
    public static String APP_KEY = "";

    @Override
    public void onCreate() {
        super.onCreate();
        DiagnoseUtil.init(getApplicationContext(), "com.dottmed.demo.fileprovider", SN_TYPE, true);
    }
}
