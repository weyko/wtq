package chat.session.group.bean;

import java.io.Serializable;

/**
 * @ClassName: ChatGroupData
 * @Description: 群组数据
 */
public class ChatGroupData implements Serializable {
	private static final long serialVersionUID = 10001L;
	/* 群Id */
	private long id;
	/** 群名称 */
	private String groupName;
	/** 验证类型 */
	private int verifyType;
	/** 开放类型 */
	private int publicType;
	/** 创建者Id */
	private long creatorId;
	/** 创建者Id */
	private long createBy;
	/** 当前人数 */
	private int nowCnt;
	/** 最大人数 */
	private int maxCnt;
	/* 群头像 */
	private String photoUrl;
	/** 群二维码 */
	private String qrcodeUrl;
	/** 当前用户在该群的角色 */
	private int roleType;
	/** 创建时间 */
	private long createTime;
	private int saveToContacts;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(int verifyType) {
		this.verifyType = verifyType;
	}

	public int getPublicType() {
		return publicType;
	}

	public void setPublicType(int publicType) {
		this.publicType = publicType;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(long createBy) {
		this.createBy = createBy;
	}

	public int getNowCnt() {
		return nowCnt;
	}

	public void setNowCnt(int nowCnt) {
		this.nowCnt = nowCnt;
	}

	public int getMaxCnt() {
		return maxCnt;
	}

	public void setMaxCnt(int maxCnt) {
		this.maxCnt = maxCnt;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getQrcodeUrl() {
		return qrcodeUrl;
	}

	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getSaveToContacts() {
		return saveToContacts;
	}

	public void setSaveToContacts(int saveToContacts) {
		this.saveToContacts = saveToContacts;
	}

	@Override
	public String toString() {
		return "ChatGroupData [id=" + id + ", roomName=" + groupName
				+ ", verifyType=" + verifyType + ", publicType=" + publicType
				+ ", creatorId=" + creatorId + ", createBy=" + createBy
				+ ", nowCnt=" + nowCnt + ", maxCnt=" + maxCnt + ", photoUrl="
				+ photoUrl + ", qrcodeUrl=" + qrcodeUrl + ", roleType="
				+ roleType + ", createTime=" + createTime + ", saveToContacts="
				+ saveToContacts + "]";
	}
 
}
