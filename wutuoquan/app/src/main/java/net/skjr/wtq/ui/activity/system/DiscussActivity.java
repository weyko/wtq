package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.DiscussBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/23 9:45
 * 描述	      发表评论
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DiscussActivity extends BaseToolbarActivity {

    private EditText mDiscuss_content;
    private DiscussBiz mBiz;
    private TextView mSubmit;
    private int mStockid;
    private String mTrim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);
        initToolbar("发表评论");
        initVariables();
        initView();
        initData();
    }

    private void initVariables() {
        mBiz = new DiscussBiz();
    }

    private void initData() {
        Intent intent = getIntent();
        mStockid = intent.getIntExtra("stockid", 0);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrim = mDiscuss_content.getText().toString().trim();
                if(TextUtils.isEmpty(mTrim)) {
                    showToast("请输入评论");
                    return;
                }
                Subscription s = mBiz.publishDiscuss(mStockid, mTrim)
                        .subscribe(new Action1<APIResult<Object>>() {
                            @Override
                            public void call(APIResult<Object> apiResult) {
                                onPublishContentComplete(apiResult);
                            }
                        });
                addSubscription(s);
            }
        });


    }

    private void onPublishContentComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {
            showToast("发表成功");
            finish();
        } else {
            showToast(apiResult.message);
        }
    }

    private void initView() {
        mDiscuss_content = (EditText) findViewById(R.id.discuss_content);
        mSubmit = (TextView) findViewById(R.id.submit);


    }
}
