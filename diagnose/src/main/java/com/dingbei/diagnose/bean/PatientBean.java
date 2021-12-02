package com.dingbei.diagnose.bean;

import java.io.Serializable;

/**
 * @author Dayo
 * @desc 病人信息bean
 */

public class PatientBean implements Serializable {

    private String gender;
    private String name;
    private String mobile;
    private String id;
    private int age;
    private String file_url;
    private String birthday;
    private String allergy;

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
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

    public int getAge() {
        return age;
    }
}
