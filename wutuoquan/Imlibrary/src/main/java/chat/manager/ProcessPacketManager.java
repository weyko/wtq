package chat.manager;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.delay.packet.DelayInformation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.user.UserInfoHelp;
import chat.listener.MXStanzaListener;
import chat.service.MessageInfoReceiver;
import chat.session.bean.IMChatTextBody;
import chat.session.bean.IMRichBaseBody;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.bean.MsgSInviteBody;
import chat.session.bean.MsgSSBody;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgReadEnum;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.extension.ClientReceiptExtension;
import chat.session.extension.CommonExtension;
import chat.session.extension.MochatExtension;
import chat.session.extension.ServerReceiptExtension;
import chat.session.group.bean.GroupMemberBaseBean;
import chat.session.group.bean.IMSGroupBaseBean;
import chat.session.group.bean.IMSGroupMembers;
import chat.session.group.bean.IMSGroupUpdate;
import chat.session.offline.OfflineBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;

public class ProcessPacketManager {
    private static ProcessPacketManager instatnce;
    private String domain = URLConfig.getServerName();
    private XmppSessionManager sessionManager;
    private MXStanzaListener mxStanzaListener;
    /**
     * 线程池
     */
    private ExecutorService loadPoor = null;

    public ProcessPacketManager(XmppSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ProcessPacketManager(MXStanzaListener mxStanzaListener,
                                XmppSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.mxStanzaListener = mxStanzaListener;
        if (loadPoor == null) {
            loadPoor = Executors.newFixedThreadPool(10);
        }
    }

    public static ProcessPacketManager getInstance(
            MXStanzaListener mxStanzaListener, XmppSessionManager sessionManager) {
        if (instatnce == null) {
            instatnce = new ProcessPacketManager(mxStanzaListener,
                    sessionManager);
        }
        return instatnce;
    }

    public void parsPacket(final Stanza packet, final boolean isOffline) {
        if (loadPoor == null) {
            loadPoor = Executors.newFixedThreadPool(10);
        }
        loadPoor.submit(new Runnable() {
            @Override
            public void run() {
                doProcessPacket(packet, isOffline);
            }
        });
    }

    public void shotDown() {
        if (loadPoor != null) {
            loadPoor.shutdown();
            loadPoor = null;
        }
    }

    public void doProcessPacket(Stanza packet, boolean isOffline) {
        final Message msg = (Message) packet;
        String packetStr = msg.toXML().toString();
        if ("not-authorized".equals(packetStr)) {
            return;
        }
        Type type = msg.getType();
        if (type.equals(Type.error))
            return;
        String ty = null;
        int subtype = 1;
        long time = 0;
        long offlineTime = -1;
        ExtensionElement serverReceipt = msg
                .getExtension(ServerReceiptExtension.serverReceipt, ServerReceiptExtension.namespace);
        if (serverReceipt != null) {//判断消息是否发送成功
            ExtensionElement commonReceipt = msg
                    .getExtension(CommonExtension.common, CommonExtension.namespace);
            if (commonReceipt instanceof CommonExtension) {//解析消息
                CommonExtension rpExtension = (CommonExtension) commonReceipt;
                String from = rpExtension.getValue(CommonExtension.from);
                if (TextUtils.isEmpty(from)) {
                    from = msg.getFrom();
                }
                if (!TextUtils.isEmpty(from) && from.contains("@")) {
                    from = from.substring(0, from.indexOf("@"));
                }
                // 处理默认类型，一般消息
                try {
                    time = Long.valueOf(rpExtension.getValue("ts"));
                } catch (Exception e) {
                    time = ChatUtil.getInstance().getSendMsgTime();
                }
                int boxType = SessionTypeEnum.valueOf(type.name().toUpperCase()).getValue();
                //处理回执消息的会话ID
                String sessionId = from + "@" + boxType;
                if (isOffline) {//如果是离线消息，则加入离线队列
                    DelayInformation delayInfo = msg.getExtension(CommonExtension.delay, CommonExtension.namespace_delay);
                    OfflineBean offlineBean = new OfflineBean();
                    offlineBean.setMsgId(msg.getStanzaId());
                    offlineTime = delayInfo.getStamp().getTime();
                    offlineBean.setTime(offlineTime);
                    ChatOfflineManager.getInstance().addOfflineQueue(offlineBean);
                }
                doReceiptStatus(sessionId, msg, time);
                if(isOffline){//消息处理完了之后再移除离线队列
                    UserInfoHelp.getInstance().setOfflineTime(String.valueOf(time));
                    ChatOfflineManager.getInstance().removeMsg(msg.getStanzaId());
                }
            }
        } else {
            SessionTypeEnum baseClass = SessionTypeEnum.NORMAL;
            ExtensionElement clientReceipt = msg
                    .getExtension(ClientReceiptExtension.clientReceipt, ClientReceiptExtension.namespace);
            if (clientReceipt != null) {//判断对方是否已经接收到消息
                //暂不处理
            } else {
                ExtensionElement extensionElement = msg
                        .getExtension(MochatExtension.delivery, MochatExtension.namespace);
                if (extensionElement != null) {//正常消息
                    if (extensionElement instanceof MochatExtension) {
                        MochatExtension mochatExtension = (MochatExtension) extensionElement;
                        ty = mochatExtension.getValue("ty");
                        try {
                            subtype = Integer.valueOf(mochatExtension
                                    .getValue(MochatExtension.subtype));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ExtensionElement commonReceipt = msg
                            .getExtension(CommonExtension.common, CommonExtension.namespace);
                    if (commonReceipt instanceof CommonExtension) {
                        CommonExtension rpExtension = (CommonExtension) commonReceipt;
                        // 处理默认类型，一般消息
                        try {
                            time = Long.valueOf(rpExtension.getValue("ts"));
                            if (isOffline) {//如果是离线消息，则加入离线队列
                                DelayInformation delayInfo = msg.getExtension(CommonExtension.delay, CommonExtension.namespace_delay);
                                OfflineBean offlineBean = new OfflineBean();
                                offlineBean.setMsgId(msg.getStanzaId());
                                offlineTime = delayInfo.getStamp().getTime();
                                offlineBean.setTime(offlineTime);
                                ChatOfflineManager.getInstance().addOfflineQueue(offlineBean);
                            }
                        } catch (Exception e) {
                            time = System.currentTimeMillis();
                        }
                        String from = msg.getFrom();
                        boolean isSelf = false;// 是否为自己
                        if (!MochatExtension.sgroup.equals(ty)
                                && type.equals(Type.groupchat)) {
                            if (from.contains("/")) {
                                from = from.substring(from.indexOf("/") + 1);
                                isSelf = from.equals(IMClient
                                        .getInstance().getSSOUserId());
                            }
                        }
                        boolean isNeedToPase = false;
                        if (ty != null) {// 如果为单、系统消息
                            if (ty.equals(MochatExtension.sgroup)) {
                                isNeedToPase = true;
                            } else if (ty.equals(MochatExtension.s)) {
                                isNeedToPase = true;
                            } else if (!isSelf) {
                                isNeedToPase = true;
                            }
                        } else if (!isSelf) {
                            isNeedToPase = true;
                        }
                        if (ty != null) {
                            try {
                                baseClass = SessionTypeEnum.valueOf(ty
                                        .toUpperCase());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (isNeedToPase || isOffline) {
                            MessageBean bean = parseBody(msg, baseClass, type,
                                    subtype, time);
                            if (sessionManager != null && bean != null
                                    && !bean.isSelfRemoveChatRoom)
                                sessionManager.NotifyForNewMessage(bean, offlineTime);
                        }
                    }
                }
            }
        }
    }

    /**
     * @return void
     * @Title: doReceiptStatus
     * @param:
     * @Description: 执行消息状态更新
     */
    private synchronized void doReceiptStatus(String sessionId, final Message msg,
                                              long time) {
        if (mxStanzaListener != null)
            mxStanzaListener.removeMessageTimeOut(sessionId, "", msg.getStanzaId());// 从超时消息队列中移除
        ChatTembMsgManager.getInstance().deleteTembInfo(msg.getStanzaId());
        sessionManager.notifyMessageStateChange(sessionId, "", msg.getStanzaId(), time, MsgDirectionEnum.Out.getValue(), MsgStatusEnum.success.getValue(), MsgReadEnum.readed.getValue());
    }

    /**
     * 根据回执内容解析包装消息体
     *
     * @return
     */

    private MessageBean parseBody(Message message, SessionTypeEnum sessionType,
                                  Type chatType, int subtype, long time) {
        MessageBean bean = new MessageBean();
        ImUserBean imUserBean = new ImUserBean();
        bean.setDirect(MsgDirectionEnum.In.getValue());
        bean.setMsgStatus(MsgStatusEnum.success);
        bean.msgCode = message.getStanzaId();
        bean.setSessionType(sessionType);
        bean.setMsgType(subtype);
        String body = message.getBody();
        String imFrom = message.getFrom();
        String imTo = message.getTo();
        String fromId = "";
        String toId = "";
        if (imTo.length() > 0) {
            toId = imTo.indexOf("@") != -1 ? imTo.substring(0,
                    imTo.indexOf("@")) : imTo;
        }
        if(imFrom==null){
            doSendRecive(bean, fromId, toId);
            return null;
        }
        fromId = imFrom.indexOf("@") != -1 ? imFrom.substring(0,
                imFrom.indexOf("@")) : imFrom;
        if (sessionType == SessionTypeEnum.GROUPCHAT && imFrom.contains("/")) {
            fromId = imFrom.substring(imFrom.indexOf("/") + 1);
        }
        bean.setChatWith(fromId);
        switch (sessionType) {
            case NORMAL:
            case GROUPCHAT:
                ChatUtil.paseBodyForChat(body, bean, subtype,
                        fromId, toId);
                bean.setMsgType(subtype);
                imUserBean = null;
                break;
            case FOLLOW:
                IMChatTextBody txtBody = JSON.parseObject(body,
                        IMChatTextBody.class);
                bean.setFrom(fromId);
                bean.setTo(toId);
                bean.setAttachment(txtBody);
                bean.setSession(txtBody.getAttr1());
                bean.setMsgType(subtype);
//                    imUserBean.setIMUserBody(txtBody.getImUserBody());
                break;
            case RICH:
                ChatUtil.paseBodyForRich(body, bean, subtype,
                        fromId, toId);
                bean.setMsgType(subtype);
                IMRichBaseBody richBody = bean.getAttachment();
                imUserBean.setIMUserBody(richBody.getImUserBody());
                break;
            case S:
                ChatUtil.paseBodyForS(body, bean, subtype,
                        toId);
                MsgSInviteBody inviteBody = bean.getAttachment();
                fromId = inviteBody.getFrom();
                bean.setChatWith(fromId);
                inviteBody.setMsgTy(subtype);
                imUserBean.setIMUserBody(inviteBody.getImUserBody());
                inviteBody.setMsgTs(time);
                bean.setMsgType(subtype);
            case SS:
                if (paseBodyOfSS(subtype, time, bean, imUserBean, body, fromId, toId))
                    return null;
                break;
            case SGROUP:
                if (!paseBodyOfSGroup(subtype, time, bean, imUserBean, body, fromId, toId)) {
                    return null;
                }
                break;
            default:
                doSendRecive(bean, fromId, toId);
                return null;
        }
        bean.setSessionType(chatType == Type.groupchat ? SessionTypeEnum.GROUPCHAT
                : SessionTypeEnum.NORMAL);
        if (imUserBean != null) {
            imUserBean.setMxId(fromId);
            imUserBean.setMsgTime(time);
        }
        if (sessionType != SessionTypeEnum.SS)
            bean.setSessionId(bean.getChatWith() + "@" + bean.getSessionType().getValue());
        bean.setDirect(MsgDirectionEnum.In.getValue());
        bean.setMsgTime(time);
        bean.setMsgStatus(MsgStatusEnum.success);
        bean.setImUserBean(imUserBean);
        return bean;
    }

    private boolean paseBodyOfSGroup(int subtype, long time, MessageBean bean, ImUserBean imUserBean, String body, String fromId, String toId) {
        ChatUtil.paseBodyForSGroup(body, bean,
                subtype, toId);
        IMSGroupBaseBean sGroupBody = bean.getAttachment();
        boolean isSelf = IMClient.getInstance().getSSOUserId()
                .equals(String.valueOf(sGroupBody.getExecutorId()));
        switch (subtype) {// 被执行者更新相应的群状态，用于处理当前用户不在群的状况
            case IMTypeUtil.SGroupTy.CREATE:
                if (isSelf) {
                    ChatGroupManager.getInstance().updateGroupName(
                            sGroupBody.getRoomId(),
                            sGroupBody.getRoomName());
                    doSendRecive(bean, fromId, toId);
                    return false;
                }
                IMResourceManager.initGroupInfo(IMClient
                        .getInstance().getContext(), sGroupBody
                        .getRoomId());
                IMSGroupBaseBean baseBeanCreate = sGroupBody;
                bean.setChatWith(baseBeanCreate.getRoomId());
                imUserBean.setAvatar(baseBeanCreate.getPhotoUrl());
                imUserBean.setName(baseBeanCreate.getRoomName());
                bean.setImUserBean(imUserBean);
                ChatGroupManager.getInstance()
                        .insertGroupForMsg(bean);
                doSendRecive(bean, fromId, toId);
                return false;
            case IMTypeUtil.SGroupTy.ADD_MEMBER:
                IMSGroupMembers groupMembers =bean.getAttachment();
                bean.setChatWith(groupMembers.getRoomId());
                imUserBean.setAvatar(groupMembers.getPhotoUrl());
                imUserBean.setName(groupMembers.getRoomName());
                bean.setImUserBean(imUserBean);
                ChatGroupManager.getInstance().insertGroupForMsg(bean);
                ChatMembersManager.getInstance().insertGroupMembers(
                        groupMembers);
                break;
            case IMTypeUtil.SGroupTy.UPDATE_MYNICKNAME:
                IMSGroupBaseBean baseBean = sGroupBody;
                if (!isSelf)
                    ChatMembersManager.getInstance().updateMyNickName(
                            baseBean.getRoomId(), baseBean.getExecutorId(),
                            baseBean.getExecutorNickName());
                ChatUtil.sendUpdateNotify(
                        // 通知更新当前会话室该群成员的昵称
                        IMClient.getInstance().getContext(),
                        MessageInfoReceiver.EVENT_UPDATE_GROUP_MEMBER_NICKNAME,
                        baseBean.getRoomId() + ","
                                + baseBean.getExecutorId() + ","
                                + baseBean.getExecutorNickName(), baseBean.getRoomId() + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
                doSendRecive(bean, fromId, toId);
                return false;
            case IMTypeUtil.SGroupTy.REMOVE_MEMBER:
                IMSGroupMembers removeMember = bean.getAttachment();
                boolean isDeleteSelf = false;
                if (removeMember != null) {
                    List<GroupMemberBaseBean> members = removeMember.getMemberList();
                    String executorId = sGroupBody.getExecutorId();
                    String roomId = removeMember.getRoomId();
                    if (members != null) {// 遍历移除列表，判断是否为删除自己
                        for (GroupMemberBaseBean member : members) {
                            if (IMClient.getInstance()
                                    .getSSOUserId()
                                    .equals(member.getUserId())) {
                                ChatGroupManager.getInstance()
                                        .setGroupInValid(roomId);
                                ChatMembersManager.getInstance()
                                        .removeMember(roomId,
                                                member.getUserId());
                                isDeleteSelf = true;
                                bean.isSelfRemoveChatRoom = IMClient.getInstance()
                                        .getSSOUserId()
                                        .equals(executorId);
                                ChatUtil.sendUpdateNotify(
                                        IMClient.getInstance().getContext(),
                                        MessageInfoReceiver.EVENT_CLEAR,
                                        roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT, roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
                            }
                        }
                    }
                    if (!isDeleteSelf) {
                        ChatMembersManager.getInstance()
                                .removeMembersForSGroup(roomId, members);
                    }
                }
                break;
            case IMTypeUtil.SGroupTy.DISSOLVE:
                IMSGroupBaseBean dissolve = sGroupBody;
                ChatGroupManager.getInstance().setGroupInValid(
                        dissolve.getRoomId());
                if (isSelf) {
                    ChatMessageManager.getInstance()
                            .destoryChatMessage(
                                    dissolve.getRoomId() + "@"
                                            + IMTypeUtil.BoxType.GROUP_CHAT, "", true);
                    ChatSessionManager.getInstance()
                            .deleteById(
                                    dissolve.getRoomId() + "@"
                                            + IMTypeUtil.BoxType.GROUP_CHAT, "");
                    ChatUtil.sendUpdateNotify(
                            IMClient.getInstance().getContext(),
                            MessageInfoReceiver.EVENT_CLEAR,
                            dissolve.getRoomId() + "@" + IMTypeUtil.BoxType.GROUP_CHAT, dissolve.getRoomId() + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
                    doSendRecive(bean, fromId, toId);
                    return false;
                } else {
                    if (!ChatGroupManager.getInstance().isExitGroup(
                            dissolve.getRoomId())) {
                        bean.setChatWith(dissolve.getRoomId());
                        imUserBean.setAvatar(dissolve.getPhotoUrl());
                        imUserBean.setName(dissolve.getRoomName());
                        bean.setImUserBean(imUserBean);
                        ChatGroupManager.getInstance().insertGroupForMsg(
                                bean);
                    }
                }
                break;
            case IMTypeUtil.SGroupTy.UPDATE_BASE:
                if (sGroupBody != null) {
                    IMSGroupUpdate updateBean = bean.getAttachment();
                    if (updateBean != null && updateBean.getTemplate() == 3) {// 切换群主
                        if (isSelf) {
                            ChatMessageManager.getInstance()
                                    .destoryChatMessage(
                                            updateBean.getRoomId() + "@"
                                                    + IMTypeUtil.BoxType.GROUP_CHAT,
                                            "", true);
                            ChatSessionManager.getInstance().deleteById(
                                    updateBean.getRoomId() + "@"
                                            + IMTypeUtil.BoxType.GROUP_CHAT, "");
                            ChatGroupManager.getInstance().setGroupInValid(
                                    updateBean.getRoomId());
                            ChatUtil.sendUpdateNotify(
                                    IMClient.getInstance().getContext(),
                                    MessageInfoReceiver.EVENT_CLEAR,
                                    updateBean.getRoomId() + "@"
                                            + IMTypeUtil.BoxType.GROUP_CHAT, updateBean.getRoomId() + "@"
                                            + IMTypeUtil.BoxType.GROUP_CHAT);
                            doSendRecive(bean, fromId, toId);
                            return false;
                        } else {// 如果自己是被设置的群主，则本地需要将之前的群主移除
                            ChatMembersManager.getInstance().removeMember(
                                    updateBean.getRoomId(),
                                    String.valueOf(sGroupBody
                                            .getExecutorId()));
                        }

                    }

                }
                break;
            default:
                break;
        }
        bean.setChatWith(fromId);
        bean.setSession(ChatUtil.getSessionForSGroup(
                IMClient.getInstance().getContext(), bean));
        return true;
    }

    private boolean paseBodyOfSS(int subtype, long time, MessageBean bean, ImUserBean imUserBean, String body, String fromId, String toId) {
        MsgSSBody ssBody = JSON.parseObject(body, MsgSSBody.class);
        if (subtype == 2) {// 取消关注
            int fanState = ChatContactManager.getInstance()
                    .getFanstate(fromId);
            String fanstateStr = ssBody.getField7();
            if ("follower".equals(fanstateStr)) {
                fanState = IMTypeUtil.FansStatus.ONLY_PEER_FOLLOW;
            } else if ("following".equals(fanstateStr)) {
                fanState = IMTypeUtil.FansStatus.ONLY_ME_FOLLOW;
            } else if ("friend".equals(fanstateStr)) {
                fanState = IMTypeUtil.FansStatus.FRIEND;
            } else if ("blacklist".equals(fanstateStr)) {
                fanState = IMTypeUtil.FansStatus.BLACKLIST;
            } else if ("none".equals(fanstateStr)) {
                fanState = IMTypeUtil.FansStatus.NONE;
            }
            ChatContactManager.getInstance().updateFanState(fromId,
                    fanState);
            doSendRecive(bean, fromId, toId);
            return true;
        } else {
            bean.setSessionType(SessionTypeEnum.SS);
            bean.setFrom(fromId);
            bean.setTo(toId);
            bean.setMsgTime(time);
            bean.setSession(ssBody.getField1());
            bean.setMsgType(subtype);
            bean.setChatWith(fromId);
            imUserBean.setMxId(ssBody.getField5());
            imUserBean.setAvatar(ssBody.getField4());
            imUserBean.setName(ssBody.getField6());
            bean.setAttachment(ssBody);
        }
        if (subtype == 4) {// 对方关注
            int fanstatus = ChatContactManager.getInstance()
                    .getFanstate(fromId);
            String fanstateStr = ssBody.getField7();
            if ("follower".equals(fanstateStr)) {
                fanstatus = IMTypeUtil.FansStatus.ONLY_PEER_FOLLOW;
            } else if ("following".equals(fanstateStr)) {
                fanstatus = IMTypeUtil.FansStatus.ONLY_ME_FOLLOW;
            } else if ("friend".equals(fanstateStr)) {
                fanstatus = IMTypeUtil.FansStatus.FRIEND;
            } else if ("blacklist".equals(fanstateStr)) {
                fanstatus = IMTypeUtil.FansStatus.BLACKLIST;
            } else if ("none".equals(fanstateStr)) {
                fanstatus = IMTypeUtil.FansStatus.NONE;
            }
            imUserBean.setFansStatus(fanstatus);
            bean.setSessionId(IMTypeUtil.SessionType.FRIEND.name());
            bean.setChatWith(ssBody.getField5());
            bean.setFrom(ssBody.getField5());
            imUserBean.setMxId(ssBody.getField5());
            imUserBean.setAvatar(ssBody.getField4());
            imUserBean.setName(ssBody.getField6());
            try {
                imUserBean
                        .setGender(Integer.valueOf(ssBody.getField3()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imUserBean
                        .setBirthday(Long.valueOf(ssBody.getField2()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @return void
     * @Title: doSendRecive
     * @param:
     * @Description: 发送receive回执
     */
    private void doSendRecive(MessageBean bean, String fromId, String toId) {
        bean.setFrom(fromId);
        bean.setTo(toId);
        XmppSessionManager.getInstance().doReceiveAfterInsertMsg(bean, false);
    }
}
