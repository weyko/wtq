package chat.login;
import java.io.Serializable;

import chat.volley.WBaseBean;

public class LoginEntity extends WBaseBean {
	private static final long serialVersionUID = -3753922358766045407L;
	private String userName;// 用户名
	private String userAccount;// 账号 手机号或者邮箱
//	private String passWrod;// 密码
	private String avatarUrl;
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	private SSOLoginDataBean data = new SSOLoginDataBean();
	
	public SSOLoginDataBean getData() {
		return data;
	}

	public void setData(SSOLoginDataBean data) {
		if(data!=null){
			this.data=null;
		}
		this.data = data;
	}

	public static class SSOLoginDataBean implements Serializable {
		private static final long serialVersionUID = -182430063548827400L;
		private String userId;
		private String token;
		public String getUserId() {
			if(null==userId||userId.length()==0)
				return "";
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
	}
}
