package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.skjr.wtq.R;
/**
 * 创建者     huangbo
 * 创建时间   2016/9/27 11:46
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SuccessActivity extends BaseToolbarActivity {

    private String mSuccesstext;
    private String mToolbartext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_success);
        initData();
        initView();
        initToolbar(mToolbartext);
    }

    private void initData() {
        Intent intent = getIntent();
        mSuccesstext = intent.getStringExtra("successtext");
        mToolbartext = intent.getStringExtra("toolbartext");
    }

    private void initView() {
        TextView success_text = (TextView) findViewById(R.id.success_text);
        success_text.setText(mSuccesstext);
    }

    public void resetSuccess(View view) {
        finish();
    }
}
