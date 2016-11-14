package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/19 17:15
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyInvestAccount {
    /**
     * incomeSum : 5000
     * list : [{"tenderStatus":0,"addDatetime":"2016-10-26 17:53:15","buyFraction":1,"stockImg":"/common/download?token=stock||179dd953-0c88-4a1c-9a36-c2f330b82919.png","stockStatus":8,"pledgeType":2,"buyShares":0,"tenderStatusName":"认购申请成功","stockTitle":"002","tenderMoney":0.1},{"tenderStatus":0,"addDatetime":"2016-10-26 17:37:08","buyFraction":1,"stockImg":"/common/download?token=stock||179dd953-0c88-4a1c-9a36-c2f330b82919.png","stockStatus":8,"pledgeType":2,"buyShares":0,"tenderStatusName":"认购申请成功","stockTitle":"002","tenderMoney":0.1},{"tenderStatus":0,"addDatetime":"2016-10-26 16:36:01","buyFraction":200,"stockImg":"/common/download?token=stock||179dd953-0c88-4a1c-9a36-c2f330b82919.png","stockStatus":8,"pledgeType":2,"buyShares":20,"tenderStatusName":"认购申请成功","stockTitle":"002","tenderMoney":20}]
     * tenderMoneySum : 0
     * tenderCount : 4
     */

    public int incomeSum;
    public int tenderMoneySum;
    public int tenderCount;
    /**
     * tenderStatus : 0
     * addDatetime : 2016-10-26 17:53:15
     * buyFraction : 1
     * stockImg : /common/download?token=stock||179dd953-0c88-4a1c-9a36-c2f330b82919.png
     * stockStatus : 8
     * pledgeType : 2
     * buyShares : 0
     * tenderStatusName : 认购申请成功
     * stockTitle : 002
     * tenderMoney : 0.1
     */

    public List<ListEntity> list;

    public static class ListEntity {
        public int tenderStatus;
        public String addDatetime;
        public int buyFraction;
        public String stockImg;
        public int stockStatus;
        public int pledgeType;
        public int buyShares;
        public String tenderStatusName;
        public String stockTitle;
        public String tenderNO;
        public double tenderMoney;
    }

    /**
     * incomeSum : 5000
     * list : []
     * tenderMoneySum : 0
     * tenderCount : 0
     *
     * stockTitle标题
     stockImg封面图片
     buyFraction认购份数
     tenderMoney认购金额（ 已格式化为万元）
     buyShares股权占比
     tenderStatus投资状态
     tenderStatusName 投资状态文字
     stockStatus项目状态
     addDatetime 投资时间
     */


}
