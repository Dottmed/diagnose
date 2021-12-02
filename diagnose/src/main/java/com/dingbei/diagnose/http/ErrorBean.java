package com.dingbei.diagnose.http;

/**
 * @author Dayo
 * @desc 错误消息bean
 */

public class ErrorBean {

    private int error_code;
    private String error;

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getError_code() {
        return error_code;
    }

    public String getError() {
        return error;
    }
}
