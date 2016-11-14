package net.skjr.wtq.model.account;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/11 10:14
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectDetailAccount {
    /**
     * preheatMoney : 0
     * userCityName : null
     * industryName : 餐饮
     * leadUserNickname : null
     * surplusTimeDay : 19
     * cityName : 安徽 安庆
     * resultsIncome : 0
     * stableIncome : 0
     * leadUserID : 0
     * honourIncome : 0
     * stockTypeName : 收益型
     * endTime : 1479052800
     * stockTitle : 002
     * stockMoney : 200000
     * estimatedTime : 1477367400
     * userMood : null
     * stockImg : http://wtq.daliuliang.com.cn/common/download?token=stock||179dd953-0c88-4a1c-9a36-c2f330b82919.png
     * stockID : 138
     * speed : 0
     * stockNO : 10251145332965
     * promiseBuy : 0
     * stockMoneyFormat : 20
     * userNickname : 哈哈
     * pinglunCount : 0
     * stockType : 100
     * ensureMoney : 0
     * favoriteCount : 1
     * stockStatus : 8
     */

    public ProjectInfoEntity projectInfo;

    public static class ProjectInfoEntity {
        public int preheatMoney;
        public String userCityName;
        public String industryName;
        public String leadUserNickname;
        public String leadUserImg;    //领头人头像
        public String userImg;       //筹资人头像
        public int surplusTimeDay;
        public String cityName;
        public double resultsIncome;
        public double stableIncome;
        public int leadUserID;
        public double honourIncome;
        public String stockTypeName;
        public int endTime;
        public String stockTitle;
        public int stockMoney;
        public int estimatedTime;
        public String userMood;
        public String stockImg;
        public int stockID;
        public int speed;
        public String stockNO;
        public double promiseBuy;
        public double stockMoneyFormat;
        public String userNickname;
        public int pinglunCount;
        public int stockType;
        public double ensureMoney;
        public int favoriteCount;
        public int stockStatus;
        public int sunLeadTenderMoney;
    }


    /*public ProjectInfoEntity projectInfo;

    public static class ProjectInfoEntity {
        public int stockID;
        public String stockImg;
        public String stockTitle;
        public int stockStatus;
        public String stockNO;
        public int stockType;
        public String industryName;
        public int stockMoney;
        public int preheatMoney;
        public int estimatedTime;
        public int endTime;
        public double stableIncome;
        public double resultsIncome;
        public double honourIncome;
        public double ensureMoney;
        public double promiseBuy;
        public int leadUserID;
        public String leadUserNickname;
        public String userCityName;
        public String userMood;
        public String userNickname;
        public String stockTypeName;
        public int speed;
        public int surplusTimeDay;
        public double stockMoneyFormat;
        public Object sunLeadTenderMoney;
        public int favoriteCount;
        public int pinglunCount;
    }*/

}
