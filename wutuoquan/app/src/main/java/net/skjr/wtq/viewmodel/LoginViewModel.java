package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel {
    private String phone;
    private String password;
    private String imgCode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }

    public CheckResult check(){
        if(TextUtils.isEmpty(phone)){
            return CheckResult.failure("请输入手机号");
        }
        Pattern p = Pattern.compile("1[3,4,5,6,7,8,9]\\d{9}$");
        Matcher m = p.matcher(phone);
        if(!m.matches()) {
            return CheckResult.failure("请输入正确的手机号");
        }

        if(TextUtils.isEmpty(password)){
            return CheckResult.failure("请输入密码");
        }

        if(TextUtils.isEmpty(imgCode)){
            return CheckResult.failure("请输入验证码");
        }

        return CheckResult.success();
    }
}
