package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 位置消息发送
 *TextIMBody
 * @author weyko
 *2015年3月28日下午4:35:48
 *
 */
public class IMChatLocationBody extends IMChatBaseBody implements Serializable {
	private static final long serialVersionUID = -2583839198191919L;
	private double x;//纬度
	private double y;//经度
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	/**
	* @Title: getSaveBody 
	* @param: 
	* @Description:  获取存储的消息体
	* @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("x", x);
		obj.put("y", y);
		obj.put("attr1",getAttr1());
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
		obj.put("x", x);
		obj.put("y", y);
		obj.put("attr1",getAttr1());
		paseBody(obj);
		return obj.toJSONString();
	}
}
