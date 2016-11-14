package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.business.PublishProBiz;
import net.skjr.wtq.databinding.ActivityPublishProject2Binding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.PublishProject1ViewModel;
import net.skjr.wtq.viewmodel.PublishProject2ViewModel;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/9 14:18
 * 描述	      发布项目第二步，填写联系人以及联系方式
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class PublishProject2Activity extends BaseToolbarActivity {

    private ActivityPublishProject2Binding mBinding;
    private PublishProject1ViewModel mModel1;
    private PublishProBiz mBiz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("发布项目");
        initData();
        initVariables();
    }

    private void initVariables() {
        mBiz = new PublishProBiz();
    }

    private void initData() {
        Intent intent = getIntent();
        mModel1 = (PublishProject1ViewModel) intent.getSerializableExtra("project");
    }


    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish_project2);
        PublishProject2ViewModel model = new PublishProject2ViewModel();
        mBinding.setModel(model);
    }
    public void submitProject(View view) {
        PublishProject2ViewModel model = mBinding.getModel();
        CheckResult check = model.check();
        if(check.isSuccess) {
            mModel1.setLinkman(model.getLinkman());
            mModel1.setPhone(model.getPhone());

            //提交项目
            addProject();
        } else {
            showToast(check.errorMessage);
        }
    }

    private void addProject() {
        Subscription s = mBiz.submitProject(mModel1)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> accountInfoAPIResult) {
                        onSubmitProjectComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onSubmitProjectComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            startActivity(PublishProjectSuccessActivity.class);
            finish();
        } else {
            showToast(apiResult.message);
        }
    }

}
