package com.zzuli.hwl.locationandtrance.bean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangJinRui on 2017/4/28.
 */

public class User extends DataSupport{

    private int id;

    private String username;

    private String password;

    private String mobileNum;
    private String sendMessagePhone;
    private String sendMessageDelay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (username != null ? !username.equals(user.username) : user.username != null)
            return false;
        if (password != null ? !password.equals(user.password) : user.password != null)
            return false;
        return mobileNum != null ? mobileNum.equals(user.mobileNum) : user.mobileNum == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (mobileNum != null ? mobileNum.hashCode() : 0);
        return result;
    }

    public String getSendMessagePhone() {
        return sendMessagePhone;
    }

    public void setSendMessagePhone(String sendMessagePhone) {
        this.sendMessagePhone = sendMessagePhone;
    }

    public String getSendMessageDelay() {
        return sendMessageDelay;
    }

    public void setSendMessageDelay(String sendMessageDelay) {
        this.sendMessageDelay = sendMessageDelay;
    }
}
