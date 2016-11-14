package net.skjr.wtq.data;

import android.text.TextUtils;
import android.util.Log;

import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyApp;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.core.net.okhttp.OkHttpUtils;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.common.StringUtils;
import net.skjr.wtq.core.utils.json.GsonUtils;
import net.skjr.wtq.core.utils.json.JsonUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonResult;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.tag;

/**
 * OkHttp数据业务类
 */
public class OkData {
    /**
     * 向后台发起请求, 获取服务器结果
     * 返回泛型对象
     *
     * @param request
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> APIResult<T> execute(Request request, Class<T> clazz) {
        APIResult<T> apiResult = new APIResult<>();
        apiResult.isSuccess = false;
        //从后台获取结果
        JsonResult jsonResult = doRequest(request);
        if (jsonResult == null) {
            return apiResult;
        }

        //3.与后台通讯正常，但是后台业务不正常
        if (jsonResult.status != 1) {
            apiResult.message = jsonResult.msg;
            return apiResult;
        }

        //4.如果data不为空，那么进行业务对象转换
        T resultObj = null;
        if (!TextUtils.isEmpty(jsonResult.data)) {
            resultObj = GsonUtils.get(jsonResult.data, clazz);
            //4.转换业务对象失败
            Log.e("json",jsonResult.data);
            if (resultObj == null) {
                return apiResult;
            }
        }

        //5.成功
        apiResult.isSuccess = true;
        apiResult.result = resultObj;

        return apiResult;
    }

    /**
     * 向后台发起请求, 获取服务器结果
     * 返回泛型列表对象
     *
     * @param request
     * @param clazz
     * @param type
     * @param <T>
     * @return
     */
    public static <T> APIResult<List<T>> excute(Request request, Class<T> clazz, Type type) {
        APIResult<List<T>> apiResult = new APIResult<>();
        apiResult.isSuccess = false;

        //从后台获取结果
        JsonResult jsonResult = doRequest(request);
        if (jsonResult == null) {
            return apiResult;
        }

        //3.与后台通讯正常，但是后台业务不正常
        if (jsonResult.status != 1) {
            apiResult.message = jsonResult.msg;
            return apiResult;
        }

        //4.如果data不为空，那么进行业务对象转换
        List<T> resultObj = null;
        if (!TextUtils.isEmpty(jsonResult.data)) {
            resultObj = GsonUtils.getList(jsonResult.data, type);
            //4.转换业务对象失败
            if (resultObj == null) {
                return apiResult;
            }
        }

        //5.成功
        apiResult.isSuccess = true;
        apiResult.result = resultObj;

        return apiResult;
    }

    /**
     * 发起请求,对结果解析,返回JsonResult
     *
     * @param request
     * @return
     */
    private static JsonResult doRequest(Request request) {
        //向后台发起请求
        try {
            Response response = OkHttpUtils.getInstance().getClient().newCall(request).execute();
            String str = response.body().string();

            if (AppController.isDebugMode) {
                str = StringUtils.unicodeToString(str);
            }

            L.d("<--@@", str);

            //1.如果返回的为空，那么也认为出错，因为正常情况下，都会返回一个JsonResult
            if (TextUtils.isEmpty(str)) {
                return null;
            }

            //2.解析返回的数据失败
            Log.e("json",str);
            JsonResult jsonResult = parseJsonResult(str);
            if (jsonResult == null) {
                return null;
            }

            return jsonResult;

        } catch (Exception ex) {
            L.e(ex, ex.getLocalizedMessage());
        }

        return null;
    }

    /**
     * 创建请求对象
     *
     * @param url
     * @param dataId
     * @param token
     * @param object
     * @return
     */
    public static Request createRequest(String url, String dataId, String token, String object) {
        String verify = "";

        MyApp myApp = AppController.getInstance().getMyApp();
        L.d(url);

        return new Request.Builder()
                .url(url)
                .header(Consts.HEADER_TOKEN, token)
                .header(Consts.HEADER_DEVICE, String.valueOf(myApp.getDeviceType()))
                .header(Consts.HEADER_DEVICEID, myApp.getDeviceId())
                .header(Consts.HEADER_VERIFY, verify)
                .header(Consts.HEADER_DATAID, dataId)
                .post(RequestBody.create(OkHttpUtils.MEDIA_TYPE_JSON, object))
                .build();
    }
    /**
     * 上传文件请求对象
     *
     * @param url
     * @param dataId
     * @param token
     * @param object
     * @return
     */
    public static Request createUploadRequest(String url, String dataId, String token, String object, File[] files) {
        String verify = "";

        MyApp myApp = AppController.getInstance().getMyApp();
        MultipartBody.Builder builder = new MultipartBody.Builder("AaB03x");
        builder.setType(MultipartBody.FORM);

//        for(File file:files){
        File file = files[0];
            builder.addFormDataPart("files", null, new MultipartBody.Builder("AaB03x")
                    .addPart(Headers.of("Content-Disposition", "form-data; filename=\"img.png\""), RequestBody.create(MediaType.parse("image/png"), file))
                    .build());
//        }
        builder.addPart(RequestBody.create(OkHttpUtils.MEDIA_TYPE_JSON, object));
        RequestBody requestBody = builder.build();

        return new Request.Builder()
                .url(url)
                .header(Consts.HEADER_DATAID, dataId)
                .header(Consts.HEADER_TOKEN, token)
                .header(Consts.HEADER_DEVICE, String.valueOf(myApp.getDeviceType()))
                .header(Consts.HEADER_DEVICEID, myApp.getDeviceId())
                .post(requestBody)
                .tag(tag)
                .build();
    }

    /**
     * Json字符串转换成JsonResult
     *
     * @param str 返回的数据
     */
    private static JsonResult parseJsonResult(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);

            JsonResult result = new JsonResult();
            result.status = JsonUtils.getInt(jsonObject, "status", 0);
            result.msg = JsonUtils.getString(jsonObject, "msg", "");
            result.data = JsonUtils.getString(jsonObject, "data", "");
            result.info = JsonUtils.getString(jsonObject, "info", "");

            return result;

        } catch (Exception e) {
            L.e(e, e.getLocalizedMessage());
        }

        return null;
    }
}
