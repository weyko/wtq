package chat.session.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import chat.common.util.network.HttpRequetUtil;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ContactsBean;
import chat.image.DisplayImageConfig;
import chat.manager.ChatLoadManager;
import chat.session.adapter.IMUserSelectAdapter;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.bean.IMUserBase;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.enums.SessionTypeEnum;
import chat.session.util.IMTypeUtil;
import chat.view.SideBar;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

public class ForwardMessageActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {
    private RadioGroup chated_sel = null;
    private RadioGroup friends_sel = null;
    private RadioButton chated = null;
    private RadioButton friends = null;
    private PullableListView chat_list_trans, friend_list_trans;
    /**
     * 快速选择栏
     */
    private SideBar mSideBar;
    private IMUserSelectAdapter adapter = null;
    private CommonAdapter<IMUserBase> chatListAdapter;
    private List<IMUserBase> datas = new ArrayList<IMUserBase>();
    private boolean isMulSelect = false;
    private int curType = 0;// 0-对话列表 1-好友列表
    private String cacheFriends;
    private String loginID;
    private WBaseModel<ContactsBean> model;
    private TextView title;
    private MessageBean bean;
    private boolean isNeedGroup = true;
    private PullToRefreshLayout pl_trans, pl_friend_trans;
    private View view_friend_trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);
        initEvents();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        title = (TextView) this.findViewById(R.id.title);
        chated_sel = (RadioGroup) this.findViewById(R.id.chated_sel);
        friends_sel = (RadioGroup) this.findViewById(R.id.friends_sel);
        chated = (RadioButton) this.findViewById(R.id.chated);
        friends = (RadioButton) this.findViewById(R.id.friends);
        chat_list_trans = (PullableListView) this
                .findViewById(R.id.chat_list_trans);
        friend_list_trans = (PullableListView) this
                .findViewById(R.id.friend_list_trans);
        chat_list_trans.setPullToRefreshMode(Pullable.TOP);
        friend_list_trans.setPullToRefreshMode(Pullable.TOP);
        mSideBar = (SideBar) this.findViewById(R.id.sideBar);
        view_friend_trans = this.findViewById(R.id.view_friend_trans);
        mSideBar.setListView(chat_list_trans);
        pl_trans = (PullToRefreshLayout) this.findViewById(R.id.pl_trans);
        pl_friend_trans = (PullToRefreshLayout) this.findViewById(R.id.pl_friend_trans);
        ViewTreeObserver vto = mSideBar.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mSideBar.setItemHeight(mSideBar.getMeasuredHeight() / 27);
                return true;
            }
        });
        title.setText(R.string.chat_textview_menu_transpond_to);
    }

    @Override
    protected void initEvents() {
        // TODO Auto-generated method stub
        this.findViewById(R.id.searchArea).setOnClickListener(this);
        this.findViewById(R.id.query).setOnClickListener(this);
        this.findViewById(R.id.cancel).setOnClickListener(this);
        chated.setOnClickListener(this);
        friends.setOnClickListener(this);
        pl_trans.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                convertChatListData();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        pl_trans.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                });
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        pl_friend_trans.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                serverFans(false);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        chat_list_trans.setOnItemClickListener(this);
        friend_list_trans.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        bean = (MessageBean) getIntent().getSerializableExtra(
                ChatActivity.MESSAGEBEAN);
        isNeedGroup = getIntent().getBooleanExtra("isNeedGroup", isNeedGroup);
        loginID = IMClient.getInstance().getSSOUserId();
        cacheFriends = Constant.CACHE_COMMON_PATH + loginID + "_friends.txt";
        changedData();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (model != null) {
            model.cancelRequest();
        }
        System.gc();
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.chated) {
            curType = 0;
            chated_sel.setVisibility(View.VISIBLE);
            friends_sel.setVisibility(View.GONE);
            friends.setChecked(false);
            changedData();

        } else if (i == R.id.friends) {
            curType = 1;
            chated_sel.setVisibility(View.GONE);
            friends_sel.setVisibility(View.VISIBLE);
            chated.setChecked(false);
            changedData();

        } else if (i == R.id.query || i == R.id.searchArea) {
            Intent intent = new Intent(getApplicationContext(),
                    SearchLocFansActivity.class);
            intent.putExtra("text", this.getString(R.string.search_fan_list));
            intent.putExtra("fromForward", true);
            startActivityForResult(intent, 11);
            //startActivity(intent);

        } else if (i == R.id.cancel) {
            this.finish();

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                ContactBean item = (ContactBean) data.getSerializableExtra("person");
                if (item != null) {
                    if (bean != null) {
                        String id = String.valueOf(item.getFriendID());
                        String loginID = IMClient.getInstance().getSSOUserId();
                        if (!loginID.equals(id)) {
                            Intent intent = new Intent();
                            ImUserBean imUserBean = bean.getImUserBean();
                            imUserBean.setMxId(id);
                            imUserBean.setAvatar(item.getUserImg());
                            imUserBean.setName(item.getUserNickname());
                            bean.setChatWith(id);
                            bean.setTo(id);
                            bean.setSessionId(id + "@" + SessionTypeEnum.NORMAL.getValue());
                            intent.putExtra(ChatActivity.MESSAGEBEAN, bean);
                            setResult(RESULT_OK, intent);
                        }
                    }
                    this.finish();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        gotoTrans(position, curType == 0 ? chatList : datas);
    }

    /**
     * 进行跳转
     *
     * @param position
     */
    private void gotoTrans(int position, List<IMUserBase> datas) {
        if (position >= 0 && position < datas.size()) {
            if (bean != null) {
                IMUserBase item = datas.get(position);
                Intent intent = new Intent();
                bean.setSessionType(SessionTypeEnum.fromInt(item.getBoxType()));
                ImUserBean imUserBean = bean.getImUserBean();
                imUserBean.setMxId(item.getId());
                imUserBean.setAvatar(item.getAvatarUrl());
                imUserBean.setName(item.getName());
                bean.setChatWith(item.getId());
                bean.setTo(item.getId());
                bean.setSessionId(item.getId() + "@" + item.getBoxType());
                intent.putExtra(ChatActivity.MESSAGEBEAN, bean);
                ForwardMessageActivity.this.setResult(RESULT_OK, intent);
            }
            this.finish();
        }
    }

    private void changedData() {
        if (curType == 0) {
            convertChatListData();
        } else {
            if (adapter == null) {
                adapter = new IMUserSelectAdapter(this, datas, isMulSelect);
                friend_list_trans.setAdapter(adapter);
            }
            datas.clear();
            Object obj = FileOpreation.readObjectFromFile(cacheFriends);
            if (obj instanceof ContactsBean) {// 读取缓存对象
                ContactsBean fansListBean = (ContactsBean) obj;
                convertFriendsData(fansListBean);
            } else {
                serverFans(true);
            }
        }
    }

    private List<IMUserBase> chatList;

    @SuppressWarnings("unchecked")
    private void convertChatListData() {
        pl_trans.setVisibility(View.VISIBLE);
        view_friend_trans.setVisibility(View.GONE);
        if (chatList == null) {
            chatList = new ArrayList<IMUserBase>();
        }
        HashMap<String, Object> sessions = ChatLoadManager.getInstance()
                .getSessions(true, isNeedGroup);
        chatList.clear();
        if (sessions != null) {
            try {
                chatList = (List<IMUserBase>) sessions.get("data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Collections.sort(chatList, comparator);
            if (chatListAdapter == null) {
                chatListAdapter = new CommonAdapter<IMUserBase>(this, chatList, R.layout.item_select_im_user) {
                    @Override
                    public void convert(ViewHolder helper, IMUserBase item) {
                        helper.getView(R.id.letterIndex).setVisibility(View.GONE);
                        ImageView avatarIv = helper.getView(R.id.avatar);
                        int defaultIcon = -1;
                        if (item.getBoxType() == IMTypeUtil.BoxType.GROUP_CHAT) {// 群聊
                            defaultIcon = R.drawable.ic_default_group;
                        } else {
                            defaultIcon = R.drawable.default_head;
                        }
                        if (item.getAvatarUrl() != null && item.getAvatarUrl().length() > 0) {
                            IMClient.sImageLoader.displayThumbnailImage(item.getAvatarUrl(),
                                    avatarIv, DisplayImageConfig.getChatHeaderOptions(defaultIcon),
                                    DisplayImageConfig.headThumbnailSize, DisplayImageConfig.headThumbnailSize, defaultIcon);
                        } else {
                            avatarIv.setImageResource(defaultIcon);
                        }
                        helper.setText(R.id.name, item.getName());
                    }
                };
                chat_list_trans.setAdapter(chatListAdapter);
            } else {
                chatListAdapter.setList(chatList);
                chatListAdapter.notifyDataSetChanged();
            }
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
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("code","chat-getFriendsList");
        map.put("userID",27889);
        map.put("pageIndex", String.valueOf(0));
        map.put("pageSize", String.valueOf(100));
        model.httpRequest(Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof ContactsBean) {
                            ContactsBean friendList = (ContactsBean) data;
                            if (friendList.isResult()) {
                                convertFriendsData(friendList);
                                if (friendList.getData().getList().size() > 0) {
                                    FileOpreation.writeObjectToFile(friendList,
                                            cacheFriends);// 将对象写入缓存文件
                                }
                            } else {
                                showResutToast(friendList.getCode());
                            }
                        } else if (data != null && data instanceof WBaseBean) {
                            WBaseBean info = (WBaseBean) data;
                            showResutToast(info.getCode());
                        }
                        pl_friend_trans.refreshFinish(PullToRefreshLayout.SUCCEED);
                        dissmisLoading();
                    }
                });
    }

    private void convertFriendsData(ContactsBean fansListBean) {
        pl_trans.setVisibility(View.GONE);
        view_friend_trans.setVisibility(View.VISIBLE);
        if (fansListBean.getData() != null && fansListBean.getData().getList().size() > 0) {
            ArrayList<ContactBean> list = fansListBean.getData().getList();
            datas.clear();
            for (int i = 0; i < list.size(); i++) {
                ContactBean item = list.get(i);
                IMUserBase it = new IMUserBase();
                it.setAvatarUrl(item.getUserImg());
                it.setCatalog(item.getCatalog());
                it.setId(String.valueOf(item.getFriendID()));
                it.setName(item.getUserNickname());
                it.setBoxType(IMTypeUtil.BoxType.SINGLE_CHAT);
                datas.add(it);
            }
            Collections.sort(datas, comparator);
            adapter.notifyDataSetChanged();
            mSideBar.setVisibility(View.VISIBLE);
            mSideBar.setListView(friend_list_trans);
        } else {
            mSideBar.setVisibility(View.GONE);
        }
    }

    Comparator<IMUserBase> comparator = new Comparator<IMUserBase>() {
        @Override
        public int compare(IMUserBase lhs, IMUserBase rhs) {
            long a = lhs.getTime();
            long b = rhs.getTime();
            if (a >= b) {
                return -1;
            } else {
                return 1;
            }
        }
    };
}