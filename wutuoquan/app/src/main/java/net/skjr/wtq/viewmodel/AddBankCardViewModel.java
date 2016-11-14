package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/17 20:24
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AddBankCardViewModel {
    private String username;
    private String bankCode;
    private String accountCard;
    private String bank;
    private String province;
    private String city;
    private String mobileno;
    private String verifyCode;
    private String zhihang;    //开户支行

    public String getZhihang() {
        return zhihang;
    }

    public void setZhihang(String zhihang) {
        this.zhihang = zhihang;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAccountCard() {
        return accountCard;
    }

    public void setAccountCard(String accountCard) {
        this.accountCard = accountCard;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
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

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(bank)) {
            return CheckResult.failure("请选择银行");
        }
        if(TextUtils.isEmpty(accountCard)) {
            return CheckResult.failure("请输入银行账号");
        }
        if(TextUtils.equals("省",province)) {
            return CheckResult.failure("请选择省");
        }
        if(TextUtils.equals("市",city)) {
            return CheckResult.failure("请选择市");
        }
        if(TextUtils.isEmpty(zhihang)) {
            return CheckResult.failure("请输入银行支行");
        }
        if(TextUtils.isEmpty(mobileno)) {
            return CheckResult.failure("请输入银行预留手机号");
        }
        return CheckResult.success();
    }
}
