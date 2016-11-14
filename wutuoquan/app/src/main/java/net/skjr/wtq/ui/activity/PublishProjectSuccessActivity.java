package net.skjr.wtq.ui.activity;

import android.os.Bundle;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.ui.activity.system.MainActivity;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/9 16:55
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class PublishProjectSuccessActivity extends BaseToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_success);
        initToolbar("发布项目");
    }

    /**
     * 返回首页
     * @param view
     */
    public void returnIndex(View view) {
        finish();
    }

    /**
     * 查看进度
     */
    public void gotoMyPublish(View view) {
        startActivity(MyPublishedActivity.class);
        finish();
    }
}
