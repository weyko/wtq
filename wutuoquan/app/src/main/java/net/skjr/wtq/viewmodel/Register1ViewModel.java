package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register1ViewModel implements Serializable {
    private String phone;
    private String verifycode;
    private String smsverify;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVerifycode() {
        return verifycode;
    }

    public void setVerifycode(String verifycode) {
        this.verifycode = verifycode;
    }

    public String getSmsverify() {
        return smsverify;
    }

    public void setSmsverify(String smsverify) {
        this.smsverify = smsverify;
    }

    public CheckResult check(){
        if(TextUtils.isEmpty(phone)) {
            return CheckResult.failure("请输入手机号");
        }
        /*if(TextUtils.isEmpty(verifycode)) {
            return CheckResult.failure("请输入验证码");
        }*/
        if(TextUtils.isEmpty(smsverify)) {
            return CheckResult.failure("请输入短信验证码");
        }
        Pattern p = Pattern.compile("1[3,4,5,6,7,8,9]\\d{9}$");
        Matcher m = p.matcher(phone);
        if(!m.matches()) {
            return CheckResult.failure("请输入正确的手机号");
        }
        return CheckResult.success();
    }
}
