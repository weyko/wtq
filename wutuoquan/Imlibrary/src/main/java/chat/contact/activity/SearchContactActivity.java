package chat.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.util.input.EditTextUtils;
import chat.common.util.network.HttpRequetUtil;
import chat.contact.adapter.FansAdapter;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ContactsBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

public class SearchContactActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener, TextView.OnEditorActionListener {

    private TextView mCancel;
    private EditText mComment;
    private ImageView clear_chat_search;
    private ListView mListView;
    private ArrayList<ContactBean> mDatas;
    private FansAdapter mAdapter;
    private WBaseModel<ContactsBean> model;
    private Handler handler = new Handler();
    private final int delayMillis = 100;
    private RelativeLayout search_no_data;
    private TextView no_data_tip;
    private String serachText = "";
    private boolean fromForward = false;
    /**
     * 是否为红包
     */
    private boolean isTransfer = false;
    private TextView tilteText;
    private ImageView back;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fans);
        initEvents();
        initData();
    }
    @Override
    protected void initData() {
        tilteText.setText(R.string.search_title);
    }
    private void searchConatcts(String searchKey) {
        if (model == null) {
            model = new WBaseModel<ContactsBean>(getApplicationContext(),
                    ContactsBean.class);
        }
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("code","chat-getFriend");
        map.put("userID", IMClient.getInstance().getSSOUserId());
        map.put("userNickname", searchKey);
        model.httpRequest(Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof ContactsBean) {
                            ContactsBean friendList = (ContactsBean) data;
                            if (friendList.isResult()) {
                                if (friendList.getData() != null
                                        && friendList.getData().getList().size() > 0) {
                                    ArrayList<ContactBean> list = friendList
                                            .getData().getList();
                                    mDatas.clear();
                                    mDatas.addAll(list);
                                }
                            }
                        }
                        updateView();
                    }
                });
    }
    @Override
    protected void initView() {
        tilteText= (TextView) this.findViewById(R.id.titleText);
        back= (ImageView) this.findViewById(R.id.back);
        mComment = (EditText) findViewById(R.id.search_friend_comment);
        mCancel = (TextView) findViewById(R.id.search_friend_cancel);
        mListView = (ListView) findViewById(R.id.search_friend_listview);
        if (serachText != null && serachText.length() > 0) {
            mComment.setHint(serachText);
        } else {
            mComment.setHint(this.getString(R.string.search_contacts_hint));
        }
        search_no_data = (RelativeLayout) findViewById(R.id.search_no_data);
        no_data_tip = (TextView) findViewById(R.id.no_data_tip);

        mDatas = new ArrayList<ContactBean>();
        mAdapter = new FansAdapter(this, mDatas);
        mAdapter.setIsChatMode(true);
        mListView.setAdapter(mAdapter);
        no_data_tip.setText(this.getString(R.string.fans_not_exsit));
        clear_chat_search = (ImageView) this
                .findViewById(R.id.clear_chat_search);
    }

    @Override
    protected void initEvents() {
        mComment.setOnEditorActionListener(this);
        back.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        clear_chat_search.setOnClickListener(this);
    }

    private Runnable searchRunable = new Runnable() {
        @Override
        public void run() {
            searchConatcts(mComment.getText().toString());
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (model != null) {
            model.cancelRequest();
        }
        handler.removeCallbacksAndMessages(null);
        mDatas.clear();
        mDatas = null;
        System.gc();
    }
    private void updateView(){
        /**给搜索结果进行排序，避免好友列表分开显示问题*/
        Collections.sort(mDatas, comparator);
        mAdapter.notifyDataSetChanged();
        if (mDatas.size() > 0) {
            search_no_data.setVisibility(View.GONE);
        } else {
            search_no_data.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.search_friend_cancel) {
            hideSoftKeyboard();
            finish();

        } else if (i == R.id.clear_chat_search) {
            clear_chat_search.setVisibility(View.GONE);
            search_no_data.setVisibility(View.GONE);
            mComment.setText("");
            mDatas.clear();
            mAdapter.notifyDataSetChanged();

        } else if (i == R.id.back) {
            this.finish();
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditTextUtils.hideSoftKeyboard(this, manager);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        hideSoftKeyboard();
        ContactBean item = mDatas.get(position);
        Intent intent = new Intent(this, SearchContactInfoActivity.class);
        intent.putExtra(Constant.SSO_USERID,item.getUserID());
        startActivity(intent);
    }
    Comparator<ContactBean> comparator = new Comparator<ContactBean>() {
        @Override
        public int compare(ContactBean lhs, ContactBean rhs) {
            String a = lhs.getCatalog();
            String b = rhs.getCatalog();
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchConatcts(v.getText().toString());
            return true;
        }
        return false;
    }
}