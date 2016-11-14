package chat.session.provider;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import chat.session.extension.ClientReceiptExtension;

/**
 * 客户端回执提供者
 */
public class ClientReceiptExtensionProvider extends ExtensionElementProvider {
	@Override
	public Element parse(XmlPullParser parser, int arg1)
			throws XmlPullParserException, IOException, SmackException {
        ClientReceiptExtension rpExtension = new ClientReceiptExtension(ClientReceiptExtension.clientReceipt);
        return rpExtension;

	}

}
