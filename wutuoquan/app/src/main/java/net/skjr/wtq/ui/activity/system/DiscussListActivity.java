package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.DiscussBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.DiscussListAccount;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.ui.adapter.DiscussListAdapter;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/20 16:41
 * 描述	      评论列表
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DiscussListActivity extends BaseToolbarActivity {

    private ListView mDiscuss_list;
    private DiscussBiz mBiz;
    private int mStockid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss_list);
        initToolbar("全部评论");
        initVariable();
        initView();
        initData();
    }

    private void initVariable() {
        mBiz = new DiscussBiz();
    }

    private void initData() {
        Intent intent = getIntent();
        mStockid = intent.getIntExtra("stockid",0);

        Subscription s = mBiz.getDiscussList(mStockid)
                .subscribe(new Action1<APIResult<DiscussListAccount>>() {
                    @Override
                    public void call(APIResult<DiscussListAccount> apiResult) {
                        onGetDiscussListComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onGetDiscussListComplete(APIResult<DiscussListAccount> apiResult) {
        if(apiResult.isSuccess) {
            DiscussListAccount result = apiResult.result;

            DiscussListAdapter adapter = new DiscussListAdapter(this, result.list);
            mDiscuss_list.setAdapter(adapter);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initView() {
        mDiscuss_list = (ListView) findViewById(R.id.discuss_list);
    }

    /**
     * 发表评论
     * @param view
     */
    public void applyDiscuss(View view) {
        Intent intent = new Intent(this, DiscussActivity.class);
        intent.putExtra("stockid", mStockid);
        startActivity(intent);
    }
}
