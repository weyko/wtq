package chat.session.bean;

import java.io.Serializable;

import chat.volley.WBaseBean;

public class StatusBean extends WBaseBean implements Serializable {
	private static final long serialVersionUID = 1231L;
	private String data;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
