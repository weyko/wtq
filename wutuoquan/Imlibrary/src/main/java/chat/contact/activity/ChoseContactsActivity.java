package chat.contact.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
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
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.user.UserInfoHelp;
import chat.common.util.TextUtils;
import chat.common.util.file.FileOpreation;
import chat.common.util.output.ShowUtil;
import chat.contact.adapter.FansAdapter;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ContactsBean;
import chat.dialog.CustomBaseDialog;
import chat.manager.ChatContactManager;
import chat.manager.ChatGroupManager;
import chat.manager.ChatMembersManager;
import chat.service.MessageInfoReceiver;
import chat.session.MessageBuilder;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgStatusEnum;
import chat.session.group.bean.ChatGroupBean;
import chat.session.group.bean.ChatGroupData;
import chat.session.group.bean.CreateGroupResultBean;
import chat.session.group.bean.RemoteMemberBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
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
public class ChoseContactsActivity extends BaseActivity implements
        OnItemClickListener, PullToRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static boolean reflush = false;
    //是否多选
    public static String ISMULTISELECT = "ISMULTISELECT";
    //已选择成员
    private String selectedMembers;
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
    private ArrayList<ContactBean> mDatasSearch;
    private FansAdapter mAdapter;
    private String cacheFriends;
    private WBaseModel<ContactsBean> model;
    private String loginID;
    private boolean isMultiselect = false;
    private TextView titleText, tv_newfriend_fan_list, next;
    private ImageView back;
    private EditText search_fan_list;
    private View right_title;
    /**
     * 是否已经初始化索引栏的高度
     */
    private boolean isInitedHeigh = false;
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
        isMultiselect = getIntent().getBooleanExtra(ISMULTISELECT, false);
        selectedMembers=getIntent().getStringExtra(RemoteMemberBean.MEMBES);
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
        next = (TextView) this.findViewById(R.id.next);
        back = (ImageView) this.findViewById(R.id.back);
        right_title = this.findViewById(R.id.right_title);
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
        titleText.setText(R.string.contacts_title);
        initSearch();
        mDatas = new ArrayList<ContactBean>();
        mDatasSearch = new ArrayList<ContactBean>();
        mAdapter = new FansAdapter(this, mDatasSearch);
        if (isMultiselect) {
            setIsTransfer(isMultiselect);
        }
        pl_layout.setAdapter(mAdapter);
        readCache();
        mSideBar.setListView(pl_layout);
    }

    private void initSearch() {
        search_fan_list.setFocusable(true);
        search_fan_list.setFocusableInTouchMode(true);
        search_fan_list.requestFocus();
        search_fan_list.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDatasSearch.clear();
                String searchKey=s.toString().trim();
                if (android.text.TextUtils.isEmpty(searchKey)) {
                    mDatasSearch.addAll(mDatas);
                } else {
                    for (ContactBean bean : mDatas) {
                        if (bean.getShowName().contains(searchKey)) {
                            mDatasSearch.add(bean);
                        }
                    }
                }
                mAdapter.clearSelects();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 是否为红包
     */
    public void setIsTransfer(boolean isMultiselect) {
        this.isMultiselect = isMultiselect;
        if (mAdapter != null) {
            mAdapter.setIsChatMode(false);
            next.setVisibility(View.VISIBLE);
            next.setText(R.string.ok);
            tv_newfriend_fan_list.setVisibility(View.GONE);
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
        if (mDatasSearch != null) {
            mDatasSearch.clear();
            mDatasSearch = null;
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
                mDatasSearch.clear();
                mDatas.addAll(fansListBean.getData().getList());
                Collections.sort(mDatas, comparator);
                mDatasSearch.addAll(mDatas);
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
        model.httpJsonRequest(Method.GET, URLConfig.FANS,
                null, new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof ContactsBean) {
                            ContactsBean friendList = (ContactsBean) data;
                            if (friendList.isResult()) {
                                checkFansList(friendList.getData().getList());
                                mDatas.clear();
                                mDatasSearch.clear();
                                mDatas.addAll(friendList.getData().getList());
                                Collections.sort(mDatas, comparator);
                                mDatasSearch.addAll(mDatas);
                                mAdapter.notifyDataSetChanged();
                                if (friendList.getData().getList().size() > 0) {
                                    ChatContactManager.getInstance()
                                            .insertContacts(
                                                    friendList.getData().getList());
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
        ContactBean item = mDatasSearch.get(position);
        Intent intent = new Intent();
        intent.putExtra(Constant.FRIEND_INFO, item);
        setResult(RESULT_OK, intent);
        this.finish();
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
        else if (right_title == v) {
            if(android.text.TextUtils.isEmpty(selectedMembers)){
                createGruop(mAdapter.getSelectFriends());
            }else{
                Intent intent=new Intent();
                RemoteMemberBean bean=new RemoteMemberBean();
                bean.setMembsers(mAdapter.getSelectFriends());
                intent.putExtra(RemoteMemberBean.MEMBES,bean);
                setResult(RESULT_OK,intent);
                this.finish();
            }
        }
    }
    /**群最大人数*/
    private int MAX_MEMBERS = 500;
    /**
     * 群验证，1允许任何人2需要身份验证3不允许任何人, 默认值为1
     */
    private int verifyType = 1;
    /**
     * @return void
     * @Title: createGruop
     * @param:
     * @Description: 创建群
     */
    private void createGruop(final SparseArray<ContactBean> selectFriends) {
        if(selectFriends==null){
            ShowUtil.showToast(this,R.string.chat_create_fail);
            return;
        }
        final String name = "";
        int size = selectFriends.size();
        List<HashMap<String, Object>> participantList = new ArrayList<HashMap<String, Object>>();
        final StringBuilder groupName=new StringBuilder();
        groupName.append(new UserInfoHelp().getNickname());
        for (int i = 0; i < size; i++) {
            groupName.append(",");
            groupName.append(selectFriends.get(i).getUserNickname());
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("role", IMTypeUtil.RoleType.MEMBERS);
            item.put("userId", String.valueOf(selectFriends.get(i).getFriendID()));
            item.put("mtalkDomain", URLConfig.getServerName());
            participantList.add(item);
        }
        showLoading(getString(R.string.chat_group_create_title) + "...");
        WBaseModel<CreateGroupResultBean> mode = new WBaseModel<CreateGroupResultBean>(
                this, CreateGroupResultBean.class);
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("roomName", name);
        parmas.put("maxCnt", MAX_MEMBERS);
        parmas.put("verifyType", verifyType);
        parmas.put("photoUrl", "");
        // 房间类型 ( 1是公共群， 0是私有群，默认值为0）
        parmas.put("publicType", 0);
        parmas.put("memberList", participantList);
        mode.httpJsonRequest(Method.POST, URLConfig.CHAT_GRUOP_CREATE+ IMClient.getInstance().getSSOUserId(), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null
                                && data instanceof CreateGroupResultBean) {
                            CreateGroupResultBean resulBean = (CreateGroupResultBean) data;
                            ShowUtil.showToast(resulBean.getMsg());
                            if (resulBean.isResult()) {
                                IMClient.isGroupChange = true;
                                ChatGroupBean groupBean = new ChatGroupBean();
                                ChatGroupData groupData = resulBean.getData();
                                groupData.setRoleType(IMTypeUtil.RoleType.OWNERS);
                                groupData.setGroupName(groupName.toString());
                                groupBean.setData(groupData);
                                ChatGroupManager.getInstance().insertGroup(
                                        groupBean, false);
                                ChatGroupManager.getInstance().setIsBookTolist(
                                        String.valueOf(groupData.getId()), 1);
                                inserMembers(resulBean.getData().getId(),selectFriends);
                                insertMessage(String.valueOf(groupData.getId()),getString(R.string.chat_group_create_name_empty));
                                ChatUtil.gotoChatRoom(
                                        ChoseContactsActivity.this, groupBean);
                                ChatUtil.sendUpdateNotify(
                                        IMClient.getInstance().getContext(),
                                        MessageInfoReceiver.EVENT_UPDATE_SESSION_ONLY,
                                        groupData.getId() + "@"
                                                + IMTypeUtil.BoxType.GROUP_CHAT, groupData.getId() + "@"
                                                + IMTypeUtil.BoxType.GROUP_CHAT);
                                ChoseContactsActivity.this.finish();
                            }

                        } else if (data instanceof WBaseBean) {
                            WBaseBean resulBean = (WBaseBean) data;
                            if (resulBean.getCode().equals("mx11080029")) {
                                showOutCreateDialog();
                            } else {
                                showResutToast(resulBean.getCode());
                            }
                        } else {
                            ShowUtil.showToast(ChoseContactsActivity.this,
                                    getString(R.string.warning_service_error));
                        }
                        dissmisLoading();
                    }
                });
    }
    private void insertMessage(String roomId, String roomName) {
        MessageBean tipMessage = MessageBuilder.createTipMessage(roomId, roomName);
        tipMessage.setMsgStatus(MsgStatusEnum.success);
        ChatUtil.getInstance().insertMessage(tipMessage);
    }
    /**
     * @return void
     * @Title: inserMembers
     * @param:
     * @Description: 插入群成员
     */
    protected void inserMembers(long groupId,SparseArray<ContactBean> selectFriends) {
        if(selectFriends==null)
            return;
        ContactBean beanSelf = new ContactBean();
        UserInfoHelp userInfo = UserInfoHelp.getInstance();
        try {
            beanSelf.setFriendID(Integer.valueOf(userInfo.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        beanSelf.setUserNickname(userInfo.getNickname());
        beanSelf.setUserImg(userInfo.getAvarar());
        beanSelf.setRole(IMTypeUtil.RoleType.OWNERS);
        int key = selectFriends.keyAt(0);
        key-=1;
        selectFriends.put(key,beanSelf);
        ChatMembersManager.getInstance().insertGroupMembers(
                String.valueOf(groupId), selectFriends);
    }
    /**
     * @return void
     * @Title: showOutCreateDialog
     * @param:
     * @Description: 显示建群超限对话提示框
     */
    protected void showOutCreateDialog() {
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(ChoseContactsActivity.this,
                getString(R.string.chat_create_out_title),
                getString(R.string.chat_create_out_message), null, null,
                getString(R.string.chat_create_out_sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        dialog.setRightButtonColor(getResources().getColor(R.color.blue));
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (isMultiselect) {
                setResult(RESULT_OK, data);
                this.finish();
            }
        }
    }
}