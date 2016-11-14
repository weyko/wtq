package chat.session.observe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 作为消息观察者注解
 * Created  by: weyko on 2016/10/12.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Subcriber {
    String tag() default  "default_tag";
    ThreadMode mode() default ThreadMode.MAIN;
}
