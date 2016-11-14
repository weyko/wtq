package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.account.RealNameAccount;
import net.skjr.wtq.model.account.SettingInfoAccount;
import net.skjr.wtq.model.requestobj.ResetLoginPwdObj;
import net.skjr.wtq.model.requestobj.ResetTradePwdObj;
import net.skjr.wtq.model.requestobj.SetTradePwdObj;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 9:44
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SettingBiz extends BaseBiz {

    /**
     * 修改登录密码
     */
    public Observable<APIResult<Object>> resetLoginPwd(String oldpwd, String pwd, String conpwd, String code) {
        //后台对应的方法code
        String requestCode = Url.LOGINPWD;

        ResetLoginPwdObj obj = new ResetLoginPwdObj();
        obj.oldPassword = oldpwd;
        obj.newPassword = pwd;
        obj.confirmPassword = conpwd;
        obj.verifyCode = code;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 修改交易密码
     */
    public Observable<APIResult<Object>> resetTradePwd(int userid, String oldpwd, String pwd, String code) {
        //后台对应的方法code
        String requestCode = Url.RESETTRADEPWD;

        ResetTradePwdObj obj = new ResetTradePwdObj();
        obj.userID = userid;
        obj.oldPassword = oldpwd;
        obj.newPassword = pwd;
        obj.verifyCode = code;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 设置交易密码
     */
    public Observable<APIResult<Object>> setTradePwd(String pwd, String pwdtwo, String code) {
        //后台对应的方法code
        String requestCode = Url.SETTRADEPWD;

        SetTradePwdObj obj = new SetTradePwdObj();
        obj.newPassword = pwd;
        obj.newPasswordTwo = pwdtwo;
        obj.verifyCode = code;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 安全退出
     */
    public Observable<APIResult<Object>> loginOut() {
        //后台对应的方法code
        String requestCode = Url.LOGINOUT;

        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 实名认证页面信息
     */
    public Observable<APIResult<RealNameAccount>> realNameInfo() {
        //后台对应的方法code
        String requestCode = Url.REALNAMEINFO;

        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), RealNameAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 实名认证发送短信验证码
     */
    public Observable<APIResult<Object>> realNameSmsCode() {
        //后台对应的方法code
        String requestCode = Url.SENDCODEREALNAME;

        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 实名认证提交
     */
    public Observable<APIResult<Object>> realNameSubmit(String name, String card, String phoneCode) {
        //后台对应的方法code
        String requestCode = Url.CHECKREALNAME;
        RealNameAccount.InfoEntity obj = new RealNameAccount.InfoEntity();
        obj.cardCode = card;
        obj.realName = name;
        obj.phoneCode = phoneCode;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 设置页面信息
     */
    public Observable<APIResult<SettingInfoAccount>> settingInfo() {
        //后台对应的方法code
        String requestCode = Url.SETINFO;

        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), SettingInfoAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
