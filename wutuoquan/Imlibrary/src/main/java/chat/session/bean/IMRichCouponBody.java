package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @ClassName: IMRichCouponBody
 * @Description: 富消息： 优惠券
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年7月29日 上午11:24:09
 *
 */
public class IMRichCouponBody extends IMRichBaseBody implements Serializable {
	private static final long serialVersionUID = -1583839198191919L;
	/** 优惠券ID */
	private String field1;
	/** 优惠券名称 */
	private String field2;
	/** 优惠券描述 */
	private String field3;
	/** 优惠券价格 */
	private String field4;
	/** 优惠券图标url */
	private String field5;
	/** 优惠券注释 */
	private String field6;
	/** 店铺头像 */
	private String field7;
	/** 店铺名称 */
	private String field8;
	/** 子类型: 1、商品 ；2、现金券；3折扣券；4、兑换券；5、魔线礼品券；6、魔商礼品券 */
	private String field9;
	/** 价格/折扣 */
	private String field10;

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

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

	public String getField10() {
		return field10;
	}

	public void setField10(String field10) {
		this.field10 = field10;
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
		obj.put("field5", field5);
		obj.put("field6", field6);
		obj.put("field7", field7);
		obj.put("field8", field8);
		obj.put("field9", field9);
		obj.put("field10", field10);
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
		obj.put("field5", field5);
		obj.put("field6", field6);
		obj.put("field7", field7);
		obj.put("field8", field8);
		obj.put("field9", field9);
		obj.put("field10", field10);
		paseBody(obj);
		paseUser(obj);
		return obj.toJSONString();
	}
}