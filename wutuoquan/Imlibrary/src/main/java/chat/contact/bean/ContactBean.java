package chat.contact.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Locale;

import chat.common.util.string.PingYinUtil;

/**
 * Description:联系人实体类
 * Created  by: weyko on 2016/11/7.
 */

public class ContactBean implements Serializable{
    private int userID;//用户ＩＤ
    private int friendID;//好友ID
    private String userNickname;//好友昵称
    private String remarkName;//好友备注
    private String userImg;//好友头像
    private String addDatetime;//加友时间
    private String catalog;
    private int role;//用户角色
    private int state;//好友状态
    private int userSex;
    private String cityName;//城市名
    private String userMood;//说说
    private String phone;//手机号码
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getFriendID() {
        return friendID;
    }

    public void setFriendID(int friendID) {
        this.friendID = friendID;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getAddDatetime() {
        return addDatetime;
    }

    public void setAddDatetime(String addDatetime) {
        this.addDatetime = addDatetime;
    }
    public void setCatalog(String catalog){
        this.catalog=catalog;
    }
    public String getCatalog() {
        if(TextUtils.isEmpty(catalog)) {
            catalog = getShowName();
            if (!TextUtils.isEmpty(catalog)) {
                catalog = PingYinUtil
                        .converterToFirstSpell(catalog.substring(0, 1))
                        .substring(0, 1).toUpperCase(Locale.CHINA);
            }
        }
        return catalog;
    }
    public String getShowName(){
        return !TextUtils.isEmpty(remarkName)?remarkName:userNickname;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getUserSex() {
        return userSex;
    }
    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getUserMood() {
        return userMood;
    }

    public void setUserMood(String userMood) {
        this.userMood = userMood;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
