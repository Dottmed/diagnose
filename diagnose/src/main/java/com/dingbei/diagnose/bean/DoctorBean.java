package com.dingbei.diagnose.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Dayo
 * @desc 诊断医生bean
 */

public class DoctorBean implements Serializable {

    private String gender;
    private String level;
    private String name;
    private String mobile;
    private String id;
    private String avatar;
    private String hospital;
    private String type;
    private String online_status;

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getGender() {
        return gender;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getHospital() {
        return hospital;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DoctorBean that = (DoctorBean) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
