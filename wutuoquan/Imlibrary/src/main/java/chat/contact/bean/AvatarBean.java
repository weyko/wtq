package chat.contact.bean;
import chat.volley.WBaseBean;

public class AvatarBean extends WBaseBean {

	private static final long serialVersionUID = -8923708626319346214L;
	private int id;
	/** 图片地址 */
	private String avatarUrl;
	private String seq;
	private int isAvatar;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAvatarUrl() {
		if (avatarUrl != null)
			return  avatarUrl;
		return "";
	}

	public void setAvatarUrl(String avatarUrl) {
		/*if (!TextUtils.isEmpty(avatarUrl) && !avatarUrl.startsWith("http:")) {
			avatarUrl = "http://image." + URLConfig.getMainUrl2() + "/" + avatarUrl;
		}*/
		this.avatarUrl = avatarUrl;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public int getIsAvatar() {
		return isAvatar;
	}

	public void setIsAvatar(int isAvatar) {
		this.isAvatar = isAvatar;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
