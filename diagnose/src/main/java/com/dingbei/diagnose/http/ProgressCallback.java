package com.dingbei.diagnose.http;

import java.io.File;

/**
 * @author Dayo
 * @desc 下载进度的回调
 */

public abstract class ProgressCallback extends BaseCallback {

    public abstract void onStarted();

    public abstract void onLoading(long total, long current);

    public abstract void onFinished(File file);
}
