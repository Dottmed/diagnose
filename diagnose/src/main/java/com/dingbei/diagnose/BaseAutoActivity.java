package com.dingbei.diagnose;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.ToastUtil;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseAutoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private KeycodeListener mKeycodeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentViewID());
        setStatusBarColor(R.color.theme);
        onCreating(savedInstanceState);
    }

    /**
     * @return layout ID
     */
    protected abstract @LayoutRes
    int setContentViewID();


    /**
     * init
     * @param savedInstanceState
     */
    protected abstract void onCreating(@Nullable Bundle savedInstanceState);


    /**
     * change status bar color
     * @param colorID
     */
    public void setStatusBarColor(int colorID){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //6.0以上系统设置白底黑字
                    window.setStatusBarColor(getResources().getColor(R.color.white));
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
                }else {
                    window.setStatusBarColor(getResources().getColor(colorID));
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 展示toast
     * @param msg
     */
    public void showMsg(String msg) {
        ToastUtil.show(msg, Toast.LENGTH_SHORT);
    }

    public void showJsonMsg(String json) {
        String msg = JSONObject.parseObject(json).getString("message");
        ToastUtil.show(msg, Toast.LENGTH_SHORT);
    }

    public void showError(ErrorBean error) {
        ToastUtil.show(error.getError_code() + ": " + error.getError(), Toast.LENGTH_SHORT);
    }

    public void showMsgLong(String msg) {
        ToastUtil.show(msg, Toast.LENGTH_LONG);
    }

    public void showErrorLong(ErrorBean error) {
        ToastUtil.show(error.getError(), Toast.LENGTH_LONG);
    }

    /**
     * 设置顶部栏右图并返回该view
     * @param resId
     */
    public ImageView setRightImage(int resId) {
        ImageView img_right = (ImageView) findViewById(R.id.img_right);
        if(img_right != null) {
            img_right.setImageResource(resId);
        }
        return img_right;
    }

    public ImageView setRightImage2(int resId) {
        ImageView img_right2 = (ImageView) findViewById(R.id.img_right2);
        if(img_right2 != null) {
            img_right2.setImageResource(resId);
        }
        return img_right2;
    }


    /**
     * set right text
     * @param text
     * @return
     */
    public TextView setRightText(String text) {
        TextView text_right = (TextView) findViewById(R.id.txt_right);
        if(text_right != null) {
            text_right.setText(text);
            text_right.setVisibility(View.VISIBLE);
        }
        return text_right;
    }


    /**
     * set title
     * @param title
     * @return
     */
    public TextView setTitle(String title) {
        TextView text_title = (TextView) findViewById(R.id.txt_title);
        if(text_title != null) {
            text_title.setText(title);
        }
        return text_title;
    }


    /**
     * back
     */
    public ImageView setBack() {
        ImageView back = (ImageView) findViewById(R.id.img_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return back;
    }


    /**
     * handle permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    /**
     * EditText focus
     * @param editText
     */
    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * hide softwindow
     * @param view
     */
    public void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }


    public boolean isRunningForeground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        String name = getPackageName();
        return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(name);
    }

    public void changeFocus(View view, boolean hasFocus){
        if (hasFocus) {
            view.animate().scaleX(1.05f).scaleY(1.2f).setDuration(300).start();
        } else {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        return handleKeyEvent(action, keyCode) || super.dispatchKeyEvent(event);
    }

    private boolean handleKeyEvent(int action, int keyCode) {
        if (action != KeyEvent.ACTION_DOWN) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //确定键enter
                AppLogger.e("确定键enter");
                if (mKeycodeListener != null) {
                    mKeycodeListener.onCenter();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //向下键
                AppLogger.e("向下键");
                if (mKeycodeListener != null) {
                    mKeycodeListener.onDown();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                //向上键
                AppLogger.e("向上键");
                if (mKeycodeListener != null) {
                    mKeycodeListener.onUp();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //向左键
                AppLogger.e("向左键");
                if (mKeycodeListener != null) {
                    mKeycodeListener.onLeft();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //向右键
                AppLogger.e("向右键");
                if (mKeycodeListener != null) {
                    mKeycodeListener.onRight();
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void setKeycodeListener(KeycodeListener keycodeListener){
        mKeycodeListener = keycodeListener;
    }

}
