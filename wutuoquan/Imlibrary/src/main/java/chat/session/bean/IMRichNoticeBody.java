package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName: IMRichNoticeBody
 * @Description: 富消息：公告
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年7月29日 下午12:38:49
 *
 */
public class IMRichNoticeBody extends IMRichBaseBody implements Serializable {
	private static final long serialVersionUID = 1583839198191921L;
	/** 主题 */
	private String field1;
	/** 内容 */
	private String field2;
	/** 公告时间 */
	private String field3;
	/** 详情地址 */
	private String field4;

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

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	/**
	 * @Title: getSaveBody
	 * @param:
	 * @Description: 获取存储的消息体
	 * @return String
	 */
	public String getSaveBody() {
		JSONObject obj = new JSONObject();
		obj.put("field1", field1);
		obj.put("field2", field2);
		obj.put("field3", field3);
		obj.put("field4", field4);
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
		obj.put("field3", field3);
		obj.put("field4", field4);
		paseBody(obj);
		paseUser(obj);
		return obj.toJSONString();
	}

}
