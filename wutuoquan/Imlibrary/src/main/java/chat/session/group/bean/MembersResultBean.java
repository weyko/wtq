package chat.session.group.bean;

import java.util.List;

import chat.volley.WBaseBean;

/**
  * @ClassName: MembersResultBean
  * @Description: 获取群成员结果集
 */
public class MembersResultBean extends WBaseBean {
	private static final long serialVersionUID = 222333L;
	private List<ChatGroupMemberBean> data;
	public List<ChatGroupMemberBean> getData() {
		return data;
	}
	public void setData(List<ChatGroupMemberBean> data) {
		this.data = data;
	}
	
}
