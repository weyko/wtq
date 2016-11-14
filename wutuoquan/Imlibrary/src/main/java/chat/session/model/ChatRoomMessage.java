package chat.session.model;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public interface ChatRoomMessage extends IMMessage {
    ChatRoomMessageExtension getChatRoomMessageExtension();

    void setChatRoomMessageExtension(ChatRoomMessageExtension var1);
}
