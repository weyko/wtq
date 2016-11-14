package chat.session.observe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description:
 * Created  by: weyko on 2016/10/12.
 */
public class MessageClient {
    private static final String DESCRIPTOR = MessageClient.class.getSimpleName();
    private String mDesc;
    private static  MessageClient instance;
    private final Map<IMType,ArrayList<Subscription>> subscriberMap;
    private ThreadLocal<Queue<IMType>> localEvents;
    private SubsciberHelper subsciberHelper;
    private MessageClient.EventDispatcher eventDispatcher;
    public MessageClient() {
        this(DESCRIPTOR);
    }

    public MessageClient(String name) {
        this.mDesc=name;
        this.subscriberMap = new ConcurrentHashMap<>();
        this.localEvents=new ThreadLocal(){
            @Override
            protected Queue<IMType> initialValue() {
                return new ConcurrentLinkedQueue<>();
            }
        };
        this.subsciberHelper=new SubsciberHelper(subscriberMap);
        this.eventDispatcher=new MessageClient.EventDispatcher();
    }
    public static MessageClient getInstance(){
        if(instance==null){
            synchronized (MessageClient.class){
                if(instance==null){
                    instance=new MessageClient();
                }
            }
        }
        return instance;
    }
    public void post(Object event){
        this.post(event,"default_tag");
    }
    public void post(Object event, String tag) {
        ((Queue)this.localEvents.get()).offer(new IMType(event.getClass(), tag));
        this.eventDispatcher.dispatchEvents(event);
    }
    public void register(Object subscriber){
        if(subscriber!=null){
            synchronized (this){
                if(subsciberHelper!=null)
                subsciberHelper.findSubsciberMethod(subscriber);
            }
        }
    }
    public void unregister(Object subscriber){
        if(subscriber!=null){
            synchronized (this){
                if(subsciberHelper!=null)
                subsciberHelper.removeSubsciberMethod(subscriber);
            }
        }
    }
    private class EventDispatcher {
        EventHandler mUIThreadEventHandler;
        EventHandler mPostThreadHandler;
        EventHandler mAsyncEventHandler;
        private Map<IMType, List<IMType>> mCacheIMTypes;
        MatchPolicy mMatchPolicy;

        private EventDispatcher() {
            this.mUIThreadEventHandler = new UIEventHandler();
            this.mPostThreadHandler = new DefaultEventHandler();
            this.mAsyncEventHandler = new AsyncEventHandler();
            this.mCacheIMTypes = new ConcurrentHashMap();
            this.mMatchPolicy = new DefaultMatchPolicy();
        }

        void dispatchEvents(Object aEvent) {
            Queue eventsQueue = (Queue)MessageClient.this.localEvents.get();

            while(eventsQueue.size() > 0) {
                this.deliveryEvent((IMType)eventsQueue.poll(), aEvent);
            }

        }

        private void deliveryEvent(IMType type, Object aEvent) {
            Class eventClass = aEvent.getClass();
            List eventTypes = null;
            if(this.mCacheIMTypes.containsKey(eventClass)) {
                eventTypes = (List)this.mCacheIMTypes.get(type);
            } else {
                eventTypes = this.mMatchPolicy.findMatchEventTypes(type, aEvent);
                this.mCacheIMTypes.put(type, eventTypes);
            }

            Iterator var6 = eventTypes.iterator();

            while(var6.hasNext()) {
                IMType eventType = (IMType)var6.next();
                this.handleEvent(eventType, aEvent);
            }

        }

        private void handleEvent(IMType eventType, Object aEvent) {
            List subscriptions = (List)MessageClient.this.subscriberMap.get(eventType);
            if(subscriptions != null) {
                Iterator var5 = subscriptions.iterator();
                while(var5.hasNext()) {
                    Subscription subscription = (Subscription)var5.next();
                    ThreadMode mode = subscription.threadMode;
                    EventHandler eventHandler = this.getEventHandler(mode);
                    eventHandler.handlerEvent(subscription, aEvent);
                }
            }
        }

        private EventHandler getEventHandler(ThreadMode mode) {
            return mode == ThreadMode.ASYNC?this.mAsyncEventHandler:(mode == ThreadMode.POST?this.mPostThreadHandler:this.mUIThreadEventHandler);
        }
    }
}
