package com.dingbei.diagnose;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.dingbei.diagnose.http.ErrorBean;
import com.dingbei.diagnose.utils.AppLogger;
import com.dingbei.diagnose.utils.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    protected Context mContext;
    protected SmartRefreshLayout mLayout_refresh;
    private KeycodeListener mKeycodeListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreatingView(inflater, container, savedInstanceState);
    }


    protected abstract View onCreatingView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
    }

    /**
     * init RefreshLayout
     * @param view
     */
    protected void initRefreshLayout(View view) {
        mLayout_refresh = (SmartRefreshLayout) view.findViewById(R.id.ly_refresh);
        if(mLayout_refresh == null) {
            return;
        }

        mLayout_refresh.setRefreshHeader(new MaterialHeader(getContext()).setColorSchemeColors(0xff4fb1ae, 0xfffdd900));
        mLayout_refresh.setRefreshFooter(new BallPulseFooter(getContext()).setAnimatingColor(getResources().getColor(R.color.theme))
                .setSpinnerStyle(SpinnerStyle.Scale));
        mLayout_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                BaseFragment.this.onRefresh();
            }
        });
        mLayout_refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                BaseFragment.this.onLoadmore();
            }
        });
    }

    /**
     * override when you need
     */
    public void onRefresh() {

    }

    /**
     * close loading
     */
    public void closeRefresh() {
        if (mLayout_refresh != null) {
            if (mLayout_refresh.getState() == RefreshState.Refreshing) {
                mLayout_refresh.finishRefresh();
            }
        }
    }

    /**
     * show loading
     */
    public void showRefresh() {
        if(mLayout_refresh != null) {
            mLayout_refresh.autoRefresh();
        }
    }

    /**
     * override when you need
     */
    public void onLoadmore(){

    }

    /**
     * close load more
     */
    public void closeLoadmore(){
        if(mLayout_refresh != null) {
            if(mLayout_refresh.getState() == RefreshState.Loading) {
                mLayout_refresh.finishLoadMore();
            }
        }
    }



    /**
     * show toast
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


    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    public void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    public void changeFocus(View view, boolean hasFocus){
        if (hasFocus) {
            view.animate().scaleX(1.05f).scaleY(1.2f).setDuration(300).start();
        } else {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        return handleKeyEvent(action, keyCode);
    }

    public boolean handleKeyEvent(int action, int keyCode) {
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
