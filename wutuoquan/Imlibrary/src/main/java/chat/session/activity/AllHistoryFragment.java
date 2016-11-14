package chat.session.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.base.BaseFragment;
import chat.base.IMClient;
import chat.common.util.input.EditTextUtils;
import chat.common.util.network.ConnectStateUtil;
import chat.common.util.output.LogUtil;
import chat.common.util.output.ShowUtil;
import chat.manager.ChatContactManager;
import chat.manager.ChatGroupManager;
import chat.manager.ChatLoadManager;
import chat.manager.ChatMessageManager;
import chat.manager.ChatNotifyManager;
import chat.manager.ChatSessionManager;
import chat.manager.XmppServerManager;
import chat.manager.XmppSessionManager;
import chat.service.MessageInfoReceiver;
import chat.service.MessageReciveObserver;
import chat.session.adapter.ChatAllHistoryAdapter;
import chat.session.bean.MessageBean;
import chat.session.enums.SessionTypeEnum;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.view.ActionSheet;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;

/**
 * @version 显示所有会话记录，包括陌生人
 */
public class AllHistoryFragment extends BaseFragment implements OnClickListener, MessageReciveObserver,
        ActionSheet.MenuItemClickListener, OnItemLongClickListener, PullToRefreshLayout.OnRefreshListener,
        XmppServerManager.OnMessageSendListener, OnItemClickListener {
    protected static final int REQUEST_UPDATE_LIST = 201512;
    protected static final int REQUEST_UPDATE_UNREADS = 201513;
    public static String TAG = "AllHistoryFragment";
    private static int allTopCount = 0;// 统计所有置顶数
    private InputMethodManager inputMethodManager;
    /**
     * 错误提示框
     */
    private RelativeLayout errorItem;
    private TextView error_Msg;// 错误文本内容
    private List<MessageBean> list = new ArrayList<MessageBean>();
    private ChatAllHistoryAdapter adapter;
    private PullableListView listview;
    private String userid;
    private Resources resources;
    /**
     * 主容器
     */
    private View main_allhistory;
    /**
     * 空白页
     */
    private TextView tv_empty_allhistory;
    private PullToRefreshLayout ph_history_chat;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String sessionId = "";
            String shopId = "";
            if (msg.obj instanceof HashMap) {
                HashMap<String, String> maps = (HashMap<String, String>) msg.obj;
                sessionId = maps.get("sessionId");
            } else {
                sessionId = (String) msg.obj;
            }
            if (msg.what == REQUEST_UPDATE_LIST) {
                boolean isUpdated = ChatLoadManager.getInstance()
                        .updateSessions(sessionId, shopId, list, false);
                if (isUpdated) {
                    adapter.notifyDataSetChanged();
                }
                if (list != null)
                    showPageView(list.size() > 0);
            } else if (msg.what == REQUEST_UPDATE_UNREADS) {
                boolean isUpdated = ChatLoadManager.getInstance()
                        .updateSessions(sessionId, shopId, list, true);
                if (isUpdated) {
                    adapter.notifyDataSetChanged();
                }
            }
        }

        ;
    };
    private Runnable delayConnectFailedRunnable = new Runnable() {
        @Override
        public void run() {
            reflushState();
        }
    };
    private boolean loadTask = false;
    private int loadTaskCount = 0;
    private Runnable loadConversationsRunable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            doLoadConversationsWithRecentChat();
            if (loadTaskCount > 0) {
                loadTaskCount = 0;
                loadTask = true;
                handler.removeCallbacks(loadConversationsRunable);
                handler.postDelayed(loadConversationsRunable, 800);
            }
        }
    };
    /**
     * 长按 记住当前列
     */
    private int currentPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        resources = getActivity().getResources();
        loadConversationsWithRecentChat();// 有新消息，通知刷新页面
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        userid = IMClient.getInstance().getSSOUserId();
        MessageInfoReceiver.registerReceiverHandler(this);
        XmppSessionManager.getInstance().addOnMessageSendListener(this);
        setContentView(R.layout.fragment_conversation_history);
        iniView(getContentView());
        initEvent();
        initData();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        MessageInfoReceiver.unregisterReceiverHandler(this);
        XmppSessionManager.getInstance().removeOnMessageSendListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        IMClient.isChatPage = false;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MessageInfoReceiver.unregisterReceiverHandler(this);
        XmppSessionManager.getInstance().removeOnMessageSendListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EditTextUtils.hideSoftKeyboard(
                getActivity(),
                (InputMethodManager) getActivity().getSystemService(
                        Service.INPUT_METHOD_SERVICE));
        ChatNotifyManager.getInstance().clearNotify();
        IMClient.isChatPage = true;
