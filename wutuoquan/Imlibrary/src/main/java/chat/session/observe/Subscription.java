package chat.session.observe;

import java.lang.reflect.Method;

/**
 * Description:
 * Created  by: weyko on 2016/10/12.
 */
public class Subscription {
    public Object subscriber;
    public Method targetMethod;
    public ThreadMode threadMode;

    public Subscription(Object subscriber, TargetMethod targetMethod) {
        this.subscriber = subscriber;
        this.targetMethod = targetMethod.method;
        this.threadMode = targetMethod.threadMode;
    }

    public int hashCode() {
        byte result = 1;
        int result1 = 31 * result + (this.subscriber == null?0:this.subscriber.hashCode());
        result1 = 31 * result1 + (this.targetMethod == null?0:this.targetMethod.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(this.getClass() != obj.getClass()) {
            return false;
        } else {
            Subscription other = (Subscription)obj;
            if(this.subscriber == null) {
                if(other.subscriber != null) {
                    return false;
                }
            } else if(!this.subscriber.equals(other.subscriber)) {
                return false;
            }

            if(this.targetMethod == null) {
                if(other.targetMethod != null) {
                    return false;
                }
            } else if(!this.targetMethod.equals(other.targetMethod)) {
                return false;
            }

            return true;
        }
    }
}
