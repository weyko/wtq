package chat.session.observe;

import android.os.Handler;
import android.os.Looper;

/**
 * Description: 主线程分发事件
 * Created  by: weyko on 2016/10/14.
 */
public class UIEventHandler implements EventHandler {
    private Handler mUIHandler=new Handler(Looper.getMainLooper());
    private DefaultEventHandler eventHandler=new DefaultEventHandler();
    public void handlerEvent(final Subscription subscription, final Object event) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                UIEventHandler.this.eventHandler.handlerEvent(subscription,event);
            }
        });
    }
}
