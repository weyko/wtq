package net.skjr.wtq.model.account;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/12 10:09
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class OrderInfoAccount {
    /**
     * sketch : 来来来来来来来来
     * fraction : 100
     * baozhenMoneyLilv : 0.07
     * buyFraction : 0
     * stockImg : http://wtq.daliuliang.com.cn/common/download?token=stock||88a1c099-e4e1-4923-b74c-a395977038f7.png
     * availableMoney : 0.04
     * leadUserID : 0
     * stockTitle : 林飞001
     * stockShares : 0.1
     * stockMoney : 100000
     */

    public DataEntity data;

    public static class DataEntity {
        public String sketch;
        public int fraction;
        public double baozhenMoneyLilv;
        public int buyFraction;
        public String stockImg;
        public double availableMoney;
        public int leadUserID;
        public String stockTitle;
        public double stockShares;
        public int stockMoney;
    }

}
