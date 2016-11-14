package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: IMRichBaseBody
 * @Description: 富消息继承类
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年7月29日 下午4:55:01
 *
 */
public class IMRichBaseBody extends IMUserBody implements ImAttachment {
	private static final long serialVersionUID = 14567L;
	/** 时间戳 */
	private long ts;
	/** 组名 */
	private String gn;
	/** 消息类型 */
	private int ty;

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public String getGn() {
		return gn;
	}

	public void setGn(String gn) {
		this.gn = gn;
	}

	public int getTy() {
		return ty;
	}

	public void setTy(int ty) {
		this.ty = ty;
	}

	/**
	 * @Title: paseBody
	 * @Description: 包装消息体
	 * @return void
	 */
	public void paseBody(JSONObject obj) {
		obj.put("ts", ts);
		obj.put("gn", gn);
		obj.put("ty", ty);
	}

	/**
	 * @Title: saveToDB
	 * @param:
	 * @Description: 只做消息传递字段
	 * @return void
	 */
	public void paseUser(JSONObject obj) {
		obj.put("avatar", getAvatar());
		obj.put("name", getName());
		obj.put("gender", getGender());
	}

	@Override
	public String getSaveBody() {
		return null;
	}

	@Override
	public String getSendBody() {
		return null;
	}
}