package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/8 10:29
 * 描述	      首页数据
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class TabhomeAccount {

    /**
     * userCount : 6852
     * bonusCount : 695
     * stockMoneyCount : 53426
     * projectCount : 56
     */

    public CountListEntity countList;
    /**
     * preheatMoney : 0
     * support : 0
     * speed : 0
     * stockNO : 10061424897595
     * surplusTimeDay : 28
     * cityName : null
     * stockMoneyFormat : 1
     * stockTypeName : 股权型
     * endTime : 1478361600
     * stockTitle : 似懂非懂是
     * stockMoney : 10000
     * estimatedTime : 1475817442
     * stockType : 200
     * stockImg : /common/download?token=stock||b3253433-4edd-4a02-90d3-08f6a44d17ca.jpg
     * stockStatus : 8
     * stockShares : 10
     */

    public List<ChouziListEntity> chouziList;
    /**
     * imgUrl : /common/download?token=article||b10e7e95-c5fc-49d5-ae5d-08179e9c7b3f.png
     * imgName : banner.png
     * linkUrl :
     * imgID : 31
     * imgType : null
     * inLink : 2
     * imgTitle : banner.png
     */

    public List<BannerListEntity> bannerList;
    /**
     * preheatMoney : 678
     * support : 0
     * speed : 13
     * stockNO : 092314311015245
     * surplusTimeDay : -1
     * cityName : 广东 深圳
     * stockMoneyFormat : 0.5
     * stockTypeName : 股权型
     * endTime : 1478361600
     * stockTitle : 测试1
     * stockMoney : 5000
     * estimatedTime : 1475817442
     * stockType : 200
     * stockImg : http://imglf0.ph.126.net/1EnYPI5Vzo2fCkyy2GsJKg==/2829667940890114965.jpg
     * stockStatus : 4
     * stockShares : 0.1
     */

    public List<YureListEntity> yureList;

    public static class CountListEntity {
        public int userCount;
        public String bonusCount;
        public String stockMoneyCount;
        public int projectCount;
    }

    public static class ChouziListEntity {
        public int preheatMoney;
        public int support;
        public int speed;
        public String stockNO;
        public int surplusTimeDay;
        public Object cityName;
        public double stockMoneyFormat;
        public String stockTypeName;
        public int endTime;
        public String stockTitle;
        public int stockMoney;
        public int estimatedTime;
        public int stockType;
        public String stockImg;
        public int stockStatus;
        public double stockShares;
    }

    public static class BannerListEntity {
        public String imgUrl;
        public String imgName;
        public String linkUrl;
        public int imgID;
        public Object imgType;
        public int inLink;
        public String imgTitle;
    }

    public static class YureListEntity {
        public int preheatMoney;
        public int support;
        public int speed;
        public String stockNO;
        public int surplusTimeDay;
        public String cityName;
        public double stockMoneyFormat;
        public String stockTypeName;
        public int endTime;
        public String stockTitle;
        public int stockMoney;
        public int estimatedTime;
        public int stockType;
        public String stockImg;
        public int stockStatus;
        public double stockShares;
    }
}
