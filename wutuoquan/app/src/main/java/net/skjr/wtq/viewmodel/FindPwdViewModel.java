package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import java.io.Serializable;

public class FindPwdViewModel implements Serializable {
    private String phone;
    private String phoneCode;
    private String password;
    private String confirmPassword;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public CheckResult check(){
        if(TextUtils.isEmpty(phone)){
            return CheckResult.failure("请输入手机号");
        }

        if(TextUtils.isEmpty(phoneCode)){
            return CheckResult.failure("请输入验证码");
        }

        /*if(TextUtils.isEmpty(password)){
            return CheckResult.failure("请输入新密码");
        }

        if(TextUtils.isEmpty(confirmPassword)){
            return CheckResult.failure("请确认新密码");
        }

        if (password.indexOf(' ') != -1 || confirmPassword.indexOf(' ') != -1) {
            return CheckResult.failure("密码不能包含空格");
        }

        if (password.length() < Consts.MIN_PWD_LENGTH || confirmPassword.length() < Consts.MIN_PWD_LENGTH) {
            return CheckResult.failure("密码由6到12位字符组成");
        }

        if (password.length() > Consts.MAX_PWD_LENGTH || confirmPassword.length() > Consts.MAX_PWD_LENGTH) {
            return CheckResult.failure("密码由6到12位字符组成");
        }

        if(!TextUtils.equals(password, confirmPassword)){
            return CheckResult.failure("两次输入的密码不一致");
        }*/

        return CheckResult.success();
    }
}
