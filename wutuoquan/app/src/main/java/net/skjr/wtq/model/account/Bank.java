
package net.skjr.wtq.model.account;

import java.io.Serializable;

/**
 * 银行
 */
public class Bank implements Serializable {
    /**
     * 编号
     */
    public String channelid = "";

    /**
     * 名称
     */
    public String channelname = "";

    /**
     * 渠道商ID
     */
    public String paycenterid = "";

    /**
     * 渠道商米gnc
     */
    public String paycentername = "";
    /**
     * 银行id
     */
    public String banknameid;
}
