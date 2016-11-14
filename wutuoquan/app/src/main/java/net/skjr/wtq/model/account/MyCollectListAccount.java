package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/25 15:20
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyCollectListAccount {

    /**
     * count : null
     * list : [{"preheatMoney":100000,"sketch":"测试","addDatetime":1477379027,"support":0,"speed":100,"stockNO":"10151750384546","surplusTimeDay":19,"cityName":"福建 南平","stockMoneyFormat":"10.00","typeCode":"stock","stockTypeName":"股权型","endTime":1479052800,"stockTitle":"2016/10/15/2","stockMoney":100000,"estimatedTime":1476513000,"userID":27889,"stockType":200,"stockImg":"http://wtq.daliuliang.com.cn/common/download?token=stock||9f6996dc-1219-437c-b14f-2d69773b983b.jpg","stockStatus":8,"stockShares":0.1},{"preheatMoney":0,"sketch":"来来来来来来来来","addDatetime":1477377190,"support":0,"speed":0,"stockNO":"10251112397925","surplusTimeDay":9,"cityName":"安徽 安庆","stockMoneyFormat":"10.00","typeCode":"stock","stockTypeName":"股权型","endTime":1478188800,"stockTitle":"林飞001","stockMoney":100000,"estimatedTime":1477365300,"userID":27889,"stockType":200,"stockImg":"http://wtq.daliuliang.com.cn/common/download?token=stock||88a1c099-e4e1-4923-b74c-a395977038f7.png","stockStatus":8,"stockShares":0.1},{"preheatMoney":0,"sketch":"林飞","addDatetime":1477367588,"support":0,"speed":0,"stockNO":"10231635445485","surplusTimeDay":47,"cityName":"北京 北京","stockMoneyFormat":"20.00","typeCode":"stock","stockTypeName":"收益型","endTime":1481472000,"stockTitle":"林飞项目","stockMoney":200000,"estimatedTime":0,"userID":27889,"stockType":100,"stockImg":"http://wtq.daliuliang.com.cn/common/download?token=stock||9f6996dc-1219-437c-b14f-2d69773b983b.jpg","stockStatus":8,"stockShares":20},{"preheatMoney":0,"sketch":"哦哦哦","addDatetime":1477367371,"support":0,"speed":0,"stockNO":"10201817113284","surplusTimeDay":55,"cityName":"甘肃 定西","stockMoneyFormat":"25.56","typeCode":"stock","stockTypeName":"收益型","endTime":1482163200,"stockTitle":"哈哈","stockMoney":255555,"estimatedTime":0,"userID":27889,"stockType":100,"stockImg":"http://wtq.daliuliang.com.cn8fb5666a-840e-44d1-b84e-6d8f3623095e.jpg","stockStatus":8,"stockShares":11}]
     */

    public Object count;
    /**
     * preheatMoney : 100000
     * sketch : 测试
     * addDatetime : 1477379027
     * support : 0
     * speed : 100
     * stockNO : 10151750384546
     * surplusTimeDay : 19
     * cityName : 福建 南平
     * stockMoneyFormat : 10.00
     * typeCode : stock
     * stockTypeName : 股权型
     * endTime : 1479052800
     * stockTitle : 2016/10/15/2
     * stockMoney : 100000
     * estimatedTime : 1476513000
     * userID : 27889
     * stockType : 200
     * stockImg : http://wtq.daliuliang.com.cn/common/download?token=stock||9f6996dc-1219-437c-b14f-2d69773b983b.jpg
     * stockStatus : 8
     * stockShares : 0.1
     */

    public List<ListEntity> list;

    public static class ListEntity {
        public int preheatMoney;
        public String sketch;
        public int addDatetime;
        public int support;
        public int speed;
        public String stockNO;
        public int surplusTimeDay;
        public String cityName;
        public String stockMoneyFormat;
        public String typeCode;
        public String stockTypeName;
        public int endTime;
        public String stockTitle;
        public int stockMoney;
        public int estimatedTime;
        public int userID;
        public int stockType;
        public String stockImg;
        public int stockStatus;
        public double stockShares;
    }
}
