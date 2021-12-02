package com.dingbei.diagnose.bean;

/**
 * @author Dayo
 * @desc 用户信息
 */

public class UserInfoBean {

    private String schedule;
    private String cs_id;
    private String gender;
    private String level;
    private String name;
    private String mobile;
    private String id;
    private String avatar;
    private String hospital;
    private DepartmentBean department;
    private String merit;
    private String desc;
    private String type;
    private CountBean zhuanzhen_count;
    private CountBean huizhenzhen_count;
    private CountBean prescription_count;
    private String ecg_card_no;
    private String ecg_card_key;
    private boolean can_diagnosis;
    private boolean can_own_medicine;

    public static final String TYPE_COUNTRY = "1";
    public static final String TYPE_SPECIALIST = "2";
    public static final String TYPE_GENERAL = "3";

    public DepartmentBean getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentBean department) {
        this.department = department;
    }

    public boolean isCan_diagnosis() {
        return can_diagnosis;
    }

    public void setCan_diagnosis(boolean can_diagnosis) {
        this.can_diagnosis = can_diagnosis;
    }

    public boolean isCan_own_medicine() {
        return can_own_medicine;
    }

    public void setCan_own_medicine(boolean can_own_medicine) {
        this.can_own_medicine = can_own_medicine;
    }

    public String getEcg_card_no() {
        return ecg_card_no;
    }

    public void setEcg_card_no(String ecg_card_no) {
        this.ecg_card_no = ecg_card_no;
    }

    public String getEcg_card_key() {
        return ecg_card_key;
    }

    public void setEcg_card_key(String ecg_card_key) {
        this.ecg_card_key = ecg_card_key;
    }

    public CountBean getZhuanzhen_count() {
        return zhuanzhen_count;
    }

    public void setZhuanzhen_count(CountBean zhuanzhen_count) {
        this.zhuanzhen_count = zhuanzhen_count;
    }

    public CountBean getHuizhenzhen_count() {
        return huizhenzhen_count;
    }

    public void setHuizhenzhen_count(CountBean huizhenzhen_count) {
        this.huizhenzhen_count = huizhenzhen_count;
    }

    public CountBean getPrescription_count() {
        return prescription_count;
    }

    public void setPrescription_count(CountBean prescription_count) {
        this.prescription_count = prescription_count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMerit() {
        return merit;
    }

    public void setMerit(String merit) {
        this.merit = merit;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setCs_id(String cs_id) {
        this.cs_id = cs_id;
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

    public String getSchedule() {
        return schedule;
    }

    public String getCs_id() {
        return cs_id;
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

    public static class CountBean {

        /**
         * week : 1
         * month : 1
         */
        private String week;
        private String month;

        public void setWeek(String week) {
            this.week = week;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getWeek() {
            return week;
        }

        public String getMonth() {
            return month;
        }
    }

    public static class DepartmentBean {

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
}
