package chat.session.group.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName: IMSGroupUpdate
 * @Description: 群基本属性更新(群昵称， 群图片，群主) subtype=1
 */
public class IMSGroupUpdate extends IMSGroupBaseBean implements Serializable {
	private static final long serialVersionUID = -222222330L;
	private String roomId;
	private String roomName;
	private String photoUrl;
	private String ownerId;

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

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getSaveBody() {
		JSONObject object = new JSONObject();
		object.put("roomId", roomId);
		object.put("roomName", roomName);
		object.put("photoUrl", photoUrl);
		object.put("ownerId", ownerId);
		return object.toJSONString();
	}

}
