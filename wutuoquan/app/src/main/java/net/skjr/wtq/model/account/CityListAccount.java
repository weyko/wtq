package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/12 14:35
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class CityListAccount {

    /**
     * count : 34
     */

    public CountEntity count;
    /**
     * class_id : 2
     * class_name : 北京
     * class_type : 1
     */

    public List<ListEntity> list;

    public static class CountEntity {
        public int count;
    }

    public static class ListEntity {
        public int class_id;
        public String class_name;
        public int class_type;
    }
}
