package chat.session.observe;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Description: 异步分发事件
 * Created  by: weyko on 2016/10/14.
 */

public class AsyncEventHandler implements EventHandler {
    private DispatcherThread dispatcherThread=new DispatcherThread(AsyncEventHandler.class.getName());
    private DefaultEventHandler defaultEventHandler=new DefaultEventHandler();
    public AsyncEventHandler() {
        dispatcherThread.start();
    }

    public void handlerEvent(final Subscription subscription, final Object event) {
        dispatcherThread.post(new Runnable() {
            @Override
            public void run() {
                AsyncEventHandler.this.defaultEventHandler.handlerEvent(subscription,event);
            }
        });
    }
    class DispatcherThread extends HandlerThread {
        protected Handler mAsyncHandler;

        public DispatcherThread(String name) {
            super(name);
        }

        public void post(Runnable runnable) {
            if(this.mAsyncHandler == null) {
                throw new NullPointerException("mAsyncHandler == null, please call start() first.");
            } else {
                this.mAsyncHandler.post(runnable);
            }
        }
        public synchronized void start() {
            super.start();
            this.mAsyncHandler = new Handler(this.getLooper());
        }
    }
}
