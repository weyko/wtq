package net.skjr.wtq.application;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.utils.CommonUtils;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.json.JsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import chat.base.IMClient;

/**
 * 主Application，全局只有一个
 * 所有的全局变量入口均在这里
 * 所有的全局功能类均在这里初始化
 *
 * @author SongTao
 */
public class AppController extends Application {
    public static Context applicationContext;
    private static AppController instance;

    //app是否在前台
    private static boolean activityVisible;
    public String currentActivity;

    //整个App全局核心业务相关的逻辑都抽离到MyApp中，保持AppController干净清晰
    private MyApp myApp;

    public MyApp getMyApp() {
        if (myApp == null)
            myApp = new MyApp(getApplicationContext());

        return myApp;
    }

    //是否是调试模式
    public static boolean isDebugMode = false;

    public static synchronized AppController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = this;
        instance = this;
        //初始化聊天
        IMClient.getInstance().initIM(this);
        initApp();
    }

    private void initApp() {
        //TODO:是否是调试模式，发布时需要设置为false
        isDebugMode = true;

        //LeakCanary
//        LeakCanary.install(this);

        //开启日志
        L.init(Consts.APP_ENAME, isDebugMode);

        //初始化全局对象
        myApp = new MyApp(getApplicationContext());

        //初始化全局异常处理
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext(), isDebugMode);

        //JsonUtils的调试模式
        JsonUtils.isPrintException = isDebugMode;

    }

    /**
     * app是否在前台
     *
     * @return
     */
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    /**
     * 进入前台，由BaseActivity触发
     */
    public static void activityResumed() {
        //如果是指定的Activity,那么不进行超时计算
        if (isSystemActivity())
            return;

        activityVisible = true;

        boolean isTimeOut = isBackgroudTimeOut();
        if (isTimeOut) {
            AppController.getInstance().getMyApp().isBackgroudTimeOut = true;
            EventBus.getDefault().post(new Event.BackgroudTimeOutEvent());
        }

        //清除上次的进入后台时间
        MyPreference.setBackgroudBeginTime("");

        //L.d("进入前台");
    }

    /**
     * 进入后台，由BaseActivity触发
     */
    public static void activityPaused() {
        //如果是指定的Activity,那么不进行超时计算
        if (isSystemActivity())
            return;

        activityVisible = false;

        String time = CommonUtils.getCurrentTimeString();
        MyPreference.setBackgroudBeginTime(time);
        //L.d("进入后台，时间是：" + time);
    }

    /**
     * 是否已经后台超时，比如放入后台1分钟后
     *
     * @return
     */
    public static boolean isBackgroudTimeOut() {
        String backDateStr = MyPreference.getBackgroundBeginTime();
        if (TextUtils.isEmpty(backDateStr))
            return false;

        String currentDateStr = CommonUtils.getCurrentTimeString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date backDate = format.parse(backDateStr);
            Calendar backCalendar = Calendar.getInstance();
            backCalendar.setTime(backDate);

            Date currentDate = format.parse(currentDateStr);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);

            long startTim = backCalendar.getTimeInMillis();
            long endTim = currentCalendar.getTimeInMillis();
            long diff = endTim - startTim;
            int minutes = (int) (diff / 1000 / 60);

            //90s超时
            if (minutes > 1.5) {
                L.d("AppController-登录超时：" + backDateStr + "——" + currentDateStr);
                return true;
            }

            return false;

        } catch (Exception e) {

        }

        return true;
    }

    private static boolean isSystemActivity() {
        String currentActivity = AppController.getInstance().currentActivity;

        if (TextUtils.equals(currentActivity, "LoginActivity")
                || TextUtils.equals(currentActivity, "LoginSuccessActivity")
                || TextUtils.equals(currentActivity, "RegisterActivity")
                || TextUtils.equals(currentActivity, "RegisterSuccessActivity")
                || TextUtils.equals(currentActivity, "FindPwdActivity")
                || TextUtils.equals(currentActivity, "FindPwdSuccessActivity")
                || TextUtils.equals(currentActivity, "SystemWebActivity"))
            return true;

        return false;
    }

}
