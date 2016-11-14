package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.SettingBiz;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.databinding.ActivityLoginPwdBinding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.viewmodel.ResetLoginPwdViewModel;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 9:28
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LoginPwdActivity extends BaseToolbarActivity {

    private ImageView mReset_login_verify;
    private SettingBiz mBiz;
    private ActivityLoginPwdBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("修改登录密码");
        initVariables();
        initView();
    }

    private void initView() {
        mReset_login_verify = (ImageView) findViewById(R.id.reset_login_verify);
        displayCaptcha();
    }
    private void initVariables() {
        mBiz = new SettingBiz();
    }
    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_pwd);
        ResetLoginPwdViewModel model = new ResetLoginPwdViewModel();
        mBinding.setModel(model);
    }

    /**
     * 登录验证码
     */
    private void displayCaptcha() {
        String deviceType = String.valueOf(getMyApp().getDeviceType());
        String deviceId = getMyApp().getDeviceId();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = Url.IMGVARIFY + "?type=login&device=" + deviceType + "&deviceID=" + deviceId + "&a=" + timestamp;

        ImageLoaderUtils.displayImage(this, url, mReset_login_verify);
    }

    /**
     * 提交密码
     * @param view
     */
    public void submitLoginPwd(View view) {
        ResetLoginPwdViewModel model = mBinding.getModel();
        String old = model.getOldPassword();
        String newpwd = model.getNewPassword();
        String conpwd = model.getConfirmPassword();
        String code = model.getVerifyCode();
        Subscription s = mBiz.resetLoginPwd(old, newpwd, conpwd, code)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onResetComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onResetComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","修改成功");
            intent.putExtra("toolbartext","修改登录密码");
            startActivity(intent);
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
