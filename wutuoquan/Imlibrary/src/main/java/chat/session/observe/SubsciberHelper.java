package chat.session.observe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Description:
 * Created  by: weyko on 2016/10/12.
 */
public class SubsciberHelper {
    private Map<IMType,ArrayList<Subscription>> subscriberMap;
    public SubsciberHelper(Map<IMType, ArrayList<Subscription>> subscriberMap) {
        this.subscriberMap = subscriberMap;
    }

    public void findSubsciberMethod(Object subscriber) {
        if(subscriberMap==null){
            throw  new NullPointerException(" the subscriberMap is null");
        }else{
            //for --->Class clazz=subscriber.getClass();clazz!=null&&!isSystemCalss(clazz.getName());clazz=clazz.getSuperclass() 可以选择遍历父类
            Class clazz=subscriber.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method:methods){
                Subcriber annotation = method.getAnnotation(Subcriber.class);
                if(annotation!=null){
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if(parameterTypes!=null&&parameterTypes.length>0){
                        Class paramType = this.convertType(parameterTypes[0]);
                        IMType imType=new IMType(paramType,annotation.tag());
                        TargetMethod targetMethod=new TargetMethod(method,paramType,annotation.mode());
                        this.subscibe(imType,targetMethod,subscriber);
                    }
                }
            }
        }
    }

    private void subscibe(IMType imType, TargetMethod targetMethod, Object subscriber) {
        ArrayList<Subscription> subscriptions = subscriberMap.get(imType);
        if(subscriptions==null){
            subscriptions=new ArrayList<Subscription>();
        }
        Subscription subscription=new Subscription(subscriber,targetMethod);
        if(!subscriptions.contains(subscription)){
            subscriptions.add(subscription);
            this.subscriberMap.put(imType,subscriptions);
        }
    }
    /*private boolean isSystemCalss(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.");
    }*/
    private Class<?> convertType(Class<?> eventType) {
        Class returnClass = eventType;
        if(eventType.equals(Boolean.TYPE)) {
            returnClass = Boolean.class;
        } else if(eventType.equals(Integer.TYPE)) {
            returnClass = Integer.class;
        } else if(eventType.equals(Float.TYPE)) {
            returnClass = Float.class;
        } else if(eventType.equals(Double.TYPE)) {
            returnClass = Double.class;
        }

        return returnClass;
    }

    public void removeSubsciberMethod(Object subscriber) {
        Iterator<ArrayList<Subscription>> iterator = this.subscriberMap.values().iterator();
        while(true) {
            ArrayList<Subscription> subscriptions;
            do {
                if(!iterator.hasNext()) {
                    return;
                }
                subscriptions = (ArrayList) iterator.next();
                if(subscriptions != null) {
                    LinkedList foundSubscriptions = new LinkedList();
                    Iterator subIterator = subscriptions.iterator();
                    while(subIterator.hasNext()) {
                        Subscription subscription = (Subscription)subIterator.next();
                        if(subscription.subscriber.equals(subscriber)) {
                            foundSubscriptions.add(subscription);
                        }
                    }
                    subscriptions.removeAll(foundSubscriptions);
                }
            } while(subscriptions != null && subscriptions.size() != 0);
            iterator.remove();
        }
    }
}
