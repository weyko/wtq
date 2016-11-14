package net.skjr.wtq.model.account;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/24 17:21
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class RealNameAccount {
    /**
     * cardID : null
     * phone : 1501****5678
     * realName : null
     * realNameStatus : 0
     */

    public InfoEntity info;

    public static class InfoEntity {
        public String cardID;
        public String phone;
        public String realName;
        public int realNameStatus;

        public String phoneCode;
        public String cardCode;
    }

}
