package chat.common.util.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: 网络请求工具类
 * Created  by: weyko on 2016/11/8.
 */
public class HttpRequetUtil {
    public HttpRequetUtil() {
    }
    public static JSONObject getJson(HashMap<String,Object> map){
        JSONObject obj=new JSONObject();
        try {
            obj.put("code",map.get("code"));
            map.remove("code");
            JSONObject dataJson=new JSONObject();
            Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, Object> next = it.next();
                dataJson.put(next.getKey(),next.getValue());
            }
            obj.put("data",dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
