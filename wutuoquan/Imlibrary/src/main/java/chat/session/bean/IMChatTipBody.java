package chat.session.bean;
import chat.session.enums.NotificationType;

/**
 * 文本提示类消息   IMChatTipBody
 * @author weyko 2015年3月28日下午4:35:48
 *
 */
public class IMChatTipBody implements ImAttachment{
	private static final long serialVersionUID = -1583839198192919L;
	private NotificationType type;

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	@Override
	public String getSaveBody() {
		return null;
	}

	@Override
	public String getSendBody() {
		return null;
	}
}