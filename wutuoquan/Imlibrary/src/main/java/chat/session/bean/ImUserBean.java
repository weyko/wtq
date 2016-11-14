package chat.session.bean;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.util.Locale;

import chat.common.util.CursorUtil;
import chat.common.util.TextUtils;
import chat.common.util.string.PingYinUtil;
import chat.contact.bean.ContactBean;
import chat.manager.AbstractChatDBManager;

public class ImUserBean extends IMUserBody implements Serializable {
	private static final long serialVersionUID = 12637792L;
	/** 魔线id */
	private String mxId;
	/** 生日 */
	private long birthday;
	/** 出生地 */
	private String mxFullArea = "";
	/** 备注 */
	private String remark = "";
	/** 个性签名 */
	private String sign;
	/** 用户名字母，排序用 */
	private String pingyin = "";
	/** 关注状态 */
	private int fansStatus;
	/** 是否置顶 */
	private int isTop;
	/** 是否免打扰 */
	private int isForbid;
	/** 角色 */
	private int roleType;
	private String mtalkDomain;
	private long msgTime;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPingyin() {
		return pingyin;
	}

	public void setPingyin(String pingyin) {
		if (TextUtils.getString(remark).length() > 0)
			pingyin = PingYinUtil.converterToFirstSpell(remark.substring(0, 1))
					.substring(0, 1).toUpperCase(Locale.CHINA);
		else {
			if (TextUtils.getString(getName()).length() > 0)
				pingyin = PingYinUtil
						.converterToFirstSpell(getName().substring(0, 1))
						.substring(0, 1).toUpperCase(Locale.CHINA);
			else
				pingyin = "#";
		}
		this.pingyin = pingyin;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public String getMxId() {
		return mxId;
	}

	public void setMxId(String mxId) {
		this.mxId = mxId;
	}

	public String getMxFullArea() {
		return mxFullArea;
	}

	public void setMxFullArea(String mxFullArea) {
		this.mxFullArea = mxFullArea;
	}

	public int getFansStatus() {
		return fansStatus;
	}

	public void setFansStatus(int fansStatus) {
		this.fansStatus = fansStatus;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public int getIsTop() {
		return isTop;
	}

	public void setIsTop(int isTop) {
		this.isTop = isTop;
	}

	public int getIsForbid() {
		return isForbid;
	}

	public void setIsForbid(int isForbid) {
		this.isForbid = isForbid;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

	public String getMtalkDomain() {
		return mtalkDomain;
	}

	public void setMtalkDomain(String mtalkDomain) {
		this.mtalkDomain = mtalkDomain;
	}

	public long getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(long msgTime) {
		this.msgTime = msgTime;
	}

	/**
	 * @Title: setIMUserBody
	 * @param:
	 * @Description: 设置聊天的基本用户信息
	 * @return void
	 */
	public void setIMUserBody(IMUserBody userBody) {
		if (userBody != null) {
			setName(userBody.getName());
			setAvatar(userBody.getAvatar());
			setGender(userBody.getGender());
		}
	}

	/**
	 * @Title: buildContentValues
	 * @param:
	 * @Description: 填充数据
	 * @return ContentValues
	 */
	public ContentValues buildContentValues(boolean isSetFansState) {
		ContentValues values = new ContentValues();
		values.put(AbstractChatDBManager.ContactsColumn.W_ID, this.mxId);
		values.put(AbstractChatDBManager.ContactsColumn.AVATAR, getAvatar());
		values.put(AbstractChatDBManager.ContactsColumn.NAME, getName());
		values.put(AbstractChatDBManager.ContactsColumn.SEX, getGender());
		values.put(AbstractChatDBManager.ContactsColumn.BIRTHDAY, getBirthday());
		values.put(AbstractChatDBManager.ContactsColumn.FULL_AREA, getMxFullArea());
		if (!isSetFansState)
			values.put(AbstractChatDBManager.ContactsColumn.FANS_STATUS, getFansStatus());
//		values.put(ContactsColumn.REMARK, this.remark);
		values.put(AbstractChatDBManager.ContactsColumn.SIGN, this.sign);
		return values;
	}

	/**
	 * @Title: buildContentValues
	 * @param:
	 * @Description: 设置数据
	 * @return ContentValues
	 */

	/**
	 * @Title: setValueFromCursor
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return void
	 */
	public void setValueFromCursor(Cursor cursor) {
		setMxId(CursorUtil.getString(cursor, AbstractChatDBManager.ContactsColumn.W_ID));
		setAvatar(CursorUtil.getString(cursor, AbstractChatDBManager.ContactsColumn.AVATAR));
		setName(CursorUtil.getString(cursor, AbstractChatDBManager.ContactsColumn.NAME));
		setGender(CursorUtil.getInt(cursor, AbstractChatDBManager.ContactsColumn.SEX));
		setBirthday(CursorUtil.getLong(cursor, AbstractChatDBManager.ContactsColumn.BIRTHDAY));
		setMxFullArea(CursorUtil.getString(cursor,AbstractChatDBManager. ContactsColumn.FULL_AREA));
		setFansStatus(CursorUtil.getInt(cursor, AbstractChatDBManager.ContactsColumn.FANS_STATUS));
		setRemark(CursorUtil.getString(cursor,AbstractChatDBManager. ContactsColumn.REMARK));
		setSign(CursorUtil.getString(cursor,AbstractChatDBManager. ContactsColumn.SIGN));
		setIsTop(CursorUtil.getInt(cursor, AbstractChatDBManager.ContactsColumn.IS_TOP));
		setIsForbid(CursorUtil.getInt(cursor,AbstractChatDBManager. ContactsColumn.IS_FORBID));
		String name = TextUtils.getString(getName());
		if (name.length() > 0)
			setPingyin(PingYinUtil
					.converterToFirstSpell(getName().substring(0, 1))
					.substring(0, 1).toUpperCase(Locale.CHINA));
		else {
			setPingyin("#");
		}
	}

	public ContactBean getFansBean() {
		ContactBean bean = new ContactBean();
		bean.setCatalog(getPingyin());
		bean.setUserNickname(getName());
		bean.setUserImg(getAvatar());
		try {
			bean.setFriendID(Integer.valueOf(getMxId()));
		} catch (Exception e) {
		}
		return bean;
	}

	@Override
	public String toString() {
		return "ImUserBean [mxId=" + mxId + ", birthday=" + birthday
				+ ", mxFullArea=" + mxFullArea + ", remark=" + remark
				+ ", pingyin=" + pingyin + ", fansStatus=" + fansStatus + "]";
	}
}
