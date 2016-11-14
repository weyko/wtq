package net.skjr.wtq.application;

import android.content.Context;
import android.text.TextUtils;

import net.skjr.wtq.common.utils.PreferencesUtils;
import net.skjr.wtq.core.utils.common.CryptoUtils;
import net.skjr.wtq.core.utils.common.SerializeUtils;
import net.skjr.wtq.model.SystemInfo;

import java.util.List;

/**
 * Preference帮助类
 */
public class MyPreference {
    private static final String BASE_URL_KEY = "k0";
    private static final String BACKGROUND_BEGIN_TIME = "k1";

    // 是否曾经打开过程序（即非第一次打开）
    private static final String HAS_OPEN_APP = "k11";

    private static final String PHONE_KEY = "k14";

    private static final String TOKEN_KEY = "k15";

    private static final String BIND_KEY = "k16";

    private static final String LAST_PHONE_KEY = "k18";

    private static final String FAVORITE_LIST = "k21";

    private static final String LOCK_PATTERN= "k71";

    private static final String LOCK_ENABLED = "k72";

    private static final String ACCOUNT_INFO = "k81";

    private static final String PLATFORM_INFO = "k82";

    private static final String MSG_NUM_TIME = "k83";

    private static final String SYSTEM_INFO = "k99";

    private static final String LONGITUDE = "l01";
    private static final String LATITUDE = "l02";
    private static final String CITY = "l03";

    private static Context getContext() {
        return AppController.getInstance();
    }

    /**
     * 获取基地址
     * @return
     */
    public static String getBaseUrl(){
        return PreferencesUtils.getString(getContext(), BASE_URL_KEY);
    }

    public static void setBaseUrl(String url){
        PreferencesUtils.putString(getContext(), BASE_URL_KEY, url);
    }

    /**
     * 获取APP进入后台的时间点
     * @return
     */
    public static String getBackgroundBeginTime(){
        return PreferencesUtils.getString(getContext(), BACKGROUND_BEGIN_TIME);
    }

    public static void setBackgroudBeginTime(String time){
        PreferencesUtils.putString(getContext(), BACKGROUND_BEGIN_TIME, time);
    }

    /**
     * 是否已经打开过程序
     *
     * @return
     */
    public static boolean hasOpenApp() {
//        return false;
        return  PreferencesUtils.getBoolean(getContext(), HAS_OPEN_APP);
    }

    /**
     * 保存是否已经打开过程序
     *
     * @param hasOpen
     */
    public static void setHasOpenApp(boolean hasOpen) {
        PreferencesUtils.putBoolean(getContext(), HAS_OPEN_APP, hasOpen);
    }

    /**
     * 获取登录的手机号
     * @return
     */
    public static String getPhone(){
        return PreferencesUtils.getString(getContext(), PHONE_KEY);
    }

    public static void setPhone(String phone){
        PreferencesUtils.putString(getContext(), PHONE_KEY, phone);
    }

    /**
     * 获取登录的Token
     * @return
     */
    public static String getToken(){
        String val = PreferencesUtils.getString(getContext(), TOKEN_KEY);
        if(TextUtils.isEmpty(val))
            return "";

        return CryptoUtils.AES.decrypt(val);
    }

    public static void setToken(String token){
        if(TextUtils.isEmpty(token)) {
            PreferencesUtils.putString(getContext(), TOKEN_KEY, "");
            return;
        }

        String val = CryptoUtils.AES.encrypt(token);
        PreferencesUtils.putString(getContext(), TOKEN_KEY, val);
    }

    /**
     * 获取登录用户的绑定理财师类型
     * 理财师绑定类型  0-未绑定 1-主动添加  2-平台推荐
     * @return
     */
    public static int getBindType(){
        return PreferencesUtils.getInt(getContext(), BIND_KEY);
    }

    public static void setBindType(int type){
        PreferencesUtils.putInt(getContext(), BIND_KEY, type);
    }

    /**
     * 获取上次登录的手机号码，登录前，判断登录号码是否与lastphone一致，来判断是否是与上次不同的人登录
     * 用户每次登录成功后，同时写入号码到phone/lastphone
     * 用户退出登录，清除phone，保持lastphone不变
     * @return
     */
    public static String getLastPhone(){
        return PreferencesUtils.getString(getContext(), LAST_PHONE_KEY);
    }

