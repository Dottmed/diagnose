package com.dingbei.diagnose.http;

public abstract class BaseCallback {

    public abstract void onSuccess(String json);

    public abstract void onError(ErrorBean error);

}
