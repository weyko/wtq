package net.skjr.wtq.model.system;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/14 15:23
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class BankListObj {

    /**
     * name : 中国银行
     * code : BOC
     * parentCode : bank
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
