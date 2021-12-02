package com.dingbei.diagnose.message;

/**
 * @author Dayo
 * @time 2017/11/16 17:41
 * @desc eventbus传递的消息
 */

public interface MessageType {

    //发起人取消rtc
    String RTC_CANCEL_BY_LAUNCHER = "rtc_cancel_by_launcher";
    //其他人拒绝了rtc请求
    String RTC_REJECT = "rtc_reject";
}
