package chat.session;

import com.imlibrary.R;

import org.jivesoftware.smack.packet.Message;

import java.io.File;

import chat.base.IMClient;
import chat.common.util.TextUtils;
import chat.media.BitmapDecoder;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.IMChatBaseBody;
import chat.session.bean.IMChatGifBody;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.IMChatLocationBody;
import chat.session.bean.IMChatTextBody;
import chat.session.bean.IMChatVideoBody;
import chat.session.bean.ImAttachment;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.group.bean.IMSGroupBaseBean;
import chat.session.model.CustomMessageConfig;
import chat.session.util.ChatUtil;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public class MessageBuilder {
    public MessageBuilder() {
    }

    /**
     * 创建文本消息
     * @param toId
     * @param sessionType
     * @param content
     * @return
     */
    public static MessageBean createTextMessage(String toId, SessionTypeEnum sessionType, String content) {
        MessageBean txtBean;
        (txtBean = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.text.getValue();
        txtBean.setSession(content);
        IMChatTextBody txtBody;
        (txtBody=new IMChatTextBody()).setAttr1(content);
        setUserInfo(txtBody);
        txtBean.setAttachment(txtBody);
        return txtBean;
    }

    /**
     * 创建GIF表情消息
     * @param toId
     * @param sessionType
     * @param gifName
     * @return
     */
    public static MessageBean createGifMessage(String toId, SessionTypeEnum sessionType, String gifName) {
        MessageBean gifBean;
        (gifBean = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.gif.getValue();
        gifBean.setSession(IMClient.getInstance().getContext().getString(R.string.gif));
        IMChatGifBody txtBody;
        if (!gifName.endsWith(".gif")) {
            gifName += ".gif";
        }
        (txtBody=new IMChatGifBody()).setAttr1(gifName);
        setUserInfo(txtBody);
        gifBean.setAttachment(txtBody);
        return gifBean;
    }

    /**
     * 创建图片消息
     * @param toId
     * @param sessionType
     * @param var2
     * @return
     */
    public static MessageBean createImageMessage(String toId, SessionTypeEnum sessionType, File var2) {
        return createImageMessage(toId, sessionType, var2, (String)null);
    }

    /**
     * 创建图片消息
     * @param toId
     * @param sessionType
     * @param file
     * @param url
     * @return
     */
    public static MessageBean createImageMessage(String toId, SessionTypeEnum sessionType, File file, String url) {
        MessageBean imgBean;
        (imgBean = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.image.getValue();
        IMChatImageBody imageBody;
        (imageBody = new IMChatImageBody()).setLocalUrl(file.getPath());
        imageBody.setAttr1(url);
        imageBody.setTs(file.length());
        int[] var4 = BitmapDecoder.decodeBound(file);
        imageBody.setWidth(var4[0]);
        imageBody.setHeight(var4[1]);
        setUserInfo(imageBody);
        imgBean.setAttachment(imageBody);
        imgBean.setSession(IMClient.getInstance().getContext().getString(R.string.picture));
        return imgBean;
    }

    /**
     * 创建位置信息
     * @param toId
     * @param sessionType
     * @param latitude
     * @param longitude
     * @param locationAddress
     * @return
     */
    public static MessageBean createLocationMessage(String toId, SessionTypeEnum sessionType, double latitude,
                                                    double longitude, String locationAddress) {
        MessageBean locationBean;
        (locationBean = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.location.getValue();
        IMChatLocationBody locationBody;
        (locationBody = new IMChatLocationBody()).setX(latitude);
        locationBody.setY(longitude);
        locationBody.setAttr1(locationAddress);
        locationBean.setAttachment(locationBody);
        locationBean.setSession(IMClient.getInstance().getContext().getString(R.string.location_recv));
        return locationBean;
    }
    /**
     * 创建音频消息
     * @param toId
     * @param sessionType
     * @param timeLen
     * @param filePath
     * @return
     */
    public static MessageBean createAudioMessage(String toId, SessionTypeEnum sessionType, int timeLen, String filePath) {
        MessageBean audioBean;
        (audioBean = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.audio.getValue();
        IMChatAudioBody audioBody;
        (audioBody = new IMChatAudioBody()).setLocalUrl(filePath);
        audioBody.setAttr2(timeLen);
        audioBean.setAttachment(audioBody);
        audioBean.setSession(IMClient.getInstance().getContext().getString(R.string.voice));
        return audioBean;
    }

    /**
     * 发送视频消息
     * @param toId
     * @param sessionType
     * @param filePath
     * @param url
     * @return
     */
    public static MessageBean createVideoMessage(String toId, SessionTypeEnum sessionType, String filePath, String url) {
        MessageBean videoBean;
        (videoBean = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.video.getValue();
        IMChatVideoBody videoBody;
        (videoBody = new IMChatVideoBody()).setUrl(url);
        videoBody.setLocalUrl(filePath);
        videoBean.setSession(IMClient.getInstance().getContext().getString(R.string.video));
        videoBean.setAttachment(videoBody);
        return videoBean;
    }

    public static MessageBean createTipMessage(String toId, SessionTypeEnum sessionType, String session) {
        MessageBean var3;
        (var3 = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.tip.getValue();
        var3.setSession(session);
        return var3;
    }
    public static MessageBean createTipMessage(String roomId, String roomName) {
        ImUserBean imUserBean = new ImUserBean();
        imUserBean.setMxId(roomId);
        MessageBean tipMessage = MessageBuilder.createTipMessage(roomId, SessionTypeEnum.GROUPCHAT, TextUtils.getStringFormat(IMClient.getInstance().getContext(),
                R.string.chat_group_created_hint, roomName));
        tipMessage.setImUserBean(imUserBean);
        tipMessage.setFrom(roomId);
        tipMessage.setChatWith(roomId);
        IMSGroupBaseBean baseBean = new IMSGroupBaseBean();
        baseBean.setRoomName(roomName);
        baseBean.setRoomId(roomId);
        tipMessage.setAttachment(baseBean);
        return tipMessage;
    }

    public static MessageBean createCustomMessage(String toId, SessionTypeEnum sessionType, ImAttachment var2) {
        return createCustomMessage(toId, sessionType, (String)null, var2, (CustomMessageConfig)null);
    }

    public static MessageBean createCustomMessage(String toId, SessionTypeEnum sessionType, String var2, ImAttachment var3) {
        return createCustomMessage(toId, sessionType, var2, var3, (CustomMessageConfig)null);
    }

    public static MessageBean createCustomMessage(String toId, SessionTypeEnum sessionType, String var2, ImAttachment var3, CustomMessageConfig var4) {
        MessageBean var5;
        (var5 = initSendMessage(toId, sessionType)).msgType = MsgTypeEnum.custom.getValue();
        var5.setSession(var2);
        var5.setAttachment(var3);
        var5.setConfig(var4);
        return var5;
    }

    public static MessageBean createEmptyMessage(String toId, SessionTypeEnum sessionType, long time) {
        MessageBean var4;
        (var4 = new MessageBean()).sessionId = toId;
        var4.sessionType = sessionType;
        var4.msgTime = time;
        return var4;
    }

    private static MessageBean initSendMessage(String toId, SessionTypeEnum sessionType) {
        MessageBean imBean;
        Message message=new Message();
        (imBean = new MessageBean()).msgCode =message.getStanzaId();
        imBean.setTo(toId);
        imBean.setChatWith(toId);
        imBean.setFrom(IMClient.getInstance().getSSOUserId());
        imBean.setDirect(MsgDirectionEnum.Out.getValue());
        imBean.setMsgStatus(MsgStatusEnum.sending);
        imBean.sessionType = sessionType;
        imBean.sessionId=toId+"@"+sessionType.getValue();
        imBean.msgTime = ChatUtil.getInstance().getSendMsgTime();
        return imBean;
    }
    private static void setUserInfo(IMChatBaseBody imBaseBody){
//        imBaseBody.setName(UserInfoHelp.getInstance().getName());
//        imBaseBody.setAvatar(UserInfoHelp.getInstance().getAvarar());
    }
}
