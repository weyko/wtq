package chat.listener;

import android.content.Context;
import android.os.Handler;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import chat.common.util.TextUtils;
import chat.common.util.output.ShowUtil;
import chat.manager.ChatMessageManager;
import chat.manager.ChatTembMsgManager;
import chat.manager.ProcessPacketManager;
import chat.manager.XmppServerManager;
import chat.manager.XmppSessionManager;

/**
 * openfire回调接口 . 用于处理发送、回执的状态更新，消息封装，超时队列添加移除。 MXStanzaListener
 */
public class MXStanzaListener implements StanzaListener {
    private static final long DESTOTY_DELAY = 15000;// 销毁延时
    private static final String TAG_XMPP = "XmppSessionManager";
    private XmppSessionManager sessionManager;
    private Context context;
    private ProcessPacketManager processPacketManager;
    private Map<String, Runnable> messageTimeOutArray = null;
    private String SPLIT_STR = "[WTQ]";

    public class MXRunnable implements Runnable {
        private String msgCode;
        private String sessionId;
        private String shopId;
        public MXRunnable(String sessionId,String shopId, String msgCode) {
            this.sessionId = sessionId;
            this.shopId=shopId;
            this.msgCode = msgCode;
        }

        @Override
        public void run() {
            shopId= TextUtils.getString(shopId);
            sessionManager.notifyMessageStateChange(sessionId,shopId, msgCode,-1, 0, -1, 0);
            messageTimeOutArray.remove(sessionId + SPLIT_STR + msgCode+SPLIT_STR+shopId);
        }

    }

    public ProcessPacketManager getProcessPacketManager() {
        return processPacketManager;
    }

    // 处理消息队列handler
    private Handler handler;

    public MXStanzaListener(Context context, XmppSessionManager sessionManager,
                            Handler handler) {
        this.sessionManager = sessionManager;
        this.context = context;
        this.handler = handler;
        // 监听消息发送回调事件
        processPacketManager = ProcessPacketManager.getInstance(this,
                sessionManager);
        messageTimeOutArray = Collections
                .synchronizedMap(new HashMap<String, Runnable>());// 超时处理队列(同步处理)
    }

    /**
     * 添加消息监听
     *
     * @param messageListener
     */
    private XmppServerManager.OnMessageSendListener onMessageSendListener;

    public void addOnMessageSendListener(
            XmppServerManager.OnMessageSendListener onMessageSendListener) {
        this.onMessageSendListener = onMessageSendListener;
    }

    @Override
    public void processPacket(Stanza packet) {
        processPacketManager.parsPacket(packet,false);
        //设置响应时间，解决ping判断离线出现错误的问题。
        if (sessionManager != null) {
            sessionManager.setPingResponseTime();
        }
    }

    /**
     * 添加消息延时队列
     *
     * @param msgCode
     */
    public void addMessageTimeOut(String sessionId,String shopId, String msgCode) {
        if (sessionId == null || msgCode == null)
            return;
        shopId= TextUtils.getString(shopId);
        if (!messageTimeOutArray.containsKey(sessionId + SPLIT_STR + msgCode+SPLIT_STR+shopId)) {//46161@1[MOXIAN]X2293-15
            MXRunnable mxRunnable = new MXRunnable(sessionId,shopId, msgCode);
            handler.postDelayed(mxRunnable, DESTOTY_DELAY);
            messageTimeOutArray.put(sessionId + SPLIT_STR + msgCode+SPLIT_STR+shopId, mxRunnable);
        }
    }

    /**
     * 移除消息延迟队列
     */
    public void removeMessageTimeOut(String sessionId,String shopId, String msgCode) {
        if (sessionId == null || msgCode == null)
            return;
        shopId= TextUtils.getString(shopId);
        if (messageTimeOutArray != null && handler != null
                && messageTimeOutArray.containsKey(sessionId + SPLIT_STR + msgCode+SPLIT_STR+shopId)) {
            handler.removeCallbacks(messageTimeOutArray.get(sessionId + SPLIT_STR + msgCode+SPLIT_STR+shopId));
            messageTimeOutArray.remove(sessionId + SPLIT_STR + msgCode + SPLIT_STR + shopId);
            ChatTembMsgManager.getInstance().deleteTembInfo(msgCode);
        }
    }

    /**
     * @return boolean
     * @Title: isQueueOn
     * @param:
     * @Description: 是否加入队列
     */
    public boolean isQueueOn(String sessionId,String shopId, String msgCode) {
        if (sessionId == null || msgCode == null)
            return false;
        shopId= TextUtils.getString(shopId);
        if (messageTimeOutArray != null) {
            return !messageTimeOutArray.containsKey(sessionId + SPLIT_STR + msgCode+SPLIT_STR+shopId);
        }
        return false;
    }

    /**
     * 移除所有消息超时监听
     */
    public synchronized void removeAllTimeOutRunnable() {
        ShowUtil.log(TAG_XMPP, "------removeAllTimeOutRunnable");
        if (messageTimeOutArray.size() > 0) {
            Set<String> keys = messageTimeOutArray.keySet();
            for (String key : keys) {
                handler.post(messageTimeOutArray.get(key));
                String[] split = key.split(SPLIT_STR);
                if (split == null || split.length < 3) {
                    continue;
                }
                ChatMessageManager.getInstance().updateMessageSendStatus(split[0], split[2],
                        split[1], -1);
            }
        }
        messageTimeOutArray.clear();
    }

    public void relese() {
        if (processPacketManager != null)
            processPacketManager.shotDown();
    }
}
