package net.skjr.wtq.model.system;

/**
 * request-找回登录密码
 */
public class FindPwdObj {
    public String phone;
    public String phoneCode;
    public String password;
    public String confirmPassword;

    /**
     *
     注册终端1 PC 2 安卓 3 IOS 4 微信
     */
    public int teminal = 2;
}
