package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/11/3 14:13
 * 描述	      新增收货地址
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AddAddressViewModel {
    private String name;
    private String phone;
    private String province;
    private String city;
    private String county;
    private String address;
    private String num;
    private boolean deault;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public boolean isDeault() {
        return deault;
    }

    public void setDeault(boolean deault) {
        this.deault = deault;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(name)) {
            return CheckResult.failure("请输入收件人姓名");
        }
        if(TextUtils.isEmpty(phone)) {
            return CheckResult.failure("请输入收件人手机号码");
        }
        if(TextUtils.isEmpty(address)) {
            return CheckResult.failure("请输入收件人详细地址");
        }
        if(TextUtils.isEmpty(num)) {
            return CheckResult.failure("请输入邮政编码");
        }
        return CheckResult.success();
    }
}
