package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import net.skjr.wtq.common.Consts;

public class AccountPwdViewModel {
    private String oldPwd;
    private String newPwd1;
    private String newPwd2;
    private String code;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd1() {
        return newPwd1;
    }

    public void setNewPwd1(String newPwd1) {
        this.newPwd1 = newPwd1;
    }

    public String getNewPwd2() {
        return newPwd2;
    }

    public void setNewPwd2(String newPwd2) {
        this.newPwd2 = newPwd2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CheckResult check() {
        if (TextUtils.isEmpty(oldPwd)) {
            return CheckResult.failure("请输入原密码");
        }

        if (TextUtils.isEmpty(newPwd1)) {
            return CheckResult.failure("请输入新密码");
        }

        if (TextUtils.isEmpty(newPwd2)) {
            return CheckResult.failure("请确认新密码");
        }

        if (newPwd1.indexOf(' ') != -1 || newPwd2.indexOf(' ') != -1) {
            return CheckResult.failure("密码不能包含空格");
        }

        if (newPwd1.length() < Consts.MIN_PWD_LENGTH || newPwd2.length() < Consts.MIN_PWD_LENGTH) {
            return CheckResult.failure("密码由6到12位字符组成");
        }

        if (newPwd1.length() > Consts.MAX_PWD_LENGTH || newPwd2.length() > Consts.MAX_PWD_LENGTH) {
            return CheckResult.failure("密码由6到12位字符组成");
        }

        if (!TextUtils.equals(newPwd1, newPwd2)) {
            return CheckResult.failure("两次输入的密码不一致");
        }

        if (TextUtils.isEmpty(code)) {
            return CheckResult.failure("请输入验证码");
        }

        return CheckResult.success();
    }
}
