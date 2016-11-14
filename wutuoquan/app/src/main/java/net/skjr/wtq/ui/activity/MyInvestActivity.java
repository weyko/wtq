package net.skjr.wtq.ui.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.TabHomeBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.MyInvestAccount;
import net.skjr.wtq.ui.adapter.MyInvestListAdapter;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/27 19:21
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyInvestActivity extends BaseToolbarActivity {

    private ListView mMy_invest_list;
    private TabHomeBiz mBiz;
    private TextView mMyinvest_income;
    private TextView mMyinvest_project;
    private TextView mMyinvest_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinvest);
        initToolbar("我投资的");
        initVariables();
        initData();
        initView();

    }

    private void initVariables() {
        mBiz = new TabHomeBiz();
    }

    private void initData() {
        showProgressDialog();
        Subscription s = mBiz.getMyInvestAccount(1,10)
                .subscribe(new Action1<APIResult<MyInvestAccount>>() {
                    @Override
                    public void call(APIResult<MyInvestAccount> accountInfoAPIResult) {
                        onGetMyInvestAccountComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetMyInvestAccountComplete(APIResult<MyInvestAccount> apiResult) {
        if(apiResult.isSuccess) {
            MyInvestAccount result = apiResult.result;
            mMyinvest_income.setText(result.incomeSum+"元");
            mMyinvest_project.setText(result.tenderCount+"个");
            mMyinvest_total.setText(result.tenderMoneySum+"元");
            List<MyInvestAccount.ListEntity> list = result.list;
            MyInvestListAdapter adapter = new MyInvestListAdapter(this, list);
            mMy_invest_list.setAdapter(adapter);
            dismissProgressDialog();
            if(list.size() == 0) {
                showToast("您还没有投资任何项目");
            }
        } else {
            showToast(apiResult.message);
            dismissProgressDialog();
        }
    }

    private void initView() {
        mMy_invest_list = (ListView) findViewById(R.id.my_invest_list);
        mMyinvest_income = (TextView) findViewById(R.id.myinvest_income);
        mMyinvest_project = (TextView) findViewById(R.id.myinvest_project);
        mMyinvest_total = (TextView) findViewById(R.id.myinvest_total);
    }
}
