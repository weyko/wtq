package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.TabHomeBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.MyPublishAccount;
import net.skjr.wtq.ui.adapter.MyPublishedListAdapter;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/27 19:15
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyPublishedActivity extends BaseToolbarActivity {

    private ListView mMypublish_list;
    private TabHomeBiz mBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypublished);
        initToolbar("我发起的");
        initVariables();
        initData();
        initView();
    }
    private void initVariables() {
        mBiz = new TabHomeBiz();
    }

    private void initData() {
        showProgressDialog();
        Subscription s = mBiz.getMyPublishAccount(1,10)
                .subscribe(new Action1<APIResult<MyPublishAccount>>() {
                    @Override
                    public void call(APIResult<MyPublishAccount> accountInfoAPIResult) {
                        onGetMyPublishAccountComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetMyPublishAccountComplete(APIResult<MyPublishAccount> apiResult) {

        if(apiResult.isSuccess) {
            MyPublishAccount result = apiResult.result;
            final List<MyPublishAccount.ListEntity> list = result.list;
            MyPublishedListAdapter adapter = new MyPublishedListAdapter(this, list);
            mMypublish_list.setAdapter(adapter);
            dismissProgressDialog();
            mMypublish_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(list.get(i).stockStatus == Consts.SHENHE) {
                        showToast("项目还在审核中");
                        return;
                    }
                    Intent intent = new Intent(MyPublishedActivity.this, ProjectDetailsActivity.class);
                    intent.putExtra("pid",list.get(i).stockNO);
                    startActivity(intent);
                }
            });
        } else {
            showToast(apiResult.message);
            dismissProgressDialog();
        }
    }

    private void initView() {
        mMypublish_list = (ListView) findViewById(R.id.mypublish_list);

    }
}
