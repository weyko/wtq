package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: IMChatBaseBody
 * @Description: 基本消息继承类
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年7月28日 上午11:10:33
 *
 */
public abstract class IMChatBaseBody implements ImAttachment {
	private static final long serialVersionUID = 10003226L;
	/** 消息内容 */
	private String attr1;

	public String getAttr1() {
		return attr1;
	}

	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}

	/**
	 * @Title: paseBody
	 * @param:
	 * @Description: 对象转换成json字符串，需要存储到消息表的字段
	 * @return String
	 */
	public void paseBody(JSONObject obj) {
		obj.put("attr1", getAttr1());
	}
//	/**
//	* @Title: saveToDB
//	* @param:
//	* @Description: 只做消息传递字段
//	* @return void
//	 */
//	public void paseUser(JSONObject obj){
//		obj.put("avatar", getAvatar());
//		obj.put("name", getName());
//		obj.put("gender", getGender());
//	}
//	public  IMUserBody getImUserBody(){
//		IMUserBody userBody=new IMUserBody();
//		userBody.setGender(getGender());
//		userBody.setName(getName());
//		userBody.setAvatar(getAvatar());
//		return userBody;
//	}
	@Override
	public String toString() {
		return "IMChatBaseBody [attr1=" + attr1+"]";
	}
}
