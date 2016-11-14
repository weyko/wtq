package net.skjr.wtq.model;

import java.io.Serializable;

/**
 * 对后台请求的包装对象
 */
public class JsonRequest implements Serializable {
    public String code;
//    public String userPID = "1001";
//    public String sign = "";
    public Object data;

    public JsonRequest() {
    }

    public JsonRequest(String code){
        this.code = code;
        this.data = new Object();
    }

    public JsonRequest(String code, Object data) {
        this.code = code;
        this.data = data;
    }

//    public JsonRequest(String code, String userPID, String sign, Object data) {
//        this.code = code;
//        this.userPID = userPID;
//        this.sign = sign;
//        this.data = data;
//    }
}
