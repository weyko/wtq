package chat.session.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: MsgBody
 * @Description: 消息基础类
 */
public class MsgBaseBody extends IMUserBody implements ImAttachment {
	private static final long serialVersionUID = 10002223L;
	/** 消息发起者 */
	private String from;
	/** 消息接受者 */
	private String to;
	/** 基本消息类型 */
	private int msgTy;
	/** 消息时间 */
	private long msgTs;
	/** 会话内容 */
	private String session = "";
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public int getMsgTy() {
		return msgTy;
	}

	public void setMsgTy(int msgTy) {
		this.msgTy = msgTy;
	}

	public long getMsgTs() {
		return msgTs;
	}

	public void setMsgTs(long msgTs) {
		this.msgTs = msgTs;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
	/**
	 * @Title: paseBody
	 * @param:
	 * @Description: 封装消息体
	 * @return void
	 */
	public void paseBody(JSONObject obj) {
		obj.put("from", from);
		obj.put("to", to);
		obj.put("msgTy", msgTy);
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
