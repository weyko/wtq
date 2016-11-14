package net.skjr.wtq.model.system;

import java.io.Serializable;

/**
 * request-发送短信验证码
 */
public class SendSmsCodeObj implements Serializable{
    public String phone;
    public String verifyCode;
}
