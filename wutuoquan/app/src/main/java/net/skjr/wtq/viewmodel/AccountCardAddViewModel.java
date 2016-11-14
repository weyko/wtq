package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

public class AccountCardAddViewModel {
    private String name;
    private String account;
    private String branch;
    private String phone;
    private String code;

    private String bank;
    private String city1;
    private String city2;
    private String city3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCity1() {
        return city1;
    }

    public void setCity1(String city1) {
        this.city1 = city1;
    }

    public String getCity2() {
        return city2;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public String getCity3() {
        return city3;
    }

    public void setCity3(String city3) {
        this.city3 = city3;
    }

    public CheckResult check(){
        if(TextUtils.isEmpty(name)){
            return CheckResult.failure("请输入姓名");
        }

        if(TextUtils.isEmpty(bank)){
            return CheckResult.failure("请选择银行");
        }

        if(TextUtils.isEmpty(account)){
            return CheckResult.failure("请输入银行卡帐号");
        }

        if(TextUtils.isEmpty(city1)){
            return CheckResult.failure("请选择省");
        }

        if(TextUtils.isEmpty(city2)){
            return CheckResult.failure("请选择市");
        }

        if(TextUtils.isEmpty(city3)){
            return CheckResult.failure("请选择区");
        }

        if(TextUtils.isEmpty(branch)){
            return CheckResult.failure("请输入开户支行");
        }

        if(TextUtils.isEmpty(phone)){
            return CheckResult.failure("请输入手机号码");
        }

        if(TextUtils.isEmpty(code)){
            return CheckResult.failure("请输入验证码");
        }

        return CheckResult.success();
    }
}
