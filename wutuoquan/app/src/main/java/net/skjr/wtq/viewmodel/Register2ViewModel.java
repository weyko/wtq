package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/10 15:43
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class Register2ViewModel {
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

    public CheckResult check() {
        if(TextUtils.isEmpty(password)) {
            return CheckResult.failure("请输入密码");
        }
        if(TextUtils.isEmpty(confirmPassword)) {
            return CheckResult.failure("请确认密码");
        }
        return CheckResult.success();
    }
}
