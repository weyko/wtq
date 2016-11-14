package chat.session.observe;

/**
 * Description: 消息注解回调接口
 * Created  by: weyko on 2016/10/14.
 */

public interface EventHandler {
    void handlerEvent(Subscription subscription, Object event);
}
