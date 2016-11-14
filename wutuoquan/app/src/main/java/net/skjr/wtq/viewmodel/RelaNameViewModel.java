package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 10:15
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class RelaNameViewModel {
    private String name;
    private String num;
    private String code;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(name)) {
            return CheckResult.failure("请输入真实姓名");
        }
        if(TextUtils.isEmpty(num)) {
            return CheckResult.failure("请输入身份证号码");
        }
        if(TextUtils.isEmpty(code)) {
            return CheckResult.failure("请输入验证码");
        }
        return CheckResult.success();
    }
}
