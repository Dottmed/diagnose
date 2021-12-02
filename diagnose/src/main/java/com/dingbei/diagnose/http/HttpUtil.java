package com.dingbei.diagnose.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.FileUtil;
import com.dingbei.diagnose.utils.PreferencesUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    private static HttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private long cacheSize = 1024 * 1024 * 20; //缓存最大值
    private int staletime = 2; //缓存保存时间
    private static String agentName = "dingbei_unicom/2.0";
    private static String versionName = "1.0";
    private String userAgent;


    private HttpUtil() {
        mOkHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(new File(FileUtil.APP_CACHE_PATH), cacheSize)) //对get请求进行缓存
                //.retryOnConnectionFailure(false) //不重连
                .cookieJar(new CookieJar() { //cookie持久化

                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookieList) {
                        cookieStore.put(url.host(), cookieList);
                        for (Cookie c : cookieList) {
                            if (c.name().equals("csrftoken")) {
                                PreferencesUtil.putString(PreferencesUtil.COOKIE_TOKEN_VALUE, c.value());
                                PreferencesUtil.putString(PreferencesUtil.COOKIE_TOKEN, c.toString());
                            }
                            if(c.name().equals("sessionid")) {
                                PreferencesUtil.putString(PreferencesUtil.COOKIE_SESSION, c.toString());
                            }
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookieList = cookieStore.get(url.host());
                        if(cookieList == null){
                            cookieList = new ArrayList<Cookie>();
                            String token = PreferencesUtil.getString(PreferencesUtil.COOKIE_TOKEN);
                            if(!TextUtils.isEmpty(token)){
                                Cookie cookie = Cookie.parse(url,token);
                                cookieList.add(cookie);
                            }

                            String session = PreferencesUtil.getString(PreferencesUtil.COOKIE_SESSION);
                            if(!TextUtils.isEmpty(session)){
                                Cookie cookie = Cookie.parse(url,session);
                                cookieList.add(cookie);
                            }
                            cookieStore.put(url.host(),cookieList);
                        }
                        return cookieList;
                    }
                })
                .build();

        mHandler = new Handler(Looper.getMainLooper());
    }

    private synchronized static HttpUtil getInstance() {
        if (mInstance == null) {
            mInstance = new HttpUtil();
        }
        return mInstance;
    }

    /**
     * get请求
     *
     * @param url
     * @param callback
     * @param params
     */
    public static void get(String url, HttpParams params, BaseCallback callback) {
        getInstance().getRequest(url, callback, params);
    }

    private void getRequest(String url, BaseCallback callback, HttpParams params) {
        Request request = new Request.Builder()
                //没有超过过时时间StaleTime的话，返回缓存数据，否则重新去获取网络数据，StaleTime限制了默认数据fresh时间
                .cacheControl(new CacheControl.Builder().maxStale(staletime, TimeUnit.SECONDS).build())
                .url(appendUrl(url, params))
                .removeHeader("User-Agent")
                .addHeader("User-Agent", getUserAgent())
                .build();

        deliveryResult(callback, request);
    }

    //设置User-Agent用于后台统计信息
    private String getUserAgent() {
        if(TextUtils.isEmpty(userAgent)) {
            userAgent = System.getProperty("http.agent");
            StringBuffer sb = new StringBuffer(agentName);
            sb.append(versionName + " ");

            for (int i = 0, length = userAgent.length(); i < length; i++) {
                char c = userAgent.charAt(i);
                //避免出现中文
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
            }

            userAgent = sb.toString();
        }

        return userAgent;
    }

    //拼接请求url
    private String appendUrl(String url, HttpParams params) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        if (url.startsWith("http")) {
            return url;
        } else {
            return new StringBuilder(BaseHttp.HOST).append(url).append(getUrlParamsByMap(params)).toString();
        }
    }

    private String getUrlParamsByMap(HttpParams map) {
        if (map == null || map.size() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        for (HttpParams.KeyValue entry : map.getParams()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);//截取字符串,后面的是开区间
        }
        return s;
    }

    /**
     * post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    public static void post(String url, HttpParams params, BaseCallback callback) {
        getInstance().postRequest(url, callback, params);
    }

    private void postRequest(String url, BaseCallback callback, HttpParams params) {
        Request request = buildPostRequest(appendUrl(url, null), params);
        deliveryResult(callback, request);
    }

    private Request buildPostRequest(String url, HttpParams params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (HttpParams.KeyValue entry : params.getParams()) {
                String key = entry.getKey();
                if(entry.getValue() != null){
                    String value = entry.getValue().toString();
                    builder.add(key, value);
                }
            }
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", getUserAgent())
                .post(formBody)
                .build();
        return request;
    }

    /**
     * 表单是json的post请求
     *
     * @param url
     * @param json
     * @param callback
     */
    public static void postJson(String url, String json, BaseCallback callback) {
        getInstance().postJsonRequest(url, json, callback);
    }

    private void postJsonRequest(String url, String json, BaseCallback callback) {
        //url = TextUtils.isEmpty(url) ? BaseHttp.POSTHOST : BaseHttp.HOST + url;
        url = BaseHttp.HOST + url;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", getUserAgent())
                .post(body)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 表单含有文件的post请求
     *
     * @param url
     * @param params   参数的集合
     * @param files    上传文件的集合
     * @param callback
     */
    public static void postMultipart(String url, HttpParams params, Map<String, File> files, BaseCallback callback) {
        getInstance().postMultipartRequest(url, params, files, callback);
    }

    private void postMultipartRequest(String url, HttpParams params, Map<String, File> files, BaseCallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (params != null && params.size() > 0) {
            for (HttpParams.KeyValue entry : params.getParams()) {
                builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"" + entry.getKey() + "\""),
                        RequestBody.create(null, entry.getValue().toString()));
            }
        }

        if (files != null && files.size() > 0) {
            for (Map.Entry<String, File> entry : files.entrySet()) {
                builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"" + entry.getKey() + "\";filename=\"" + entry.getValue().getName() + "\""),
                        RequestBody.create(MediaType.parse(judgeType(entry.getValue().getName())), entry.getValue()));
            }
        }

        MultipartBody multipartBody = builder.build();
        url = BaseHttp.HOST + url;
        Request request = new Request.Builder()
                .url(url)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", getUserAgent())
                .post(multipartBody)
                .build();
        deliveryResult(callback, request);
    }

    private String judgeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(fileName);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 发送请求
     * @param resultCallback
     * @param request
     */
    private void deliveryResult(final BaseCallback resultCallback, Request request) {
        //1.连接服务器
        //2.连接成功，有response
        //3.code为200 - 300
        //4.符合业务逻辑，才是一次成功的请求
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String message;
                AppLogger.e(e.getMessage());
                if(e instanceof java.net.SocketTimeoutException) {
                    message = "连接超时";
                }else if(e instanceof javax.net.ssl.SSLHandshakeException) {
                    message = "无效证书";
                }else if(e instanceof FileNotFoundException) {
                    message = "文件错误";
                }else {
                    message = "网络不给力，请检查您的网络环境";
                }
                failCallback(resultCallback, 0, message);
            }

            @Override
            public void onResponse(Call call, Response response) {
                //int code = response.code();
                String message = response.message();
                try {
                    String result = response.body().string();
                    ErrorBean error = JSONObject.parseObject(result, ErrorBean.class);
                    int code = error.getError_code();
                    if(code == 0 || (code >= 200 && code < 300)) {
                        successCallBack(resultCallback, result);
                    }else {
                        failCallback(resultCallback, code, error.getError());
                        AppLogger.e(code + "  " + message + "  " + result);
                    }
                } catch (JSONException e) {
                    //不是json格式的错误
                    failCallback(resultCallback, 0, message);
                    AppLogger.e(message);
                } catch (Exception e) {
                    failCallback(resultCallback, 0, e.getMessage());
                    AppLogger.e(e.getMessage());
                }
            }
        });
    }

    //网络连接失败回调
    private void failCallback(final BaseCallback resultCallback, final int code, final String message) {
        if (resultCallback == null) {
            return;
        }

        //由子线程返回主线程
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ErrorBean error = new ErrorBean();
                error.setError_code(code);
                error.setError(message);

                resultCallback.onError(error);
            }
        });
    }

    private void successCallBack(final BaseCallback resultCallback, final String result) {
        if (resultCallback == null) {
            return;
        }

        //由子线程返回主线程
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                resultCallback.onSuccess(result);
            }
        });
    }

}
