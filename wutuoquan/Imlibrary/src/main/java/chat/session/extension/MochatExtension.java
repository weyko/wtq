package chat.session.extension;

import org.jivesoftware.smack.packet.DefaultExtensionElement;

public class MochatExtension extends DefaultExtensionElement {
	public static final String delivery = "sk";//
	public static final String namespace = "urn:xmpp:sk";
	public static final String id = "id";
	public static final String from = "from";//消息来源ID，用于处理回执消息
	public static final String acked = "acked";//消息已发送属性
	public static final String ty = "ty";//消息父类型
	public static final String subtype = "subtype";//消息子类型
	public static final String sgroup   = "sgroup";//群系统消息
	public static final String normal = "normal";// 默认，聊天类型
	public static final String rich = "rich";// 富消息(1 : 商品,2: 优惠劵,3: 动态)
	public static final String groupchat = "groupchat";// 群聊
	public static final String s = "s";//单统消息类型
	public MochatExtension(String elementName) {
		super(elementName, namespace);
	}

	public String toXML() {
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append("<").append(getElementName())
				.append(" xmlns=\"").append(getNamespace()).append("\" ");
		for (String s : getNames()) {
			localStringBuilder.append(s).append("=\"").append(getValue(s))
					.append("\" ");
		}
		localStringBuilder.append("/>");
		return localStringBuilder.toString();
	}

}
