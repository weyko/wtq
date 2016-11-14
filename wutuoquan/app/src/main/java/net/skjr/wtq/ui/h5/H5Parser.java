package net.skjr.wtq.ui.h5;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.common.StringUtils;
import net.skjr.wtq.core.utils.json.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 需要处理的H5动作，由H5Parser解析出来
 * closeType 1:不关闭当前窗口，新开窗口；2：关闭当前窗口，新开窗口；3：在当前窗口打开
 */
public class H5Parser {
    /**
     * 是否是一个H5的互调用
     *
     * @param url
     * @return
     */
    public static boolean isH5Call(String url) {
        final String pre = Url.BASE_BRIDGE_URL + "?";
        if (!url.contains(pre)) {
            return false;
        }

        return true;
    }

    /**
     * 解析URL
     *
     * @param url
     * @return
     */
    public static H5Action parseUrl(String url) {

        final String pre = Url.BASE_BRIDGE_URL + "?";
        Map<String, String> map = getParamsMap(url, pre);
        if (map != null) {
            try {
                return parseH5Call(map);
            } catch (Exception ex) {
                L.e(ex, ex.getLocalizedMessage());
            }
        }

        return null;
    }

    /**
     * H5解析的入口
     *
     * @param map 参数集合
     */
    private static H5Action parseH5Call(Map<String, String> map) {
        if (!map.containsKey("code")) {
            return null;
        }

        String code = map.get("code").toLowerCase();
        return parseCode(code.toLowerCase(), map);
    }

    /**
     * 根据不同code，进行处理
     *
     * @param code
     * @param map
     * @return
     */
    private static H5Action parseCode(String code, Map<String, String> map) {
        //拨打电话
        if (StringUtils.isEquals(code, H5Action.CALLMOBILE)) {

            String dataId = map.get(H5Action.DATA_ID).toLowerCase();
            String data = map.get(H5Action.DATA).toLowerCase();

            String phone = JsonUtils.getString(data, "mobile", "");
            if (!TextUtils.isEmpty(phone)) {

                H5Action action = new H5Action();
                action.dataId = dataId;
                action.code = H5Action.CALLMOBILE;
                action.phone = phone;
                return action;
            }

            return null;
        }

        //PopPage，新页面
        if (StringUtils.isEquals(code, H5Action.POPPAGE)) {
            String dataId = map.get(H5Action.DATA_ID).toLowerCase();
            String data = map.get(H5Action.DATA);

            String url = JsonUtils.getString(data, "url", "");
            String title = "信息";
            title = JsonUtils.getString(data, "title", "信息");
            int type = JsonUtils.getInt(data, "type", 0);
            if (!TextUtils.isEmpty(url)) {
                return parsePopPage(url, type, title);
            }

            return null;
        }

        // closePage，关闭当前页面
        if (StringUtils.isEquals(code, H5Action.CLOSEPAGE)) {
            H5Action action = new H5Action();
            action.code = H5Action.CLOSEPAGE;

            return action;
        }

        //连连支付
        if(StringUtils.isEquals(code, H5Action.PAYMENT)) {
            H5Action action = new H5Action();
            action.code = H5Action.PAYMENT;
            return action;

        }
        if(StringUtils.isEquals(code, H5Action.RECHARGE)) {
            H5Action action = new H5Action();
            action.code = H5Action.RECHARGE;
            return action;

        }

        //ajax
        if (StringUtils.isEquals(code, H5Action.AJAX)) {
            String dataId = map.get(H5Action.DATA_ID).toLowerCase();
            String data = map.get(H5Action.DATA).toLowerCase();

            String url = JsonUtils.getString(data, "url", "");
            String data2 = JsonUtils.getString(data, "data", "");
            if (!TextUtils.isEmpty(url)) {
                H5Action action = new H5Action();
                action.code = H5Action.AJAX;
                action.url = url;
                action.data = data;
                action.dataId = dataId;

                return action;
            }

            return null;
        }

        //gotoAccount，跳转到账户中心
        if (StringUtils.isEquals(code, H5Action.GOTOACCOUNT)) {
            String dataId = map.get(H5Action.DATA_ID).toLowerCase();
            String data = map.get(H5Action.DATA).toLowerCase();

            boolean close = JsonUtils.getBoolean(data, "close", false);
            H5Action action = new H5Action();
            action.code = H5Action.GOTOACCOUNT;
            action.close = close;
            action.dataId = dataId;

            return action;
        }

        //gotoHome，跳转到首页
        if (StringUtils.isEquals(code, H5Action.GOTOHOME)) {
            String dataId = map.get(H5Action.DATA_ID).toLowerCase();
            String data = map.get(H5Action.DATA).toLowerCase();

            boolean close = JsonUtils.getBoolean(data, "close", false);

            H5Action action = new H5Action();
            action.code = H5Action.GOTOHOME;
            action.close = close;
            action.dataId = dataId;

            return action;
        }

        //gotoLogin，跳转到登录页
        if (StringUtils.isEquals(code, H5Action.GOTOLOGIN)) {
            String dataId = map.get(H5Action.DATA_ID).toLowerCase();
            String data = map.get(H5Action.DATA).toLowerCase();

            boolean close = JsonUtils.getBoolean(data, "close", false);
            H5Action action = new H5Action();
            action.code = H5Action.GOTOLOGIN;
            action.close = close;
            action.dataId = dataId;

            return action;
        }

        //gotoRegister，跳转到注册页
        if (StringUtils.isEquals(code, H5Action.GOTOREGISTER)) {
            String dataId = map.get(H5Action.DATA_ID).toLowerCase();
            String data = map.get(H5Action.DATA).toLowerCase();

            boolean close = JsonUtils.getBoolean(data, "close", false);
            H5Action action = new H5Action();
            action.code = H5Action.GOTOREGISTER;
            action.close = close;
            action.dataId = dataId;

            return action;
        }

        //popAddCard，新增银行卡
        if (StringUtils.isEquals(code, H5Action.POPADDCARD)) {
            H5Action action = new H5Action();
            action.code = H5Action.POPADDCARD;

            return action;
        }

        //showProgress，转圈
        if (StringUtils.isEquals(code, H5Action.SHOWPROGRESS)) {
            H5Action action = new H5Action();
            action.code = H5Action.SHOWPROGRESS;

            return action;
        }

        //hideProgress，停止转圈
        if (StringUtils.isEquals(code, H5Action.HIDEPROGRESS)) {
            H5Action action = new H5Action();
            action.code = H5Action.HIDEPROGRESS;

            return action;
        }

        //showToast，toast
        if (StringUtils.isEquals(code, H5Action.SHOWTOAST)) {
            String data = map.get(H5Action.DATA).toLowerCase();

            final String msg = JsonUtils.getString(data, "msg", "");

            if (!TextUtils.isEmpty(msg)) {
                H5Action action = new H5Action();
                action.code = H5Action.SHOWTOAST;
                action.msg = msg;

                return action;
            }

            return null;
        }

        //popMsg
        if (StringUtils.isEquals(code, "popmsg")) {
            String data = map.get(H5Action.DATA).toLowerCase();
            String dataId = map.get(H5Action.DATA_ID).toLowerCase();

            final String msg = JsonUtils.getString(data, "msg", "");
            int type = JsonUtils.getInt(data, "type", 0);
            if (!TextUtils.isEmpty(msg)) {
                H5Action action = new H5Action();
                action.code = H5Action.POPMSG;
                action.msg = msg;
                action.type = type;
                action.dataId = dataId;

                return action;
            }

            return null;
        }


        return null;
    }

