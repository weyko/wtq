package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/18 9:34
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class BankListAccount {
    /**
     * branch : null
     * bank : 招商银行
     * accountCard : 588995589
     * useMark : 1
     * bankUserName : 彭石刚
     * accountBankID : 10023
     * bankCode : null
     */

    public List<BankListEntity> bankList;

    public static class BankListEntity {
        public String branch;
        public String bank;
        public String accountCard;
        public int useMark;
        public String bankUserName;
        public String accountBankID;
        public String bankCode;
    }


     public List<BankListAccount.ListEntity> list;


    public static class ListEntity {

        public String accountBankID;
        public String bankCode ;
        public String accountCard ;
        public String bank ;
        public String branch ;
        public String bankUserName ;
        public boolean useMark ;
    }

}
