package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.business.RegisterBiz;
import net.skjr.wtq.databinding.ActivityRegist2Binding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.Register1ViewModel;
import net.skjr.wtq.viewmodel.Register2ViewModel;

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
public class Regist2Activity extends BaseToolbarActivity {

    private RegisterBiz mBiz;
    private ActivityRegist2Binding mBinding;
    private Register2ViewModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("注册");
        initVariables();
        initData();
    }
    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_regist2);
        Register2ViewModel model = new Register2ViewModel();
        mBinding.setModel(model);
    }

    private void initData() {
        Intent intent = getIntent();
        Register1ViewModel register = (Register1ViewModel) intent.getSerializableExtra("register");
        mModel = mBinding.getModel();
        mModel.setPhone(register.getPhone());
        mModel.setPhoneCode(register.getSmsverify());
    }

    private void initVariables() {
        mBiz = new RegisterBiz();
    }

    private void regist() {
        Subscription s = mBiz.register(mModel)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        Log.e("huang",mModel.getPhone()+mModel.getPhoneCode()+mModel.getPassword()+mModel.getConfirmPassword());
                        onRegistComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onRegistComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            getMyApp().isNewStart = false;
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","注册成功");
            intent.putExtra("toolbartext","注册");
            startActivity(intent);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }

    public void submitRegist(View view) {
        CheckResult check = mModel.check();
        if(check.isSuccess) {
            regist();
        } else {
            showToast(check.errorMessage);
        }
    }

}
