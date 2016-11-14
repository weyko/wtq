package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 图片消息发送 TextIMBody
 * 
 * @author weyko 2015年3月28日下午4:35:48
 */
public class IMChatImageBody extends IMChatBaseBody implements Serializable {
	private static final long serialVersionUID = -2583839198191919L;
	private String attr1;// 链接地址
	private String localUrl;// 本地地址
	private int width;// 宽度
	private int height;// 高度
	private long ts;//时长

	@Override
	public String getAttr1() {
		return attr1;
	}

	@Override
	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	/**
	 * @Title: getSaveBody
	 * @param:
	 * @Description: 获取存储的消息体
	 * @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("attr1", attr1);
		obj.put("localUrl", localUrl);
		obj.put("width", width);
		obj.put("height", height);
		paseBody(obj);
		return obj.toJSONString();
	}

	/**
	 * @Title: getSaveBody
	 * @param:
	 * @Description: 获取发送的消息体
	 * @return String
	 */
	public String getSendBody() {
		JSONObject obj = new JSONObject();
		obj.put("attr1", attr1);
		obj.put("localUrl", localUrl);
		obj.put("width", width);
		obj.put("height", height);
		paseBody(obj);
		return obj.toJSONString();
	}
}
