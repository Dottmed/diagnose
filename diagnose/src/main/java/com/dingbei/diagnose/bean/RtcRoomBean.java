package com.dingbei.diagnose.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Dayo
 * @desc rtc房间bean
 */
public class RtcRoomBean implements Serializable {

    private PatientBean patient;
    private ArrayList<DoctorBean> users;
    private String token;
    private int room_id;
    private int appid;

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public void setUsers(ArrayList<DoctorBean> users) {
        this.users = users;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public ArrayList<DoctorBean> getUsers() {
        return users;
    }

    public String getToken() {
        return token;
    }

    public DoctorBean getUser(String id) {
        for (DoctorBean user : users) {
            if(user.getId().equals(id)) {
                return user;
            }
        }

        return null;
    }
}
