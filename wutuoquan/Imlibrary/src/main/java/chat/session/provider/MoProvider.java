package chat.session.provider;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import chat.session.extension.MochatExtension;

/***
 * 消息头部提供者
 */
public class MoProvider extends ExtensionElementProvider {

	@Override
	public Element parse(XmlPullParser parser, int arg1)
			throws XmlPullParserException, IOException, SmackException {
		String id = parser.getAttributeValue(null, "id");
		String ty = parser.getAttributeValue(null, "ty");
		String subtype = parser.getAttributeValue(null, "subtype");
        MochatExtension rpExtension = new MochatExtension(MochatExtension.delivery);
		rpExtension.setValue("id", id);
		rpExtension.setValue("ty", ty);
		rpExtension.setValue("subtype", subtype);
        return rpExtension;
	}
}
