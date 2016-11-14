package chat.session.observe;

import java.lang.reflect.InvocationTargetException;

/**
 * Description: 默认分发事件
 * Created  by: weyko on 2016/10/14.
 */
public class DefaultEventHandler implements EventHandler {
    public void handlerEvent(Subscription subscription, Object event) {
        if(subscription!=null&&subscription.subscriber!=null){
            try {
                subscription.targetMethod.invoke(subscription.subscriber,event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
