package chat.manager;

import android.text.TextUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.time.packet.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import chat.base.IMClient;
import chat.common.user.UserInfoHelp;
import chat.common.util.output.ShowUtil;
import chat.session.extension.CommonExtension;
import chat.session.offline.EntityTimeManager;
import chat.session.offline.OfflineBean;
import chat.session.offline.OfflineMessageManager;

/**
 * Description: 离线消息管理
 * Created  by: weyko
 */
public class ChatOfflineManager {
    private static ChatOfflineManager instance;
    //请求失败后，最大请求次数
    private final int MAX_REQUEST_COUNT = 2;
    private final char[] zeroArray =
            "0000000000000000000000000000000000000000000000000000000000000000".toCharArray();
    private List<OfflineBean> msgOfflineQueue;
    private OfflineMessageManager offlineMessageManager;
    private XMPPConnection xmppConnection;
    private XmppSessionManager sessionManager;
    //请求失败次数
    private int requestFailCount = 0;

    public ChatOfflineManager() {
        msgOfflineQueue = new ArrayList<OfflineBean>();
    }

    public static ChatOfflineManager getInstance() {
        if (instance == null) {
            instance = new ChatOfflineManager();
        }
        return instance;
    }

    /***
     * 初始化
     */
    public void init(XMPPConnection xmppConnection) {
        if (xmppConnection == null)
            return;
        if (offlineMessageManager == null)
            offlineMessageManager = new OfflineMessageManager(xmppConnection);
    }

    /**
     * 加载离线消息
     *
     * @param xmppConnection
     */
    public void loadOfflineMessages(XMPPConnection xmppConnection, XmppSessionManager sessionManager) {
        this.xmppConnection = xmppConnection;
        this.sessionManager = sessionManager;
        init(xmppConnection);
        if (xmppConnection != null && msgOfflineQueue != null && msgOfflineQueue.size() == 0) {
            String offlineTime = UserInfoHelp.getInstance().getOfflineTime();
            if (!TextUtils.isEmpty(offlineTime)) {//从最近的离线消息开始
                offlineTime = zeroPadString(offlineTime, 15);
            } else {
                offlineTime = null;
            }
            ShowUtil.log("weyko", "offlineTime==============" + offlineTime);
            getDotTimeWithServer();
            doneOfflineMessages(offlineTime);
        }
    }

    /**
     * 获取本地与服务器时间差
     */
    public void getDotTimeWithServer() {
        if(xmppConnection==null)
            return;
        EntityTimeManager entityTimeManager = EntityTimeManager.getInstanceFor(xmppConnection);
        try {
            Time time = entityTimeManager.getTime("sk");
            long sTime = time.getTime().getTime();
            ShowUtil.log("weyko","sTime---------------"+sTime);
            if (sTime > 0)
                IMClient.getInstance().diffTime = sTime - System.currentTimeMillis();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理离线消息
     */
    private void doneOfflineMessages(String msgTime) {
        if (offlineMessageManager == null)
            return;
        List<Message> messages = null;
        try {
            messages = offlineMessageManager.getMessagesByTs(msgTime);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        //解析消息
        if (messages != null && messages.size() > 0) {
            requestFailCount = 0;
            //排序
            Collections.sort(messages, new Comparator<Message>() {
                @Override
                public int compare(Message lhs, Message rhs) {
                    DelayInformation delaylhs = lhs.getExtension(CommonExtension.delay, CommonExtension.namespace_delay);
                    DelayInformation delayrhs = rhs.getExtension(CommonExtension.delay, CommonExtension.namespace_delay);
                    return delaylhs.getStamp().compareTo(delayrhs.getStamp());
                }
            });
            ProcessPacketManager processManager = new ProcessPacketManager(sessionManager);
            for (Message message : messages) {
                processManager.parsPacket(message, true);
            }
        } else {
            if (messages==null||messages.size() == 0) {//删除最近离线时间
                requestFailCount = 0;
                UserInfoHelp.getInstance().setOfflineTime(null);
                notifyOnline();
            } else {//处理请求失败情况，如果请求失败，尝试请求2次，如果还是失败，则直接通知上线
                requestFailCount++;
                if (requestFailCount > MAX_REQUEST_COUNT) {
                    //通知上线
                    notifyOnline();
                } else {
                    doneOfflineMessages(msgTime);
                }
            }
        }
    }

    /**
     * 重置请求失败次数
     */
    public void resetRequstCount() {
        requestFailCount = 0;
    }

    /**
     * 增加离线队列
     */
    public void addOfflineQueue(OfflineBean offlineBean) {
        if (msgOfflineQueue != null && offlineBean != null) {
            if (msgOfflineQueue.size() == 0)
                msgOfflineQueue.add(offlineBean);
            else {//将最大的放到第一个位置
                if (offlineBean.getTime() >= msgOfflineQueue.get(0).getTime()) {
                    msgOfflineQueue.add(0, offlineBean);
                } else {
                    msgOfflineQueue.add(offlineBean);
                }
            }
        }
    }

    /***
     * 移除消息
     */
    public void removeMsg(String msgId) {
        if (msgOfflineQueue != null) {
            if (msgOfflineQueue.size() == 1) {//如果队列里面只有最后一条消息
                String time = String.valueOf(msgOfflineQueue.get(0).getTime());
                if (msgOfflineQueue.get(0).getMsgId().equals(msgId)) {
                    msgOfflineQueue.remove(0);
                    doneOfflineMessages(zeroPadString(time, 15));
                }
            } else {
                Iterator<OfflineBean> iterator = msgOfflineQueue.iterator();
                while (iterator.hasNext()) {
                    OfflineBean next = iterator.next();
                    if (next.getMsgId().equals(msgId))
                        iterator.remove();
                }
            }
        }
    }

    /**
     * 通知上线
     */
    private void notifyOnline() {
        if (xmppConnection == null)
            return;
        Presence presence = new Presence(Presence.Type.available);
        try {
            xmppConnection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private String zeroPadString(String string, int length) {
        if (string == null || string.length() > length) {
            return string;
        }
        StringBuilder buf = new StringBuilder(length);
        buf.append(zeroArray, 0, length - string.length()).append(string);
        return buf.toString();
    }

    /**
     * 销毁
     */
    public void onDestory() {
        if (msgOfflineQueue != null) {
            msgOfflineQueue.clear();
            msgOfflineQueue = null;
        }
    }
}
