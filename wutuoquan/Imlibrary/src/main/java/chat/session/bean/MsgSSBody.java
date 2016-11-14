package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: MsgSSBody
 * @Description:系统推送消息
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年8月31日 下午4:16:10
 *
 */
public class MsgSSBody extends MsgBaseBody {
	private static final long serialVersionUID = 3232237L;
	private int ty;
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	private String field5;
	private String field6;
	private String field7;

	public int getTy() {
		return ty;
	}

	public void setTy(int ty) {
		this.ty = ty;
	}

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

	public String getSaveBody(){
		JSONObject object=new JSONObject();
		object.put("ty", ty);
		object.put("field1", field1);
		object.put("field2", field2);
		object.put("field3", field3);
		object.put("field4", field4);
		object.put("field5", field5);
		object.put("field6", field6);
		object.put("field7", field7);
		return object.toJSONString();
	}
}
