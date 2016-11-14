package net.skjr.wtq.model.system;

import java.io.Serializable;

/**
 * 注册对象
 */
public class RegisterObj implements Serializable {
    public String userName;
    public String phone;
    public String phoneCode;
    public String verifyCode;
    public String passWord;
    public String confirmPassword;
    public String invitePhone;
    public String inviteCode;

    /**
     *注册终端1 PC 2 安卓 3 IOS 4 微信
     */
    public int regTerminal = 2;

    /**
     * 是否同意注册协议 0未同意 1同意
     */
    public int agreement= 1;
}
