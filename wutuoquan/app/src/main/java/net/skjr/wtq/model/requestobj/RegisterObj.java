package net.skjr.wtq.model.requestobj;

import java.io.Serializable;

/**
 * 注册对象
 */
public class RegisterObj implements Serializable {
    public String userName;
    public String phone;
    public String phoneCode;
    public String verifyCode;
    public String password;
    public String confirmPassword;
    public String invitePhone;
    public String inviteCode;

    /**
     *注册终端 3 IOS 4 安卓
     */
    public int regTerminal = 4;

    /**
     * 是否同意注册协议 0未同意 1同意
     */
    public int agreement= 1;
}
