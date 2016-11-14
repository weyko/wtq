package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/24 19:55
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyPublishAccount {

    /**
     * count : null
     * list : [{"preheatMoney":0,"sketch":"五十我会","support":0,"speed":0,"stockNO":"10249512675687","surplusTimeDay":0,"cityName":"安徽 巢湖","stockMoneyFormat":50,"stockTypeName":"收益型","endTime":null,"stockTitle":"图故意","stockMoney":500000,"estimatedTime":0,"stockType":100,"stockImg":"/common/download?token=user|27889|c865fe1d-46c7-4460-b14e-585bcdd538f1.jpg?t=1477273837","stockStatus":1,"stockShares":10},{"preheatMoney":0,"sketch":"近距离","support":0,"speed":0,"stockNO":"102320031036585","surplusTimeDay":0,"cityName":"安徽 蚌埠","stockMoneyFormat":55.5555,"stockTypeName":"收益型","endTime":null,"stockTitle":"近距离","stockMoney":555555,"estimatedTime":0,"stockType":100,"stockImg":"/common/download?token=user|27889|0dd98653-d26f-4f3a-9874-a336b7d11003.jpg?t=1477223967","stockStatus":1,"stockShares":12},{"preheatMoney":0,"sketch":"哦哦哦","support":0,"speed":0,"stockNO":"10201817113284","surplusTimeDay":56,"cityName":"甘肃 定西","stockMoneyFormat":25.5555,"stockTypeName":"收益型","endTime":1482163200,"stockTitle":"哈哈","stockMoney":255555,"estimatedTime":0,"stockType":100,"stockImg":"8fb5666a-840e-44d1-b84e-6d8f3623095e.jpg","stockStatus":8,"stockShares":11}]
     */

    public Object count;
    /**
     * preheatMoney : 0
     * sketch : 五十我会
     * support : 0
     * speed : 0
     * stockNO : 10249512675687
     * surplusTimeDay : 0
     * cityName : 安徽 巢湖
     * stockMoneyFormat : 50
     * stockTypeName : 收益型
     * endTime : null
     * stockTitle : 图故意
     * stockMoney : 500000
     * estimatedTime : 0
     * stockType : 100
     * stockImg : /common/download?token=user|27889|c865fe1d-46c7-4460-b14e-585bcdd538f1.jpg?t=1477273837
     * stockStatus : 1
     * stockShares : 10
     */

    public List<ListEntity> list;

    public static class ListEntity {
        public int preheatMoney;
        public String sketch;
        public int support;
        public int speed;
        public String stockNO;
        public int surplusTimeDay;
        public String cityName;
        public double stockMoneyFormat;
        public String stockTypeName;
        public Object endTime;
        public String stockTitle;
        public int stockMoney;
        public int estimatedTime;
        public int stockType;
        public String stockImg;
        public int stockStatus;
        public int stockShares;
    }
}
