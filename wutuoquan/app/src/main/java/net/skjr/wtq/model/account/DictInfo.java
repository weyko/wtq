package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/8 19:34
 * 描述	      行业信息
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DictInfo {

    /**
     * name : 餐饮
     * code : canyin
     * parentCode : industry
     * sort : 1
     * items : []
     * subItemMap : {}
     * itemMap : {}
     * data : null
     */

    public List<ListEntity> list;

    public static class ListEntity {
        public String name;
        public String code;
        public String parentCode;
        public int sort;
        public SubItemMapEntity subItemMap;
        public ItemMapEntity itemMap;
        public Object data;
        public List<?> items;

        public static class SubItemMapEntity {
        }

        public static class ItemMapEntity {
        }
    }
}
