package net.skjr.wtq.ui.h5;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyApp;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.core.utils.L;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 封装Webview的常规配置
 */
public class WebViewUtils {
    /**
     * 设置常规的选项
     *
     * @param webView
     */
    public static void initWebView(WebView webView) {
        WebSettings setting = webView.getSettings();
        setting.setSavePassword(false);
        setting.setJavaScriptEnabled(true);//设置使用够执行JS脚本
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setSupportMultipleWindows(true);
        setting.setBuiltInZoomControls(false);//是否显示缩放工具
        setting.setSupportZoom(true);//是否支持缩放

        setting.setAllowFileAccess(true);// 设置允许访问文件数据

        //setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        setting.setDomStorageEnabled(true);
        setting.setDefaultTextEncodingName("GBK");//设置字符编码
    }

    /**
     * 进行销毁处理
     *
     * @param activity
     * @param webView
     */
    public static void destroyWebView(Activity activity, WebView webView) {
        // webview的 ZoomButton，也就是那两个放大和缩小的按钮，导致的。如果设置为让他们出现，
        // 并且可以自动隐藏，那么，由于他们的自动隐藏是一个渐变的过程，所以在逐渐消失的过程中
        // 如果调用了父容器的destroy方法，就会导致Leaked。
        // 处理缩放按钮延迟销毁导致内存泄露的问题
        ViewGroup view = (ViewGroup) activity.getWindow().getDecorView();
        view.removeAllViews();

        destroyWebView(webView);
    }

    public static void destroyWebView(WebView webView){
        if (webView == null)
            return;

        //销毁WebView，避免内存泄漏
        try {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.setVisibility(View.GONE);
            //webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }catch (Exception ex){
            L.e(ex, ex.getMessage());
        }
    }

    /**
     * WebView加载URL时的Header
     *
     * @param dataId
     * @return
     */
    public static Map<String, String> getHeaderMap(String dataId) {
        Map<String, String> map = new HashMap<>();

        MyApp myApp = AppController.getInstance().getMyApp();

        String token = myApp.getTokenForServer();

        //TODO:verify
        String verify = "";

        map.put(Consts.HEADER_DATAID, dataId);
        map.put(Consts.HEADER_VERIFY, verify);
        map.put(Consts.HEADER_TOKEN, token);
        map.put(Consts.HEADER_DEVICE, String.valueOf(myApp.getDeviceType()));
        map.put(Consts.HEADER_DEVICEID, myApp.getDeviceId());

        return map;
    }

    public static Map<String, String> getHeaderMap() {
        String dataId = UUID.randomUUID().toString();
        return getHeaderMap(dataId);
    }
}
