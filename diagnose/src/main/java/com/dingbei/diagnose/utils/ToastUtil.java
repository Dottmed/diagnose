package com.dingbei.diagnose.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Toast工具类  保证不重复显示Toast
 */
public class ToastUtil {

    private static Toast toast = null;
    private static Context context;

    public static void init(Context context) {
        ToastUtil.context = context;
    }

    /**
     * @param text
     * @param duration
     */
    public static void show(String text, int duration) {
        if (toast == null) {
            //注意：为防止内存泄漏传入全局的context
            toast = Toast.makeText(context, text, duration);
            if (toast == null)
                return;
        }
        toast.setText(text);
        toast.setDuration(duration);
        toast.show();
    }

}
