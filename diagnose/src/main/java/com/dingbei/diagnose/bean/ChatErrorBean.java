package com.dingbei.diagnose.bean;

/**
 * @author Dayo
 * @desc 聊天错误消息bean
 */

public class ChatErrorBean {

    /**
     * biz : chat
     * error : 请先登录
     * status : 1
     */
    private String biz;
    private String error;
    private String status;

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBiz() {
        return biz;
    }

    public String getError() {
        return error;
    }

    public String getStatus() {
        return status;
    }
}
