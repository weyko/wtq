package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import net.skjr.wtq.common.Consts;

public class RegisterViewModel {
    private String name;
    private String pwd1;
    private String pwd2;
    private String imgCode;
    private String phone;
    private String smsCode;
    private String phone2;
    private boolean protocolCheckd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd1() {
        return pwd1;
    }

    public void setPwd1(String pwd1) {
        this.pwd1 = pwd1;
    }

    public String getPwd2() {
        return pwd2;
    }

    public void setPwd2(String pwd2) {
        this.pwd2 = pwd2;
    }

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public boolean isProtocolCheckd() {
        return protocolCheckd;
    }

    public void setProtocolCheckd(boolean protocolCheckd) {
        this.protocolCheckd = protocolCheckd;
    }

    public CheckResult check(){
        if(TextUtils.isEmpty(name)){
            return CheckResult.failure("请输入用户名");
        }

        if(TextUtils.isEmpty(pwd1)){
            return CheckResult.failure("请输入密码");
        }

        if(TextUtils.isEmpty(pwd2)){
            return CheckResult.failure("请确认密码");
        }

        if (pwd1.indexOf(' ') != -1 || pwd2.indexOf(' ') != -1) {
            return CheckResult.failure("密码不能包含空格");
        }

        if (pwd1.length() < Consts.MIN_PWD_LENGTH || pwd2.length() < Consts.MIN_PWD_LENGTH) {
            return CheckResult.failure("密码由6到12位字符组成");
        }

        if (pwd1.length() > Consts.MAX_PWD_LENGTH || pwd2.length() > Consts.MAX_PWD_LENGTH) {
            return CheckResult.failure("密码由6到12位字符组成");
        }

        if(!TextUtils.equals(pwd1, pwd2)){
            return CheckResult.failure("两次输入的密码不一致");
        }

        if(TextUtils.isEmpty(imgCode)){
            return CheckResult.failure("请输入验证码");
        }

        if(TextUtils.isEmpty(imgCode)){
            return CheckResult.failure("请输入手机号码");
        }

        if(TextUtils.isEmpty(smsCode)){
            return CheckResult.failure("请输入手机验证码");
        }

        if(!protocolCheckd){
            return CheckResult.failure("请阅读服务协议并勾选");
        }

        return CheckResult.success();
    }
}
