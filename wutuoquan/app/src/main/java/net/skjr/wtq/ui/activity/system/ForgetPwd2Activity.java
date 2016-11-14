package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.business.LoginBiz;
import net.skjr.wtq.databinding.ActivityForgetPwd2Binding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.FindPwdViewModel;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/27 11:46
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ForgetPwd2Activity extends BaseToolbarActivity {

    private ActivityForgetPwd2Binding mBinding;
    private FindPwdViewModel mModel;
    private LoginBiz mBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("忘记密码");
        initVariables();
        initData();
    }
    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forget_pwd2);
        FindPwdViewModel model = new FindPwdViewModel();
        mBinding.setModel(model);
    }

    private void initData() {
        Intent intent = getIntent();
        FindPwdViewModel register = (FindPwdViewModel) intent.getSerializableExtra("findpwd");
        mModel = mBinding.getModel();
        mModel.setPhone(register.getPhone());
        mModel.setPhoneCode(register.getPhoneCode());
    }

    private void initVariables() {
        mBiz = new LoginBiz();
    }


    /**
     * 点击下一步，提交密码
     * @param view
     */
    public void findPwd2(View view) {
        CheckResult check = mModel.check();
        if(check.isSuccess) {
            Subscription s = mBiz.resetPwd(mModel)
                    .subscribe(new Action1<APIResult<Object>>() {
                        @Override
                        public void call(APIResult<Object> apiResult) {
                            onRegistComplete(apiResult);
                        }
                    });
            addSubscription(s);
        } else {
            showToast(check.errorMessage);
        }
    }
    private void onRegistComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","修改成功");
            intent.putExtra("toolbartext","忘记密码");
            startActivity(intent);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }
}
