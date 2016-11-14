package chat.session.bean;

import java.io.Serializable;
import java.util.Map;

import chat.session.enums.AttachStatusEnum;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.group.bean.ChatGroupBean;
import chat.session.model.ChatRoomMessage;
import chat.session.model.ChatRoomMessageExtension;
import chat.session.model.CustomMessageConfig;
import chat.session.model.IMMessage;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public class MessageBean implements ChatRoomMessage, IMMessage, Serializable {
    private static long serialVersionUID = -1949246189525361810L;
    /**
     * 唯一标识ID
     */
    public String msgCode;
    /**
     * 会话ID
     */
    public String sessionId;
    /**
     * 会话类型
     */
    public SessionTypeEnum sessionType;
    /**
     * 消息类型
     */
    public int msgType;
    /**
     * 消息时间
     */
    public long msgTime;
    public boolean isSelfRemoveChatRoom = false;
    public String j;
    /**
     * 发起者ID
     */
    private String from;
    /**
     * 接收者ID
     */
    private String to;
    /**
     * 当前聊天对象ID
     */
    private String chatWith;
    /**
     * 消息状态
     */
    private MsgStatusEnum msgStatus;
    /**
     * 是否已读
     */
    private int isRead;
    /**
     * 是否已收听
     */
    private int isListen;
    /**
     * 是否置顶
     */
    private int isTop;
    /**
     * 是否免打扰
     */
    private int isForbid;
    /**
     * 消息方向
     */
    private MsgDirectionEnum direct;
    /**
     * 会话内容
     */
    private String session;
    /**
     * 消息体
     */
    private ImAttachment attachment;
    /**
     * 转换状态
     */
    private AttachStatusEnum attachStatus;
    private int unReads;
    /**
     * 自定义消息配置
     */
    private CustomMessageConfig config;
    /**
     * 用户信息
     */
    private ImUserBean imUserBean;
    /***
     * 群信息
     */
    private ChatGroupBean chatGroupBean;
    /**
     * 备注
     */
    private String remark;
    private String t;
    private ChatRoomMessageExtension u;

    public MessageBean() {
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionTypeEnum getSessionType() {
        return this.sessionType;
    }

    public void setSessionType(SessionTypeEnum sessionType) {
        this.sessionType = sessionType;
    }

    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.fromInt(this.msgType);
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public MsgStatusEnum getMsgStatus() {
        return this.msgStatus;
    }

    public void setMsgStatus(MsgStatusEnum msgStatus) {
        this.msgStatus = msgStatus;
    }

    public MsgDirectionEnum getDirect() {
        return this.direct;
    }

    public void setDirect(int direct) {
        this.direct = MsgDirectionEnum.fromInt(direct);
    }

    public String getSession() {
        return this.session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public long getMsgTime() {
        return this.msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public <T extends ImAttachment> T getAttachment() {
        return (T)attachment;
    }

    public void setAttachment(ImAttachment attachment) {
        this.attachment = attachment;
    }

    public AttachStatusEnum getAttachStatus() {
        return this.attachStatus == null ? AttachStatusEnum.def : this.attachStatus;
    }

    public void setAttachStatus(AttachStatusEnum attachStatus) {
        this.attachStatus = attachStatus;
    }

    public boolean isTheSame(IMMessage msg) {
        if (msg != null) {
            return sessionId != null && sessionId.equals(msg.getSessionId());
        } else {
            return false;
        }
    }

    public CustomMessageConfig getConfig() {
        return this.config;
    }

    public void setConfig(CustomMessageConfig config) {
        this.config = config;
    }

    @Override
    public Map<String, Object> getRemoteExtension() {
        return null;
    }

    @Override
    public void setRemoteExtension(Map<String, Object> var1) {

    }

    @Override
    public Map<String, Object> getLocalExtension() {
        return null;
    }

    @Override
    public void setLocalExtension(Map<String, Object> var1) {

    }

    public String getPushContent() {
        return this.t;
    }

    public void setPushContent(String var1) {
        this.t = var1;
    }

    @Override
    public Map<String, Object> getPushPayload() {
        return null;
    }

    @Override
    public void setPushPayload(Map<String, Object> var1) {

    }

    @Override
    public String getSendBody() {
        return attachment == null ? null : attachment.getSendBody();
    }

    public ChatRoomMessageExtension getChatRoomMessageExtension() {
        return this.u;
    }

    public void setChatRoomMessageExtension(ChatRoomMessageExtension var1) {
        this.u = var1;
    }

    public String getChatWith() {
        return chatWith;
    }

    public void setChatWith(String chatWith) {
        this.chatWith = chatWith;
    }

    public ImUserBean getImUserBean() {
        if(imUserBean==null){
            imUserBean=new ImUserBean();
        }
        return imUserBean;
    }

    public void setImUserBean(ImUserBean imUserBean) {
        this.imUserBean = imUserBean;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int read) {
        isRead = read;
    }

    public int getIsListen() {
        return isListen;
    }

    public void setIsListen(int listen) {
        isListen = listen;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUnReads() {
        return unReads;
    }

    public void setUnReads(int unReads) {
        this.unReads = unReads;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public int getIsForbid() {
        return isForbid;
    }

    public void setIsForbid(int isForbid) {
        this.isForbid = isForbid;
    }

    public ChatGroupBean getChatGroupBean() {
        return chatGroupBean;
    }

    public void setChatGroupBean(ChatGroupBean chatGroupBean) {
        this.chatGroupBean = chatGroupBean;
    }
}
