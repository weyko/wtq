package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.requestobj.GetCashObj;
import net.skjr.wtq.model.requestobj.LoginObj;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/18 15:57
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AssetsBiz extends BaseBiz{

    /**
     * 提现信息
     */
    public Observable<APIResult<Object>> getCashInfo(String phone) {
        //后台对应的方法code
        String requestCode = Url.GETCASHINFO;

        LoginObj obj = new LoginObj();
        obj.phone = phone;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 提现
     */
    public Observable<APIResult<Object>> getCash(String accountBankID, String pwd, String money) {
        //后台对应的方法code
        String requestCode = Url.GETCASH;

        GetCashObj obj = new GetCashObj();
//        obj.accountBankID = accountBankID;
        obj.payPassword = pwd;
//        obj.wmoney = money;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
