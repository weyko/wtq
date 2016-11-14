package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.LianlianPayResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/20 13:46
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class RechargeActivity extends BaseToolbarActivity implements CompoundButton.OnCheckedChangeListener {

    private TextView mRecharge_aval_money;
    private CheckBox mRecharge_pay_type1;
    private CheckBox mRecharge_pay_type2;
    private AccountBiz mBiz;

    private int mPayType = 1;
    private EditText mRecharge_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        initToolbar("充值");
        initVariables();
        initView();
        initData();
    }

    private void initVariables() {
        mBiz = new AccountBiz();
    }

    private void initData() {
        //获取可用资金
        Intent intent = getIntent();
        String avail_money = intent.getStringExtra("avail_money");
        mRecharge_aval_money.setText(avail_money);
    }

    private void initView() {
        mRecharge_aval_money = (TextView) findViewById(R.id.recharge_aval_money);
        mRecharge_pay_type1 = (CheckBox) findViewById(R.id.recharge_pay_type1);
        mRecharge_pay_type2 = (CheckBox) findViewById(R.id.recharge_pay_type2);
        mRecharge_num = (EditText) findViewById(R.id.recharge_num);
        mRecharge_pay_type1.setChecked(true);
        mRecharge_pay_type1.setOnCheckedChangeListener(this);
        mRecharge_pay_type2.setOnCheckedChangeListener(this);
    }


    /**
     * 确认充值
     */
    public void recharge(View view) {
        switch (mPayType) {
            case 1:
                weixinPay();
                break;
            case 2:
                lianLianPay();
                break;
            default:
                showToast("请选择充值方式");
                break;
        }
    }

    /**
     * 连连支付
     */
    private void lianLianPay() {
        String num = mRecharge_num.getText().toString().trim();
        if(TextUtils.isEmpty(num)) {
            showToast("请输入充值金额");
            return;
        }
        double count = Double.parseDouble(num);
        Subscription s = mBiz.recharge(count)
                .subscribe(new Action1<APIResult<LianlianPayResult>>() {
                    @Override
                    public void call(APIResult<LianlianPayResult> accountInfoAPIResult) {
                        onPayComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onPayComplete(APIResult<LianlianPayResult> apiResult) {
        if(apiResult.isSuccess) {
            finish();
            LianlianPayResult result = apiResult.result;
            WebActivity.open(this, "充值", result.paymenUrl+"?req_data="+result.reqData);
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 微信支付
     */
    private void weixinPay() {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.recharge_pay_type1:
                if(b) {
                    mRecharge_pay_type2.setChecked(false);
                    mPayType = 1;
                }
                break;
            case R.id.recharge_pay_type2:
                if(b) {
                    mRecharge_pay_type1.setChecked(false);
                    mPayType = 2;
                }
                break;

        }
    }
}
