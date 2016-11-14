
package net.skjr.wtq.model.system;

import java.io.Serializable;

/**
 * request-登录
 */
public class LoginObj implements Serializable {
    public String userName;
    public String passWord;
    public String verifyCode;

    /**
     * 登录方式1 PC 2 微信 3 IOS 4 Android
     */
    public String loginMethod = "4";
}
