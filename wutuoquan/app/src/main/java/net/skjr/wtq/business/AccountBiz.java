package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.account.LianlianPayResult;
import net.skjr.wtq.model.account.MyAssetAccount;
import net.skjr.wtq.model.account.OrderInfoAccount;
import net.skjr.wtq.model.requestobj.ForgetTrade;
import net.skjr.wtq.model.requestobj.OrderInfoObj;
import net.skjr.wtq.model.requestobj.ProjectDetailObj;
import net.skjr.wtq.model.requestobj.RechargeObj;
import net.skjr.wtq.model.requestobj.SaveApplyObj;
import net.skjr.wtq.model.system.UserInfoObj;
import net.skjr.wtq.viewmodel.SaveApplyViewModel;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/11 18:55
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AccountBiz extends BaseBiz {

    /**
     * 我的资产以及交易记录
     */
    public Observable<APIResult<MyAssetAccount>> getMyAssets() {
        //后台对应的方法code
        String requestCode = Url.MYASSETS;

        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), MyAssetAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 认购订单信息
     */
    public Observable<APIResult<OrderInfoAccount>> getBuyOrderInfo(String num) {
        //后台对应的方法code
        String requestCode = Url.ORDERINFO;
        ProjectDetailObj obj = new ProjectDetailObj();
        obj.stockNO = num;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), OrderInfoAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 申请入驻
     */
    public Observable<APIResult<Object>> saveApply(SaveApplyViewModel model) {
        //后台对应的方法code
        String requestCode = Url.SAVEAPPLY;
        SaveApplyObj obj = new SaveApplyObj();
        obj.brandName = model.getName();
        obj.industryCode = model.getInsdutry();
        obj.introduce = model.getDesc();
        obj.addressName = model.getArea();
        obj.addressID = model.getAreaId();
        obj.directCount = Integer.valueOf(model.getDirectCount());
        obj.leagueCount = Integer.valueOf(model.getLeagueCount());
        obj.contactName = model.getUser();
        obj.contactPhone = model.getPhone();

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 个人资料
     */
    public Observable<APIResult<UserInfoObj>> getUserInfo() {
        //后台对应的方法code
        String requestCode = Url.USERINFO;
        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), UserInfoObj.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 支付
     */
    public Observable<APIResult<LianlianPayResult>> gotoPay(OrderInfoObj obj) {
        String code = "lianlianzf-getPayment";
        JsonRequest request = new JsonRequest(code, obj);
        String token = getToken();

        return RxData.excute(Url.GOTOPAY, getDataId(), token, getRequestContent(request), LianlianPayResult.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 余额支付
     */
    public Observable<APIResult<LianlianPayResult>> availPay(OrderInfoObj obj) {
        String code = Url.INVESTMENT;
        JsonRequest request = new JsonRequest(code, obj);
        String token = getToken();

        return RxData.excute(Url.GOTOPAY, getDataId(), token, getRequestContent(request), LianlianPayResult.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 忘记交易密码-发送验证码
     */
    public Observable<APIResult<Object>> forgetTradeSmsCode() {
        String code = Url.FORGETTRADESMSCODE;
        JsonRequest request = new JsonRequest(code);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 忘记交易密码-校验验证码
     */
    public Observable<APIResult<Object>> forgetTradeConfirmCode(String phoneCode) {
        String code = Url.FORGETTRADECONFIRM;
        ForgetTrade obj = new ForgetTrade();
        obj.phoneCode = phoneCode;
        JsonRequest request = new JsonRequest(code, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 忘记交易密码-保存
     */
    public Observable<APIResult<Object>> forgetTradeSubmitCode(String phoneCode, String newPassword, String newPasswordTwo) {
        String code = Url.FORGETTRADESUBMIT;
        ForgetTrade obj = new ForgetTrade();
        obj.phoneCode = phoneCode;
        obj.newPassword = newPassword;
        obj.newPasswordTwo = newPasswordTwo;
        JsonRequest request = new JsonRequest(code, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 充值
     */
    public Observable<APIResult<LianlianPayResult>> recharge(double rechargeMoney) {
        String code = "lianlianzf-getRecharge";
        RechargeObj obj = new RechargeObj();
        obj.rechargeMoney = rechargeMoney;
        JsonRequest request = new JsonRequest(code, obj);
        String token = getToken();

        return RxData.excute(Url.GOTOPAY, getDataId(), token, getRequestContent(request), LianlianPayResult.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
