package net.skjr.wtq.model.account;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/28 14:11
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GetCashAccount {

    /**
     * bankStr : 光大银行 尾号（9588）
     * accountBankID : 10026
     */

    public AccountBankEntity accountBank;
    /**
     * accountBank : {"bankStr":"光大银行 尾号（9588）","accountBankID":10026}
     * phone : 150****5678
     * payStatus : 1
     * cashMoney : 957300
     * availableMoney : 957300
     * realNameStatus : 1
     * cashRate : 0
     */

    public String phone;
    public int payStatus;
    public double cashMoney;
    public double availableMoney;
    public int realNameStatus;
    public double cashRate;

    public static class AccountBankEntity {
        public String bankStr;
        public int accountBankID;
    }
}
