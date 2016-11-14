package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.application.MyPreference;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.utils.CommenUtils;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/17 9:50
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ForgetTradePwd1Activity extends BaseToolbarActivity {

    private EditText mFind_trade_pwd_code;
    private AccountBiz mBiz;

    boolean isCounting;
    boolean isProcessing;
    CountDownTimer countDownTimer;
    private String mCode;
    private TextView mFind_trade_pwd_phone;
    private TextView mSmscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_trade1);
        initToolbar("忘记交易密码");
        initVariables();
        initView();
    }

    private void initView() {
        mFind_trade_pwd_code = (EditText) findViewById(R.id.find_trade_pwd_code);
        mFind_trade_pwd_phone = (TextView) findViewById(R.id.find_trade_pwd_phone);
        mSmscode = (TextView) findViewById(R.id.smscode);

        mFind_trade_pwd_phone.setText(CommenUtils.getSecurityPhone(MyPreference.getPhone()));
    }

    private void initVariables() {
        mBiz = new AccountBiz();
    }

    /**
     * 获取验证码
     * @param view
     */
    public void onSmsCaptchaClick(View view) {
        mFind_trade_pwd_code.setText("");

        if (isProcessing)
            return;

        if (isCounting)
            return;
        isProcessing = true;
        isCounting = true;

        Subscription s = mBiz.forgetTradeSmsCode()
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
                mSmscode.setTextColor(ForgetTradePwd1Activity.this.getResources().getColor(R.color.whole_style_color));
                mSmscode.setText("等待" + millisUntilFinished / 1000 + "秒");
            }

            public void onFinish() {
                mSmscode.setTextColor(ForgetTradePwd1Activity.this.getResources().getColor(R.color.whole_style_color));
                mSmscode.setText("重新获取");
                isCounting = false;
            }
        }.start();
    }

    private void onSendSmsCodeComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            showToast("验证码已发送");
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 点击下一步
     * @param view
     */
    public void onNextClick(View view) {
        mCode = mFind_trade_pwd_code.getText().toString().trim();
        if(TextUtils.isEmpty(mCode)) {
            showToast("请输入验证码");
        } else {
            sendSmsCode();
        }
    }

    private void sendSmsCode() {
        Subscription s = mBiz.forgetTradeConfirmCode(mCode)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onConfirmSmsCodeComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onConfirmSmsCodeComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, ForgetTradePwd2Activity.class);
            intent.putExtra("smscode",mCode);
            startActivity(intent);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }
}
