package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/9 11:19
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class PublishProject1ViewModel implements Serializable {


    private String stockTitle;
    private String stockImg;
    private String regionCity;
    private String sketch;
    private String purpose;
    private String linkman;
    private String phone;

    private String industryType;
    private String fraction;
    private String validDays;

    private String stockMoney;
    private String stockShares;

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getStockTitle() {
        return stockTitle;
    }

    public void setStockTitle(String stockTitle) {
        this.stockTitle = stockTitle;
    }

    public String getStockImg() {
        return stockImg;
    }

    public void setStockImg(String stockImg) {
        this.stockImg = stockImg;
    }

    public String getRegionCity() {
        return regionCity;
    }

    public void setRegionCity(String regionCity) {
        this.regionCity = regionCity;
    }

    public String getSketch() {
        return sketch;
    }

    public void setSketch(String sketch) {
        this.sketch = sketch;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

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

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getFraction() {
        return fraction;
    }

    public void setFraction(String fraction) {
        this.fraction = fraction;
    }

    public String getValidDays() {
        return validDays;
    }

    public void setValidDays(String validDays) {
        this.validDays = validDays;
    }

    public String getStockMoney() {
        return stockMoney;
    }

    public void setStockMoney(String stockMoney) {
        this.stockMoney = stockMoney;
    }

    public String getStockShares() {
        return stockShares;
    }

    public void setStockShares(String stockShares) {
        this.stockShares = stockShares;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(stockImg)) {
            return CheckResult.failure("请上传项目封面");
        }
        if(TextUtils.isEmpty(stockTitle)) {
            return CheckResult.failure("请输入项目名称");
        }
        if(TextUtils.isEmpty(industryType)) {
            return CheckResult.failure("请选择所属行业");
        }
        if(TextUtils.isEmpty(regionCity)) {
            return CheckResult.failure("请选择所在地");
        }
        if(TextUtils.isEmpty(purpose)) {
            return CheckResult.failure("请输入筹资目的");
        }
        if(TextUtils.isEmpty(sketch)) {
            return CheckResult.failure("请输入项目描述");
        }
        if(TextUtils.isEmpty(stockMoney)) {
            return CheckResult.failure("请输入目标金额");
        }
        if(TextUtils.isEmpty(validDays)) {
            return CheckResult.failure("请输入筹资期限");
        }
        if(TextUtils.isEmpty(stockShares)) {
            return CheckResult.failure("请出让股份");
        }
        if(TextUtils.isEmpty(fraction)) {
            return CheckResult.failure("请输入出让份数");
        }
        if(!checked) {
            return CheckResult.failure("请阅读并同意风险提示");
        }

        return CheckResult.success();
    }
}
