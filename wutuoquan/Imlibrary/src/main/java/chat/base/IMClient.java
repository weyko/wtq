package chat.base;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;
import java.util.ArrayList;

import chat.common.config.Constant;
import chat.common.user.UserInfoHelp;
import chat.image.IMImageLoader;
import chat.login.LoginEntity;
import chat.manager.ChatBaseManager;
import chat.manager.IMResourceManager;
import chat.manager.XmppServerManager;
import chat.manager.XmppSessionManager;
import chat.service.MessageInfoReceiver;
import chat.service.MessageReciveObserver;

/**
 * Description:
 */
public class IMClient {
    private static IMClient instance;
    public static boolean isAppRunningBackground=false;//是否后台运行
    private ArrayList<FragmentActivity> activitys;// 将activity存储到队列，统一管理
    public static boolean isChatPage=false;//是否在聊天页面
    public Handler msgTimoutHandler;
    private UnlimitedDiscCache discCache = null;
    public static IMImageLoader sImageLoader = new IMImageLoader();
    public static IMImageLoader chatLocalImgeLoader = new IMImageLoader();
    public long diffTime = 0;// 记录服务器与本地时间的差值。
    /* 群是否有变化 */
    public static boolean isGroupChange = false;
    private Context context;
    /**
     * get the instance of IMClient
     * @return
     */
    public static IMClient getInstance() {
        if(instance==null){
            synchronized (IMClient.class){
                if(instance==null){
                    instance=new IMClient();
                }
            }
        }
        return instance;
    }
    public IMClient() {
    }
    public  void initIM(Context context) {
        this.context = context;
        saveSSOLoginInfo("27901","27901");
        initImageLoader(context);
    }
    /**
     * @方法说明: 初始化initImageLoader
     */
    private static void initImageLoader(Context context) {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int memory = (int) (maxMemory / 5);
        int maxLimit = (int) (maxMemory / 2);
        int minSize = 50 * 1024 * 1024;
        if (memory < minSize) {
            memory = minSize;
        }
        if (memory > maxLimit) {
            memory = maxLimit;
        }
        // 开始构建
        sImageLoader.initConfig(context, memory);
        chatLocalImgeLoader.initConfig(context, memory, false);
    }
    public Context getContext(){
        if(context==null) try {
            throw new  Exception("content is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }

    /**
     * 连接聊天服务
     */
    public void startIMServer(){
        XmppServerManager.getInstance(XmppSessionManager.getInstance())
                .startConnectService();
        MessageInfoReceiver.unregisterAllHandler();
    }
    /**
     * 结束聊天服务器
     */
    public void stopIMServer() {
        MessageInfoReceiver.unregisterAllHandler();
        IMResourceManager.resetIM();
        ChatBaseManager.getInstance().release();
    }
    /**
     * 注册聊天监听器
     * @param msgReviceObsever 用于监听消息状态
     * @param onMessageSendListener 用于监听发送过来的新消息
     */
    public void registerIMListener(MessageReciveObserver msgReviceObsever, XmppServerManager.OnMessageSendListener onMessageSendListener){
        MessageInfoReceiver.registerReceiverHandler(msgReviceObsever);
        XmppSessionManager.getInstance().addOnMessageSendListener(onMessageSendListener);
    }
    /**
     * 移除聊天监听器
     * @param msgReviceObsever 用于监听消息状态
     * @param onMessageSendListener 用于监听发送过来的新消息
     */
    public void unRegisterIMListener(MessageReciveObserver msgReviceObsever, XmppServerManager.OnMessageSendListener onMessageSendListener){
        MessageInfoReceiver.unregisterReceiverHandler(msgReviceObsever);
        XmppSessionManager.getInstance().removeOnMessageSendListener(onMessageSendListener);
    }
    /**
     * 获取聊天服务器用户Id
     *
     * @return
     */
    public String getSSOUserId() {
        return UserInfoHelp.getInstance(context).getUserId();
    }

    /**
     * 获取聊天服务器token
     *
     * @return
     */
    public String getSSOToken() {
        return UserInfoHelp.getInstance(context).getToken();
    }
    /**
     * 清除UserId和TokenId
     *
     * @return void
     * @Title: clearSSOLoginInfo
     * @param:
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void removeSSOLoginInfo() {
        UserInfoHelp.getInstance(context).removeToken();
    }

    /**
     * 保存聊天服务器验证信息
     *
     * @param userid
     * @param token
     */
    public  void saveSSOLoginInfo(String userid,String token) {
        if (userid != null && userid.length() > 0 && token != null && token.length() > 0) {
            UserInfoHelp.getInstance(context).setUserId(userid);
            UserInfoHelp.getInstance(context).setToken(token);
        }
    }
    /**
     * 保存聊天服务器验证信息
     *
     * @param loginEntity 聊天登录实体类
     */
    public  void saveSSOLoginInfo(LoginEntity loginEntity) {
        if (loginEntity != null) {
            LoginEntity.SSOLoginDataBean data=loginEntity.getData();
            if(data!=null){
                if(data.getUserId()!=null) UserInfoHelp.getInstance(context).setUserId(data.getUserId());
                if(data.getToken()!=null) UserInfoHelp.getInstance(context).setToken(data.getToken());
            }
            if(loginEntity.getUserName()!=null)UserInfoHelp.getInstance(context).setNickname(loginEntity.getUserName());
            if(loginEntity.getAvatarUrl()!=null)UserInfoHelp.getInstance(context).setAvarar(loginEntity.getAvatarUrl());
        }
    }

    public Handler getTimeoutHandler() {
        if (msgTimoutHandler == null)
            msgTimoutHandler = new Handler();
        return msgTimoutHandler;
    }
    /**
     * 根据url获取缓存文件
     * @param imageUrl
     * @return
     */
    public File findInImageLoaderCache(String imageUrl) {
        if (imageUrl == null || imageUrl.length() <= 0) {
            return null;
        }
        return DiskCacheUtils.findInCache(imageUrl, discCache);
    }
    public UnlimitedDiscCache getUnlimitedDiscCache() {
        if (discCache == null) {
            File cacheDir = new File(Constant.IMAGES_FOLDER);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            discCache = new UnlimitedDiscCache(cacheDir);
            cacheDir = null;
        }
        return discCache;
    }
    /**
     * 添加activity
     *
     * @param activity
     */
    public void addActivity(FragmentActivity activity) {
        if (activitys == null) {
            activitys = new ArrayList<FragmentActivity>();
        }
        if (!activitys.contains(activity)) {
            activitys.add(activity);
        }
    }
    /**
     * 获取activity
     *
     * @return
     */
    public BaseActivity getLastActivity() {
        try {
            return (BaseActivity) activitys.get(activitys.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<FragmentActivity> getActivitys() {
        return activitys;
    }
    /**
     * 移除activity
     *
     * @param activity
     */
    public void removeActivity(FragmentActivity activity) {
        if (activitys != null && activitys.contains(activity)) {
            activitys.remove(activity);
        }
    }
    /**
     * 清空activity
     */
    public void finishActivity() {
        if (activitys != null) {
            for (FragmentActivity activity : activitys) {
                if (activity != null) {
                    activity.finish();
                }
            }
            activitys.clear();
        }
    }
}
