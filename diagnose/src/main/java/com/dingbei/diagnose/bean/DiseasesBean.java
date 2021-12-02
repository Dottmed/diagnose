package com.dingbei.diagnose.bean;

import java.io.Serializable;

/**
 * @author Dayo
 * @time 2019/9/29 15:06
 * @desc 疾病
 */
public class DiseasesBean implements Serializable {

    private String id;
    private String name;

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
}
