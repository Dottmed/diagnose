package com.dingbei.diagnose.bean;

import java.util.List;

/**
 * @author Dayo
 * @time 2018/9/13 12:33
 * @desc 诊断意见详情
 */

public class OpinionDetailBean {

    private DoctorBean owner;
    private List<String> images;
    private String id;
    private String audio;
    private String video;
    private String desc;

    public void setOwner(DoctorBean owner) {
        this.owner = owner;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DoctorBean getOwner() {
        return owner;
    }

    public List<String> getImages() {
        return images;
    }

    public String getId() {
        return id;
    }

    public String getAudio() {
        return audio;
    }

    public String getVideo() {
        return video;
    }

    public String getDesc() {
        return desc;
    }

}
