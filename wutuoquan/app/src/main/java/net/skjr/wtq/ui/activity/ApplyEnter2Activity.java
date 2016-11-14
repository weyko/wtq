package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.databinding.ActivityApplyEnter2Binding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.system.SuccessActivity;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.SaveApply2ViewModel;
import net.skjr.wtq.viewmodel.SaveApplyViewModel;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 14:57
 * 描述	      申请入驻，填写联系人及电话
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ApplyEnter2Activity extends BaseToolbarActivity {

    private AccountBiz mBiz;
    private ActivityApplyEnter2Binding mBinding;
    private SaveApplyViewModel mExtra;
    private CheckBox mEnter_read_protocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initBinding();
        initVariables();
        initToolbar("申请入驻");
        initView();
    }

    private void initView() {
        mEnter_read_protocol = (CheckBox) findViewById(R.id.enter_read_protocol);
        mEnter_read_protocol.setChecked(true);
    }

    private void initData() {
        Intent intent = getIntent();
        mExtra = (SaveApplyViewModel) intent.getSerializableExtra("applyinfo");
    }

    private void initVariables() {
        mBiz = new AccountBiz();
    }

    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_enter2);
        SaveApply2ViewModel model = new SaveApply2ViewModel();
        mBinding.setModel(model);
    }

    /**
     * 申请入驻
     * @param view
     */
    public void saveApply(View view) {
        SaveApply2ViewModel model = mBinding.getModel();
        CheckResult check = model.check();
        if(check.isSuccess) {
            if(!mEnter_read_protocol.isChecked()) {
                showToast("请先阅读并同意协议");
                return;
            }
            mExtra.setUser(model.getUser());
            mExtra.setPhone(model.getPhone());
            applyToServer(mExtra);
        } else {
            showToast(check.errorMessage);
        }
    }

    /**
     * 提交到服务器
     */
    private void applyToServer(SaveApplyViewModel model) {
        Subscription s = mBiz.saveApply(model)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onApplyComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onApplyComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","提交成功");
            intent.putExtra("toolbartext","申请入驻");
            startActivity(intent);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }
}