    public static void setLastPhone(String pattern){
        PreferencesUtils.putString(getContext(), LAST_PHONE_KEY, pattern);
    }

    /**
     * 获取上次打开消息中心的时间（时间戳）
     * @return
     */
    public static String getLastMessageCenterTime(){
        return PreferencesUtils.getString(getContext(), MSG_NUM_TIME);
    }

    public static void setLastMessageCenterTime(String time){
        PreferencesUtils.putString(getContext(), MSG_NUM_TIME, time);
    }


    /**
     * 获取是否启用了手势
     * @return
     */
    public static boolean getLockEnabled(){
        return PreferencesUtils.getBoolean(getContext(), LOCK_ENABLED);
    }

    public static void setLockEnabled(boolean enabled){
        PreferencesUtils.putBoolean(getContext(), LOCK_ENABLED, enabled);
    }
    /**
     * 获取经度
     * @return
     */
    public static String getLongitude(){
        return PreferencesUtils.getString(getContext(), LONGITUDE);
    }

    public static void setLongitude(String enabled){
        PreferencesUtils.putString(getContext(), LONGITUDE, enabled);
    }
    /**
     * 获取纬度
     * @return
     */
    public static String getLatitude(){
        return PreferencesUtils.getString(getContext(), LATITUDE);
    }

    public static void setLatitude(String enabled){
        PreferencesUtils.putString(getContext(), LATITUDE, enabled);
    }
    /**
     * 获取城市
     * @return
     */
    public static String getCity(){
        return PreferencesUtils.getString(getContext(), CITY);
    }

    public static void setCity(String enabled){
        PreferencesUtils.putString(getContext(), CITY, enabled);
    }

    /**
     * 获取收藏的自选列表
     * @return
     */
    public static List<String> getFavoriteList(){
        return getList(FAVORITE_LIST);
    }

    public static void setFavoriteList(List<String> list){
        if(list ==null)
            return;
        if(list.size() == 0)
            return;

        setList(FAVORITE_LIST, list);
    }

    public static void clearFavoriteList(){
        clearList(FAVORITE_LIST);
    }

    /**
     * 获取系统信息
     * @return
     */
    public static SystemInfo getSystemInfo(){
        Object object = getObj(SYSTEM_INFO);

        if(object == null)
            return null;

        return (SystemInfo)object;
    }

    public static void setSystemInfo(SystemInfo info){
        setObj(SYSTEM_INFO, info);
    }

    public static void clearSystemInfo(){
        clearObj(SYSTEM_INFO);
    }


    public static void clearPlatformInfo(){
        clearObj(PLATFORM_INFO);
    }



    /**
     * 获取对象
     * @param key
     * @return
     */
    public static Object getObj(String key) {
        String str = PreferencesUtils.getString(getContext(), key);
        if (TextUtils.isEmpty(str))
            return null;

        return SerializeUtils.str2Obj(str);
    }

    /**
     * 保存可序列化对象
     * @param key
     * @param obj
     */
    public static void setObj(String key, Object obj) {
        String str = SerializeUtils.obj2Str(obj);

        if (TextUtils.isEmpty(str))
            return;

        PreferencesUtils.putString(getContext(), key, str);
    }

    /**
     * 清除对象
     * @param key
     */
    public static void clearObj(String key){
        PreferencesUtils.putString(getContext(), key, "");
    }

    /**
     * 获取列表
     * @param key
     * @param <T>
     * @return
     */
    public static <T> List<T> getList(String key) {
        String str = PreferencesUtils.getString(getContext(), key);
        if (TextUtils.isEmpty(str))
            return null;

        return SerializeUtils.string2List(str);
    }

    /**
     * 保存可序列化对象列表
     * @param key
     * @param list
     * @param <T>
     */
    public static <T> void setList(String key, List<T> list) {
        String str = SerializeUtils.list2String(list);

        if (TextUtils.isEmpty(str))
            return;

        PreferencesUtils.putString(getContext(), key, str);
    }

    /**
     * 清除可序列化对象列表
     * @param key
     */
    public static void clearList(String key){
        PreferencesUtils.putString(getContext(), key, "");
    }
}
