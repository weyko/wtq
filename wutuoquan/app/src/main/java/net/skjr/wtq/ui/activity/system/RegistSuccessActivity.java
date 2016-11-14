package net.skjr.wtq.ui.activity.system;

import android.os.Bundle;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.ui.activity.BaseActivity;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/20 14:12
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class RegistSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_success);
    }

    public void registSuccess(View view) {
        finish();
    }
}
