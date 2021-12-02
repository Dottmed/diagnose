package com.dingbei.diagnose.bean;

import java.util.List;

/**
 * @author Dayo
 */

public class ChatMessageListBean {

    /**
     * next :
     * previous :
     * results : [{"created_time":"2018-08-30T09:44:06.575161Z","name":"在于-转诊","id":"29998","reception":"6591","room":"zz29998","patient_file":"https://dingbei.qihuob2b.com"}]
     */
    private String next;
    private String previous;
    private List<ChatMessageBean> results;

    public void setNext(String next) {
        this.next = next;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public void setResults(List<ChatMessageBean> results) {
        this.results = results;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<ChatMessageBean> getResults() {
        return results;
    }

}
