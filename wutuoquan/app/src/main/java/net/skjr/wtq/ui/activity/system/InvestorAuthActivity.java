package net.skjr.wtq.ui.activity.system;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.SystemBiz;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.CheckUserAccount;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/9 18:02
 * 描述	      认证投资人
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class InvestorAuthActivity extends BaseToolbarActivity implements View.OnClickListener {

    private SystemBiz mBiz;
    private TextView mInvestor_realname;
    private RadioGroup mInvestor_condition;
    private RadioButton mCondition1;
    private RadioButton mCondition2;
    private RadioButton mCondition3;
    private EditText mInvestor_company;
    private EditText mInvestor_contact;
    private CheckBox mInvestor_isread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investor_auth);
        EventBus.getDefault().register(this);
        initToolbar("认证投资人");
        initVariables();
        initView();
        loadView();
    }

    private void initView() {
        mInvestor_realname = (TextView) findViewById(R.id.investor_realname);
        mInvestor_condition = (RadioGroup) findViewById(R.id.investor_condition);
        mCondition1 = (RadioButton) findViewById(R.id.condition1);
        mCondition2 = (RadioButton) findViewById(R.id.condition2);
        mCondition3 = (RadioButton) findViewById(R.id.condition3);
        mInvestor_company = (EditText) findViewById(R.id.investor_company);
        mInvestor_contact = (EditText) findViewById(R.id.investor_contact);
        mInvestor_isread = (CheckBox) findViewById(R.id.investor_isread);

        mInvestor_isread.setChecked(true);
        mCondition1.setChecked(true);
    }

    private void loadView() {
        /*if(!mRealName) {
            mInvestor_realname.setText("未实名");
            mInvestor_realname.setOnClickListener(this);
        } else {
            mInvestor_realname.setText("已实名");
        }*/
        isRealName();
    }

    private void initVariables() {
        mBiz = new SystemBiz();
    }


    /**
     * 点击下一步
     */
    public void authInvestor(View view) {
        if(!check()) {
            return;
        }
        String condition = "";
        switch (mInvestor_condition.getCheckedRadioButtonId()) {
            case R.id.condition1:
                condition = mCondition1.getText().toString();
                break;
            case R.id.condition2:
                condition = mCondition2.getText().toString();
                break;
            case R.id.condition3:
                condition = mCondition3.getText().toString();
                break;
        }
        String company = mInvestor_company.getText().toString().trim();
        String contact = mInvestor_contact.getText().toString().trim();
        Log.e("condition",condition);
        Subscription s = mBiz.authInvestor(condition, company, contact)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> accountInfoAPIResult) {
                        onAuthInvestorComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onAuthInvestorComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            startActivity(AuthInvestorSuccessActivity.class);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }

    private boolean check() {
        if(TextUtils.equals(mInvestor_realname.getText(),"未实名")) {
            showToast("请先实名认证");
            return false;
        }
        if(!mInvestor_isread.isChecked()) {
            showToast("请阅读并同意相关协议");
            return false;
        }
        return true;
    }

    /**
     * 是否实名
     * @return
     */
     private boolean mRealName = false;
    private void isRealName() {
        SystemBiz biz = new SystemBiz();
        Subscription s = biz.isRealName()
                .subscribe(new Action1<APIResult<CheckUserAccount>>() {
                    @Override
                    public void call(APIResult<CheckUserAccount> apiResult) {
                        if(apiResult.isSuccess) {
                            CheckUserAccount result = apiResult.result;
                            if (result.authStatus == 1) {
                                mRealName = true;
                            }
                            if(!mRealName) {
                                mInvestor_realname.setText("未实名");
                                mInvestor_realname.setOnClickListener(InvestorAuthActivity.this);
                            } else {
                                mInvestor_realname.setText("已实名");
                            }
                        } else {
                            showToast(apiResult.message);
                        }
                    }

                });

        addSubscription(s);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.investor_realname:
                startActivity(RealNameAuthActivity.class);
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 实名认证完成
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthRealNameInvestorComplete(Event.AuthRealNameInvestorCompleteEvent event) {
        isRealName();
    }
}
