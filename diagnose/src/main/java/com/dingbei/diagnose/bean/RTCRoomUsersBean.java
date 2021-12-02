package com.dingbei.diagnose.bean;

import java.util.List;

/**
 * @author Dayo
 * @time 2018/11/24 14:43
 */
public class RTCRoomUsersBean {

    private List<UserBean> users;

    public List<UserBean> getUsers() {
        return users;
    }

    public void setUsers(List<UserBean> users) {
        this.users = users;
    }

    public static class UserBean {

        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
