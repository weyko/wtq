package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/9 16:28
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class PublishProject2ViewModel {
    private String linkman;
    private String phone;

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(linkman)) {
            return CheckResult.failure("请输入联系人");
        }
        if(TextUtils.isEmpty(phone)) {
            return CheckResult.failure("请输入联系方式");
        }
        return CheckResult.success();
    }
}
