package chat.session.observe;

import java.lang.reflect.Method;

/**
 * Description:
 * Created  by: weyko on 2016/10/12.
 */
public class TargetMethod {
    public Method method;
    public Class<?> imType;
    public ThreadMode threadMode;

    public TargetMethod(Method md, Class<?> clazz, ThreadMode mode) {
        this.method = md;
        this.method.setAccessible(true);
        this.imType = clazz;
        this.threadMode = mode;
    }

    public int hashCode() {
        byte result = 1;
        int result1 = 31 * result + (this.imType == null?0:this.imType.hashCode());
        result1 = 31 * result1 + (this.method == null?0:this.method.getName().hashCode());
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
            TargetMethod other = (TargetMethod)obj;
            if(this.imType == null) {
                if(other.imType != null) {
                    return false;
                }
            } else if(!this.imType.equals(other.imType)) {
                return false;
            }

            if(this.method == null) {
                if(other.method != null) {
                    return false;
                }
            } else if(!this.method.getName().equals(other.method.getName())) {
                return false;
            }
            return true;
        }
    }
}
