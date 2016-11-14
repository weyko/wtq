package net.skjr.wtq.common;

import android.net.Uri;

/**
 * 全部事件的引用类，方便代码引用
 */
public class Event {

    private Event() {

    }

    /**
     * 事件基类，用于跟后台通讯的事件通知
     * @param <T>
     */
    public static class BaseEvent<T>{
        /**
         * 与后台通讯成功失败
         */
        public boolean isSuccess;

        /**
         * 返回的信息
         */
        public String message;

        /**
         * 返回的对象
         */
        public T result;

        public BaseEvent(boolean isSuccess, String message, T result) {
            this.isSuccess = isSuccess;
            this.message = message;
            this.result = result;
        }
    }

    /**
     * 关闭注册界面
     */
    public static class CloseRegEvent {
    }


    /**
     * 上传用户头像
     */
    public static class uploadUserHeadCompleteEvent{
        public boolean isSuccess;
        public String message;

        public uploadUserHeadCompleteEvent(boolean isSuccess, String message) {
            this.isSuccess = isSuccess;
            this.message = message;
        }
    }


    /**
     * 后台超时-登录超时
     */
    public static class BackgroudTimeOutEvent{

    }

    /**
     * 刷新主页
     */
    public static class RefreshHomeTabEvent{

    }

    /**
     * 刷新账户
     */
    public static class RefreshTabMine{

    }

    /**
     * 打开主界面某个标签的事件通知
     */
    public static class OpenMainTabEvent{
        public int index;

        public OpenMainTabEvent(int index) {
            this.index = index;
        }
    }

    /**
     * 立刻打开首页标签事件
     */
    public static class OpenHomeTabEvent{

    }

    /**
     * 添加银行卡完成
     */
    public static class AddBankCardCompleteEvent{
        public boolean isSuccess;
        public String message;

        public AddBankCardCompleteEvent(boolean isSuccess, String message) {
            this.isSuccess = isSuccess;
            this.message = message;
        }
    }

    /**
     * 下载更新完成
     */
    public static class DownloadApkCompleteEvent{
        public Uri downloadFileUri;

        public DownloadApkCompleteEvent(Uri downloadFileUri) {
            this.downloadFileUri = downloadFileUri;
        }
    }

    /**
     * 实名认证完成，认证投资人界面
     */
    public static class AuthRealNameInvestorCompleteEvent{}
    /**
     * 设置交易密码完成，刷新设置界面
     */
    public static class SetTradePwdCompleteEvent{}
    /**
     * 实名认证完成，刷新资产界面状态
     */
    public static class AuthRealNameCompleteEvent{}
    /**
     * 登录完成，进行处理
     */
    public static class LoginCompleteEvent{}
    /**
     * 充值完成
     */
    public static class RechargeCompleteEvent{}
    /**
     * 刷新用户信息页面
     */
    public static class RefreshUserInfoCompleteEvent{}
    /**
     * 刷新地址列表页面
     */
    public static class RefreshAddressListCompleteEvent{}
    /**
     * 刷新评论列表页面
     */
    public static class RefreshDiscussListCompleteEvent{}

}
