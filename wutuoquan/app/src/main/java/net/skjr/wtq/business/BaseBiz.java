package net.skjr.wtq.business;


import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyApp;
import net.skjr.wtq.application.MyPreference;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.core.utils.json.GsonUtils;
import net.skjr.wtq.model.JsonRequest;

import java.util.UUID;

/**
 * 业务基类
 */
public class BaseBiz {

    /**
     * 统一的后台API请求地址
     */
    protected String requestUrl = Url.getBaseUrl();

    /**
     * 获取唯一识别码
     * @return
     */
    protected String getDataId(){
        return UUID.randomUUID().toString();
    }

    /**
     * 获取当前用户有效的token
     * @return
     */
    protected String getToken(){
        if(AppController.getInstance().getMyApp().isNeedLogin())
            return "";

        return MyPreference.getToken();
    }

    /**
     * 获取JsonRequest的JsonString
     * @param request
     * @return
     */
    protected String getRequestContent(JsonRequest request){
        return GsonUtils.toJson(request);
    }

    protected MyApp getMyApp() {
        return AppController.getInstance().getMyApp();
    }

}
