package chat.session.group.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chat.common.util.string.PingYinUtil;
import chat.volley.WBaseBean;

/**
 * @ClassName: ChatGroupInfoResultBean
 * @Description: 群组信息详情
 */
public class ChatGroupInfoResultBean extends WBaseBean implements Serializable {
	private static final long serialVersionUID = 55555L;
	private List<ChatGroupInfoData> data;

	public List<ChatGroupInfoData> getData() {
		return data;
	}

	public void setData(List<ChatGroupInfoData> data) {
		this.data = data;
	}

	public static class ChatGroupInfoData extends ChatGroupData implements
			Serializable {
		private static final long serialVersionUID = 55556L;
		private List<MembersData> memberList;
		private int roomNameUpdated;
		private String nickName;//我在群里的昵称
		
		public int getRoomNameUpdated() {
			return roomNameUpdated;
		}

		public void setRoomNameUpdated(int roomNameUpdated) {
			this.roomNameUpdated = roomNameUpdated;
		}

		public String getNickName() {
			return nickName;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public List<MembersData> getMemberList() {
			if (memberList == null)
				memberList = new ArrayList<MembersData>();
			return memberList;
		}

		public void setMemberList(List<MembersData> memberList) {
			this.memberList = memberList;
		}

		public static class MembersData implements Serializable {
			private static final long serialVersionUID = 55557L;
			/* 加入时间 */
			private String joinDate;
			/** 聊天domain */
			private String mtalkDomain;
			/** 昵称 */
			private String nickName;
			/** 头像 */
			private String photoUrl;
			/** 二维码 */
			private String qrcodeUrl;
			/** 角色 */
			private int role;
			/** 用户Id */
			private long userId;
			private String pingyin;

			public String getJoinDate() {
				return joinDate;
			}

			public void setJoinDate(String joinDate) {
				this.joinDate = joinDate;
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

			public int getRole() {
				return role;
			}

			public void setRole(int role) {
				this.role = role;
			}

			public long getUserId() {
				return userId;
			}

			public void setUserId(long userId) {
				this.userId = userId;
			}

			public String getQrcodeUrl() {
				return qrcodeUrl;
			}

			public void setQrcodeUrl(String qrcodeUrl) {
				this.qrcodeUrl = qrcodeUrl;
			}

			public String getPingyin() {
				if (!TextUtils.isEmpty(nickName))
					pingyin = PingYinUtil
							.converterToFirstSpell(nickName.substring(0, 1))
							.substring(0, 1).toUpperCase(Locale.CHINA);
				else {
					this.pingyin = "";
				}
				return pingyin;
			}

			public void setPingyin(String pinyin) {
				this.pingyin = pinyin;
			}

		}

	}
}
