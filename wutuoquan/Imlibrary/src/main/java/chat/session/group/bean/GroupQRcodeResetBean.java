package chat.session.group.bean;

import java.io.Serializable;

import chat.volley.WBaseBean;

/**
  * @ClassName: GroupQRcodeResetBean
  * @Description:  重置二维码实体
 */
public class GroupQRcodeResetBean extends WBaseBean implements Serializable {
	private static final long serialVersionUID = 5474575471L;
	private String data;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
