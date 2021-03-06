package com.dingbei.diagnose;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.PermissionRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;



/**
 * @author Dayo
 * @time 2018/1/3 11:17
 * @desc H5页面，有以下name可选：
 */

public class H5Fragment extends BaseFragment {

    public static final String EX_URL = "url";
    public static final String HAS_TITLE = "has_title";
    private View mView;
    private WebView mWeb_detail;
    private String mUrl;
    private ProgressBar mView_loading;

    @Override
    protected View onCreatingView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dingbei_fragment_h5, null);
        initView();
        getData();
        return mView;
    }

    private void initView() {
        mView_loading = mView.findViewById(R.id.view_loading);

        mWeb_detail = mView.findViewById(R.id.webView);
        mWeb_detail.setVerticalScrollBarEnabled(false); //垂直滚动条不显示
        mWeb_detail.setHorizontalScrollBarEnabled(false); //水平滚动条不显示

        WebSettings settings = mWeb_detail.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //设置js可以直接打开窗口

        settings.setSupportZoom(false); //是否支持缩放，默认true
        settings.setBuiltInZoomControls(false); //是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false
        settings.setDisplayZoomControls(false); //是否显示窗口悬浮的缩放控制，默认true
        settings.setUseWideViewPort(true); //是否支持ViewPort的meta tag属性，如果页面有ViewPort meta tag 指定的宽度，则使用meta tag指定的值，否则默认使用宽屏的视图窗口
        settings.setLoadWithOverviewMode(true); //是否启动概述模式浏览界面，当页面宽度超过WebView显示宽度时，缩小页面适应WebView。默认false

        settings.setAllowFileAccess(true); //允许访问文件
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.setAllowUniversalAccessFromFileURLs(false);

        settings.setPluginState(WebSettings.PluginState.ON); //设置支持插件
        settings.setBlockNetworkImage(false); //解决图片不显示
        settings.setDomStorageEnabled(true); //适应Html5的一些方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(0); //https跟http混用
        }
    }

    private void getData() {
        Bundle bundle = getArguments();
        if(bundle == null) {
            return;
        }

        mUrl = bundle.getString(EX_URL);
        final boolean hasTitle = bundle.getBoolean(HAS_TITLE);

        if(!TextUtils.isEmpty(mUrl)) {
            mWeb_detail.loadUrl(mUrl);
            mWeb_detail.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(url == null) return false;

                    //处理通过url_scheme跳转其他app
                    try {
                        if (url.startsWith("http:") || url.startsWith("https:")) {
                            view.loadUrl(url);
                            return super.shouldOverrideUrlLoading(view, url);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        }
                    } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                        return false;
                    }
                }
            });

            mWeb_detail.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    mView_loading.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if(!hasTitle) {
                        //显示title
                        BaseAutoActivity activity = (BaseAutoActivity) getActivity();
                        if(activity != null) {
                            activity.setTitle(title);
                        }
                    }
                }

                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    result.confirm();
                    return true;
                }

                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    getActivity().runOnUiThread(() -> {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            //通过浏览器访问摄像头，同样也要申请权限
                            request.grant(request.getResources());
                            request.getOrigin();
                        }
                    });
                }

            });

            mWeb_detail.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // 注意up和down都会调用一次
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        setBack();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void setBack() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mWeb_detail.canGoBack()) {
                    mWeb_detail.goBack();// 返回前一个页面
                }else {
                    FragmentActivity activity = getActivity();
                    if(activity != null) {
                        activity.finish();
                    }
                }
            }
        });
    }

    public void reload(String url) {
        mUrl = url;
        mWeb_detail.loadUrl(mUrl);
    }

    public void clearCache() {
        mWeb_detail.clearCache(true);
        mWeb_detail.freeMemory();
        mWeb_detail.clearHistory();
        mWeb_detail.clearFormData();
        showMsg("清除成功");
    }

}
