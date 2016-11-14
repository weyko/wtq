package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: MsgSInviteBody
 * @Description: 单系统邀请
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年8月31日 下午4:16:10
 *
 */
public class MsgSInviteBody extends MsgBaseBody implements ImAttachment {
	private static final long serialVersionUID = 5232231L;
	private String avatar;
	private String roomId;
	private String roomName;
	private String roomIcon;
	private String nickName;
	private String userId;
	private String mtalkDomain;
	private String key;
 
	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getRoomId() {
		return roomId;
	}


	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}


	public String getRoomName() {
		return roomName;
	}


	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomIcon() {
		return roomIcon;
	}


	public void setRoomIcon(String roomIcon) {
		this.roomIcon = roomIcon;
	}


	public String getNickName() {
		return nickName;
	}


	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getMtalkDomain() {
		return mtalkDomain;
	}


	public void setMtalkDomain(String mtalkDomain) {
		this.mtalkDomain = mtalkDomain;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getSaveBody() {
		JSONObject obj=new JSONObject();
		obj.put("avatar", avatar);
		obj.put("roomId", roomId);
		obj.put("roomName", roomName);
		obj.put("roomIcon", roomIcon);
		obj.put("nickName", nickName);
		obj.put("userId", userId);
		obj.put("mtalkDomain", mtalkDomain);
		obj.put("key", key);
		return obj.toJSONString();
	}

	@Override
	public String getSendBody() {
		return null;
	}

}
