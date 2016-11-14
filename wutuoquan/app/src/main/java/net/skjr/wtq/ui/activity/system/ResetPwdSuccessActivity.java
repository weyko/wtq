package net.skjr.wtq.ui.activity.system;

import android.os.Bundle;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/27 11:46
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ResetPwdSuccessActivity extends BaseToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_success);
        initToolbar("忘记密码");
    }

    public void resetSuccess(View view) {
        finish();
    }
}
