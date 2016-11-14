package net.skjr.wtq.core.utils;


import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * 日志封装类
 * 对com.orhanobut.logger.Logger进行简单封装，避免升级接口变化
 */
public class L {

    public static void init(String tag, boolean isDebugMode){
        if(isDebugMode){
            Logger
                    .init(tag)               // default tag : PRETTYLOGGER or use just init()
                    .hideThreadInfo()
                    .methodCount(5)          // default 2
                    .logLevel(LogLevel.FULL);  // default : LogLevel.FULL
        }else{
            Logger
                    .init(tag)               // default tag : PRETTYLOGGER or use just init()
                    .hideThreadInfo()
                    .methodCount(4)          // default 2
                    .logLevel(LogLevel.NONE);
        }
    }

    public static void d(String message) {
        Logger.d(message);
    }

    public static void i(String message){
        Logger.i(message);
    }

    /**
     * Logger.d("hello %s %d", "world", 5);
     *
     * @param message
     * @param args
     */
    public static void d(String message, Object... args) {
        Logger.d(message, args);
    }

    public static void d(String tag, String message) {
        Logger.t(tag).d(message);
    }

    public static void d(String tag, String message, Object... args) {
        Logger.t(tag).d(message, args);
    }

    public static void e(String message) {
        Logger.e(message);
    }

    public static void e(String message, Object... args) {
        Logger.e(message, args);
    }

    public static void e(Exception ex, String message) {
        Logger.e(ex, message);
    }

    public static void e(Exception ex, String message, Object... args) {
        Logger.e(ex, message, args);
    }

    public static void e(String tag, Exception ex, String message) {
        Logger.t(tag).e(ex, message);
    }

    public static void e(String tag, Exception ex, String message, Object... args) {
        Logger.t(tag).e(ex, message, args);
    }

    public static void json(String json) {
        Logger.json(json);
    }

    public static void json(String tag, String json) {
        Logger.t(tag).json(json);
    }
}
