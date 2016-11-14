package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.LoginBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.databinding.ActivityLoginBinding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.system.LoginResultObj;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.LoginViewModel;

import org.greenrobot.eventbus.EventBus;

import chat.base.IMClient;
import chat.login.LoginEntity;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/20 11:44
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LoginActivity extends BaseToolbarActivity implements View.OnClickListener {

    private Button mRegist;
    private Button mLogin;
    private ImageView mLogin_verify;
    private ActivityLoginBinding mBinding;
    private LoginBiz mBiz;
    private TextView mForgot_password;
    private String mLastPage = "00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("登录");
        initVariables();
        initData();
        initView();
    }

    /**
     * 接受上个页面传来的参数，判断登录后页面的去向
     */
    private void initData() {
        Intent intent = getIntent();
        mLastPage = intent.getStringExtra("page");
        if(TextUtils.isEmpty(mLastPage)) {
            mLastPage = "null";
        }
    }

    private void initVariables() {
        mBiz = new LoginBiz();
    }

    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginViewModel model = new LoginViewModel();
        mBinding.setModel(model);
    }

    private void initView() {
        mRegist = (Button) findViewById(R.id.regist);
        mLogin = (Button) findViewById(R.id.login);
        mLogin_verify = (ImageView) findViewById(R.id.login_verify);
        mForgot_password = (TextView) findViewById(R.id.forgot_password);
        mRegist.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mForgot_password.setOnClickListener(this);
        mLogin_verify.setOnClickListener(this);
        displayCaptcha();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist:
                startActivity(Regist1Activity.class);
                break;
            case R.id.login_verify:
                displayCaptcha();
                break;
            case R.id.login:
                userLogin();
                break;
            case R.id.forgot_password:
                startActivity(ForgetPwd1Activity.class);
                break;
        }
    }

    /**
     * 初始化聊天用户信息
     * @param model
     */
    private void initImUserInfo(LoginViewModel model) {
        LoginEntity loginEntity=new LoginEntity();
        LoginEntity.SSOLoginDataBean data=new LoginEntity.SSOLoginDataBean();
        String userId=model.getPhone();
        if("1".equals(userId)){
            userId="27901";
        }else  if("2".equals(userId)){
            userId="27902";
        }else  if("3".equals(userId)){
            userId="27903";
        }else  if("4".equals(userId)){
            userId="27904";
        }
        data.setToken(userId);
        data.setUserId(userId);
        loginEntity.setData(data);
        loginEntity.setUserName(userId);
        IMClient.getInstance().saveSSOLoginInfo(loginEntity);
    }

    private void userLogin() {
        LoginViewModel model = mBinding.getModel();
        CheckResult check = model.check();
        initImUserInfo(model);//这里测试用，正式环境请移至登录成功之后操作
        if(check.isSuccess) {
            login(model.getPhone(), model.getPassword(), model.getImgCode());
        } else {
            showToast(check.errorMessage);
        }
    }

    /**
     * 登录验证码
     */
    private void displayCaptcha() {
        String deviceType = String.valueOf(getMyApp().getDeviceType());
        String deviceId = getMyApp().getDeviceId();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = Url.IMGVARIFY + "?type=login&device=" + deviceType + "&deviceID=" + deviceId + "&a=" + timestamp;

        ImageLoaderUtils.displayImage(this, url, mLogin_verify);
    }
    /**
     * 登录
     */
    private void login(String phone, String pwd, String code) {
        Subscription s = mBiz.login(phone, pwd, code)
                .subscribe(new Action1<APIResult<LoginResultObj>>() {
                    @Override
                    public void call(APIResult<LoginResultObj> apiResult) {
                        onLoginComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onLoginComplete(APIResult<LoginResultObj> apiResult) {
        if(apiResult.isSuccess) {
            LoginResultObj result = apiResult.result;
            //首先清除旧的登录信息
            getMyApp().clearLoginInfo();
            //保存新的登录信息
            getMyApp().saveLoginInfo(result.phone, result.token);
            getMyApp().isNewStart = false;

            //根据上级页面的不同，进行不同操作
            switch (mLastPage) {
                case Consts.DETAIL:
                    EventBus.getDefault().post(new Event.LoginCompleteEvent());
                    finish();
                    break;
                case Consts.PUBLISHPROJECT:
                    finish();
                    break;
                case Consts.TABMINE:
                    EventBus.getDefault().post(new Event.OpenMainTabEvent(3));
                    finish();
                    break;
                default:
                    finish();
                    break;
            }

        } else {
            displayCaptcha();
            mBinding.captcha1.setText("");
            showToast(apiResult.message);
        }
    }
}
