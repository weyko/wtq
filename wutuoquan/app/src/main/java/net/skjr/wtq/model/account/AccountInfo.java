package net.skjr.wtq.model.account;

import java.io.Serializable;

/**
 * result-账户信息
 */
public class AccountInfo implements Serializable {

    public String app;

    public String phone;

    //public String token = "";

    /**
     * 账户
     */
    public String userName = "";
    public String realName = "";

    /**
     * 是否实名认证 0-未认证  1-已认证
     */
    public int realNameStatus = 0;

    /**
     * 是否设置过交易密码，1=设置过
     */
    public int payStatus;

    /**
     * 账户总额
     */
    public String totalMoney = "0.00";

    /**
     * 冻结金额
     */
    public String unavailableMoney = "0.00";

    /**
     * 可用金额
     */
    public String availableMoney = "0.00";

    /**
     * 总收益
     */
    public String incomeTotal = "0.00";

    /**
     * 今日收益
     */
    public String incomeToday = "0.00";

    /**
     * 是否已经实名认证
     * @return
     */
    public boolean isRealName(){
        return realNameStatus == 1;
    }

    /**
     * 是否已经设置过交易密码
     * @return
     */
    public boolean hasSetTradePwd(){
        return payStatus == 1;
    }

    /**
     * 用户级别
     */
    public String userGrade;

    /**
     * 用户头像
     */
    public String userHeadImg;

    /**
     * 推荐连接
     */

    public String inviteUrl;
}
