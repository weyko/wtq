package chat.session.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public class ChatRoomMessageExtension implements Serializable {
    private long roleInfoTimeTag = -1L;
    private String nick;
    private String avatar;
    private Map<String, Object> extension;

    public ChatRoomMessageExtension() {
    }

    public long getRoleInfoTimeTag() {
        return this.roleInfoTimeTag;
    }

    public void setRoleInfoTimeTag(long var1) {
        this.roleInfoTimeTag = var1;
    }

    public String getSenderNick() {
        return this.nick;
    }

    public void setSenderNick(String var1) {
        this.nick = var1;
    }

    public String getSenderAvatar() {
        return this.avatar;
    }

    public void setSenderAvatar(String var1) {
        this.avatar = var1;
    }

    public Map<String, Object> getSenderExtension() {
        return this.extension;
    }

    public void setSenderExtension(Map<String, Object> var1) {
        this.extension = var1;
    }
}