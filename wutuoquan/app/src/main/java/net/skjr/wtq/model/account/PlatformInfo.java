package net.skjr.wtq.model.account;

import java.io.Serializable;

/**
 * 平台数据
 */
public class PlatformInfo implements Serializable {
    /**
     * 投资总额
     */
    public String tenderMoney = "0.00";

    /**
     * 获得收益
     */
    public String tenderInterest = "0.00";
}
