package net.skjr.wtq.application;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.skjr.wtq.common.FromType;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.sys.DeviceUuidFactory;

import java.util.UUID;

/**
 * 全局业务逻辑对象，在AppController中初始化
 * 保存全局变量，进行一些全局操作
 */
public class MyApp {
    private Context context;

    /**
     * 从那个界面跳转而来
     */
    public int From = FromType.NONE;

    /**
     * 是否是新启动的，即不是从后台唤醒，而是重新启动了APP，从Splash界面进入的
     * 如果此标志为true，那么需要登录的地方，必须登录一次
     */
    public boolean isNewStart;

    /**
     * 放入后台是否超时
     * 写入true：当放入后台，再唤醒，发现超时，写入true
     * 写入false：当登录成功/手势解锁成功，写入false
     */
    public boolean isBackgroudTimeOut;

    /**
     * 未读的消息条数
     */
    public String msgNum;

    public MyApp(Context context) {
        this.context = context;
    }

    /**
     * 是否需要登录（弹出登录框或者手势框）
     * @return
     */
    public boolean isNeedLogin(){
        //如果是新启动，那么需要登录
        if(isNewStart){
            Log.e("need","新启动");
            return true;
        }

        //如果不存在登录信息，那么需要登录
        if(!existLoginInfo()) {
            Log.e("need","不存在登录信息");
            return true;
        }

        //如果已经放入后台超时，那么需要登录
        /*if(isBackgroudTimeOut) {
            Log.e("need","登录超时");
            return true;
        }*/
        Log.e("need","无需登录");
        return false;
    }



    /**
     * 与后台通讯以及WebView加载H5时的Header，所需要的Token
     * @return
     */
    public String getTokenForServer(){
        //如果是第一次启动，或者后台超时，那么传给后台Header的Token为空
        if(isNewStart || isBackgroudTimeOut){
            L.d("getTokenForServer-Token=空");
            return "";
        }

        return MyPreference.getToken();
    }


    /**
     * 是否已经保存过登录信息
     *
     * @return
     */
    public boolean existLoginInfo() {
        if(TextUtils.isEmpty(MyPreference.getPhone()) || TextUtils.isEmpty(MyPreference.getToken()))
            return false;

        return true;
    }

    /**
     * 保存当前的登录信息
     * 1.登录成功后，调用（注意，要先cleanLoginInfo）
     * 2.注册成功后，调用（注意，要先cleanLoginInfo）
     * @param phone
     * @param token
     */
    public void saveLoginInfo(String phone, String token){
        MyPreference.setPhone(phone);
        MyPreference.setToken(token);
        //MyPreference.setBindType(bindType);
    }

    /**
     * 清除保存的登录信息
     * 1.退出登录
     * 2.注册成功后，先清除以前的
     */
    public void clearLoginInfo() {
        MyPreference.setPhone("");
        MyPreference.setToken("");
        //MyPreference.setBindType(0);

        //账户信息也清除
//        MyPreference.clearAccountInfo();
    }

    /**
     * 获取设备ID
     *
     * @return
     */
    public String getDeviceId() {
        DeviceUuidFactory factory = new DeviceUuidFactory(context);
        UUID uuid = factory.getDeviceUuid();
        return uuid.toString();
    }

    /**
     * 获取设备类型
     * 注册终端  3 IOS 4 安卓
     * @return
     */
    public int getDeviceType(){
        return 4;
    }
}
