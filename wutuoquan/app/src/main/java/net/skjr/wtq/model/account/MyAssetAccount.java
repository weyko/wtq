package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/11 15:59
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyAssetAccount {

    /**
     * totalMoney : 10000
     * availableMoney : 9200
     * frozenMoney : 800
     */

    public AccountInfoEntity accountInfo;
    /**
     * businessName : 提现冻结
     * addDatetime : 2016-10-10 12:52:24
     * availableMoneyAdd : -200
     */

    public List<AccountListEntity> accountList;

    public static class AccountInfoEntity {
        public double totalMoney;
        public double availableMoney;
        public double frozenMoney;
    }

    public static class AccountListEntity {
        public String businessName;
        public String addDatetime;
        public double availableMoneyAdd;
    }
}
