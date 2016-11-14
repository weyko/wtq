package chat.session.attachment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description:
 * Created  by: weyko on 2016/5/31.
 */
public class JsonManager {
    public static final JSONObject a(String var0) {
        try {
            return new JSONObject(var0);
        } catch (JSONException var1) {
            return null;
        }
    }

    public static final JSONArray b(String var0) {
        try {
            return new JSONArray(var0);
        } catch (JSONException var1) {
            return null;
        }
    }

    public static final int a(JSONObject var0, String var1) {
        try {
            return var0.getInt(var1);
        } catch (JSONException var2) {
            return 0;
        }
    }

    public static final long b(JSONObject var0, String var1) {
        try {
            return var0.getLong(var1);
        } catch (JSONException var2) {
            return 0L;
        }
    }

    public static final double c(JSONObject var0, String var1) {
        try {
            return var0.getDouble(var1);
        } catch (JSONException var2) {
            return 0.0D;
        }
    }

    public static final String d(JSONObject var0, String var1) {
        try {
            return var0.getString(var1);
        } catch (JSONException var2) {
            return null;
        }
    }

    public static final JSONObject e(JSONObject var0, String var1) {
        try {
            return var0.getJSONObject(var1);
        } catch (JSONException var2) {
            return null;
        }
    }

    public static final JSONArray f(JSONObject var0, String var1) {
        try {
            return var0.getJSONArray(var1);
        } catch (JSONException var2) {
            return null;
        }
    }

    public static final String a(JSONArray var0, int var1) {
        try {
            return var0.getString(var1);
        } catch (JSONException var2) {
            return null;
        }
    }

    public static final void a(JSONObject var0, String var1, String var2) {
        try {
            var0.put(var1, var2);
        } catch (JSONException var3) {
            ;
        }
    }

    public static final void a(JSONObject var0, String var1, int var2) {
        try {
            var0.put(var1, var2);
        } catch (JSONException var3) {
            ;
        }
    }

    public static final void a(JSONObject var0, String var1, long var2) {
        try {
            var0.put(var1, var2);
        } catch (JSONException var4) {
            ;
        }
    }
}
