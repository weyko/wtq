package net.skjr.wtq.core.net.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import net.skjr.wtq.core.net.DataCallback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * OKHttp的高级封装，封装异步处理到UI线程，不暴露OKHttp的API给外部
 * 如果需要更多精细的控制，那么直接调用OKHttpUtils
 */
public class OkHttpManager {

    private static OkHttpManager mInstance;
    private Handler handler;
    private static Gson gson = new Gson();
    private static final String TAG = "OkHttpClientManager";

    private OkHttpManager() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpManager();
                }
            }
        }

        return mInstance;
    }

    public Handler getHandler() {
        if (handler == null)
            handler = new Handler(Looper.getMainLooper());

        return handler;
    }

    /**
     * 获取字符串
     *
     * @param url
     * @param callback
     */
    public static void get(String url, @NonNull final DataCallback<String> callback) {
        get(url, TAG, callback);
    }

    /**
     * 获取字符串
     *
     * @param url
     * @param tag
     * @param callback
     */
    public static void get(String url, String tag, @NonNull final DataCallback<String> callback) {
        OkHttpUtils.get(url, tag, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendToMainThread(false, e.getMessage(), "", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                sendToMainThread(true, "", result, callback);
            }
        });
    }

    /**
     * 获取对象（从json转换而来）
     *
     * @param url
     * @param callback
     * @param <T>
     */
    public static <T> void getObject(String url, @NonNull final DataCallback<T> callback) {
        getObject(url, TAG, callback);
    }

    /**
     * 获取对象（从json转换而来）
     *
     * @param url
     * @param tag
     * @param callback
     * @param <T>
     */
    public static <T> void getObject(String url, String tag, @NonNull final DataCallback<T> callback) {
        OkHttpUtils.get(url, tag, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendToMainThread(false, e.getMessage(), null, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Type type = getSuperclassTypeParameter(callback.getClass());
                T result = gson.fromJson(response.body().charStream(), type);
                sendToMainThread(true, "", result, callback);
            }
        });
    }

    /**
     * 获取字符串, POST
     *
     * @param url
     * @param callback
     */
    public static void post(String url, Object object, @NonNull final DataCallback<String> callback) {
        post(url, TAG, object, callback);
    }

    /**
     * 获取字符串,POST
     *
     * @param url
     * @param tag
     * @param callback
     */
    public static void post(String url, String tag, Object object, @NonNull final DataCallback<String> callback) {
        String json = gson.toJson(object);
        OkHttpUtils.postJson(url, tag, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendToMainThread(false, e.getMessage(), "", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                sendToMainThread(true, "", result, callback);
            }
        });
    }

    /**
     * 发送POST请求,获取对象
     *
     * @param url
     * @param object
     * @param callback
     * @param <T>
     */
    public static <T> void postObject(String url, Object object, @NonNull final DataCallback<T> callback) {
        postObject(url, TAG, object, callback);
    }

    /**
     * 发送POST请求,获取对象
     *
     * @param url
     * @param tag
     * @param object
     * @param callback
     * @param <T>
     */
    public static <T> void postObject(String url, String tag, Object object, @NonNull final DataCallback<T> callback) {
        String json = gson.toJson(object);
        OkHttpUtils.postJson(url, tag, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendToMainThread(false, e.getMessage(), null, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Type type = getSuperclassTypeParameter(callback.getClass());
                T result = gson.fromJson(response.body().charStream(), type);
                sendToMainThread(true, "", result, callback);
            }
        });
    }

    /**
     * 取消指定标签的请求
     *
     * @param tag
     */
    public static void cancel(@NonNull String tag) {
        OkHttpUtils.cancleRequest(tag);
    }

    /**
     * 通过反射获取某个类的Type，用于Gson进行转换
     *
     * @param subclass
     * @return
     */
    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * 发送到主线程
     *
     * @param isSuccess
     * @param errorMessage
     * @param result
     * @param callback
     * @param <T>
     */
    private static <T> void sendToMainThread(final boolean isSuccess, final String errorMessage, final T result, final DataCallback<T> callback) {
        getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onProcessComplete(isSuccess, errorMessage, result);
            }
        });
    }
}
