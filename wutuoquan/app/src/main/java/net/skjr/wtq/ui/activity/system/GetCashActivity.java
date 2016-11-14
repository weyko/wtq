package net.skjr.wtq.ui.activity.system;

import android.os.Bundle;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AssetsBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/17 9:20
 * 描述	      提现
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GetCashActivity extends BaseToolbarActivity {

    private AssetsBiz mBiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcash);
        initToolbar("提现");
        initVariables();
        initData();
    }

    private void initData() {
        String phone = "";
        Subscription s = mBiz.getCashInfo(phone)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onGetCashInfoComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onGetCashInfoComplete(APIResult<Object> apiResult) {

    }

    private void initVariables() {
        mBiz = new AssetsBiz();
    }
}
