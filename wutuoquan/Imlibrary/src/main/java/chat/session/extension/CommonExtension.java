package chat.session.extension;

import org.jivesoftware.smack.packet.DefaultExtensionElement;

/**
 * 处理消息共同体
 */
public class CommonExtension extends DefaultExtensionElement {
	public static final String namespace = "urn:xmpp:sk";
	public static final String namespace_delay= "urn:xmpp:delay";//离线消息
	public static final String delay= "delay";//离线消息标签
	public static final String id = "id";
	public static final String ty = "ty";
	public static final String from = "from";//消息来源ID，用于处理回执消息
	public static final String xmlns = "xmlns";//标识服务器域名
	public static final String common   = "common";//消息共同体标签
	public CommonExtension(String elementName) {
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
