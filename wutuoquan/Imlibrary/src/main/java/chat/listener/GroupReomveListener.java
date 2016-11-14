package chat.listener;

/**
 * @author 作者:ztai
 * @version 创建时间：2015年3月30日 下午2:43:49 类说明
 */
public abstract class GroupReomveListener implements GroupChangeListener {
	public void onInvitationReceived(String paramString1, String paramString2,
			String paramString3, String paramString4) {
	}

	public void onApplicationReceived(String paramString1, String paramString2,
			String paramString3, String paramString4) {
	}

	public void onApplicationAccept(String paramString1, String paramString2,
			String paramString3) {
	}

	public void onApplicationDeclined(String paramString1, String paramString2,
			String paramString3, String paramString4) {
	}

	public void onInvitationAccpted(String paramString1, String paramString2,
			String paramString3) {
	}

	public void onInvitationDeclined(String paramString1, String paramString2,
			String paramString3) {
	}
}