    /**
     * 处理PopPage，新开页面的类型
     *
     * @param url
     * @param closeType
     * @param title
     * @return
     */
    private static H5Action parsePopPage(String url, int closeType, String title) {
        //url = url.toLowerCase();
        String fullUrl ;
        if(url.startsWith("http")) {
            fullUrl = url;
        } else {
            fullUrl = Url.getBaseUrl() + url;
        }

        L.d("H5Parser.parsePopPage:url=" + fullUrl + ",closeType=" + closeType + ",title=" + title);

        H5Action action = new H5Action();
        action.title = title;
        action.url = fullUrl;
        action.closeType = closeType;


        //-------------------以下为不跳转定制界面的常规H5处理------------------------

        action.code = H5Action.POPPAGE_GENERAL;


        return action;
    }


    /**
     * 获取URL的参数Map
     *
     * @param url url
     * @param pre ？号之前的前缀
     * @return
     */
    private static Map<String, String> getParamsMap(String url, String pre) {
        //String pre = "http://kes.daliuliang.com.cn/mobile/api/appLocal?";

        //String url= pre + "code=100&arg=tony";

        if (url.contains(pre)) {
            int index = url.indexOf(pre);
            int end = index + pre.length();
            String queryString = url.substring(end);

            String[] queryStringSplit = queryString.split("&");
            ArrayMap<String, String> queryStringMap = new ArrayMap<>();

            String[] queryStringParam;
            for (String qs : queryStringSplit) {
                if (qs.toLowerCase().startsWith("data=")) {
                    //单独处理data项，避免data内部的&被拆分
                    int dataIndex = queryString.indexOf("data=");
                    String dataValue = queryString.substring(dataIndex + 5);
                    //String dataValue = qs.substring(5);

                    try {
                        //转码
                        dataValue = URLDecoder.decode(dataValue, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        L.e(e, e.getLocalizedMessage());
                    }

                    queryStringMap.put("data", dataValue);
                } else {
                    queryStringParam = qs.split("=");

                    String value = "";
                    if (queryStringParam.length > 1) {
                        //避免后台有时候不传值,如“key=”这种
                        value = queryStringParam[1];
                    }
                    queryStringMap.put(queryStringParam[0].toLowerCase(), value);
                }
            }

            return queryStringMap;
        }

        return null;
    }


    /**
     * 获取URL的参数Map
     *
     * @param url url
     * @return
     */
    public static Map<String, String> getParamsMap(String url) {
        String quote = "?";
        if (!url.contains(quote)) {
            return null;
        }

        try {
            int index = url.indexOf(quote);
            int end = index + quote.length();
            String queryString = url.substring(end);

            String[] queryStringSplit = queryString.split("&");
            ArrayMap<String, String> queryStringMap =
                    new ArrayMap<>();
            String[] queryStringParam;
            for (String qs : queryStringSplit) {
                queryStringParam = qs.split("=");
                queryStringMap.put(queryStringParam[0].toLowerCase(), queryStringParam[1]);
            }

            return queryStringMap;
        } catch (Exception ex) {
            L.e(ex, ex.getMessage());

        }

        return null;
    }
}
