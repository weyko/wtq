package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName: IMRichDynamicBody
 * @Description: 富消息：动态
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年7月29日 下午12:38:49
 *
 */
public class IMRichDynamicBody extends IMRichBaseBody implements Serializable {
	private static final long serialVersionUID = 1583839198191920L;
	/** 文本内容 */
	private String field1;
	/** 图片路径 */
	private String field2;
	private String field6;

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	/**
	* @Title: getSaveBody 
	* @param: 
	* @Description:  获取存储的消息体
	* @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("field1", field1);
		obj.put("field2", field2);
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
		obj.put("field1", field1);
		obj.put("field2", field2);
		paseBody(obj);
		paseUser(obj);
		return obj.toJSONString();
	}
}
