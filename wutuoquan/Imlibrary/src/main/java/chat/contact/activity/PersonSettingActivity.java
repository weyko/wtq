package chat.contact.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.BaseActivity;
import chat.common.config.Constant;
import chat.contact.bean.ContactBean;
import chat.dialog.CustomBaseDialog;
import chat.view.SettingItemView;

/**
 * Description: 联系人设置
 * Created  by: weyko on 2016/6/20.
 */
public class PersonSettingActivity extends BaseActivity implements SettingItemView.OnSwitchListener {
    private ImageView back, rightImg;
    private TextView titleText;
    private ContactBean fansBean;
    private SettingItemView sv_remark_person_setting, sv_star_person_setting, sv_limit_person_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntentParams();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_setting);
    }

    private void getIntentParams() {
        fansBean = (ContactBean) getIntent().getSerializableExtra(Constant.FRIEND_INFO);
    }

    @Override
    protected void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        rightImg = (ImageView) this.findViewById(R.id.rightImg);
        titleText = (TextView) this.findViewById(R.id.titleText);
        sv_remark_person_setting = (SettingItemView) this.findViewById(R.id.sv_remark_person_setting);
        sv_star_person_setting = (SettingItemView) this.findViewById(R.id.sv_star_person_setting);
        sv_limit_person_setting = (SettingItemView) this.findViewById(R.id.sv_limit_person_setting);
    }

    @Override
    protected void initEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constant.FRIEND_INFO, fansBean);
                setResult(RESULT_OK);
                PersonSettingActivity.this.finish();
            }
        });
        sv_star_person_setting.setOnSwitchListener(this);
    }

    @Override
    protected void initData() {
        titleText.setText(R.string.person_setting_title);
        if (fansBean == null)
            return;
        sv_remark_person_setting.setRight(fansBean.getShowName());
//        sv_star_person_setting.setChecked(fansBean.getStar() == 1 ? true : false);
//        sv_limit_person_setting.setChecked(fansBean.getLimit() == 1 ? true : false);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.sv_remark_person_setting) {

        } else if (view.getId() == R.id.tv_delete_person_setting) {
            deleteFriend();
        }
    }

    /**
     * 删除联系人
     */
    public void deleteFriend() {
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(PersonSettingActivity.this, null,
                getString(R.string.person_setting_delete_hint),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, this.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();

    }

    @Override
    public void onSwitchChanger(View view, boolean isOpen) {

    }
}
