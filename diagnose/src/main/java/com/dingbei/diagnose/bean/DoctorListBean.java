package com.dingbei.diagnose.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dayo
 * @time 2018/10/11 13:24
 * @desc 全科医生列表bean
 */

public class DoctorListBean {

    private String next;
    private String previous;
    private List<ResultBean> results;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public void setResults(List<ResultBean> results) {
        this.results = results;
    }

    public List<ResultBean> getResults() {
        return results;
    }

    public static class ResultBean implements Serializable {

        private String id;
        private String name;
        private String avatar;
        private String gender;
        private DepartmentBean department;
        private HospitalBean hospital;
        private String level;
        private String merit;
        private String desc;
        private int score;
        private String online_status;
        private List<DiseasesBean> diseases;
        private int pending_count;

        public int getPending_count() {
            return pending_count;
        }

        public void setPending_count(int pending_count) {
            this.pending_count = pending_count;
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

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public DepartmentBean getDepartment() {
            return department;
        }

        public void setDepartment(DepartmentBean department) {
            this.department = department;
        }

        public HospitalBean getHospital() {
            return hospital;
        }

        public void setHospital(HospitalBean hospital) {
            this.hospital = hospital;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
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

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getOnline_status() {
            return online_status;
        }

        public void setOnline_status(String online_status) {
            this.online_status = online_status;
        }

        public List<DiseasesBean> getDiseases() {
            return diseases;
        }

        public void setDiseases(List<DiseasesBean> diseases) {
            this.diseases = diseases;
        }

    }

}
