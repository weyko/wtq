package chat.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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
import chat.common.util.TextUtils;
import chat.common.util.file.FileOpreation;
import chat.common.util.network.HttpRequetUtil;
import chat.common.util.output.ShowUtil;
import chat.contact.adapter.FansAdapter;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ContactsBean;
import chat.manager.ChatContactManager;
import chat.session.activity.NewFriendsActivity;
import chat.session.activity.SearchLocFansActivity;
import chat.session.bean.MessageBean;
import chat.session.observe.Subcriber;
import chat.view.SubSideBar;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * 联系人
 */
public class ContactsActivity extends BaseActivity implements
        OnItemClickListener, PullToRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final int REQUST_SEACH =100 ;
    public static boolean reflush = false;
    public static String ISTRANSFER = "isTransfer";
    public static String FANSBEAN = "fansbean";
    Comparator<ContactBean> comparator = new Comparator<ContactBean>() {
        @Override
        public int compare(ContactBean lhs, ContactBean rhs) {
            String a = TextUtils.getString(lhs.getCatalog());
            String b = TextUtils.getString(rhs.getCatalog());
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };
    private PullToRefreshLayout ph_layout;
    private PullableListView pl_layout;
    /**
     * 快速选择栏
     */
    private SubSideBar mSideBar;
    private TextView tv_toast_fan_list;
    private ArrayList<ContactBean> mDatas;
    private FansAdapter mAdapter;
    private String cacheFriends;
    private WBaseModel<ContactsBean> model;
    private String loginID;
    private boolean isTransfer = false;
    private TextView titleText, tv_newfriend_fan_list;
    private ImageView back;
    private EditText search_fan_list;
    private ImageView rightImg;
    private View right_title;
    private int pageIndex=0;
    /**
     * 是否已经初始化索引栏的高度
     */
    private boolean isInitedHeigh = false;
    private int pageSize=10;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ShowUtil.log("weyko", "serverFans--handleMessage");
            if (msg.what == 1) {
                ShowUtil.log("weyko", "serverFans--what");
                ChatContactManager.getInstance().insertContacts(mDatas);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getIntentParams();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fan_list);
    }

    private void getIntentParams() {
        isTransfer = getIntent().getBooleanExtra(ISTRANSFER, false);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IMClient.isChatPage = false;
        if (reflush) {
            readCache();
            reflush = false;
        }
    }
    @Override
    protected void initView() {
        titleText = (TextView) this.findViewById(R.id.titleText);
        back = (ImageView) this.findViewById(R.id.back);
        right_title = this.findViewById(R.id.right_title);
        rightImg = (ImageView) this.findViewById(R.id.rightImg);
        tv_newfriend_fan_list = (TextView) this.findViewById(R.id.tv_newfriend_fan_list);
        search_fan_list = (EditText) this.findViewById(R.id.search_fan_list);
        ph_layout = (PullToRefreshLayout) this
                .findViewById(R.id.ph_layout);
        pl_layout = (PullableListView) this.findViewById(R.id.pl_layout);
        pl_layout.setOnItemClickListener(this);
        pl_layout.setPullToRefreshMode(Pullable.TOP);
        mSideBar = (SubSideBar) this.findViewById(R.id.sideBar);
        tv_toast_fan_list = (TextView) this.findViewById(R.id.tv_toast_fan_list);
        mSideBar.setTextView(tv_toast_fan_list);
        mSideBar.setTextSizeRate(3f / 5);
        ViewTreeObserver vto = mSideBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isInitedHeigh)
                    return;
                mSideBar.setItemHeight(mSideBar.getMeasuredHeight() / 27);
                isInitedHeigh = true;
            }
        });
    }

    @Override
    protected void initEvents() {
        back.setOnClickListener(this);
        right_title.setOnClickListener(this);
        search_fan_list.setOnClickListener(this);
        ph_layout.setOnRefreshListener(this);
        tv_newfriend_fan_list.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        loginID = IMClient.getInstance().getSSOUserId();
        titleText.setText(R.string.chat_tab_contacts);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setImageResource(R.drawable.ic_add);
        mDatas = new ArrayList<ContactBean>();
        mAdapter = new FansAdapter(this, mDatas);
        if (isTransfer) {
            setIsTransfer(isTransfer);
        }
        pl_layout.setAdapter(mAdapter);
        readCache();
        mSideBar.setListView(pl_layout);
    }

    /**
     * 是否为红包
     */
    public void setIsTransfer(boolean isTransfer) {
        this.isTransfer = isTransfer;
        if (mAdapter != null) {
            mAdapter.setIsChatMode(false);
            tv_newfriend_fan_list.setVisibility(View.GONE);
            right_title.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mDatas != null) {
            mDatas.clear();
            mDatas = null;
        }
        if (model != null) {
            model.cancelRequest();
        }
        System.gc();
    }

    private void readCache() {
        cacheFriends = Constant.CACHE_COMMON_PATH + loginID + "_friends.txt";
        Object obj = FileOpreation.readObjectFromFile(cacheFriends);
        if (obj instanceof ContactsBean) {// 读取缓存对象
            ContactsBean fansListBean = (ContactsBean) obj;
            if (fansListBean.getData() != null) {
                mDatas.clear();
                mDatas.addAll(fansListBean.getData().getList());
                Collections.sort(mDatas, comparator);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (mDatas.size() > 0) {
            serverFans(false);
        } else {
            serverFans(true);
        }
    }

    private void serverFans(boolean show) {
        if (show) {
            showLoading();
        }
        if (model == null) {
            model = new WBaseModel<ContactsBean>(getApplicationContext(),
                    ContactsBean.class);
        }
        HashMap<String,Object>map=new HashMap<String,Object>();
        map.put("code","chat-getFriendsList");
        map.put("userID",27889);
        map.put("pageIndex",String.valueOf(pageIndex));
        map.put("pageSize",String.valueOf(pageSize));
        model.httpRequest(Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof ContactsBean) {
                            ContactsBean friendList = (ContactsBean) data;
                            if (friendList.isResult()) {
                                checkFansList(friendList.getData().getList());
                                mDatas.clear();
                                mDatas.addAll(friendList.getData().getList());
                                Collections.sort(mDatas, comparator);
                                mAdapter.notifyDataSetChanged();
                                if (friendList.getData().getList().size() > 0) {
                                    ChatContactManager.getInstance()
                                            .insertContacts(friendList.getData().getList());
                                    FileOpreation.writeObjectToFile(friendList,
                                            cacheFriends);// 将对象写入缓存文件
                                }
                                mSideBar.setListView(pl_layout);
                                handler.sendEmptyMessageDelayed(1, 1000);
                            } else {
                                ShowUtil.showToast(IMClient.getInstance().getContext(),friendList.getMsg());
                            }
                        } else if (data != null && data instanceof WBaseBean) {
                            WBaseBean info = (WBaseBean) data;
                            ShowUtil.showToast(IMClient.getInstance().getContext(),info.getMsg());
                        }
                        ph_layout
                                .refreshFinish(PullToRefreshLayout.SUCCEED);
                        dissmisLoading();
                    }
                });
    }

    private void checkFansList(ArrayList<ContactBean> friendList) {
        if(friendList==null)
            return;
        for (int i = 0; i < friendList.size(); i++) {
            ContactBean item = friendList.get(i);
            if (android.text.TextUtils.isEmpty(item.getShowName())
                    || loginID.equals(String.valueOf(item.getFriendID()))) {
                friendList.remove(i);
                i--;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ContactBean item = mDatas.get(position);
        if (isTransfer) {
            Intent intent = new Intent();
            intent.putExtra(FANSBEAN, item);
            setResult(RESULT_OK, intent);
            this.finish();
        }else{
            Intent intent=new Intent(this,PersonCenterActivity.class);
            intent.putExtra(Constant.FRIEND_INFO,item);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        serverFans(false);
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
    }

    @Override
    public void onClick(View v) {
        if (v == back)
            this.finish();
        else if (v == search_fan_list) {
            Intent intent = new Intent(this, SearchContactActivity.class);
            startActivityForResult(intent,REQUST_SEACH);
        } else if (right_title == v) {
            Intent intent = new Intent(this, ContactsInvateActivity.class);
            startActivity(intent);
        } else if (tv_newfriend_fan_list == v) {
            Intent intent = new Intent(this, NewFriendsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(isTransfer){
                setResult(RESULT_OK,data);
                this.finish();
            }
        }
    }
    @Subcriber
    public void testRemove(MessageBean messageBean){
        ShowUtil.log("test",this.getClass().getName());
    }
}