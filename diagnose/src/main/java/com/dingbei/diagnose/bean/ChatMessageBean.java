package com.dingbei.diagnose.bean;

import android.text.TextUtils;

/**
 * @author Dayo
 * @desc 聊天消息bean
 */

public class ChatMessageBean {

    public static final int SENDED = 0;
    public static final int SENDING = 1;
    public static final int UNSEND = 2;

    private User user;
    private String room;
    private String reception;
    private String biz;
    private Message msg;
    private String time;
    private String src;
    private int status = SENDED;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReception() {
        return reception;
    }

    public void setReception(String reception) {
        this.reception = reception;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static class User {

        private String id;
        private String name;
        private String avatar;
        private String department;
        private String level;

        public String getDepartment() {
            return TextUtils.isEmpty(department) ? "" : department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getLevel() {
            return TextUtils.isEmpty(level) ? "" : level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class Message {

        private String type;
        private String content;
        private long mid;
        private String opinion_id;
        private String ecg_url;
        private String label;
        private String prescription_id;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getPrescription_id() {
            return prescription_id;
        }

        public void setPrescription_id(String prescription_id) {
            this.prescription_id = prescription_id;
        }

        public String getEcg_url() {
            return ecg_url;
        }

        public void setEcg_url(String ecg_url) {
            this.ecg_url = ecg_url;
        }

        public String getOpinion_id() {
            return opinion_id;
        }

        public void setOpinion_id(String opinion_id) {
            this.opinion_id = opinion_id;
        }

        public long getMid() {
            return mid;
        }

        public void setMid(long mid) {
            this.mid = mid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
