package chat.contact.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.util.HashMap;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.util.network.HttpRequetUtil;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.ContactAddBean;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ContactInfoBean;
import chat.image.DisplayImageConfig;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * 搜索联系人详情
 */
public class SearchContactInfoActivity extends BaseActivity implements View.OnClickListener {
    private int friendID;
    private TextView titleText,tv_name_contact_search,tv_remark_contact_search,tv_address_contact_search,tv_sign_contact_search,tv_add_contact_search;
    private ImageView back,iv_avatar_contact_search,iv_sex_contact_search;
    private WBaseModel model;
    private WBaseModel modelAdd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getIntentParams();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact_info);
        getUserInfo();
    }

    private void getIntentParams() {
        friendID=getIntent().getIntExtra(Constant.SSO_USERID,-1);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    @Override
    protected void initView() {
        titleText = (TextView) this.findViewById(R.id.titleText);
        tv_name_contact_search = (TextView) this.findViewById(R.id.tv_name_contact_search);
        tv_remark_contact_search = (TextView) this.findViewById(R.id.tv_remark_contact_search);
        tv_address_contact_search = (TextView) this.findViewById(R.id.tv_address_contact_search);
        tv_sign_contact_search = (TextView) this.findViewById(R.id.tv_sign_contact_search);
        tv_add_contact_search = (TextView) this.findViewById(R.id.tv_add_contact_search);
        back = (ImageView) this.findViewById(R.id.back);
        iv_avatar_contact_search = (ImageView) this.findViewById(R.id.iv_avatar_contact_search);
        iv_sex_contact_search = (ImageView) this.findViewById(R.id.iv_sex_contact_search);
    }

    @Override
    protected void initEvents() {
        back.setOnClickListener(this);
        tv_add_contact_search.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        titleText.setText(R.string.search_contact_title);
    }
    private void getUserInfo() {
        showLoading();
        if (model == null) {
            model = new WBaseModel<ContactInfoBean>(getApplicationContext(),
                    ContactInfoBean.class);
        }
        HashMap<String,Object>map=new HashMap<String,Object>();
        map.put("code","chat-getUserInfo");
        map.put("userID",IMClient.getInstance().getSSOUserId());
        map.put("userID",friendID);
        model.httpRequest(Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        dismissLoadingDialog();
                        if (data != null && data instanceof ContactInfoBean) {
                            ContactInfoBean contactInfoBean = (ContactInfoBean) data;
                            if (contactInfoBean.isResult()) {
                                updateView(contactInfoBean);
                            } else {
                                ShowUtil.showToast(IMClient.getInstance().getContext(),contactInfoBean.getMsg());
                            }
                        } else if (data != null && data instanceof WBaseBean) {
                            WBaseBean info = (WBaseBean) data;
                            ShowUtil.showToast(IMClient.getInstance().getContext(),info.getMsg());
                        }
                    }
                });
    }
    private void addContact() {
        showLoading();
        if (modelAdd == null) {
            modelAdd = new WBaseModel<ContactAddBean>(getApplicationContext(),
                    ContactAddBean.class);
        }
        HashMap<String,Object>map=new HashMap<String,Object>();
        map.put("code","chat-addFriend");
        map.put("userID",IMClient.getInstance().getSSOUserId());
        map.put("userID",friendID);
        modelAdd.httpRequest(Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        dismissLoadingDialog();
                        boolean isAdded=false;
                        if (data != null && data instanceof ContactAddBean) {
                            ContactAddBean contactAddBean = (ContactAddBean) data;
                            if (contactAddBean.isResult()) {
                                isAdded=true;
                            }
                        }
                        ShowUtil.showToast(SearchContactInfoActivity.this,"添加"+(isAdded?"已发送":"失败"));
                    }
                });
    }

    private void updateView(ContactInfoBean contactInfoBean) {
        ContactBean data = contactInfoBean.getData();
        if(data==null)
            return;
        tv_name_contact_search.setText(data.getUserNickname());
          if(TextUtils.isEmpty(data.getRemarkName()))
            tv_remark_contact_search.setVisibility(View.GONE);
             else
           tv_remark_contact_search.setText(data.getRemarkName());
        tv_address_contact_search.setText(data.getCityName());
        tv_sign_contact_search.setText(data.getUserMood());
        IMClient.sImageLoader.displayThumbnailImage(
                data.getUserImg(), iv_avatar_contact_search,
                DisplayImageConfig.userLoginItemImageOptions,
                DisplayImageConfig.headThumbnailSize,
                DisplayImageConfig.headThumbnailSize);
    }
    @Override
    public void onClick(View v) {
        if (v == back)
            this.finish();
        else if (tv_add_contact_search == v) {
            addContact();
        }
    }
    @Override
    protected void onDestroy() {
        if(model!=null){
            model.cancelRequest();
        }
        if(modelAdd!=null){
            modelAdd.cancelRequest();
        }
    }
}