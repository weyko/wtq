package chat.service;

/**
 * 监听消息发送状态
 * 
 * MessageChangeListener
 * 
 * @author Administrator 2015年3月24日下午3:14:13
 *
 */
public interface MessageChangeListener {
	public static final int MESSAGE_SEND_NOT = -1; // 未发送

	public static final int MESSAGE_SEND_ING = 0; // 发送中

	public static final int MESSAGE_SEND_SUC = 1; // 已发送
	public void onMessageStateChange(int msg_state, int msg_id);
}
