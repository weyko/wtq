package chat.session.provider;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import chat.session.extension.CommonExtension;

/**
 * 消息公用内容提供者
 */
public class CommonExtensionProvider extends ExtensionElementProvider {

	@Override
	public Element parse(XmlPullParser parser, int arg1)
			throws XmlPullParserException, IOException, SmackException {
        String tsValue = parser.getAttributeValue(null, "ts");
        String from = parser.getAttributeValue(null, "from");
        String to = parser.getAttributeValue(null, "to");
        String messageId = parser.getAttributeValue(null, "messageId");
        CommonExtension rpExtension = new CommonExtension(CommonExtension.common);
        rpExtension.setValue("from", from);
        rpExtension.setValue("to", to);
        rpExtension.setValue("messageId", messageId);
        if(tsValue != null && !"".equals(tsValue.trim())){
            rpExtension.setValue("ts", tsValue);
        }
        return rpExtension;

	}

}
