package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.account.BankListAccount;
import net.skjr.wtq.model.account.CityListAccount;
import net.skjr.wtq.model.account.DictInfo;
import net.skjr.wtq.model.account.DictListData;
import net.skjr.wtq.model.requestobj.BindBankObj;
import net.skjr.wtq.model.requestobj.LoginObj;
import net.skjr.wtq.model.requestobj.SetBankObj;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/14 15:21
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class BankListBiz extends BaseBiz {

    /**
     * 获取银行列表
     */
    public Observable<APIResult<DictInfo>> getBankList() {

        //后台对应的方法code
        String requestCode = Url.DICTLIST;
        DictListData dict = new DictListData();
        dict.code = "bank";
        JsonRequest request = new JsonRequest(requestCode, dict);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), DictInfo.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取银行卡列表
     */
    public Observable<APIResult<BankListAccount>> getBankCardList() {

        //后台对应的方法code
        String requestCode = Url.BANKCARDLIST;
        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), BankListAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String[] getCityItems(List<CityListAccount.ListEntity> list) {
        String items[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            items[i] = list.get(i).class_name;
        }

        return items;
    }


    /**
     * 添加银行卡短信验证码
     */
    public Observable<APIResult<Object>> sendAddCardSmsCode(String phone) {

        //后台对应的方法code
        String requestCode = Url.ADDCARDSMSCODE;
        LoginObj obj = new LoginObj();
        obj.phone = phone;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑定银行卡
     */
    public Observable<APIResult<Object>> bindingBank(String bankcode, String bank, String accountcard, String province,
                                                     String city, String phone, String verify) {

        //后台对应的方法code
        String requestCode = Url.BINDBANK;
        BindBankObj obj = new BindBankObj();
        obj.bankCode = bankcode;
        obj.bank = bank;
        obj.accountCard = accountcard;
        obj.city = city;
        obj.province = province;
        obj.mobileno = phone;
        obj.verifyCode = verify;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 设置提现卡
     */
    public Observable<APIResult<Object>> setCashBankCard(String id) {

        //后台对应的方法code
        String requestCode = Url.SETBANK;
        SetBankObj obj = new SetBankObj();
        obj.accountBankID = id;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 删除银行卡
     */
    public Observable<APIResult<Object>> deleteBankCard(String id) {

        //后台对应的方法code
        String requestCode = Url.DELBANK;
        SetBankObj obj = new SetBankObj();
        obj.accountBankID = id;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
