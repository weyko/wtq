package chat.session.group.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

import chat.common.util.TextUtils;

/**
 * @ClassName: IMSGroupAddMembers
 * @Description: 新增群成员 subtype=4
 */
public class IMSGroupMembers extends IMSGroupBaseBean implements Serializable {
	private static final long serialVersionUID = 2237772222331L;
	private String roomId;
	private List<GroupMemberBaseBean> memberList;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public List<GroupMemberBaseBean> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<GroupMemberBaseBean> memberList) {
		this.memberList = memberList;
	}

	public String getSaveBody() {
		JSONObject object = new JSONObject();
		object.put("roomId", roomId);
		JSONArray jsonArray = new JSONArray();
		for (GroupMemberBaseBean bean : memberList) {
			JSONObject item = new JSONObject();
			item.put("userId", bean.getUserId());
			item.put("role", bean.getRole());
			item.put("mtalkDomain", TextUtils.getString(bean.getMtalkDomain()));
			jsonArray.add(item);
		}
		object.put("memberList", jsonArray);
		return object.toJSONString();
	}

}
