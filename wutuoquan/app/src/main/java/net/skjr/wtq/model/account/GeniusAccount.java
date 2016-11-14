package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/19 11:29
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GeniusAccount {

    /**
     * count : 2
     */

    public CountEntity count;
    /**
     * addDatetime : 1475899434
     * editUserID : 23
     * sort : 0
     * powerfulID : 3
     * operateTerminal : null
     * powerfulImg : /common/download?token=stock/powerful||2429e02a-65f0-4e5d-97aa-789e6607fda3.png
     * detailsImg :
     * name : df
     * operateIP : null
     * synopsis : wefwgerg
     * editDatetime : 1475999401
     * powerfulStatus : 1
     * duties : dfgdfg
     * projectName : dfdg
     * addUserID : null
     */

    public List<ListEntity> list;

    public static class CountEntity {
        public int count;
    }

    public static class ListEntity {
        public String addDatetime;
        public int editUserID;
        public int sort;
        public int powerfulID;
        public Object operateTerminal;
        public String powerfulImg;
        public String detailsImg;
        public String name;
        public Object operateIP;
        public String synopsis;
        public String editDatetime;
        public int powerfulStatus;
        public String duties;
        public String projectName;
        public Object addUserID;
    }
}
