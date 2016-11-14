package chat.common.user;

import android.content.Context;
import android.text.TextUtils;

import chat.base.IMClient;
import chat.common.util.storage.PreferencesHelper;

/**
 * 存储用户信息
 */
public class UserInfoHelp {
    private static UserInfoHelp userInfo;
    private static PreferencesHelper helper;
    private static String token = null;
    private static String userid = null;

    public synchronized static UserInfoHelp getInstance(Context context) {
        if (userInfo == null)
            userInfo = new UserInfoHelp();
        if (helper == null)
            helper = new PreferencesHelper(context);
        return userInfo;
    }

    public synchronized static UserInfoHelp getInstance() {
        if (userInfo == null)
            userInfo = new UserInfoHelp();
        if (helper == null)
            helper = new PreferencesHelper(IMClient.getInstance().getContext());
        return userInfo;
    }

    /**
     * @return String
     * @Title: getUserId
     * @param:
     * @Description: 获取用户Id
     */
    public synchronized String getUserId() {
        if (userid == null || userid.length() <= 0) {
            userid = getString(helper.getString("userId", ""));
        }
        return userid;
    }

    /**
     * @return void
     * @Title: setUserId
     * @param:
     * @Description: 设置用户id
     */
    public void setUserId(String userId) {
        if (userId != null && userId.length() > 0) {
            userid = userId;
            helper.put("userId", userId);
        }
    }

    /**
     * @return String
     * @Title: getToken
     * @param:
     * @Description: 获取token
     */
    public String getToken() {
        if (token == null || token.length() <= 0) {
            token = getString(helper.getString("token"));
        }
        return token;
    }

    /**
     * 设置token
     *
     * @return void
     * @Title: setToken
     * @param:
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void setToken(String tok) {
        if (tok != null && tok.length() > 0) {
            token = tok;
            helper.put("token", token);
        }
    }

    /**
     * @return String
     * @Title: getName
     * @param:
     * @Description: 获取用户名
     */
    public String getName() {
        return getString(getUserId() + helper.getString("name"));
    }

    /**
     * @return void
     * @Title: setName
     * @param:
     * @Description: 设置用户名
     */
    public void setName(String name) {
        if (TextUtils.isEmpty(name))
            return;
        helper.put(getUserId() + "name", name);
    }

    /**
     * @return String
     * @Title: getAccount
     * @param:
     * @Description: 获取账号
     */
    public String getAccount() {
        return getString(getUserId() + helper.getString("useraccount"));
    }

    /**
     * @return void
     * @Title: setAccount
     * @param:
     * @Description: 设置账号
     */
    public void setAccount(String name) {
        if (TextUtils.isEmpty(name))
            return;
        helper.put(getUserId() + "useraccount", name);
    }

    /**
     * @return String
     * @Title: getPassWord
     * @param:
     * @Description: 获取密码
     */
    public String getPassWord() {
        return getString(getUserId() + helper.getString("userpass"));
    }

    /**
     * @return void
     * @Title: setPassWord
     * @param:
     * @Description: 设置密码
     */
    public void setPassWord(String name) {
        if (TextUtils.isEmpty(name))
            return;
        helper.put(getUserId() + "userpass", name);
    }

    /**
     * @return String
     * @Title: getNickname
     * @param:
     * @Description: 获取昵称
     */
    public String getNickname() {
        return getString(helper.getString(getUserId() + "nickname"));
    }

    /**
     * 设置昵称
     *
     * @return void
     * @Title: setNickname
     * @param:
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void setNickname(String nickname) {
        if (TextUtils.isEmpty(nickname))
            return;
        helper.put(getUserId() + "nickname", nickname);
    }

    /**
     * @return String
     * @Title: getAvarar
     * @param:
     * @Description: 获取头像
     */
    public String getAvarar() {
        return getString(helper.getString(getUserId() + "avarar"));
    }

    /**
     * @return void
     * @Title: setAvarar
     * @param:
     * @Description: 设置头像
     */
    public void setAvarar(String avarar) {
        if (TextUtils.isEmpty(avarar))
            return;
        helper.put(getUserId() + "avarar", avarar);
    }

    /**
     * @return int 性别 1：男 0：女
     * @Title: getSex
     * @param:
     * @Description: 获取性别
     */
    public int getSex() {
        return helper.getInt(getUserId() + "sex");
    }

    /**
     * @return void
     * @Title: setSex
     * @param:
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void setSex(int sex) {
        helper.put(getUserId() + "sex", sex);
    }

    /**
     * @return void
     * @Title: removeInfo
     * @param:
     * @Description: 根据Key移除信息
     */
    public void remove(String key) {
        helper.remove(key);
    }

    public void removeToken() {
        helper.remove("userId");
        helper.remove("token");
        userid = "";
        token = "";
    }

    /**
     * @return void
     * @Title: loginOut
     * @param:
     * @Description: 用户登出, 清除记录
     */
    public void loginOut() {
        helper.clear();
    }

    private String getString(Object obj) {
        if (obj == null)
            return "";
        else {
            if (obj.toString().trim().equals("null"))
                return "";
            else {
                return obj.toString().trim();
            }
        }
    }
    /**
     * @return String
     * @Title: getOfflineTime
     * @param:
     * @Description: 获取最近离线时间
     */
    public String getOfflineTime() {
        return getString(helper.getString("offlineTime"));
    }

    /**
     * @return void
     * @Title: setOfflineTime
     * @Description: 设置最近离线时间
     */
    public void setOfflineTime(String offlineTime) {
        helper.put("offlineTime", offlineTime);
    }
}
