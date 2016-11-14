package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.business.LoginBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.core.utils.ui.ToastUtils;
import net.skjr.wtq.databinding.ActivityForgetPwd1Binding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.FindPwdViewModel;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/27 11:37
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ForgetPwd1Activity extends BaseToolbarActivity {

    private LoginBiz mBiz;
    private ActivityForgetPwd1Binding mBinding;
    boolean isCounting;
    boolean isProcessing;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initVariables();
        initToolbar("忘记密码");
    }

    private void initVariables() {
        mBiz = new LoginBiz();
    }

    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forget_pwd1);
        FindPwdViewModel model = new FindPwdViewModel();
        mBinding.setModel(model);
    }

    /**
     * 获取短信验证码
     *
     * @param view
     */
    public void onSmsCaptchaClick(View view) {
        if (isProcessing)
            return;

        if (isCounting)
            return;

        FindPwdViewModel model = mBinding.getModel();
        String phone = model.getPhone();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return;
        }

        isProcessing = true;
        isCounting = true;

        //如果有填入短信验证码,那么清除
        mBinding.captcha1.setText("");

        Subscription s = mBiz.sendRegSmsCode(phone)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onSendSmsCodeComplete(apiResult);
                    }
                });
        addSubscription(s);

        //延时秒数
        int i = Consts.GET_SMSCODE_TIME;
        // 第一个参数是总的倒计时时间
        // 第二个参数是每隔多少时间(ms)调用一次onTick()方法
        countDownTimer = new CountDownTimer(i * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.forgetCode.setTextColor(ForgetPwd1Activity.this.getResources().getColor(R.color.whole_style_color));
                mBinding.forgetCode.setText("等待" + millisUntilFinished / 1000 + "秒");
            }

            public void onFinish() {
                mBinding.forgetCode.setTextColor(ForgetPwd1Activity.this.getResources().getColor(R.color.whole_style_color));
                mBinding.forgetCode.setText("重新获取");
                isCounting = false;
            }
        }.start();
    }

    /**
     * 接收验证码请求的结果
     *
     * @param apiResult
     */
    public void onSendSmsCodeComplete(APIResult<Object> apiResult) {
        isProcessing = false;

        if (apiResult.isSuccess) {

            showToast("验证码已发送");
        } else {
            showToast(apiResult.message);
            //countDownTimer.cancel();
        }

    }

    public void findPwd1(View view) {
        FindPwdViewModel model = mBinding.getModel();
        CheckResult check = model.check();
        if(check.isSuccess) {
            Subscription s = mBiz.checkSmsCode(model.getPhone(), model.getPhoneCode())
                    .subscribe(new Action1<APIResult<Object>>() {
                        @Override
                        public void call(APIResult<Object> apiResult) {
                            onCheckSmsCodeComplete(apiResult);
                        }
                    });
            addSubscription(s);
        } else {
            showToast(check.errorMessage);
        }
    }

    private void onCheckSmsCodeComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, ForgetPwd2Activity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("findpwd",mBinding.getModel());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }
}
