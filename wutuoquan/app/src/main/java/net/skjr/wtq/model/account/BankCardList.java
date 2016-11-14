package net.skjr.wtq.model.account;

import java.util.ArrayList;
import java.util.List;

/**
 * 银行卡列表
 */
public class BankCardList {
    /**
     * 可绑卡数量
     */
    public int bankCount;

    /**
     * 是否绑定了宝付 1已绑定 0未绑定
     */
    public int isBindBF;

    /**
     * 是否绑定了宝付
     * 当绑定宝付时，不允许再添加银行卡
     * @return
     */
    public boolean isBindBaoFu(){
        return isBindBF == 1;
    }

    /**
     * 银行卡列表
     */
    public List<BankCard> list = new ArrayList<>();
}