//        XmppServerManager.checkIMConnectAndLogin();
    }

    private void iniView(View contentView) {
        adapter = new ChatAllHistoryAdapter(getActivity(), list);
        errorItem = (RelativeLayout) contentView
                .findViewById(R.id.rl_error_item);
        error_Msg = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        listview = (PullableListView) contentView.findViewById(R.id.listview);
        main_allhistory = contentView.findViewById(R.id.main_allhistory);
        tv_empty_allhistory = (TextView) contentView
                .findViewById(R.id.tv_empty_allhistory);
        ph_history_chat = (PullToRefreshLayout) this
                .findViewById(R.id.ph_history_chat);
        listview.setPullToRefreshMode(Pullable.TOP);
    }

    /**
     * 连接改变监听
     */
    private void connectStateChanged() {
        handler.removeCallbacks(delayConnectFailedRunnable);
        boolean connected = XmppServerManager.getInstance(
                XmppSessionManager.getInstance()).checkIsLogin();
        if (ConnectStateUtil.isNetworkAvailable(getApplicationContext())
                && !connected) {// 有网络，没登录上
            handler.postDelayed(delayConnectFailedRunnable, 10 * 1000);
        } else {
            handler.postDelayed(delayConnectFailedRunnable, 100);
        }
    }

    /**
     * 刷新连接状态
     */
    private void reflushState() {
        if (!ConnectStateUtil.isNetworkAvailable(getApplicationContext())) {// 判断网络是否可用
            error_Msg.setText(this.getText(R.string.chat_textview_not_network));
            errorItem.setVisibility(View.VISIBLE);
            return;
        }
        boolean connected = XmppServerManager.getInstance(
                XmppSessionManager.getInstance()).checkIsLogin();
        if (connected) {
            errorItem.setVisibility(View.GONE);
        } else {
            error_Msg.setText(this.getText(R.string.chat_textview_not_login));
            errorItem.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unchecked")
    private void initEvent() {
        registerForContextMenu(listview);
        listview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                EditTextUtils.hideSoftKeyboard(getActivity(), inputMethodManager);
                return false;
            }
        });
        listview.setOnItemClickListener(this);
        ph_history_chat.setOnRefreshListener(this);
        listview.setOnItemLongClickListener(this);
    }

    /**
     * 获取所有会话
     */
    private void loadConversationsWithRecentChat() {
        // add by h.j.huang 增加消息延时刷新，处理消息过快刷新问题。
        loadTaskCount++;
        if (!loadTask) {
            loadTask = true;
            loadTaskCount = 0;
            handler.removeCallbacks(loadConversationsRunable);
            handler.postDelayed(loadConversationsRunable, 10);
        }
    }

    @SuppressWarnings("unchecked")
    private void doLoadConversationsWithRecentChat() {
        List<MessageBean> beans = new ArrayList<MessageBean>();
        try {
            HashMap<String, Object> sessions = ChatLoadManager.getInstance()
                    .getSessions(false, true);
            if (sessions != null) {
                beans = (List<MessageBean>) sessions.get("data");
                allTopCount = (Integer) sessions.get("allTopCount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.clear();
        list.addAll(beans);
        showPageView(list.size() > 0);
        if (LogUtil.isDebug()) {
            LogUtil.writeCommonLogtoFile("---------sort start------");
            for (MessageBean item : list) {
                LogUtil.writeCommonLogtoFile("sort----time="
                        + item.getMsgTime() + " isTop=" + item.getIsTop());
            }
            LogUtil.writeCommonLogtoFile("---------sort end------");
        }
        adapter.notifyDataSetChanged();
        ph_history_chat.refreshFinish(PullToRefreshLayout.SUCCEED);
        loadTask = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    // 刷新对话列表
    private void refresh() {
        loadConversationsWithRecentChat();
    }

    private void initData() {
        listview.setAdapter(adapter);
        connectStateChanged();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void handleReciveMessage(int event, final String msgCode, final int sendStatus, String sessionId, long time, String url, String nickName, String roomId) {
        // TODO Auto-generated method stub
        if (event == MessageInfoReceiver.EVENT_UPDATE_READ_NUMBERS) {
            handler.sendMessageDelayed(
                    handler.obtainMessage(REQUEST_UPDATE_UNREADS, sessionId),
                    500);
        } else if (event == MessageInfoReceiver.EVENT_CONNECTION_CHANGED) {
            connectStateChanged();// 连接状态发生改变更新连接状态UI
        } else if (event == MessageInfoReceiver.EVENT_UPDATE_SETTING) {
            loadConversationsWithRecentChat();// 置顶更新
        } else {
            handler.sendMessageDelayed(handler.obtainMessage(
                    REQUEST_UPDATE_LIST, sessionId), 500);
        }
    }

    @Override
    public void onActionSheetItemClick(int itemPosition) {
        switch (itemPosition) {
            case 0:
                deleteHistoryChat();
                break;
            case 1:
                topHistoryChat();
                break;
            case ActionSheet.CANCEL_BUTTON_ID:// 取消|默认dismiss对话框
                this.currentPosition = 0;
                break;
            default:
                break;
        }
    }
    /**
     * 置顶聊天
     */
    private void topHistoryChat() {
        if (list != null && list.size() > 0) {
            MessageBean sessionBean = list.get(currentPosition);
            boolean isTop = sessionBean.getIsTop() == 1;
            if (!isTop) {
                if (allTopCount == ChatUtil.MAX_TOP) {
                    ShowUtil.showToast(getActivity(), resources
                            .getString(R.string.chat_listdialog_top_limit));
                    return;
                }
            }
            boolean isSetSuccess = ChatSessionManager.getInstance().updateTop(
                    sessionBean.getSessionId(), !isTop);
            String showMsg = "";
            if (isSetSuccess) {
                if (sessionBean.getSessionType() == SessionTypeEnum.GROUPCHAT) {
                    ChatGroupManager.getInstance().setIsTop(
                            sessionBean.getChatWith(), isTop ? 0 : 1);
                } else if (sessionBean.getSessionType() == SessionTypeEnum.NORMAL) {
                    ChatContactManager.getInstance().setIsTop(
                            sessionBean.getChatWith(), isTop ? 0 : 1);
                }
                showMsg = resources
                        .getString(!isTop ? R.string.chat_listdialog_top_success
                                : R.string.chat_listdialog_top_cancal_success);
                loadConversationsWithRecentChat();// 有新消息，通知刷新页面
                if (isTop) {
                    allTopCount--;
                }
            } else {
                showMsg = resources
                        .getString(!isTop ? R.string.chat_listdialog_top_fail
                                : R.string.chat_listdialog_top_cancal_fail);
            }
            ShowUtil.showToast(getActivity(), showMsg);
        }
    }

    /**
     * 删除历史记录
     */
    private void deleteHistoryChat() {
        MessageBean sessionBean = list.get(currentPosition);
        if (sessionBean == null)
            return;
        String sessionId = sessionBean.getSessionId();
        boolean isDeleted = ChatMessageManager.getInstance()
                .deleteChatTable(sessionId, "");
        String showMsg = resources
                .getString(isDeleted ? R.string.chat_listdialog_delete_success
                        : R.string.chat_listdialog_delete_fail);
        ShowUtil.showToast(getActivity(), showMsg);
        if (isDeleted) {
            ChatMessageManager.getInstance().setMessageReadStatusByGroup(sessionId, "",
                    sessionId);
            list.remove(currentPosition);
            adapter.notifyDataSetChanged();
            ChatUtil.sendUpdateNotify(getActivity(),
                    MessageInfoReceiver.EVENT_UPDATE_READ_NUMBERS, "msg", sessionId);
            showPageView(list.size() > 0);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        if (position < list.size() && position >= 0) {
            String sessionId = list.get(position).getSessionId();
            if (sessionId.equals(IMTypeUtil.SessionType.MOBIZ.name()))// 非单聊、群聊集合不能进行相关操作
                return true;
            showMenuDialog(position);
            this.currentPosition = position;
        }
        return true;
    }

    /**
     * 显示菜单栏对话框
     *
     * @param position
     */
    private void showMenuDialog(int position) {
        getActivity().setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet menuView = new ActionSheet(getActivity());
        menuView.setCancelButtonTitle(getString(R.string.cancel));
        menuView.setCurrentItems("");
        String sessionId = list.get(position).getSessionId();
        if (sessionId.equals(IMTypeUtil.SessionType.FRIEND.name()))
            menuView.addItems(getActivity().getString(
                    R.string.show_tv_del_1));
        else {
            String topStr = getActivity().getString(
                    R.string.chat_listdialog_top);
            if (list.get(position).getIsTop() == 1) {
                topStr = getActivity().getString(
                        R.string.chat_listdialog_top_cancal);
            }
            String deleteStr = resources
                    .getString(R.string.chat_listdialog_delete);
            menuView.addItems(deleteStr, topStr);
        }
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @return void
     * @Title: showPageView
     * @param:
     * @Description: 显示页面
     */
    private void showPageView(boolean hasData) {
        main_allhistory.setVisibility(hasData ? View.VISIBLE : View.GONE);
        tv_empty_allhistory.setVisibility(!hasData ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 100);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSuccess(final MessageBean messageBean) {
        if (messageBean == null)
            return;
        String sessionId = messageBean.getSessionId();
        if (sessionId.contains("_")) {
            HashMap<String, String> maps = new HashMap<String, String>();
            maps.put("sessionId", IMTypeUtil.SessionType.MOBIZ.name());
            handler.sendMessageDelayed(
                    handler.obtainMessage(REQUEST_UPDATE_LIST,
                            maps), 500);
        } else
            handler.sendMessageDelayed(
                    handler.obtainMessage(REQUEST_UPDATE_LIST,
                            sessionId), 500);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list != null && list.size() > 0 && position < list.size()) {
            MessageBean sessionBean = list.get(position);
            String sessionId = sessionBean.getSessionId();
//            if (sessionId.equals(String
//                    .valueOf(IMTypeUtil.SessionType.FRIEND.name()))) {// 新朋友
//                Intent intent = new Intent(getActivity(),
//                        NewFriendsActivity.class);
//                startActivity(intent);
//                return;
//            }
            ChatUtil.gotoChatRoom(getActivity(), sessionBean);
        }
    }
}
