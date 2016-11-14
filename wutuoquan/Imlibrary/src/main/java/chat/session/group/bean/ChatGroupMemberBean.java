package chat.session.group.bean;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Locale;

import chat.common.util.string.PingYinUtil;
import chat.session.bean.ImUserBean;

/**
 * @ClassName: ChatGroupMemberBean
 * @Description: 群成员实体类
 */
public class ChatGroupMemberBean implements Serializable {
	private static final long serialVersionUID = 1002322321L;
	private long id;
	/** 魔线Id */
	private long userId;
	/** 所属群组 */
	public long roomId;
	/** 加入时间 */
	private long joinDate;
	/** 用户性别 */
	private int sexType;
	/** 城市编码 */
	private int pubCityDict;
	/** 群创建者 */
	public int createBy;
	/** 用户角色 */
	public int roleType;
	/*** 创建时间 */
	private long createTime;
	/** 群域名 */
	private String mtalkDomain;
	/** 是否禁言 */
	public int isban;
	/** 用户的备注 */
	public String remark;
	/** 用户的头像 */
	public String avatar;
	/** 用户的昵称 */
	public String nickName;
	/** 用户信息 */
	public ImUserBean imUserBean;
	/* 分类索引 */
	private String pingyin;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public long getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(long joinDate) {
		this.joinDate = joinDate;
	}

	public int getSexType() {
		return sexType;
	}

	public void setSexType(int sexType) {
		this.sexType = sexType;
	}

	public int getPubCityDict() {
		return pubCityDict;
	}

	public void setPubCityDict(int pubCityDict) {
		this.pubCityDict = pubCityDict;
	}

	public int getCreateBy() {
		return createBy;
	}

	public void setCreateBy(int createBy) {
		this.createBy = createBy;
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

	public String getMtalkDomain() {
		return mtalkDomain;
	}

	public void setMtalkDomain(String mtalkDomain) {
		this.mtalkDomain = mtalkDomain;
	}

	public int getIsban() {
		return isban;
	}

	public void setIsban(int isban) {
		this.isban = isban;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public ImUserBean getImUserBean() {
		return imUserBean;
	}

	public void setImUserBean(ImUserBean imUserBean) {
		this.imUserBean = imUserBean;
	}

	public String getPingyin() {
		if (!TextUtils.isEmpty(nickName))
			pingyin = PingYinUtil
					.converterToFirstSpell(nickName.substring(0, 1))
					.substring(0, 1).toUpperCase(Locale.CHINA);
		else {
			this.pingyin = "#";
		}
		return pingyin;
	}

	public void setPingyin(String pinyin) {
		this.pingyin = pinyin;
	}

}
