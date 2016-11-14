package chat.session.group.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: IMSGroupAddAdmins
 * @Description: 新增管理员列表  subtype=2
 */
public class IMSGroupAdmins extends IMSGroupBaseBean implements Serializable {
	private static final long serialVersionUID = 2237872222331L;
	private String roomId;
	private List<GroupMemberBaseBean> adminList;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}


	public List<GroupMemberBaseBean> getAdminList() {
		return adminList;
	}

	public void setAdminList(List<GroupMemberBaseBean> adminList) {
		this.adminList = adminList;
	}

	public String getSaveBody() {
		JSONObject object = new JSONObject();
		object.put("roomId", roomId);
		JSONArray jsonArray = new JSONArray();
		for (GroupMemberBaseBean bean : adminList) {
			JSONObject item = new JSONObject();
			item.put("userId", bean.getUserId());
			jsonArray.add(item);
		}
		object.put("adminList", jsonArray);
		return object.toJSONString();
	}

}
