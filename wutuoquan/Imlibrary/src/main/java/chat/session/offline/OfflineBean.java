package chat.session.offline;

/**
 * Description: 离线消息尸体类
 * Created  by: weyko on 2016/6/30.
 */
public class OfflineBean {
    private String msgId;
    private long time;
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
