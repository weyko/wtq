package chat.manager;

import android.content.Context;

import com.android.volley.Request.Method;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.service.MessageInfoReceiver;
import chat.session.group.bean.ChatGroupBean;
import chat.session.group.bean.ChatGroupListBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * @ClassName: IMResourceManager
 * @Description: 资源管理
 */
public class IMResourceManager {
    /**
     * @return void
     * @Title: initDataForGroupsAndFriends
     * @param:
     * @Description: 初始化群组、好友列表
     */
    public static void initDataForGroupsAndFriends(
            final BaseActivity context) {
        getGroups(context);
    }
    /**
     * @return void
     * @Title: getMembers
     * @param:
     * @Description: 获取群列表
     */
    private static void getGroups(final BaseActivity context) {
        WBaseModel<ChatGroupListBean> mode = new WBaseModel<ChatGroupListBean>(
                context, ChatGroupListBean.class);
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        mode.httpJsonRequest(Method.GET, URLConfig.CHAT_GRUOP_LIST, parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof ChatGroupListBean) {
                                ChatGroupManager.getInstance()
                                        .insertGroupInfos((ChatGroupListBean) data);
                            }
                        }
                    }
                });
    }

    /**
     * @return void
     * @Title: initGroupInfo
     * @param:
     * @Description: 获取群信息
     */
    public static void initGroupInfo(Context context, final String roomId) {
        WBaseModel<ChatGroupBean> mode = new WBaseModel<ChatGroupBean>(
                context, ChatGroupBean.class);
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        mode.httpJsonRequest(Method.GET,
                String.format(URLConfig.CHAT_GRUOP_BASE, roomId), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof ChatGroupBean) {
                                ChatGroupManager.getInstance().insertGroup(
                                        (ChatGroupBean) data, false);
                                ChatUtil.sendUpdateNotify(
                                        IMClient.getInstance().getContext(),
                                        MessageInfoReceiver.EVENT_GROUPINFO,
                                        "", roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
                            }
                        }
                    }
                });
    }

    /**
     * @return long
     * @Title: getBeiJingTime
     * @param:
     * @Description: 获取北京时间
     */
    public static long getBeiJingTime() {
        long time = 0;
        try {
            URL url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect(); // 发出连接
            long ld = uc.getDate(); // 取得网站日期时间
            Date date = new Date(ld); // 转换为标准时间对象
            time = date.getTime();
        } catch (Exception e) {
            time = new Date().getTime();
        }

        return time;
    }
    /**
     * @return void
     * @Title: onIMDestory
     * @param:
     * @Description:释放所有聊天管理
     */
    public static void releaseManagerForIM() {
        ChatMessageManager.getInstance().reset();
        ChatSessionManager.getInstance().reset();
        ChatContactManager.getInstance().reset();
        ChatGroupManager.getInstance().reset();
    }

    /**
     * @return void
     * @Title: resetIM
     * @param:
     * @Description: 重启聊天服务
     */
    public static void resetIM() {
        releaseManagerForIM();
        XmppServerManager.ExitService();
    }
}
