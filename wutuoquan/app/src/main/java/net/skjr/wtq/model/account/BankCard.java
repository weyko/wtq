package net.skjr.wtq.model.account;


import net.skjr.wtq.core.utils.common.StringUtils;

/**
 * 银行卡
 */
public class BankCard {



    /**
     * 银行账号唯一ID
     */
    public int accountBankID;

    /**
     * 银行编码
     */
    public String bankCode;

    /**
     * 银行账号
     */
    public String accountCard;

    /**
     * 银行名称
     */
    public String bank;

    /**
     * 支行名称
     */
    public String branch;

    /**
     * 银行账号户名
     */
    public String bankUserName;

    /**
     * 是否提现卡 0：否 1：是
     */
    public String useMark;

    /**
     * 是否提现卡
     * @return
     */
    public Boolean isTixianCard(){
        return StringUtils.isEquals(useMark, "1");
    }


}
