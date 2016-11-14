package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 11:38
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SetTradePwdViewModel {
    private String newPassword;
    private String newPasswordTwo;
    private String verifyCode;
    private int userID;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordTwo() {
        return newPasswordTwo;
    }

    public void setNewPasswordTwo(String newPasswordTwo) {
        this.newPasswordTwo = newPasswordTwo;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(newPassword)) {
            return CheckResult.failure("请输入新密码");
        }
        if(TextUtils.isEmpty(newPasswordTwo)) {
            return CheckResult.failure("请确认新密码");
        }
        if(TextUtils.isEmpty(verifyCode)) {
            return CheckResult.failure("请输入验证码");
        }
        return CheckResult.success();
    }
}
