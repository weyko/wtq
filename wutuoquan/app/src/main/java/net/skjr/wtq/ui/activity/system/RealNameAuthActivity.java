package net.skjr.wtq.ui.activity.system;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.business.SettingBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.core.utils.ui.ToastUtils;
import net.skjr.wtq.databinding.ActivityRealnameAuthBinding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.RealNameAccount;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.viewmodel.RelaNameViewModel;

import org.greenrobot.eventbus.EventBus;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/12 18:08
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class RealNameAuthActivity extends BaseToolbarActivity {

    private SettingBiz mBiz;
    private ActivityRealnameAuthBinding mBinding;

    CountDownTimer countDownTimer;
    boolean isCounting;
    boolean isProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("实名认证");
        initView();
        initVariables();
        initData();
    }

    private void initView() {

    }

    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_realname_auth);
        RelaNameViewModel model = new RelaNameViewModel();
        mBinding.setModel(model);
    }

    private void initData() {
        showProgressDialog();
        Subscription s = mBiz.realNameInfo()
                .subscribe(new Action1<APIResult<RealNameAccount>>() {
                    @Override
                    public void call(APIResult<RealNameAccount> accountInfoAPIResult) {
                        onGetInfoComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetInfoComplete(APIResult<RealNameAccount> apiResult) {
        if(apiResult.isSuccess) {
            RealNameAccount result = apiResult.result;
            RealNameAccount.InfoEntity info = result.info;
            mBinding.realnamePhone.setText(info.phone);
            dismissProgressDialog();
        } else {
            showToast(apiResult.message);
            dismissProgressDialog();
        }
    }

    private void initVariables() {
        mBiz = new SettingBiz();
    }

    public void onSmsVerifyClick(View view) {
        if (isProcessing)
            return;

        if (isCounting)
            return;

        RelaNameViewModel model = mBinding.getModel();
        String card = model.getNum();
        String name = model.getName();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show(this, "请输入真实姓名");
            return;
        }

        if (TextUtils.isEmpty(card)) {
            ToastUtils.show(this, "请输入身份证号");
            return;
        }

        isProcessing = true;
        isCounting = true;

        //如果有填入短信验证码,那么清除
        mBinding.smscode.setText("");

        Subscription s = mBiz.realNameSmsCode()
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
                mBinding.smscode.setTextColor(RealNameAuthActivity.this.getResources().getColor(R.color.whole_style_color));
                mBinding.smscode.setText("等待" + millisUntilFinished / 1000 + "秒");
            }

            public void onFinish() {
                mBinding.smscode.setTextColor(RealNameAuthActivity.this.getResources().getColor(R.color.whole_style_color));
                mBinding.smscode.setText("重新获取");
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

    /**
     * 提交实名认证
     */
    public void onSubmitClick(View view) {
        RelaNameViewModel model = mBinding.getModel();
        String name = model.getName();
        String id = model.getNum();
        String code = model.getCode();
        if(TextUtils.isEmpty(name)) {
            showToast("请输入真实姓名");
            return;
        }
        if(TextUtils.isEmpty(id)) {
            showToast("请输入身份证号");
            return;
        }
        if(TextUtils.isEmpty(code)) {
            showToast("请输入短信验证码");
            return;
        }
        Subscription s = mBiz.realNameSubmit(name, id, code)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onSubmitRealNameComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onSubmitRealNameComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            EventBus.getDefault().post(new Event.AuthRealNameInvestorCompleteEvent());
            finish();
        } else {
            showToast(apiResult.message);
        }
    }
}
