package com.dingbei.diagnose;

import android.content.Context;
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
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.dingbei.diagnose.http.BaseHttp;
import com.dingbei.diagnose.utils.PreferencesUtil;


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
        //mWeb_detail.setWebChromeClient(new WebChromeClient());
        //mWeb_detail.setWebViewClient(new WebViewClient());
        mWeb_detail.setVerticalScrollBarEnabled(false); //垂直滚动条不显示
        mWeb_detail.setHorizontalScrollBarEnabled(false); //水平滚动条不显示

        WebSettings mSettings = mWeb_detail.getSettings();
        mSettings.setJavaScriptEnabled(true);
        mSettings.setBuiltInZoomControls(false); //设置显示缩放按钮
        mSettings.setAllowFileAccess(true); //允许访问文件
        mSettings.setPluginState(WebSettings.PluginState.ON); //设置支持插件
        mSettings.setJavaScriptCanOpenWindowsAutomatically(true); //设置js可以直接打开窗口
        mWeb_detail.addJavascriptInterface(new WebAppInterface(getContext()), "JS2Android");
        mSettings.setBlockNetworkImage(false); //解决图片不显示
        //mSettings.setDomStorageEnabled(true); //适应Html5的一些方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); //https跟http混用
        }

        //登录状态需要设置cookie
        String token = PreferencesUtil.getString(PreferencesUtil.COOKIE_TOKEN);
        String session = PreferencesUtil.getString(PreferencesUtil.COOKIE_SESSION);
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();

        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(session)) {
            /* delete old cookies */
            cookieManager.removeSessionCookie();

            cookieManager.setCookie(BaseHttp.HOST, token);
            cookieManager.setCookie(BaseHttp.HOST, session);
            CookieSyncManager.getInstance().sync();
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


    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void goBack() {
            setBack();
        }

    }

}
