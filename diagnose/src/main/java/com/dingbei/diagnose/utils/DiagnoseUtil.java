package com.dingbei.diagnose.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.DoctorListActivity;
import com.dingbei.diagnose.RemoteActivity;
import com.dingbei.diagnose.bean.PatientBean;
import com.dingbei.diagnose.bean.SignBean;
import com.dingbei.diagnose.gallery.CoreConfig;
import com.dingbei.diagnose.gallery.FunctionConfig;
import com.dingbei.diagnose.gallery.GalleryFinal;
import com.dingbei.diagnose.gallery.GlideImageLoader;
import com.dingbei.diagnose.gallery.ThemeConfig;
import com.dingbei.diagnose.http.BaseCallback;
import com.dingbei.diagnose.http.BaseHttp;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.http.HttpParams;
import com.dingbei.diagnose.http.HttpUtil;
import com.dingbei.diagnose.websocket.WebSocketSetting;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

/**
 * @author Dayo
 * @time 2019/7/4 19:06
 */
public class DiagnoseUtil {

    public static Context applicationContext;
    public static String fileProvider;
    public static String userId;
    public static String historyUrl;
    public static String SN_TYPE;

    /**
     * 初始化一些配置
     */
    @Keep
    public static void init(Context applicationContext, String fileProvider, String snType, boolean release) {
        DiagnoseUtil.applicationContext = applicationContext;
        DiagnoseUtil.fileProvider = fileProvider;
        DiagnoseUtil.SN_TYPE = snType;
//        if(release) {
//            BaseHttp.HOST = BaseHttp.HOST_RELEASE;
//            BaseHttp.API = BaseHttp.API_RELEASE;
//            BaseHttp.SOCKET_URL = BaseHttp.SOCKET_RELEASE;
//        }

        //开启socket
//        initSocket();

        //创建缓存目录
//        createFile();

        //初始化工具类
        ToastUtil.init(applicationContext);
        PreferencesUtil.init(applicationContext, "dingbei");
        String address = PreferencesUtil.getString(PreferencesUtil.API_ADDRESS);
        if(!TextUtils.isEmpty(address)) {
            //配置过地址就用新的
            BaseHttp.API = address;
        }

        //初始化相册
//        initGalleryfinal();

        initX5();
    }

    private static void initSocket() {
        //websocket配置
        WebSocketSetting.setConnectUrl(BaseHttp.SOCKET_URL);
        WebSocketSetting.setReconnectWithNetworkChanged(false);
    }

    private static void createFile() {
        FileUtil.createFile(FileUtil.APP_CACHE_PATH);
        FileUtil.createFile(FileUtil.TEMP);
        FileUtil.createFile(FileUtil.CACHE);
        FileUtil.createFile(FileUtil.PHOTO);
    }

    private static void initGalleryfinal() {
        ThemeConfig theme = ThemeConfig.DARK;
        //theme = new ThemeConfig.Builder().build();

        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)//相机
                .setEnableEdit(false)//编辑
                .setEnableCrop(false)//裁剪
                .setEnableRotate(false)//旋转
                .setCropSquare(false)//裁剪矩形
                .setEnablePreview(true)//预览功能
                .build();

        GlideImageLoader imageLoader = new GlideImageLoader();

        CoreConfig coreConfig = new CoreConfig.Builder(applicationContext, imageLoader, theme)
                .setFunctionConfig(functionConfig)
                .build();

        GalleryFinal.init(coreConfig);
    }

    private static void initX5() {
        // 为了提高内核占比，在初始化前可配置允许移动网络下载内核（大小 40-50 MB）。默认移动网络不下载
        QbSdk.setDownloadWithoutWifi(true);

        QbSdk.initX5Environment(applicationContext, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {
                AppLogger.e("是否使用X5：" + isX5);
            }
        });

        // TBS内核首次使用和加载时，ART虚拟机会将Dex文件转为Oat，该过程由系统底层触发且耗时较长，很容易引起anr问题，解决方法是使用TBS的 ”dex2oat优化方案“。
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
    }

    @Keep
    public static void diagnose(final Context context, String app_key, final String inquiry_no,
                                final String name, final String idno, final SignBean sign) {
//        HttpParams params = new HttpParams();
//        params.put("app_key", app_key);
//        params.put("inquiry_no", inquiry_no);
//        HttpUtil.post(BaseHttp.API + "cygzz/remote_diagnosis/", params, new BaseCallback() {
//            @Override
//            public void onSuccess(String json) {
//                JSONObject object = JSONObject.parseObject(json);
//                historyUrl = object.getString("medical_history_url");
//                getPatient(context, inquiry_no, idno, name, sign);
//            }
//
//            @Override
//            public void onError(ErrorBean error) {
//                ToastUtil.show(error.getError(), Toast.LENGTH_SHORT);
//            }
//        });

        String url = String.format("?idno=%s&name=%s", idno, name);
        context.startActivity(new Intent(context, RemoteActivity.class).putExtra("url", url));
    }

    private static void getPatient(final Context context, final String inquiry_no, String idno, String name, final SignBean sign) {
        HttpParams params = new HttpParams();
        params.put("idno", idno);
        params.put("name", name);
        HttpUtil.post("diagnosis/byidno/", params, new BaseCallback() {
            @Override
            public void onSuccess(String json) {
                JSONObject object = JSONObject.parseObject(json);
                String str = object.getString("patient");
                PatientBean bean = JSONObject.parseObject(str, PatientBean.class);
                userId = object.getString("user_id");

                if(context != null) {
                    context.startActivity(new Intent(context, DoctorListActivity.class)
                            .putExtra("patient_id", bean.getId())
                            .putExtra("inquiry_no", inquiry_no)
                            .putExtra("sign", sign));
                }
            }

            @Override
            public void onError(ErrorBean error) {
                ToastUtil.show(error.getError(), Toast.LENGTH_SHORT);
            }
        });
    }

    public static void initLog(boolean isLog) {
        AppLogger.isLog = isLog;
    }

}
