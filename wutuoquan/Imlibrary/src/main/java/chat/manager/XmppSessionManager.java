package chat.manager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.imlibrary.R;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.user.UserInfoHelp;
import chat.common.util.TextUtils;
import chat.common.util.output.ShowUtil;
import chat.listener.MXStanzaListener;
import chat.service.MessageInfoReceiver;
import chat.session.bean.MessageBean;
import chat.session.bind.MessageRemindBean;
import chat.session.bind.MessageRemindUtils;
import chat.session.enums.SessionTypeEnum;
import chat.session.extension.ClientReceiptExtension;
import chat.session.extension.MochatExtension;
import chat.session.util.ChatUtil;

/**
 * Xmpp业务逻辑管理类。 发送消息、监听xmpp连接状态、处理回执信息
 */
public class XmppSessionManager implements ConnectionListener {
    private static final String TAG_XMPP = "XmppSessionManager";
    /**
     * 设置超时时间
     */
    private static final long REPLAY_TIME = 13 * 1000;
    private static XmppSessionManager instance = null;
    private final int POOL_SIZE = 10;
    private XmppServerManager xmppServerManager;
    private XMPPTCPConnection xmpptcpConnection;
    private Context context;
    private MediaPlayer mMediaplayer;
    private ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
    private ArrayList<XmppServerManager.OnMessageSendListener> msgListeners;
    private ChatManager chatManager = null;
    private MultiUserChatManager multChatManager = null;
    /**
     * 为xmpp添加连接监听器
     *
     * @param connection
     */
    private MXStanzaListener mxStanzaListener = null;
    // 处理超时消息队列handler
    private Handler handler;
    private MXPingListener mxPingListener = null;
    private Runnable vibrationRunnable = new Runnable() {
        @Override
        public void run() {
            Vibrator vib = (Vibrator) context
                    .getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(500);
        }
    };
    private Runnable playVoiceRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mMediaplayer != null)
                    mMediaplayer.release();
                mMediaplayer = MediaPlayer.create(context, R.raw.ms_get);
                mMediaplayer.setLooping(false);
                mMediaplayer.start();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            mMediaplayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mMediaplayer.release();
                }
            });
        }
    };

    private XmppSessionManager() {
        Log.d("weyko",
                "this..--------------------------------code=" + this.hashCode());
        this.context = IMClient.getInstance().getContext();
        if (msgListeners == null)
            msgListeners = new ArrayList<XmppServerManager.OnMessageSendListener>();
        handler = IMClient.getInstance().getTimeoutHandler();
    }

    public static synchronized XmppSessionManager getInstance() {

        if (instance == null) {
            instance = new XmppSessionManager();
        }
        return instance;
    }

    public void setXmppServerManager(XmppServerManager xmppServerManager) {
        this.xmppServerManager = xmppServerManager;
        // xmpptcpConnection = xmppServerManager.getXmppConnection();
    }

    public void Exit() {
        if (handler != null) {
            handler.removeCallbacks(playVoiceRunnable);
            handler.removeCallbacks(vibrationRunnable);
        }
        if (pool != null) {
            pool.shutdownNow();
        }
        if (mMediaplayer != null)
            mMediaplayer.release();
        mMediaplayer = null;
        pool = null;
        chatManager = null;
        multChatManager = null;
        ChatNotifyManager.getInstance().release();
    }

    public void clearListener() {
        if (xmpptcpConnection != null && mxStanzaListener != null) {
            xmpptcpConnection.removeAsyncStanzaListener(mxStanzaListener);
        }
        if (xmpptcpConnection != null && mxPingListener != null) {
            xmpptcpConnection.removeAsyncStanzaListener(mxPingListener);
        }
    }

    /**
     * 初始化连接监听器
     */
    public void initializeConnection(XMPPTCPConnection xmpptcpConnection) {
        if (xmpptcpConnection != null) {
            this.xmpptcpConnection = xmpptcpConnection;
            this.xmpptcpConnection.addConnectionListener(this);
            addListener(xmpptcpConnection);
            chatManager = ChatManager.getInstanceFor(xmpptcpConnection);
            multChatManager = MultiUserChatManager
                    .getInstanceFor(xmpptcpConnection);
        }
    }

    public void addListener(XMPPConnection connection) {
        // just need Messages
        StanzaFilter filterMessage = new StanzaTypeFilter(Message.class);
        if (mxStanzaListener == null) {
            mxStanzaListener = new MXStanzaListener(context, instance, handler);
        }
        connection.setPacketReplyTimeout(REPLAY_TIME);
        connection.addAsyncStanzaListener(mxStanzaListener, filterMessage);
        addPingListener(connection);
    }

    private void addPingListener(XMPPConnection connection) {
        // just need iq
        StanzaFilter filterMessage = new StanzaTypeFilter(IQ.class);
        if (mxPingListener == null) {
            mxPingListener = new MXPingListener();
        }
        connection.addAsyncStanzaListener(mxPingListener, filterMessage);
    }

    public void setPingResponseTime() {
        if (xmppServerManager != null) {
            xmppServerManager.setPingResponseTime();
        }
    }

    /**
     * 添加消息监听
     *
     * @param onMessageSendListener
     */
    public void addOnMessageSendListener(
            XmppServerManager.OnMessageSendListener onMessageSendListener) {
        if (msgListeners != null
                && !msgListeners.contains(onMessageSendListener))
            msgListeners.add(onMessageSendListener);
    }

    public void removeOnMessageSendListener(
            XmppServerManager.OnMessageSendListener onMessageSendListener) {
        if (msgListeners != null
                && msgListeners.contains(onMessageSendListener))
            msgListeners.remove(onMessageSendListener);
    }

    @Override
    public void reconnectionSuccessful() {
        // TODO Auto-generated method stub
        ShowUtil.log(TAG_XMPP, "reconnectionSuccessful");
        xmppServerManager.connectChanged(true);
    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        // TODO Auto-generated method stub
        ShowUtil.log(TAG_XMPP, "reconnectionFailed");
        xmppServerManager.connectChanged(false);
    }

    @Override
    public void reconnectingIn(int arg0) {
        ShowUtil.log(TAG_XMPP, "reconnectingIn");
        xmppServerManager.connectChanged(false);
    }

    @Override
    public void connectionClosedOnError(Exception arg0) {
        String error = TextUtils.getString(arg0.getMessage());
        Log.d("weyko", "connectionClosedOnError--------------->" + error);
        if (error.contains("conflict") && context != null) {// 异地登陆
            ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
                    MessageInfoReceiver.EVENT_LOGINOUT, "", "");
        } else {
            xmppServerManager.connectChanged(false);
        }
    }

    @Override
    public void connectionClosed() {
        ShowUtil.log(TAG_XMPP, "connectionClosed");
        xmppServerManager.connectChanged(false);
    }

    @Override
    public void connected(XMPPConnection arg0) {
        // TODO Auto-generated method stub
        ShowUtil.log(TAG_XMPP, "connected");
        xmppServerManager.connectChanged(true);
    }

    @Override
    public void authenticated(XMPPConnection arg0, boolean arg1) {
        sendBroadcastConnectSate(MessageInfoReceiver.EVENT_CONNECTION_CHANGED);
    }

    /**
     * 广播连接状态
     */
    public void sendBroadcastConnectSate(int stateType) {
        Intent itent = new Intent();
        itent.setAction(MessageInfoReceiver.ACTION);
        itent.putExtra("type", stateType);
        itent.putExtra("msg", "connect_info");
        context.sendBroadcast(itent);
    }

    /**
     * 监听发送状态对应更新
     *
     * @param diraction  发送方向，0：发送，1：接受
     * @param sendStatus 发送状态
     * @param isReaded   是否已接受
     */
    public synchronized void notifyMessageStateChange(String sessionId, String shopId, String msg_code,long time,
                                                      int diraction, final int sendStatus, final int isReaded) {
        boolean isUpdate = false;
        if (diraction == 0) {
            isUpdate = ChatMessageManager.getInstance().updateState(sessionId, shopId, msg_code,
                    sendStatus, isReaded);
            if (isUpdate)
                ChatSessionManager.getInstance().updateSessionStatus(msg_code,
                        isReaded, sendStatus);

        } else {
            isUpdate = ChatMessageManager.getInstance().updateState(sessionId, shopId, msg_code,
                    1, isReaded);
            if (isUpdate)
                ChatSessionManager.getInstance().updateSessionStatus(msg_code,
                        isReaded, 1);
        }
        Intent itent_getacc = new Intent();
        itent_getacc.setAction(MessageInfoReceiver.ACTION);
        itent_getacc.putExtra("type",
                MessageInfoReceiver.EVENT_UPDATE_CHAT);
        itent_getacc.putExtra("msgCode", msg_code);
        itent_getacc.putExtra("time", time);
        itent_getacc.putExtra("sessionId", sessionId);
        itent_getacc.putExtra("sendStatus", sendStatus);
        context.sendBroadcast(itent_getacc);
    }

    /**
     * 发送请求、回执消息
     *
     * @param message
     * @param toID
     * @param type    TODO
     */
    public void sendReceiveMsg(final Message message, final String toID,
                               final Type type) {
        if (pool == null)
            pool = Executors.newFixedThreadPool(POOL_SIZE);
        pool.execute(new Runnable() {
            public void run() {
                String chatInfo;
                if (Type.groupchat.equals(type)) {
                    chatInfo = URLConfig.getServerName();
                } else {
                    if (toID == null)
                        return;
                    chatInfo = toID.indexOf("@") != -1 || URLConfig.IM_SERVERNAME.equals(toID) ? toID : toID
                            + URLConfig.IM_SERVERNAME;
                }
                try {
                    Chat chat = ChatManager.getInstanceFor(xmpptcpConnection)
                            .createChat(chatInfo);
                    chat.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 魔聊消息发送
     *
     * @param userId  发送给的用户
     * @param content 消息内容
     */
    public void sendMessage(final String userId, final String content,
                            final MessageBean messageBean) {
        ShowUtil.log(TAG_XMPP, "userId----->" + userId);// 1011479586
        if (pool == null)
            pool = Executors.newFixedThreadPool(POOL_SIZE);
        pool.execute(new Runnable() {
            public void run() {
                Message message = new Message();
                boolean isGroup = false;
                if (messageBean.getSessionType() == SessionTypeEnum.GROUPCHAT) {
                    isGroup = true;
                }
                message.setType(isGroup ? Type.groupchat : Type.chat);
                String domain = isGroup ? "@conference."
                        + URLConfig.getServerName() : URLConfig.IM_SERVERNAME;
                String chatInfo = userId + domain;
                if (isGroup)
                    groupChat(content, messageBean, message, isGroup, chatInfo);
                else
                    singleChat(content, messageBean, message, isGroup, chatInfo);
            }

            private void singleChat(final String content,
                                    final MessageBean messageBean, Message message,
                                    boolean isGroup, String chatInfo) {
                Chat chat = null;
                if (chatManager == null) {
                    notifyMessageStateChange(messageBean.getSessionId(), "", messageBean.getMsgCode(),messageBean.getMsgTime(), 0, -1, 0);
                    return;
                }
                chat = chatManager.createChat(chatInfo);
                messageBean.setMsgCode(messageBean.getMsgCode());
                try {
                    message.setStanzaId(messageBean.getMsgCode());
                    message.setBody(content);
                    MochatExtension mochatExtension = new MochatExtension(
                            MochatExtension.delivery);
                    mochatExtension.setValue(MochatExtension.ty, messageBean.getSessionType().name().toLowerCase());
                    mochatExtension.setValue(MochatExtension.subtype,
                            String.valueOf(messageBean.getMsgType().getValue()));
                    ShowUtil.log(TAG_XMPP, "sendMessage------------>"
                            + messageBean.getMsgCode());
                    message.addExtension(mochatExtension);
                    chat.sendMessage(message);
                    ShowUtil.log(TAG_XMPP, "send chat meassge success!");
                    if (mxStanzaListener != null) {
                        mxStanzaListener.addMessageTimeOut(messageBean.getSessionId(), "", messageBean
                                .getMsgCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowUtil.log(
                            TAG_XMPP,
                            "send chat meassge failed! the reason:"
                                    + e.getMessage());
                }
            }

            private void groupChat(final String content,
                                   final MessageBean messageBean, Message message,
                                   boolean isGroup, String chatInfo) {
                MultiUserChat chat = null;
                if (multChatManager == null) {
                    notifyMessageStateChange(messageBean.getSessionId(), "", messageBean.getMsgCode(),messageBean.getMsgTime(), 0, -1, 0);
                    return;
                }
                chat = multChatManager.getMultiUserChat(chatInfo);
                messageBean.setMsgCode(messageBean.getMsgCode());
                try {
                    message.setStanzaId(messageBean.getMsgCode());
                    message.setBody(content);
                    MochatExtension mochatExtension = new MochatExtension(
                            MochatExtension.delivery);
                    mochatExtension.setValue(MochatExtension.ty,
                            isGroup ? Type.groupchat.name() : Type.chat.name());
                    mochatExtension.setValue(MochatExtension.subtype,
                            String.valueOf(messageBean.getMsgType().getValue()));
                    ShowUtil.log(TAG_XMPP, "sendMessage------------>"
                            + messageBean.getMsgCode());
                    message.addExtension(mochatExtension);
                    chat.sendMessage(message);
                    ShowUtil.log(TAG_XMPP, "send chat meassge success!");
                    if (mxStanzaListener != null) {
                        mxStanzaListener.addMessageTimeOut(messageBean.getSessionId(), "", messageBean
                                .getMsgCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowUtil.log(
                            TAG_XMPP,
                            "send chat meassge failed! the reason:"
                                    + e.getMessage());
                }
            }
        });
    }

    /**
     * 获取请求、回执消息
     *
     * @param msg_code
     * @param fromID
     * @param toID
     * @param ty       其它特殊类型，比如销毁等
     * @return
     */
    public Message getReceivedMessage(String msg_code, String fromID,
                                      String toID, String ty, boolean isReback, boolean isOffline) {
        if (isOffline)
            return null;
        Message receivedMessage = new Message();
        receivedMessage.setStanzaId(msg_code);
        receivedMessage.setTo(toID);
        receivedMessage.setBody(msg_code);
        receivedMessage.setFrom(fromID);
        if (isReback) {
            ClientReceiptExtension clientReceiptExtension = new ClientReceiptExtension(
                    ClientReceiptExtension.clientReceipt);
            receivedMessage.addExtension(clientReceiptExtension);
        } else {
            MochatExtension rpExtension = new MochatExtension(
                    MochatExtension.delivery);
            rpExtension.setValue("id", msg_code);
            rpExtension.setValue(MochatExtension.from, fromID);
            if (null != ty && ty.length() > 0)
                rpExtension.setValue("ty", ty);
            receivedMessage.addExtension(rpExtension);
        }
        return receivedMessage;
    }

    /**
     * 发送已收听回执
     *
     * @param messageBean
     */
    public void sendListened(MessageBean messageBean) {
        if (messageBean.getSessionType() != SessionTypeEnum.NORMAL)
            return;
        ChatMessageManager.getInstance().updateMessageListenStatus(messageBean.getSessionId(), "", messageBean.getMsgCode());
        Intent itent_listened = new Intent();
        itent_listened.setAction(MessageInfoReceiver.ACTION);
        itent_listened.putExtra("type", MessageInfoReceiver.EVENT_UPDATE_READ_NUMBERS);
        context.sendBroadcast(itent_listened);
    }

    /**
     * 震动
     */
    public void playNewMessageVibration() {
        System.out.println("-------playNewMessageVibration-----");
        handler.removeCallbacks(vibrationRunnable);
        handler.postDelayed(vibrationRunnable, 500);
    }

    /**
     * 播放新消息提示音
     */
    public void playNewMessageVoice() {
        System.out.println("-------playNewMessageVoice-----");
        handler.removeCallbacks(playVoiceRunnable);
        handler.postDelayed(playVoiceRunnable, 500);
    }

    /**
     * 有新消息时，发送通知
     */
    public void NotifyForNewMessage(MessageBean messageBean, long offlineTime) {
        if (messageBean == null || messageBean.getAttachment() == null)
            return;
        boolean isOffline = offlineTime > 0;//是否离线
        long insertMessage = ChatUtil.getInstance().insertMessage(messageBean);
        if (insertMessage > 0) {// 只有插入成功之后或者本地已存在才做操作
            boolean isAppRunningBackground = IMClient.isAppRunningBackground;// 判断app当前是否为后台运行
            boolean isChatPage = IMClient.isChatPage;// 判断app当前是否为聊天页（含聊天列表）
            if (isAppRunningBackground
                    || (!isAppRunningBackground && !isChatPage)) {// 当不在聊天页面时，播放新消息提示音
                // if (!ChatActivity.isChatting)
                MessageRemindBean.MessageRemindData data = MessageRemindUtils
                        .getMessageRemind(context);
                boolean isMsgNeedNotify = true;
                if (SessionTypeEnum.SGROUP == messageBean.getSessionType()) {//群系统消息不需要提醒
                    isMsgNeedNotify = false;
                }
                if (MessageRemindUtils.isMessageRemind(data) && isMsgNeedNotify) {// 是否提醒
                    boolean isForbid = ChatSessionManager.getInstance()
                            .isForbid(messageBean.getSessionId());
                    if (!isForbid) {// 如果聊天对象被设置为免打扰
                        if (MessageRemindUtils.isSoundRemindOpen(data))// 声音是否打开
                            playNewMessageVoice();
                        if (MessageRemindUtils.isVibrationRemindOpen(data))// 震动是否打开
                            playNewMessageVibration();
                        String userName = "";
                        if (messageBean.getSession() != null) {
                            if (messageBean.getImUserBean() != null) {
                                userName = messageBean.getImUserBean().getName();
                            }
                            String notifyMsg = messageBean.getSession();
                            if (userName.length() > 0) {
                                notifyMsg = userName + ": " + notifyMsg;
                            }
                            ChatNotifyManager.getInstance().sendNotifyForNewMessage(context,notifyMsg);// 发送通知栏消息
                        }
                    }
                }

            }
            boolean isMsgNull = true;
            if (msgListeners != null) {
                for (XmppServerManager.OnMessageSendListener listener : msgListeners) {
                    if (listener != null) {
                        listener.onSuccess(messageBean);
                        isMsgNull = false;
                    }
                }
            }
            if (isMsgNull) {
                Intent itent = new Intent();
                itent.setAction(MessageInfoReceiver.ACTION);
                itent.putExtra("type", MessageInfoReceiver.EVENT_UPDATE_READ_NUMBERS);
                context.sendBroadcast(itent);
            }
            /**修改发送receive方式，当插入消息成功之后才发送，避免消息丢失 */
            doReceiveAfterInsertMsg(messageBean, isOffline);
            if (isOffline) {//记录处理离线消息的时间，避免异常操作导致离线消息获取中断,以便下次继续获取剩余的离线消息
                UserInfoHelp.getInstance().setOfflineTime(String.valueOf(offlineTime));
                ChatOfflineManager.getInstance().removeMsg(messageBean.getMsgCode());
            }
        } else if (insertMessage == 0) {//本地已存在也要发送
            doReceiveAfterInsertMsg(messageBean, isOffline);
            if(isOffline)
                ChatOfflineManager.getInstance().removeMsg(messageBean.getMsgCode());
        }

    }

    /**
     * @return void
     * @Title: doReceiveAfterInsertMsg
     * @param: 发送receive 到服务器
     * @Description:
     */
    public void doReceiveAfterInsertMsg(MessageBean messageBean, boolean isOffline) {
        if (messageBean == null)
            return;
        SessionTypeEnum sessionType = messageBean.getSessionType();
        String ty = "";
        if (SessionTypeEnum.RICH == sessionType) {
            ty = MochatExtension.rich;
        } else {
            ty = MochatExtension.normal;
        }
        Message receivedMessage = getReceivedMessage(messageBean.getMsgCode(),
                messageBean.getTo(), messageBean.getFrom(),
                ty, true, isOffline);
        if (receivedMessage != null)
            sendReceiveMsg(receivedMessage, messageBean.getFrom(), Type.groupchat);
    }

    /**
     * 销毁播放器
     */
    public void releaseMediaPlayer() {
        if (mMediaplayer != null)
            mMediaplayer.release();
    }

    /**
     * 移除所有超时队列
     */
    public void removeAllTimeOutRunnable() {
        if (mxStanzaListener != null) {
            mxStanzaListener.removeAllTimeOutRunnable();
        }
    }

    public MXStanzaListener getMXStanzaListener() {
        return mxStanzaListener;
    }

    public void relese() {
        if (mxStanzaListener != null) {
            mxStanzaListener.relese();
        }
    }

    private class MXPingListener implements StanzaListener {
        @Override
        public void processPacket(Stanza arg0) throws NotConnectedException {
            // TODO Auto-generated method stub
            if (arg0 != null && !arg0.hasExtension("bind")) {
                setPingResponseTime();
            }
        }
    }
}
