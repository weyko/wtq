package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.ui.widgets.ClearableEditText;

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
public class ForgetTradePwd2Activity extends BaseToolbarActivity {

    private ClearableEditText mTrade_newpwd;
    private ClearableEditText mTrade_confirm;
    private AccountBiz mBiz;
    private String mSmscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_trade2);
        initToolbar("忘记交易密码");
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        mSmscode = intent.getStringExtra("smscode");
    }

    private void initView() {
        mTrade_newpwd = (ClearableEditText) findViewById(R.id.trade_newpwd);
        mTrade_confirm = (ClearableEditText) findViewById(R.id.trade_confirm);
    }

    private void initVariables() {
        mBiz = new AccountBiz();
    }

    /**
     * 提交新密码
     * @param view
     */
    public void submitNewPwd(View view) {
        String newPwd = mTrade_newpwd.getText().toString().trim();
        String confirm = mTrade_confirm.getText().toString().trim();
        if(TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirm)) {
            showToast("请输入新密码及确认");
            return;
        }
        Subscription s = mBiz.forgetTradeSubmitCode(newPwd, confirm, mSmscode)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onSubmitSmsCodeComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onSubmitSmsCodeComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","找回成功");
            intent.putExtra("toolbartext","忘记交易密码");
            startActivity(intent);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }
}
