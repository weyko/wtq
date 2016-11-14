package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 9:48
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ResetLoginPwdViewModel {
    private String verifyCode;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(oldPassword)) {
            return CheckResult.failure("请输入原密码");
        }
        if(TextUtils.isEmpty(newPassword)) {
            return CheckResult.failure("请输入新密码");
        }
        if(TextUtils.isEmpty(confirmPassword)) {
            return CheckResult.failure("请确认新密码");
        }
        if(!newPassword.equals(confirmPassword)) {
            return CheckResult.failure("两次新密码不一致");
        }
        if(TextUtils.isEmpty(verifyCode)) {
            return CheckResult.failure("请输入验证码");
        }
        return CheckResult.success();
    }
}
