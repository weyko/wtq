package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 15:05
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SaveApply2ViewModel {
    private String user;
    private String phone;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(user)) {
            return CheckResult.failure("请输入联系人");
        }
        if(TextUtils.isEmpty(phone)) {
            return CheckResult.failure("请输入联系方式");
        }
        return CheckResult.success();
    }
}
