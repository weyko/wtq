package chat.session.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.util.ToolsUtils;
import chat.common.util.output.ShowUtil;
import chat.common.util.time.DateUtils;
import chat.manager.ChatContactManager;
import chat.manager.ChatLoadManager;
import chat.manager.XmppServerManager;
import chat.manager.XmppSessionManager;
import chat.service.MessageInfoReceiver;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.bean.StatusBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.session.util.RelationType;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;
/**
 * @ClassName: NewFriendsActivity
 * @Description: 新朋友列表
 */
public class NewFriendsActivity extends BaseActivity implements
        PullToRefreshLayout.OnRefreshListener, XmppServerManager.OnMessageSendListener{
    public static final String NEWFRIEND_FILTER = "NEW_FRIEND";
    /**
     * 标题
     */
    private TextView titleText;
    /* 数据集 */
    private PullableListView listview_group_mobiz;
    private PullToRefreshLayout ph_group_mobiz;
    private CommonAdapter<ImUserBean> adapter;
    private List<ImUserBean> list;
    private ImUserBean userBean = null;
    private String hiStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_newfriends);
        initData();
        loadData();
        initEvents();
    }

    protected void initData() {
        list = new ArrayList<ImUserBean>();
        titleText.setText(getString(R.string.chat_list_newfriend_title));
        XmppSessionManager.getInstance().addOnMessageSendListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(NEWFRIEND_FILTER);
        if (receiver != null)
            registerReceiver(receiver, filter);
        ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
                MessageInfoReceiver.EVENT_UPDATE_READ_NUMBERS,
                IMTypeUtil.SessionType.FRIEND.name(), IMTypeUtil.SessionType.FRIEND.name());
        hiStr = getString(R.string.chat_newfriend_hint);
    }

    @Override
    protected void initView() {
        titleText = (TextView) this.findViewById(R.id.titleText);
        ph_group_mobiz = (PullToRefreshLayout) this
                .findViewById(R.id.ph_group_mobiz);
        listview_group_mobiz = (PullableListView) this
                .findViewById(R.id.listview_group_mobiz);
        listview_group_mobiz.setPullToRefreshMode(Pullable.TOP);
        listview_group_mobiz.setDivider(getResources().getDrawable(
                R.color.bg_color));
        int diver = ToolsUtils.dip2px(this, 16);
        listview_group_mobiz.setDividerHeight(diver);
        listview_group_mobiz.setPadding(0, diver, 0, 0);
    }
    @Override
    protected void initEvents() {
        this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doBack();
            }
        });
        ph_group_mobiz.setOnRefreshListener(this);
    }

    @Override
    public void onBackPressed() {
        doBack();
        super.onBackPressed();
    }

    protected void doBack() {
        this.finish();
    }

    private void loadData() {
        List<ImUserBean> sessionForMobiz = ChatLoadManager.getInstance()
                .getListOfNewFriend();
        if (sessionForMobiz == null)
            return;
        list.clear();
        list.addAll(sessionForMobiz);
        updateAadapter();
    }

    /**
     * @return void
     * @Title: updateAadapter
     * @param:
     * @Description: 更新内容
     */
    private void updateAadapter() {
        if (adapter == null) {
            adapter = new CommonAdapter<ImUserBean>(this, list,
                    R.layout.item_newfriend) {
                @Override
                public void convert(ViewHolder helper, final ImUserBean item) {
                    helper.setText(R.id.name_item_newfriend, item.getName());
//                    String name = item.getRemark();
//                    if (TextUtils.isEmpty(name))
//                        name = item.getName();
                    helper.setText(R.id.msg_item_newfriend, hiStr);
                    long birthday = item.getBirthday();
                    TextView age_item_newfriend = helper
                            .getView(R.id.age_item_newfriend);
                    if (birthday == 0) {
                        age_item_newfriend.setText("");
                    } else {
                        age_item_newfriend.setText(String.valueOf(birthday));
                    }
                    long time = item.getMsgTime();
                    if (String.valueOf(time).length() <= 10) {// 兼容旧版本
                        time *= 1000;
                    }
                    helper.setText(R.id.time_item_newfriend, DateUtils
                            .getTimesTampString(NewFriendsActivity.this, time));

                    age_item_newfriend.setBackgroundResource(item
                            .getGender() == 1 ? R.drawable.shape_male_bg
                            : R.drawable.shape_famale_bg);
                    int sexIcon = 0;
                    if (item.getGender() == 1) {// 1:男；　２：女
                        sexIcon = R.drawable.ic_friend_man;
                    } else {
                        sexIcon = R.drawable.ic_friend_female;
                    }
                    age_item_newfriend.setCompoundDrawablesWithIntrinsicBounds(
                            sexIcon, 0, 0, 0);
                    String avatarUrl = item.getAvatar();
                    if (avatarUrl != null && avatarUrl.length() > 0) {
                        helper.setImageByUrl(R.id.avatar_item_newfriend,
                                avatarUrl, 120);
                    } else {
                        if (item.getGender() == 2) {
                            helper.setImageResource(R.id.avatar_item_newfriend,
                                    R.drawable.default_head);
                        } else {
                            helper.setImageResource(R.id.avatar_item_newfriend,
                                    R.drawable.default_head);
                        }
                    }
                    helper.getView(R.id.avatar_item_newfriend)
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {

                                }
                            });
                    helper.getConvertView().setOnClickListener(
                            new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    ChatUtil.gotoChatRoom(
                                            NewFriendsActivity.this, item);
                                }
                            });
                    final TextView state = helper
                            .getView(R.id.state_item_newfriend);
                    state.setVisibility(View.VISIBLE);
                    final int fansStatus = item.getFansStatus();
                    state.setEnabled(true);
                    switch (fansStatus) {
                        case IMTypeUtil.FansStatus.BLACKLIST:
                        case IMTypeUtil.FansStatus.NONE:
                        case IMTypeUtil.FansStatus.ONLY_PEER_FOLLOW:
                            state.setText(getString(R.string.chat_newfriend_follow));
                            state.setBackgroundResource(R.drawable.add_contacts_friend_invite);
                            state.setTextColor(getResources().getColor(
                                    R.color.main_color));
                            state.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    userBean = item;
                                    updateFanstate(item);
                                }
                            });
                            break;
                        case IMTypeUtil.FansStatus.ONLY_ME_FOLLOW:
                            state.setBackgroundResource(R.drawable.btn_gray_rect_line);
                            state.setText(getString(R.string.chat_newfriend_following));
                            state.setEnabled(false);
                            state.setTextColor(getResources().getColor(
                                    R.color.text_color_no_click));
                            break;
                        case IMTypeUtil.FansStatus.FRIEND:
                            state.setBackgroundResource(R.drawable.btn_gray_rect_line);
                            state.setText(getString(R.string.chat_newfriend_followed));
                            state.setEnabled(false);
                            state.setTextColor(getResources().getColor(
                                    R.color.text_color_no_click));
                            break;
                        default:
                            break;
                    }
                }
            };
            listview_group_mobiz.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        loadData();
        ph_group_mobiz.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        XmppSessionManager.getInstance().removeOnMessageSendListener(this);
    }

    /***
     * 监听关注操作广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1 != null) {
                String action = arg1.getAction();
                if (NEWFRIEND_FILTER.equals(action)) {
                    String userId = arg1.getStringExtra("userId");
                    for (ImUserBean userBean : list) {
                        if (userBean.getMxId().equals(userId)) {
                            int fanstate = arg1.getIntExtra("fanState",
                                    userBean.getFansStatus());
                            userBean.setFansStatus(fanstate);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onSuccess(MessageBean messageBean) {
        if (messageBean == null)
            return;
        if (list == null)
            list = new ArrayList<ImUserBean>();
        handler.sendEmptyMessage(100);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            loadData();
        }
    };
    public void showLoading(boolean show) {
        if (show) {
            showLoading();
        } else {
            dissmisLoading();
        }
    }
    public void onRelationChange(StatusBean data, RelationType type) {
        if (data != null) {
            if (data.isResult()) {
                ShowUtil.showToast(NewFriendsActivity.this,
                        getString(R.string.chat_follow_follow_toast));
            } else {
                showResutToast(data.getCode());
            }
            try {
                int fanstatus = IMTypeUtil.FansStatus.NONE;
                if ("follower".equals(data.getData())) {
                    fanstatus = IMTypeUtil.FansStatus.ONLY_PEER_FOLLOW;
                } else if ("following".equals(data.getData())) {
                    fanstatus = IMTypeUtil.FansStatus.ONLY_ME_FOLLOW;
                } else if ("friend".equals(data.getData())) {
                    fanstatus = IMTypeUtil.FansStatus.FRIEND;
                } else if ("blacklist".equals(data.getData())) {
                    fanstatus = IMTypeUtil.FansStatus.BLACKLIST;
                } else if ("none".equals(data.getData())) {
                    fanstatus = IMTypeUtil.FansStatus.NONE;
                } else {
                    return;
                }
                if (userBean != null) {
                    userBean.setFansStatus(fanstatus);
                    ChatContactManager.getInstance().updateFanState(
                            userBean.getMxId(), fanstatus);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ShowUtil.showToast(NewFriendsActivity.this,
                    getString(R.string.warning_service_error));
        }
    }

    /**
     * @return void
     * @Title: updateFanstate
     * @param:
     * @Description: 更新粉丝状态
     */
    private void updateFanstate(final ImUserBean userBean) {

    }
}
