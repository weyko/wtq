package net.skjr.wtq.ui.activity.system;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.application.MyPreference;
import net.skjr.wtq.business.SettingBiz;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.common.utils.CommonUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.SettingInfoAccount;
import net.skjr.wtq.utils.CommenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 创建者     huangbo
 * 创建时间   2016/9/29 11:43
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SettingActivity extends BaseToolbarActivity {

    private SettingBiz mBiz;
    private TextView mSetting_realname;
    private TextView mSetting_phone;
    private View mSetting_auth_realname;
    private TextView mSetting_trade_pwd;
    private int mPayStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        EventBus.getDefault().register(this);
        initToolbar("设置");
        initView();
        initVariables();
        initData();
    }

    private void initView() {
        mSetting_realname = (TextView) findViewById(R.id.setting_realname);
        mSetting_phone = (TextView) findViewById(R.id.setting_phone);
        mSetting_auth_realname = findViewById(R.id.setting_auth_realname);
        mSetting_trade_pwd = (TextView) findViewById(R.id.setting_trade_pwd);

        mSetting_phone.setText(CommenUtils.getSecurityPhone(MyPreference.getPhone()));
    }

    private void initData() {
        showProgressDialog();
        Subscription s = mBiz.settingInfo()
                .subscribe(new Action1<APIResult<SettingInfoAccount>>() {
                    @Override
                    public void call(APIResult<SettingInfoAccount> apiResult) {
                        onGetSetInfoComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onGetSetInfoComplete(APIResult<SettingInfoAccount> apiResult) {
        if(apiResult.isSuccess) {
            SettingInfoAccount result = apiResult.result;
            SettingInfoAccount.InfoEntity info = result.info;
            if(info.realNameStatus == 1) {
                mSetting_realname.setText(info.realName);
                mSetting_auth_realname.setFocusable(false);
            } else {
                mSetting_realname.setText("去实名");
                mSetting_auth_realname.setFocusable(true);
            }

            mPayStatus = info.payStatus;

            dismissProgressDialog();

        } else {
            showToast(apiResult.message);
            dismissProgressDialog();
        }
    }

    private void initVariables() {
        mBiz = new SettingBiz();
    }

    /**
     * 实名认证
     */
    public void realNameAuth(View view) {
        startActivity(RealNameAuthActivity.class);
    }
    /**
     * 认证手机
     */
    public void phoneAuth(View view) {

    }
    /**
     * 修改登录密码
     */
    public void loginPwd(View view) {
        startActivity(LoginPwdActivity.class);
    }
    /**
     * 设置交易密码
     */
    public void tradePwd(View view) {
        if(mPayStatus == 1) {
            startActivity(ForgetTradePwd1Activity.class);
        } else {
            startActivity(SetTradePwdActivity.class);
        }
    }

    /**
     * 关于我们
     */
    public void aboutUs(View view) {
        WebActivity.open(this, "关于我们", Url.ABOUTUS);
    }
    /**
     * 帮助中心
     */
    public void helpCenter(View view) {
        WebActivity.open(this, "帮助中心", Url.HELPCENTER);
    }
    /**
     * 安全退出
     */
    public void loginOut(View view) {


        CommonUtils.showDialog(this, "是否退出当前账号？", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subscription s = mBiz.loginOut()
                        .subscribe(new Action1<APIResult<Object>>() {
                            @Override
                            public void call(APIResult<Object> apiResult) {
                                if(apiResult.isSuccess) {
                                    getMyApp().clearLoginInfo();
                                    finish();
                                    startActivity(LoginActivity.class);
                                    EventBus.getDefault().post(new Event.OpenMainTabEvent(0));
                                } else {
                                    showToast(apiResult.message);
                                }
                            }

                        });

                addSubscription(s);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }, "取消","确定");

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 刷新设置界面数据
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetTradePwdComplete(Event.SetTradePwdCompleteEvent event) {
        initData();
    }
}
