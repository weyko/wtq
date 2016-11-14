package net.skjr.wtq.model.account;

import java.io.Serializable;

/**
 * request-添加银行卡
 */
public class AddBankCardObj implements Serializable{
    /**
     * 银行卡号
     */
    public String bankNO;

    /**
     * 开户地址 省+市+区
     */
    public String branch;

    public String phoneCode;

    /**
     * 银行编码
     */
    public String bankCode;

    /**
     * 支行名称
     */
    public String bankName;


}

/*

    "bankNO":"222222222222",
        "branch":"广东省深圳市福田区",
        "phoneCode":"888888",
        "bankCode":"BOC",
        "bankName":"中国银行梅林支行"


 */
