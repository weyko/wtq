package chat.session.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
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
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.util.file.FileOpreation;
import chat.common.util.input.EditTextUtils;
import chat.common.util.network.HttpRequetUtil;
import chat.contact.activity.ContactsActivity;
import chat.contact.adapter.FansAdapter;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ContactsBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

public class SearchLocFansActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {

    private TextView mCancel;
    private EditText mComment;
    private ImageView clear_chat_search;
    private ListView mListView;
    private ArrayList<ContactBean> mDatas;
    private FansAdapter mAdapter;
    private WBaseModel<ContactsBean> model;
    private Handler handler = new Handler();
    private final int delayMillis = 100;
    private String searchKey = "";
    private RelativeLayout search_no_data;
    private TextView no_data_tip;
    private String cacheFriends;
    private String loginID;
    private List<ContactBean> fansDatas = new ArrayList<ContactBean>();
    private String serachText = "";
    private boolean isGroupMemberActivity;
    private ArrayList<ContactBean> friendsDatas;
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
        Intent intent = this.getIntent();
        if (intent != null) {
            serachText = intent.getStringExtra("text");
            isGroupMemberActivity = intent.getBooleanExtra(
                    "groupMemberActivity", false);
            friendsDatas = (ArrayList<ContactBean>) intent
                    .getSerializableExtra("friendsData");
            fromForward = intent.getBooleanExtra("fromForward", false);
            isTransfer = intent.getBooleanExtra(ContactsActivity.ISTRANSFER, false);
        }
        setContentView(R.layout.activity_search_fans);
        initEvents();

        if (isGroupMemberActivity) {
            fansDatas = friendsDatas;
        } else {
            initData();
        }
    }
    @Override
    protected void initData() {
        setIsTransfer(isTransfer);
        tilteText.setText(R.string.search_title);
        loginID = IMClient.getInstance().getSSOUserId();
        cacheFriends = Constant.CACHE_COMMON_PATH + loginID + "_friends.txt";
        Object obj = FileOpreation.readObjectFromFile(cacheFriends);
        if (obj instanceof ContactsBean) {// 读取缓存对象
            ContactsBean fansListBean = (ContactsBean) obj;
            ArrayList<ContactBean> list = fansListBean.getData().getList();
            checkFansList(list);
            if (list.size() > 0) {
                fansDatas.clear();
                fansDatas.addAll(list);
                Collections.sort(fansDatas, comparator);
            } else {
                serverFans();
            }
        } else {
            serverFans();
        }
    }
    private void serverFans() {
        if (model == null) {
            model = new WBaseModel<ContactsBean>(getApplicationContext(),
                    ContactsBean.class);
        }
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("code","chat-getFriendsList");
        map.put("userID",27889);
        map.put("pageIndex", String.valueOf(0));
        map.put("pageSize", String.valueOf(10));
        model.httpRequest(Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof ContactsBean) {
                            ContactsBean friendList = (ContactsBean) data;
                            if (friendList.isResult()) {
                                checkFansList(friendList.getData().getList());
                                if (friendList.getData() != null
                                        && friendList.getData().getList().size() > 0) {
                                    ArrayList<ContactBean> list = friendList
                                            .getData().getList();
                                    fansDatas.clear();
                                    fansDatas.addAll(list);
                                    Collections.sort(fansDatas, comparator);
                                }
                                if (friendList.getData().getList().size() > 0) {
                                    FileOpreation.writeObjectToFile(friendList,
                                            cacheFriends);// 将对象写入缓存文件
                                }
                            }
                        }
                    }
                });
    }
    private void searchConact(String searchText) {
        if (model == null) {
            model = new WBaseModel<ContactsBean>(getApplicationContext(),
                    ContactsBean.class);
        }
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("code","chat-getFriendsList");
        map.put("userID",27889);
        map.put("pageIndex", String.valueOf(0));
        map.put("pageSize", String.valueOf(10));
        model.httpRequest(Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof ContactsBean) {
                            ContactsBean friendList = (ContactsBean) data;
                            if (friendList.isResult()) {
                                checkFansList(friendList.getData().getList());
                                if (friendList.getData() != null
                                        && friendList.getData().getList().size() > 0) {
                                    ArrayList<ContactBean> list = friendList
                                            .getData().getList();
                                    fansDatas.clear();
                                    fansDatas.addAll(list);
                                    Collections.sort(fansDatas, comparator);
                                }
                                if (friendList.getData().getList().size() > 0) {
                                    FileOpreation.writeObjectToFile(friendList,
                                            cacheFriends);// 将对象写入缓存文件
                                }
                            }
                        }
                    }
                });
    }

    private void checkFansList(ArrayList<ContactBean> friendList) {
        for (int i = 0; i < friendList.size(); i++) {
            ContactBean item = friendList.get(i);
            if (loginID.equals(String.valueOf(item.getFriendID())) || TextUtils.isEmpty(item.getShowName())) {
                friendList.remove(i);
                i--;
            }
        }
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
        if (fromForward || isTransfer) {
            mAdapter.setIsChatMode(false);
        }
        mListView.setAdapter(mAdapter);
        no_data_tip.setText(this.getString(R.string.fans_not_exsit));
        clear_chat_search = (ImageView) this
                .findViewById(R.id.clear_chat_search);
    }

    /**
     * 是否为转账or红包
     */
    public void setIsTransfer(boolean isTransfer) {
        this.isTransfer = isTransfer;
        if (mAdapter != null) {
            mAdapter.setIsChatMode(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initEvents() {
        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKey = s.toString().trim();
                search_no_data.setVisibility(View.GONE);
                if (searchKey != null && searchKey.length() > 0) {
                    handler.removeCallbacks(searchRunable);
                    handler.postDelayed(searchRunable, delayMillis);
                    clear_chat_search.setVisibility(View.VISIBLE);
                } else {
                    clear_chat_search.setVisibility(View.GONE);
                    mDatas.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        back.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        clear_chat_search.setOnClickListener(this);
    }

    private Runnable searchRunable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            serverFansSearchCondition(searchKey);
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
        fansDatas.clear();
        fansDatas = null;
        System.gc();
    }

    private void serverFansSearchCondition(String condition) {
        if (condition == null || condition.length() <= 0) {
            return;
        }
        condition = condition.toLowerCase();//  添加区分大小写查询功能
        mDatas.clear();
        for (int i = 0; i < fansDatas.size(); i++) {
            ContactBean item = fansDatas.get(i);
            String id = String.valueOf(item.getFriendID());
            if (item.getUserNickname().toLowerCase().contains(condition)// 添加区分大小写查询功能
                    || id.toLowerCase().startsWith(condition)
                    || (item.getRemarkName() != null && item.getRemarkName()
                    .toLowerCase().contains(condition))) {
                mDatas.add(item);
            }
        }
        if(mDatas.size()==0){

        }else{
            updateView();
        }

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

        } else {
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditTextUtils.hideSoftKeyboard(this, manager);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (position < mDatas.size()) {
            ContactBean item = mDatas.get(position);
            if (isGroupMemberActivity) {
                Intent intent = new Intent();
                long friendId = item.getFriendID();
                intent.putExtra("friendId", friendId);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
            if (fromForward) {
                Intent intent = new Intent();
                intent.putExtra("person", item);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
            if (isTransfer) {
                Intent intent = new Intent();
                intent.putExtra(ContactsActivity.FANSBEAN, item);
                setResult(RESULT_OK, intent);
                this.finish();
            }
        }
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
}