package net.skjr.wtq.model.account;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/11 16:05
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class TabMineAccount {

    /**
     * userImg : null
     * userNickname : 钟先生
     * phone : 13750237717
     * availableMoney : 9200
     */

    public UserInfoAccountEntity userInfoAccount;
    /**
     * fenhongCount : 10000
     * yongjinCount : 12000
     */

    public LeijiCountEntity leijiCount;

    public static class UserInfoAccountEntity {
        public String userImg;
        public String userNickname;
        public String phone;
        public double availableMoney;
        public int realNameStatus;
    }

    public static class LeijiCountEntity {
        public int fenhongCount;
        public int yongjinCount;
    }
}
