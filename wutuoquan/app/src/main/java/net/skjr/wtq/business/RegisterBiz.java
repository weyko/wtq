package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.system.CheckSmsCodeObj;
import net.skjr.wtq.model.requestobj.RegisterObj;
import net.skjr.wtq.model.system.SendSmsCodeObj;
import net.skjr.wtq.viewmodel.Register2ViewModel;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 注册数据类
 */
public class RegisterBiz extends BaseBiz {

    /**
     * 发送短信验证码
     * @param phone
     */
    public Observable<APIResult<Object>> sendRegSmsCode(String phone, String verityCode) {

        //后台对应的方法code
        String requestCode = Url.PHONECODE;

        SendSmsCodeObj dataObject = new SendSmsCodeObj();
        dataObject.phone = phone;
        dataObject.verifyCode = verityCode;

        JsonRequest request = new JsonRequest(requestCode, dataObject);
        String token = "";

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 注册
     * @param
     */
    public Observable<APIResult<Object>> register(Register2ViewModel model) {
        //后台对应的方法code
        String requestCode = Url.REGISTER;

        RegisterObj dataObject = new RegisterObj();
        dataObject.phone = model.getPhone();
        dataObject.phoneCode = model.getPhoneCode();
        dataObject.password = model.getPassword();
        dataObject.confirmPassword = model.getConfirmPassword();

        JsonRequest request = new JsonRequest(requestCode, dataObject);
        String token = "";

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 校验短信验证码
     */
    public Observable<APIResult<Object>> checkSmsVerify(String phone, String phonecode) {
        String code = Url.VERIFYPHONECODE;
        CheckSmsCodeObj obj = new CheckSmsCodeObj();
        obj.phone = phone;
        obj.phoneCode = phonecode;
        JsonRequest request = new JsonRequest(code, obj);
        String token = "";
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
