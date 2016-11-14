package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.TabHomeBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.MyCollectListAccount;
import net.skjr.wtq.ui.adapter.MyCollectListAdapter;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/27 19:02
 * 描述	      我收藏的
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyCollectedActivity extends BaseToolbarActivity  {

    private ListView mMycollect_list;
    private TabHomeBiz mBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollected);
        initToolbar("我收藏的");
        initVariables();
        initView();
        initData();
    }

    private void initVariables() {
        mBiz = new TabHomeBiz();
    }

    private void initData() {
        showProgressDialog();
        Subscription s = mBiz.getMyCollectAccount(1,10)
                .subscribe(new Action1<APIResult<MyCollectListAccount>>() {
                    @Override
                    public void call(APIResult<MyCollectListAccount> accountInfoAPIResult) {
                        onGetMyCollectAccountComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetMyCollectAccountComplete(APIResult<MyCollectListAccount> apiResult) {
        if(apiResult.isSuccess) {
            final List<MyCollectListAccount.ListEntity> list = apiResult.result.list;
            MyCollectListAdapter adapter = new MyCollectListAdapter(this, list);
            mMycollect_list.setAdapter(adapter);
            dismissProgressDialog();
            mMycollect_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MyCollectedActivity.this, ProjectDetailsActivity.class);
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
        mMycollect_list = (ListView) findViewById(R.id.mycollect_list);

    }

}
