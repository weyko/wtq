package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 视频消息发送 TextIMBody
 * 
 * @author weyko 2015年3月28日下午4:35:48
 */
public class IMChatVideoBody extends IMChatBaseBody implements Serializable {
	private static final long serialVersionUID = -2583839198191919L;
	private String url;// 链接地址
	private String localUrl;
	private String imgUrl;
	private long fileTime;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public long getFileTime() {
		return fileTime;
	}

	public void setFileTime(long fileTime) {
		this.fileTime = fileTime;
	}

	/**
	 * @Title: getSaveBody
	 * @param:
	 * @Description: 获取存储的消息体
	 * @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("url", url);
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
		obj.put("url", url);
		paseBody(obj);
		return obj.toJSONString();
	}
}
