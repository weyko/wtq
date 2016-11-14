package net.skjr.wtq.ui.activity;

import android.os.Bundle;
import android.widget.ListView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.TabHomeBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.GeniusAccount;
import net.skjr.wtq.ui.adapter.GeniusAdapter;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/19 9:26
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class InvestGeniusActivity extends BaseToolbarActivity {

    private ListView mGenius_list;
    private TabHomeBiz mBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_genius);
        initToolbar("管理牛人");
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        Subscription s = mBiz.getGeniusList(1,10)
                .subscribe(new Action1<APIResult<GeniusAccount>>() {
                    @Override
                    public void call(APIResult<GeniusAccount> apiResult) {
                        onGetGeniusComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onGetGeniusComplete(APIResult<GeniusAccount> apiResult) {
        if(apiResult.isSuccess) {
            GeniusAccount result = apiResult.result;
            List<GeniusAccount.ListEntity> list = result.list;
            GeniusAdapter adapter = new GeniusAdapter(this, list);
            mGenius_list.setAdapter(adapter);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initVariables() {
        mBiz = new TabHomeBiz();
    }

    private void initView() {
        mGenius_list = (ListView) findViewById(R.id.genius_list);
    }
}
