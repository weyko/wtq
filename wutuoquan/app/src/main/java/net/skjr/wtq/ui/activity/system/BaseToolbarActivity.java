package net.skjr.wtq.ui.activity.system;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;


/**
 * 用于显示标准Toolbar的基类
 */
public class BaseToolbarActivity extends BaseActivity {

    private TextView txtTitle;

    protected void initToolbar(String title){
        ImageView imgBack = (ImageView)findViewById(R.id.toolbarBack);
        txtTitle = (TextView)findViewById(R.id.toolbarTitle);
        txtTitle.setText(title);

        //点击最左边的导航图标，退出当前Activity
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 设置Toolbar的标题
     * @param title
     */
    public void setToolbarTitle(String title){
        txtTitle.setText(title);
    }
}
