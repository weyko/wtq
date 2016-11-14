package chat.contact.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.BaseActivity;
import chat.common.util.CommonUtil;
import chat.common.util.ShareUtils;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.ContactBean;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Description: 邀请好友
 * Created  by: weyko on 2016/5/30.
 */
public class ContactsInvateActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 短信请求码
     */
    private static final int REQUEST_SMS = 100;
    private ImageView back;
    private TextView titleText;
    private TextView tv_weixin_invate_contacts, tv_qq_invate_contacts, tv_sms_invate_contacts;
    /**
     * 分享工具类
     */
    private ShareUtils shareUtils;
    /**
     * 分享标题
     */
    private String shareTitle;
    /**
     * 分享内容
     */
    private String shareContent;
    /**
     * 分享链接
     */
    private String shareUrl;
    /**
     * 分享图标
     */
    private Bitmap shareIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invate_contacts);
    }

    @Override
    protected void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        titleText = (TextView) this.findViewById(R.id.titleText);
        tv_weixin_invate_contacts = (TextView) this.findViewById(R.id.tv_weixin_invate_contacts);
        tv_qq_invate_contacts = (TextView) this.findViewById(R.id.tv_qq_invate_contacts);
        tv_sms_invate_contacts = (TextView) this.findViewById(R.id.tv_sms_invate_contacts);
    }

    @Override
    protected void initEvents() {
        back.setOnClickListener(this);
        tv_weixin_invate_contacts.setOnClickListener(this);
        tv_qq_invate_contacts.setOnClickListener(this);
        tv_sms_invate_contacts.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        titleText.setText(R.string.invate_contacts_title);
        shareIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add);
        shareUtils = new ShareUtils(this);
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            this.finish();
        } else if (v == tv_weixin_invate_contacts) {//微信邀请
            /**分享邀请码的内容*/
            if (CommonUtil.checkAppIsInstall(this, "com.tencent.mm")) {
                shareUtils.shareWeChatFriends(Wechat.NAME, shareTitle, shareContent,
                        shareIcon,
                        shareUrl);
            } else {
                ShowUtil.showToast(this, getString(R.string.ssdk_wechat_client_inavailable));
            }
        } else if (v == tv_qq_invate_contacts) {//QQ邀请
            /**分享邀请码的内容*/
            if (CommonUtil.checkAppIsInstall(this, "com.tencent.mobileqq")) {
                shareUtils.shareQqOrZone(QQ.NAME, shareTitle, shareContent, shareIcon, shareUrl);
            } else {
                ShowUtil.showToast(this, getString(R.string.ssdk_qq_client_inavailable));
            }
        } else if (v == tv_sms_invate_contacts) {//短信邀请
            Intent intent = new Intent(this, ContactsActivity.class);
            intent.putExtra(ContactsActivity.ISTRANSFER, true);
            startActivityForResult(intent, REQUEST_SMS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                ContactBean fansBean = (ContactBean) data.getSerializableExtra(ContactsActivity.FANSBEAN);
                if (fansBean != null) {
                    shareUtils.shareSMS(fansBean.getUserImg(), shareContent);
                }
            }
        }
    }
}
