package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.SettingBiz;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.databinding.ActivitySettradePwdBinding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.SetTradePwdViewModel;

import org.greenrobot.eventbus.EventBus;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 11:09
 * 描述	      设置交易密码
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SetTradePwdActivity extends BaseToolbarActivity {

    private SettingBiz mBiz;
    private ActivitySettradePwdBinding mBinding;
    private ImageView mTrade_verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initView();
        initVariables();
        initToolbar("设置交易密码");
    }

    private void initView() {
        mTrade_verify = (ImageView) findViewById(R.id.trade_verify);
        displayCaptcha();
    }

    /**
     * 验证码
     */
    private void displayCaptcha() {
        String deviceType = String.valueOf(getMyApp().getDeviceType());
        String deviceId = getMyApp().getDeviceId();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = Url.IMGVARIFY + "?type=login&device=" + deviceType + "&deviceID=" + deviceId + "&a=" + timestamp;

        ImageLoaderUtils.displayImage(this, url, mTrade_verify);
    }

    private void initVariables() {
        mBiz = new SettingBiz();
    }
    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settrade_pwd);
        SetTradePwdViewModel model = new SetTradePwdViewModel();
        mBinding.setModel(model);
    }

    public void setTradePwd(View view) {
        SetTradePwdViewModel model = mBinding.getModel();
        CheckResult check = model.check();
        if(check.isSuccess) {
            String pwd = model.getNewPassword();
            String pwdtwo = model.getNewPasswordTwo();
            String code = model.getVerifyCode();

            Subscription s = mBiz.setTradePwd(pwd, pwdtwo, code)
                    .subscribe(new Action1<APIResult<Object>>() {
                        @Override
                        public void call(APIResult<Object> apiResult) {
                            onSetComplete(apiResult);
                        }
                    });
            addSubscription(s);
        } else {
            showToast(check.errorMessage);
        }
    }

    private void onSetComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","设置成功");
            intent.putExtra("toolbartext","设置交易密码");
            startActivity(intent);
            EventBus.getDefault().post(new Event.SetTradePwdCompleteEvent());
            finish();
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 点击刷新验证码
     */
    public void refreshVerify(View view) {
        displayCaptcha();
    }
}
