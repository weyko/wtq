package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/11/3 15:56
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AddressListAccount {

    /**
     * count : null
     * list : []
     */
    public String count;
    public List<ListEntity> list;

    public static class ListEntity {
        public int addressID;
        public int isDefault;
        public String provinceName;
        public String cityName;
        public String townName;
        public String detailAddress;
        public String attention;
        public String phone;
    }
}
