package net.skjr.wtq.data;

import android.text.TextUtils;

import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyApp;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.model.APIResult;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Request;
import rx.Observable;
import rx.Subscriber;

/**
 * Rx数据类
 * 对OKHttp的请求及返回进行Rx包装
 */
public class RxData {

    /**
     * 发送数据到服务器, OKHttp+RxJava
     * data是单个对象
     *
     * @param url
     * @param dataId 随机码，UUID
     * @param object JsonObject string
     * @return Observable
     */
    public static <T> Observable<APIResult<T>> excute(String url, final String dataId,
                                                      final String token, String object, final Class<T> clazz) {

        //因为统一使用post提交数据，如果部分接口，只需要获取数据，不提交任何数据，那么post空时会提交失败
        //所以这里发现为空时，添加了一个字符串，以避免提交失败；对应的后台应该忽略提交的内存。
        if (TextUtils.isEmpty(object))
            object = Consts.APP_ENAME;

        final Request request = OkData.createRequest(url, dataId, token, object);
        printLog(dataId, token, object);

        return Observable.create(new Observable.OnSubscribe<APIResult<T>>() {

            @Override
            public void call(Subscriber<? super APIResult<T>> subscriber) {
                APIResult<T> apiResult = OkData.execute(request, clazz);

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                subscriber.onNext(apiResult);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 打印调试信息
     *
     * @param dataId
     * @param token
     * @param object
     */
    private static void printLog(String dataId, String token, String object) {
        MyApp myApp = AppController.getInstance().getMyApp();
//        L.d("send to server:header->dataId=" + dataId + ",token=" + token + ",device="
//                    + String.valueOf(myApp.getDeviceType()) + ",deviceId="
//                    + myApp.getDeviceId());

        L.json("====>", object);
    }

    /**
     * 发送数据到服务器, OKHttp+RxJava
     * data是数据列表
     *
     * @param url
     * @param dataId 随机码，UUID
     * @param object JsonObject string
     * @param type   泛型列表类的type
     * @return Observable
     */
    public static <T> Observable<APIResult<List<T>>> excute(String url, final String dataId,
                                                            final String token, String object, final Class<T> clazz, final Type type) {

        //因为统一使用post提交数据，如果部分接口，只需要获取数据，不提交任何数据，那么post空时会提交失败
        //所以这里发现为空时，添加了一个字符串，以避免提交失败；对应的后台应该忽略提交的内存。
        if (TextUtils.isEmpty(object))
            object = "hszt";

        final Request request = OkData.createRequest(url, dataId, token, object);
        printLog(dataId, token, object);

        return Observable.create(new Observable.OnSubscribe<APIResult<List<T>>>() {

            @Override
            public void call(Subscriber<? super APIResult<List<T>>> subscriber) {
                APIResult<List<T>> apiResult = OkData.excute(request, clazz, type);

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                subscriber.onNext(apiResult);
                subscriber.onCompleted();
            }
        });

    }


    /**
     * 上传文件
     */
    public static <T> Observable<APIResult<T>> upload(String url, final String dataId,
                                                      final String token, String object, File[] files, final Class<T> clazz) {

        if (TextUtils.isEmpty(object))
            object = Consts.APP_ENAME;

        final Request request = OkData.createUploadRequest(url, dataId, token, object, files);
        printLog(dataId, token, object);

        return Observable.create(new Observable.OnSubscribe<APIResult<T>>() {

            @Override
            public void call(Subscriber<? super APIResult<T>> subscriber) {
                APIResult<T> apiResult = OkData.execute(request, clazz);

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                subscriber.onNext(apiResult);
                subscriber.onCompleted();
            }
        });
    }


}
