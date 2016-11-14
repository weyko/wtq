package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/23 9:17
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ReplyDiscussActivity extends BaseToolbarActivity {

    private TextView mReply_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_discuss);
        initToolbar("回复讨论");
        initView();
        initData();
    }

    private void initView() {
        mReply_user = (TextView) findViewById(R.id.reply_user);
    }

    private void initData() {
        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        mReply_user.setText(user);
    }
}
