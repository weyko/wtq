package chat.session.group.bean;

import com.alibaba.fastjson.JSONObject;

import chat.session.bean.ImAttachment;

public class IMSGroupBaseBean implements ImAttachment {
	private static final long serialVersionUID = 555566671L;
	private String roomId;
	/**
	 * 0 创建群， 1 xxx 邀请 xxx 加入群组 , 2 = 修改群昵称, 3 = 切换群主 ,4 = 修改群头像,5 =新增管理员列表, 6 {0}
	 * 加入了群组, 7 xxx退出了群组,8 = xxx将xx移除了群组,9=群解散,10 取消管理员
	 */
	private int template;
	private String executorNickName;
	/** 执行者id */
	private String executorId;
	/** 被操作对象id */
	private String executorTo;
	/* 群名称 */
	private String roomName;
	/** 群头像 */
	private String photoUrl;

	public int getTemplate() {
		return template;
	}

	public void setTemplate(int template) {
		this.template = template;
	}

	public String getExecutorNickName() {
		return executorNickName;
	}

	public void setExecutorNickName(String executorNickName) {
		this.executorNickName = executorNickName;
	}

	public String getExecutorId() {
		return executorId;
	}

	public void setExecutorId(String executorId) {
		this.executorId = executorId;
	}

	public String getExecutorTo() {
		return executorTo;
	}

	public void setExecutorTo(String executorTo) {
		this.executorTo = executorTo;
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

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getSaveBody() {
		JSONObject object = new JSONObject();
		object.put("template", template);
		object.put("executorNickName", executorNickName);
		object.put("executorId", executorId);
		object.put("executorTo", executorTo);
		object.put("roomName", roomName);
		return object.toJSONString();
	}
	@Override
	public String getSendBody() {
		return null;
	}
}
