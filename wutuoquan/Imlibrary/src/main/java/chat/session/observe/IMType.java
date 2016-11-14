package chat.session.observe;

/**
 * Description:
 * Created  by: weyko on 2016/10/12.
 */
public class IMType {
    Class<?> paramClass;
    public String tag;
    public IMType(Class<?> clazz, String tag) {
        this.tag = "default_tag";
        this.paramClass = clazz;
        this.tag = tag;
    }
    public String toString() {
        return "EventType [paramClass=" + this.paramClass.getName() + ", tag=" + this.tag + "]";
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.paramClass == null?0:this.paramClass.hashCode());
        result1 = 31 * result1 + (this.tag == null?0:this.tag.hashCode());
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
            IMType other = (IMType)obj;
            if(this.paramClass == null) {
                if(other.paramClass != null) {
                    return false;
                }
            } else if(!this.paramClass.equals(other.paramClass)) {
                return false;
            }

            if(this.tag == null) {
                if(other.tag != null) {
                    return false;
                }
            } else if(!this.tag.equals(other.tag)) {
                return false;
            }

            return true;
        }
    }
}
