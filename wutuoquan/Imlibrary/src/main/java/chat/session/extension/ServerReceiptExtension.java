package chat.session.extension;

import org.jivesoftware.smack.packet.DefaultExtensionElement;

/**
 * 处理服务器回执
 */
public class ServerReceiptExtension extends DefaultExtensionElement {
	public static final String namespace = "urn:xmpp:sk";
	public static final String id = "id";
	public static final String ts = "ts";
	public static final String from = "from";//消息来源ID，用于处理回执消息
	public static final String xmlns = "xmlns";//标识服务器域名
	public static final String serverReceipt  = "server_receipt";//服务端接收消息

	public ServerReceiptExtension(String elementName) {
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
