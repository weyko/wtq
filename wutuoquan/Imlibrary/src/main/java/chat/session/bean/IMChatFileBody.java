package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 文件消息发送 TextIMBody
 * 
 * @author weyko 2015年3月28日下午4:35:48
 */
public class IMChatFileBody extends IMChatBaseBody implements Serializable {
	private static final long serialVersionUID = -2583839198191919L;
	private String url;// 链接地址

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	/**
	* @Title: getSaveBody 
	* @param: 
	* @Description:  获取存储的消息体
	* @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("url", url);
		paseBody(obj);
		return obj.toJSONString();
	}
	/**
	* @Title: getSaveBody 
	* @param: 
	* @Description:  获取发送的消息体
	* @return String
	 */
	public String getSendBody() {
		JSONObject obj = new JSONObject();
		obj.put("url", url);
		paseBody(obj);
		return obj.toJSONString();
	}
}
