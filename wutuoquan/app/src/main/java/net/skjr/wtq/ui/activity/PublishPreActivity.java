package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.skjr.wtq.R;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.utils.CommonUtils;
import net.skjr.wtq.ui.activity.system.LoginActivity;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/29 16:38
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class PublishPreActivity extends BaseToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_pre);
        initToolbar("发布项目");
    }

    public void startNewProject(View view) {
        if(getMyApp().isNeedLogin()) {
            CommonUtils.showDialog(this, "亲，您还没有登录噢~", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PublishPreActivity.this, LoginActivity.class);
                    intent.putExtra("page", Consts.PUBLISHPROJECT);
                    startActivity(intent);
                    finish();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            },"暂不登录","现在登录");
        } else {
            startActivity(PublishProject1Activity.class);
            finish();
        }
    }
}
