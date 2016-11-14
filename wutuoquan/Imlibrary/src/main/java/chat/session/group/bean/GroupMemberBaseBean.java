package chat.session.group.bean;

import java.io.Serializable;

/**
  * @ClassName: GroupMemberBaseBean
  * @Description: 群成员基础类(由于返回类型不一样，群组信息就不继承基类了)
 */
public class GroupMemberBaseBean implements Serializable {
	private static final long serialVersionUID = -522222332L;
	private String userId;
	private int role;
	private String nickName;
	private String photoUrl;
	private String mtalkDomain;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getMtalkDomain() {
		return mtalkDomain;
	}

	public void setMtalkDomain(String mtalkDomain) {
		this.mtalkDomain = mtalkDomain;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
