package chat.session.model;
import java.io.Serializable;
import java.util.Map;

import chat.session.bean.ImAttachment;
import chat.session.enums.AttachStatusEnum;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.enums.SessionTypeEnum;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public interface IMMessage extends Serializable {
    boolean isTheSame(IMMessage var1);

    String getSessionId();
    SessionTypeEnum getSessionType();
    MsgTypeEnum getMsgType();

    MsgStatusEnum getMsgStatus();

    void setMsgStatus(MsgStatusEnum var1);

    void setDirect(int var1);

    MsgDirectionEnum getDirect();

    void setSession(String var1);

    String getSession();

    long getMsgTime();

    void setFrom(String var1);
    String getFrom();
    void setTo(String var1);
    String getTo();
    void setChatWith(String var1);

    String getChatWith();

    void setAttachment(ImAttachment attachment);

    <T extends ImAttachment> T getAttachment();

    AttachStatusEnum getAttachStatus();
    CustomMessageConfig getConfig();

    void setConfig(CustomMessageConfig var1);

    Map<String, Object> getRemoteExtension();

    void setRemoteExtension(Map<String, Object> var1);

    Map<String, Object> getLocalExtension();

    void setLocalExtension(Map<String, Object> var1);

    String getPushContent();

    void setPushContent(String var1);

    Map<String, Object> getPushPayload();

    void setPushPayload(Map<String, Object> var1);
    String getSendBody();
}
