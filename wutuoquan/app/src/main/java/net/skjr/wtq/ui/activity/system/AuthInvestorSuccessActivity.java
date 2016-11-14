package net.skjr.wtq.ui.activity.system;

import android.os.Bundle;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/25 9:23
 * 描述	      认证投资人成功
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AuthInvestorSuccessActivity extends BaseToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_investor_success);
        initToolbar("认证投资人");
    }

    public void reBackIndex(View view) {
        finish();
    }
}
