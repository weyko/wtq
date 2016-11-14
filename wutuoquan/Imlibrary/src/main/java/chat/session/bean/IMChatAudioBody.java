package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 语音消息发送 TextIMBody
 * 
 * @author weyko 2015年3月28日下午4:35:48
 *
 */
public class IMChatAudioBody extends IMChatBaseBody implements Serializable {
	private static final long serialVersionUID = -2583839198191919L;
	private String attr1;// 语音网络地址
	private int attr2;// 时长
	private String localUrl;// 语音本地地址

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}
	public int getAttr2() {
		return attr2;
	}

	public void setAttr2(int attr2) {
		this.attr2 = attr2;
	}

	/**
	* @Title: getSaveBody 
	* @param: 
	* @Description:  获取存储的消息体
	* @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("attr1", attr1);
		obj.put("attr2", attr2);
		obj.put("localUrl", localUrl);
		paseBody(obj);
		return obj.toJSONString();
	}
	/**
	* @Title: getSendBody 
	* @param: 
	* @Description: 获取发送的消息体
	* @return String
	 */
	public String getSendBody() {
		JSONObject obj = new JSONObject();
		obj.put("attr1", attr1);
		obj.put("attr2", attr2);
		paseBody(obj);
		return obj.toJSONString();
	}
}
