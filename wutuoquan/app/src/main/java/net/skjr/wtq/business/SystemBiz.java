package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.account.CheckUserAccount;
import net.skjr.wtq.model.requestobj.AuthInvestorObj;
import net.skjr.wtq.model.requestobj.CheckUserObj;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/24 10:04
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SystemBiz extends BaseBiz {

    /**
     * 是否实名
     */
    public Observable<APIResult<CheckUserAccount>> isRealName() {
        //后台对应的方法code
        String requestCode = Url.CHECKUSER;

        CheckUserObj obj = new CheckUserObj();
        obj.type = "realName";

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), CheckUserAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 是否认证投资人
     */
    public Observable<APIResult<CheckUserAccount>> isInvestor() {
        //后台对应的方法code
        String requestCode = Url.CHECKUSER;

        CheckUserObj obj = new CheckUserObj();
        obj.type = "investor";

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), CheckUserAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 认证投资人
     */
    public Observable<APIResult<Object>> authInvestor(String condition, String company, String contact) {
        //后台对应的方法code
        String requestCode = Url.INVESTORAUTH;

        AuthInvestorObj obj = new AuthInvestorObj();
        obj.authCondition = condition;
        obj.company = company;
        obj.contact = contact;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 是否被收藏
     */
    public Observable<APIResult<CheckUserAccount>> isFavorite(String code) {
        //后台对应的方法code
        String requestCode = Url.CHECKUSER;

        CheckUserObj obj = new CheckUserObj();
        obj.type = "favorite";
        obj.code = code;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), CheckUserAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
