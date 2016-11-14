package chat.volley;

import java.io.Serializable;

/**
 * Bean基类 提取接口共用字段。
 * 此类的编写请按照javaBean规范. 
 */
@SuppressWarnings("serial")
public class WBaseBean implements Serializable {
	
	private String info;//提示信息
	private String msg;//请求状态描述
	private int status;//请求状态
	private String code;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public boolean isResult(){
		return  status==1;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}