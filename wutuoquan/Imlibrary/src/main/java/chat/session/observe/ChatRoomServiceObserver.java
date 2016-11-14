package chat.session.observe;
import java.util.List;

import chat.session.bean.MessageBean;

/**
 * Description: 消息观察者
 * Created  by: weyko
 */
public interface ChatRoomServiceObserver {
    /**观察新消息*/
    void observeReceiveMessage(List<MessageBean> messages, boolean isRegister);
    /**观察消息状态*/
    void observeMsgStatus(MessageBean messages, boolean isRegister);
}
