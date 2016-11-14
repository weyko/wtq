package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/9 9:20
 * 描述	      项目列表信息
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectListAccount {

    /**
     * count : 5
     */

    public CountEntity count;
    /**
     * stockStatus : 4
     * stockImg : http://imglf0.ph.126.net/1EnYPI5Vzo2fCkyy2GsJKg==/2829667940890114965.jpg
     * stockNO : 092314311015245
     * cityName : 广东 深圳
     * stockTitle : 测试1
     * stockType : 200
     * support : 0
     * stockMoney : 5000
     * stockShares : 0.1
     * preheatMoney : 678
     * estimatedTime : 1475817442
     * endTime : 1478361600
     * stockTypeName : 股权型
     * speed : 13
     * surplusTimeDay : -1
     * stockMoneyFormat : 0.5
     */

    public List<ListEntity> list;

    public static class CountEntity {
        public int count;
    }

    public static class ListEntity {
        public int stockStatus;
        public String stockImg;
        public String stockNO;
        public String cityName;
        public String stockTitle;
        public int stockType;
        public int support;
        public int stockMoney;
        public double stockShares;
        public int preheatMoney;
        public int estimatedTime;
        public int endTime;
        public String stockTypeName;
        public int speed;
        public int surplusTimeDay;
        public double stockMoneyFormat;
    }
}
