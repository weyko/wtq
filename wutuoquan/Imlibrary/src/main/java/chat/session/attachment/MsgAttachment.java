package chat.session.attachment;

import java.io.Serializable;

/**
 * Description:
 * Created  by: weyko on 2016/4/5.
 */
public interface MsgAttachment extends Serializable {
    String toJson(boolean var1);
}
