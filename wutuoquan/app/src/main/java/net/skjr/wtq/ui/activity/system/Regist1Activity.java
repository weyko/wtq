package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.RegisterBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.core.utils.ui.ToastUtils;
import net.skjr.wtq.databinding.ActivityRegist1Binding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.Register1ViewModel;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 创建者     huangbo
 * 创建时间   2016/9/20 14:12
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class Regist1Activity extends BaseToolbarActivity {

    private ActivityRegist1Binding binding;
    private RegisterBiz biz;
    CountDownTimer countDownTimer;
    boolean isCounting;
    boolean isProcessing;
    private ImageView mRegist_imgverify;
    private CheckBox mRegist_read_protocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("注册");
        initVariables();
        initView();
    }

    private void initView() {
        mRegist_imgverify = (ImageView) findViewById(R.id.regist_imgverify);
        mRegist_read_protocol = (CheckBox) findViewById(R.id.regist_read_protocol);
        mRegist_read_protocol.setChecked(true);
        displayCaptcha();
        mRegist_imgverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCaptcha();
            }
        });
    }
    private void initVariables() {
        biz = new RegisterBiz();
    }

    private void initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_regist1);
        Register1ViewModel model = new Register1ViewModel();
        binding.setModel(model);
    }

    private void displayCaptcha() {
        String deviceType = String.valueOf(getMyApp().getDeviceType());
        String deviceId = getMyApp().getDeviceId();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = Url.IMGVARIFY + "?type=register&device=" + deviceType + "&deviceID=" + deviceId + "&a=" + timestamp;

        ImageLoaderUtils.displayImage(this, url, mRegist_imgverify);
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

        Register1ViewModel model = binding.getModel();
        String phone = model.getPhone();
        String code = model.getVerifycode();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            ToastUtils.show(this, "请输入验证码");
            return;
        }

        isProcessing = true;
        isCounting = true;

        //如果有填入短信验证码,那么清除
        binding.registSmsCode.setText("");

        Subscription s = biz.sendRegSmsCode(phone, code)
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
                binding.smscode.setTextColor(Regist1Activity.this.getResources().getColor(R.color.whole_style_color));
                binding.smscode.setText("等待" + millisUntilFinished / 1000 + "秒");
            }

            public void onFinish() {
                binding.smscode.setTextColor(Regist1Activity.this.getResources().getColor(R.color.whole_style_color));
                binding.smscode.setText("重新获取");
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
     * 点击下一步
     * @param view
     */
    public void onRegist1Click(View view) {
        Register1ViewModel model = binding.getModel();
        CheckResult check = model.check();
        if(check.isSuccess) {
            if(!mRegist_read_protocol.isChecked()) {
                showToast("请阅读并同意服务协议");
                return;
            }
            checkSmsCode(model.getPhone(),model.getSmsverify());
        } else {
            showToast(check.errorMessage);
        }
    }

    /**
     * 校验短信验证码
     */
    private void checkSmsCode(String phone, String code) {
        Subscription s = biz.checkSmsVerify(phone, code)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onCheckSmsCodeComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onCheckSmsCodeComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, Regist2Activity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("register",binding.getModel());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }

}
