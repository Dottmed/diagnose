package com.dingbei.diagnose.bean;

import java.util.List;

/**
 * @author Dayo
 * @time 2021/12/2 15:56
 * @desc 常用配置
 */
public class ConfigBean {

    private List<HospitalBean> all_hospital;
    private List<DepartmentBean> departments;

    public List<HospitalBean> getAll_hospital() {
        return all_hospital;
    }

    public void setAll_hospital(List<HospitalBean> all_hospital) {
        this.all_hospital = all_hospital;
    }

    public List<DepartmentBean> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentBean> departments) {
        this.departments = departments;
    }
}
