package net.skjr.wtq.core.net;

/**
 * 统一封装数据请求返回的回调
 */
public interface DataCallback<T> {
    /**
     * 数据请求处理完成
     *
     * @param isSuccess 是否成功
     * @param message   消息
     * @param result    返回对象
     */
    void onProcessComplete(boolean isSuccess, String message, T result);
}
