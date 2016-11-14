package net.skjr.wtq.business;

import android.util.Log;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.requestobj.FindPwdObj;
import net.skjr.wtq.model.requestobj.LoginObj;
import net.skjr.wtq.model.system.LoginResultObj;
import net.skjr.wtq.model.system.SendSmsCodeObj;
import net.skjr.wtq.viewmodel.FindPwdViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/10 16:36
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LoginBiz extends BaseBiz {

    /**
     * 登录
     * @param
     */
    public Observable<APIResult<LoginResultObj>> login(String phone, String pwd, String code) {
        //后台对应的方法code
        String requestCode = Url.LOGIN;

        LoginObj obj = new LoginObj();
        obj.phone = phone;
        obj.password = pwd;
        obj.verifyCode = code;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = "";
        Log.e("bobo",getRequestContent(request));
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), LoginResultObj.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 忘记密码-获取验证码
     */
    public Observable<APIResult<Object>> sendRegSmsCode(String phone) {

        //后台对应的方法code
        String requestCode = Url.FINDPWDGETCODE;

        SendSmsCodeObj dataObject = new SendSmsCodeObj();
        dataObject.phone = phone;

        JsonRequest request = new JsonRequest(requestCode, dataObject);
        String token = "";

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 忘记密码-校验验证码
     */
    public Observable<APIResult<Object>> checkSmsCode(String phone, String phoneCode) {

        //后台对应的方法code
        String requestCode = Url.FINDPWDCHECKCODE;

        FindPwdObj obj = new FindPwdObj();
        obj.phone = phone;
        obj.phoneCode = phoneCode;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = "";

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 忘记密码-重置密码
     */
    public Observable<APIResult<Object>> resetPwd(FindPwdViewModel obj) {

        //后台对应的方法code
        String requestCode = Url.RESETPWD;

        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = "";

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
