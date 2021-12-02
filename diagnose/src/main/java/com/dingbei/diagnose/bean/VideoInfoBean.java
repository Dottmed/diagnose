package com.dingbei.diagnose.bean;

public class VideoInfoBean {

    public VideoInfoBean() {
    }

    public VideoInfoBean(int id, long duration, String path) {
        this.id = id;
        this.duration = duration;
        this.path = path;
    }

    private int id;
    private long duration;
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
