package chat.session.group.bean;

import java.io.Serializable;
import java.util.List;

import chat.volley.WBaseBean;

public class ChatGroupListBean extends WBaseBean implements Serializable {
	private static final long serialVersionUID = 3333L;
	private List<ChatGroupData> data;
	public List<ChatGroupData> getData() {
		return data;
	}
	public void setData(List<ChatGroupData> data) {
		this.data = data;
	}
 
}
