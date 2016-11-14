package net.skjr.wtq.core.utils.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Gson库的帮助类
 */
public class GsonUtils {
    /**
     * 转换Json对象到对象（非泛型，非List）
     * @param jsonObject
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T get(JSONObject jsonObject, Class<T> cls){
        return get(jsonObject.toString(), cls);
    }

    /**
     * 转换Json字符串到对象（非泛型，非List）
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T get(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {

        }
        return t;
    }

    /**
     * 转换Json字符串到对象（非List）
     * 当转换的对象为复杂泛型类型时，可以使用此接口：
     * Type type = new TypeToken<ReturnData<Business>>(){}.getType();
     * @param jsonString
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T get(String jsonString, Type type){
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, type);
        } catch (Exception e) {

        }
        return t;
    }

    /**
     * 转换JSONObject到对象（非List）
     * 当转换的对象为复杂泛型类型时，可以使用此接口：
     * Type type = new TypeToken<ReturnData<Business>>(){}.getType();
     * @param jsonObject
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T get(JSONObject jsonObject, Type type){
        return get(jsonObject.toString(), type);
    }

    /**
     * 转换Json字符串到列表(Json字符串内容必须是数组）
     * @param jsonString
     * @param type  Type type = new TypeToken<ArrayList<T>>() {}.getType();
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> getList(String jsonString, Type type) {

        ArrayList<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, type);
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * 转换Json字符串到列表(Json字符串内容必须是数组）
     * @param jsonString
     * @return
     */
    public static List getList(String jsonString){
        TypeToken<ArrayList> typeToken = new TypeToken<ArrayList>(){};
        List list = new ArrayList();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, typeToken.getType());
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * 转换JSONArray到列表
     * @param array
     * @return
     */
    public static List getList(JSONArray array){
        return getList(array.toString());
    }

    /**
     * 对象转换为Json字符串
     * @param obj
     * @return
     */
    public static String toJson(Object obj){
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
}
