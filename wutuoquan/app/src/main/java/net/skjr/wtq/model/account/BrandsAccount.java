package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/19 11:53
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class BrandsAccount {

    /**
     * count : 6
     */

    public CountEntity count;
    /**
     * addDatetime : 1475914548
     * simpleImg : null
     * sort : null
     * industryName : null
     * industryCode : jianzhu
     * directCount : 34
     * leagueCount : 34
     * addressID : 10,142
     * operateTerminal : null
     * brandName : erg
     * detailsImg : null
     * brandID : 6
     * synopsis : null
     * operateIP : null
     * editDatetime : 1475914548
     * brandStatus : 1
     * addressName : 河北 邯郸
     * addUserID : null
     */

    public List<ListEntity> list;

    public static class CountEntity {
        public int count;
    }

    public static class ListEntity {
        public String addDatetime;
        public String simpleImg;
        public Object sort;
        public Object industryName;
        public String industryCode;
        public int directCount;
        public int leagueCount;
        public String addressID;
        public Object operateTerminal;
        public String brandName;
        public Object detailsImg;
        public int brandID;
        public String synopsis;
        public Object operateIP;
        public String editDatetime;
        public int brandStatus;
        public String addressName;
        public Object addUserID;
    }
}
