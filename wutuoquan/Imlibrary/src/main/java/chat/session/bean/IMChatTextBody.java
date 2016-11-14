package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * 文本消息发送（纯文字、表情） TextIMBody
 * 
 * @author weyko 2015年3月28日下午4:35:48
 *
 */
public class IMChatTextBody extends IMChatBaseBody{
	private static final long serialVersionUID = -1583839198191919L;
	/**
	 * @Title: getSaveBody
	 * @param:
	 * @Description: 获取存储的消息体
	 * @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("attr1", getAttr1());
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
		paseBody(obj);
		obj.put("attr1", getAttr1());
		return obj.toJSONString();
	}
}