package com.dingbei.diagnose.rtc.utils;

import java.security.KeyStore;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class QNAppServer {
    /**
     * 设置推流画面尺寸，仅用于 Demo 测试，用户可以在创建七牛 APP 时设置该参数
     */
    public static final int STREAMING_WIDTH = 480;
    public static final int STREAMING_HEIGHT = 848;
    public static final int MERGE_STREAM_WIDTH = 160;
    public static final int MERGE_STREAM_HEIGHT = 240;
    public static final String ADMIN_USER = "admin";

    public static final int[][] MERGE_STREAM_POS = new int[][] {
            // X     Y
            {320,   608 },
            {320,   304 },
            {320,   0   },
            {160,   608 },
            {160,   304 },
            {160,   0   },
            {0,     608 },
            {0,     304 },
            {0,     0   },
    };

    private static final String TAG = "QNAppServer";
    private static final String APP_SERVER_ADDR = "https://api-demo.qnsdk.com";
    public static final String APP_ID = "doj1tu5iv";

    private static class QNAppServerHolder {
        private static final QNAppServer instance = new QNAppServer();
    }

    private QNAppServer(){}

    public static QNAppServer getInstance() {
        return QNAppServerHolder.instance;
    }

    private static X509TrustManager getTrustManager() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore)null);
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    return (X509TrustManager) tm;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // This shall not happen
        return null;
    }

}
