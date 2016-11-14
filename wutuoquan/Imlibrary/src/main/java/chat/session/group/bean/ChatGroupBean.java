package chat.session.group.bean;

import java.io.Serializable;

import chat.session.bean.ImUserBean;
import chat.volley.WBaseBean;

/**
 * @ClassName: ChatGroupInfoBean
 * @Description: 群基本信息实体
 */
public class ChatGroupBean extends WBaseBean implements Serializable {
	private static final long serialVersionUID = 10213456L;
	/** 群组群组加入权限 */
	public int permission;
	/** 群组公告 */
	public String declared;
	/** 群组是否置顶 */
	public int isTop;
	/** 群组是否加入 */
	public int isJoined;
	/** 是否通知 */
	public int isNotice;
	/** 是否为通讯录模式 */
	public int isContactMode;
	private ChatGroupData data;

	public ChatGroupBean() {
		data = new ChatGroupData();
	}
	public ChatGroupData getData() {
		return data;
	}

	public void setData(ChatGroupData data) {
		this.data = data;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public String getDeclared() {
		return declared;
	}

	public void setDeclared(String declared) {
		this.declared = declared;
	}
	public int getIsTop() {
		return isTop;
	}

	public void setIsTop(int isTop) {
		this.isTop = isTop;
	}

	public int getIsJoined() {
		return isJoined;
	}

	public void setIsJoined(int isJoined) {
		this.isJoined = isJoined;
	}

	public int getIsNotice() {
		return isNotice;
	}

	public void setIsNotice(int isNotice) {
		this.isNotice = isNotice;
	}

	public int getIsContactMode() {
		return isContactMode;
	}

	public void setIsContactMode(int isContactMode) {
		this.isContactMode = isContactMode;
	}

	public ImUserBean getImUserBean() {
		ImUserBean userBean = new ImUserBean();
		userBean.setAvatar(data.getPhotoUrl());
		userBean.setName(data.getGroupName());
		return userBean;
	}

	@Override
	public String toString() {
		return "ChatGroupBean [permission=" + permission + ", declared="
				+ declared + ", isTop=" + isTop + ", isJoined=" + isJoined
				+ ", isNotice=" + isNotice + ", isContactMode=" + isContactMode
				+ ", data=" + data + "]";
	}

}
