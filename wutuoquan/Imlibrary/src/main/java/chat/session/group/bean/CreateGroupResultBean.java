package chat.session.group.bean;

import java.io.Serializable;

import chat.volley.WBaseBean;

/**
 * @ClassName: CreateGroupResultBean
 * @Description: 创建群时返回的实体
 */
public class CreateGroupResultBean extends WBaseBean implements Serializable {
	private static final long serialVersionUID = 10000L;
	private ChatGroupData data;
	public ChatGroupData getData() {
		return data;
	}
	public void setData(ChatGroupData data) {
		this.data = data;
	}

}
