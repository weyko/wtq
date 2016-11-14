package chat.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.contact.bean.ContactBean;
import chat.image.DisplayImageConfig;
import chat.session.bean.ImUserBean;
import chat.session.util.ChatUtil;
import chat.view.SettingItemView;

/**
 * Description: 好友信息
 * Created  by: weyko on 2016/6/20.
 */
public class PersonCenterActivity extends BaseActivity {
    private int REQUEST_SETTING=100;
    private ImageView back,rightImg,iv_avater_person_center;
    private TextView titleText;
    private ContactBean fansBean;
    private SettingItemView sv_nickname_person_center,sv_sex_person_center,sv_address_person_center,sv_sign_person_center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntentParams();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);
    }

    private void getIntentParams() {
        fansBean= (ContactBean) getIntent().getSerializableExtra(Constant.FRIEND_INFO);
    }

    @Override
    protected void initView() {
        back= (ImageView) this.findViewById(R.id.back);
        rightImg= (ImageView) this.findViewById(R.id.rightImg);
        iv_avater_person_center= (ImageView) this.findViewById(R.id.iv_avater_person_center);
        titleText= (TextView) this.findViewById(R.id.titleText);
        sv_nickname_person_center= (SettingItemView) this.findViewById(R.id.sv_nickname_person_center);
        sv_sex_person_center= (SettingItemView) this.findViewById(R.id.sv_sex_person_center);
        sv_address_person_center= (SettingItemView) this.findViewById(R.id.sv_address_person_center);
        sv_sign_person_center= (SettingItemView) this.findViewById(R.id.sv_sign_person_center);
    }

    @Override
    protected void initEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonCenterActivity.this.finish();
            }
        });
        this.findViewById(R.id.right_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IMClient.getInstance().getContext(),PersonSettingActivity.class);
                intent.putExtra(Constant.FRIEND_INFO,fansBean);
                startActivityForResult(intent,REQUEST_SETTING);
            }
        });
    }

    @Override
    protected void initData() {
        titleText.setText(R.string.person_center_title);
        sv_nickname_person_center.setRight(fansBean.getShowName());
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setImageResource(R.drawable.ic_more);
        IMClient.sImageLoader.displayThumbnailImage(
                fansBean.getUserImg(), iv_avater_person_center,
                DisplayImageConfig.userLoginItemImageOptions,
                DisplayImageConfig.headThumbnailSize,
                DisplayImageConfig.headThumbnailSize);
    }
    public void onClick(View view){
        if(view.getId()==R.id.sv_card_person_center){

        }else  if(view.getId()==R.id.tv_msg_person_center){
           gotoChatRoom();
        }
    }

    /**
     * 跳转到聊天页面
     */
    private void gotoChatRoom() {
        if(fansBean==null)
            return;
        String toID = String.valueOf(fansBean.getFriendID());
        ImUserBean user =new ImUserBean();
        String nickName = fansBean.getRemarkName();
        user.setName(nickName);
        user.setAvatar(fansBean.getUserImg());
        user.setMxId(toID);
        ChatUtil.gotoChatRoom(this, user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

        }
    }
}
