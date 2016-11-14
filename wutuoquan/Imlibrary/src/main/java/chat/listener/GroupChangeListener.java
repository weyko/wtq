package chat.listener;
/**
* @author 作者:ztai
* @version 创建时间：2015年3月30日 下午2:43:00
* 类说明
*/

public abstract	interface  GroupChangeListener {
	 public abstract void onInvitationReceived(String paramString1, String paramString2, String paramString3, String paramString4);

	  public abstract void onApplicationReceived(String paramString1, String paramString2, String paramString3, String paramString4);

	  public abstract void onApplicationAccept(String paramString1, String paramString2, String paramString3);

	  public abstract void onApplicationDeclined(String paramString1, String paramString2, String paramString3, String paramString4);

	  public abstract void onInvitationAccpted(String paramString1, String paramString2, String paramString3);

	  public abstract void onInvitationDeclined(String paramString1, String paramString2, String paramString3);

	  public abstract void onUserRemoved(String paramString1, String paramString2);

	  public abstract void onGroupDestroy(String paramString1, String paramString2);
}
