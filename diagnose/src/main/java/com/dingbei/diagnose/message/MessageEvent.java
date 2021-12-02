package com.dingbei.diagnose.message;

/**
 * @author Dayo
 * @time 2018/4/11 17:31
 * @desc eventbus的参数bean
 */

public class MessageEvent {

    private String message;
    private String extra;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
