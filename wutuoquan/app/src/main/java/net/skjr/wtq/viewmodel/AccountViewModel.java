package net.skjr.wtq.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;

import net.skjr.wtq.BR;

/**
 * 账户便签页视图对象
 */
public class AccountViewModel extends BaseObservable {

    private String phone;
    private String name;

    //账户总额
    private String sum = "0.00";

    //可用余额
    private String use = "0.00";

    //冻结金额
    private String freeze = "0.00";

    //累计收益
    private String incomeTotal = "0.00";

    //今日收益
    private String incomeToday = "0.00";

    /**
     * 投资总额
     */
    private String tenderMoney = "0.00";

    /**
     * 获得收益
     */
    private String tenderInterest = "0.00";

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (TextUtils.equals(this.phone, phone))
            return;

        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (TextUtils.equals(this.name, name))
            return;

        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        if (TextUtils.equals(this.sum, sum))
            return;

        this.sum = sum;
        notifyPropertyChanged(BR.sum);
    }

    @Bindable
    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        if (TextUtils.equals(this.use, use))
            return;

        this.use = use;
        notifyPropertyChanged(BR.use);
    }

    @Bindable
    public String getFreeze() {
        return freeze;
    }

    public void setFreeze(String freeze) {
        if (TextUtils.equals(this.freeze, freeze))
            return;

        this.freeze = freeze;
        notifyPropertyChanged(BR.freeze);
    }

    @Bindable
    public String getIncomeTotal() {
        return incomeTotal;
    }

    public void setIncomeTotal(String incomeTotal) {
        if (TextUtils.equals(this.incomeTotal, incomeTotal))
            return;

        this.incomeTotal = incomeTotal;
        notifyPropertyChanged(BR.incomeTotal);
    }

    @Bindable
    public String getIncomeToday() {
        return incomeToday;
    }

    public void setIncomeToday(String incomeToday) {
        if (TextUtils.equals(this.incomeToday, incomeToday))
            return;

        this.incomeToday = incomeToday;
        notifyPropertyChanged(BR.incomeToday);
    }

    @Bindable
    public String getTenderMoney() {
        return tenderMoney;
    }

    public void setTenderMoney(String tenderMoney) {
        if (TextUtils.equals(this.tenderMoney, tenderMoney))
            return;

        this.tenderMoney = tenderMoney;
        notifyPropertyChanged(BR.tenderMoney);
    }

    @Bindable
    public String getTenderInterest() {
        return tenderInterest;
    }

    public void setTenderInterest(String tenderInterest) {
        if (TextUtils.equals(this.tenderInterest, tenderInterest))
            return;

        this.tenderInterest = tenderInterest;
        notifyPropertyChanged(BR.tenderInterest);
    }

}
