package chat.session.attachment;

import org.json.JSONObject;

import chat.session.enums.NotificationType;

/**
 * Description:
 * Created  by: weyko on 2016/5/31.
 */
public abstract class NotificationAttachment implements MsgAttachment {
    private NotificationType type;

    public NotificationAttachment() {
    }

    public NotificationType getType() {
        return this.type;
    }

    public void setType(NotificationType var1) {
        this.type = var1;
    }

    public String toJson(boolean var1) {
        return null;
    }

    public final void fromJson(String var1) {
    }

    public abstract void parse(JSONObject var1);
}