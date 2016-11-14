package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import net.skjr.wtq.common.Consts;

public class AccountTradePwd1ViewModel {
    private String pwd1;
    private String pwd2;
    private String code;
    private String phone;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CheckResult check(){

        if(TextUtils.isEmpty(pwd1)){
            return CheckResult.failure("请输入交易密码");
        }

        if(TextUtils.isEmpty(pwd2)){
            return CheckResult.failure("请确认交易密码");
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

        if(TextUtils.isEmpty(code)){
            return CheckResult.failure("请输入验证码");
        }

        return CheckResult.success();
    }
}
