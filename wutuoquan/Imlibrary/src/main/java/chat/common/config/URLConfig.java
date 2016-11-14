package chat.common.config;

/**
 * Description:
 */
public class URLConfig {
    public static int conditionFlag = 0;
    public static String IMResource = "m";
    public static String HTTP_HOST = "http://" + getMainUrl() + "/";
    public static String HTTP_IM = "http://115.28.241.232:8080";
    public static String IM_HOST = getIMService();
    public static String IM_SERVERNAME = "@" + getServerName();
    /**
     * 获取聊天服务器地址
     *
     * @return
     */
    public static String getIMService() {
        String url = "";
        if (conditionFlag == 0) {
            url = "115.28.241.232";
        } else if (conditionFlag == 1) {
            url = "14.215.133.20";
        } else if (conditionFlag == 2) {
        } else if (conditionFlag == 3) {
        } else if (conditionFlag == 4) {
        }
        return url;
    }

    public static String getServerName() {
        String name = "";
        if (conditionFlag == 0) {
            name = "sk";
        } else if (conditionFlag == 1) {
            name = "openfire";
        } else if (conditionFlag == 2) {
            name = "openfire";
        } else if (conditionFlag == 3) {
        } else if (conditionFlag == 4) {
            name = "openfire";
        }
        return name;
    }
    public static String getMainUrl() {
        String url = "ddysd.tunnel.qydev.com";
        if (conditionFlag == 0) {
        } else if (conditionFlag == 1) {
        } else if (conditionFlag == 2) {
        } else if (conditionFlag == 3) {
        } else if (conditionFlag == 4) {
        }
        return url;
    }
    public static String getDomainUrl(String url) {
        url = "http://" + getMainUrl();
        return url;
    }
    /*****************************************后台接口***********************************************************/
    public static String getUrlByMain(String url) {
        return HTTP_HOST+url;
    }
    /**好友接口*/
    public static final String FANS = "mobile/api/appService";
    /** 获取单个群相关属性 */
    public static String CHAT_GRUOP_BASE = "";
    /** 获取群列表 */
    public static String CHAT_GRUOP_LIST =HTTP_IM+"/im/gc/?api_key=";
    /**获取聊天服务器时间*/
    public static String GET_SERVICE_TIME="";
    /**粉丝备注*/
    public static final String FANS_REMARK ="" ;
    /**加关注*/
    public static final String FANS_OPERATE ="" ;
    /**举报*/
    public static final String REPORT ="" ;
    /**获取好友状态*/
    public static final String FRIEND_SATTUS = "";
    /**文件上传*/
    public static final String UP_LOAD =HTTP_HOST+"mobile/api/uploadChat/";
    /**登录接口*/
    public static final String LOGIN_URL = "";
    /**获取群信息\邀请群成员*/
    public static final String CHAT_GRUOP_INFO = HTTP_IM+"/im/gc/%s/members";
    /**删除群成员*/
    public static final String CHAT_GRUOP_DELETE_MEMBER =HTTP_IM+"/im/gc/%s/member";
    /**设置管理员*/
    public static final String CHAT_GROUP_SET_MANAGER = "";
    /**设置群主*/
    public static final String CHAT_GRUOP_UPDATE = "";
    /**保存到通讯录*/
    public static final String CHAT_GROUP_SET_MY_NICKNAME = "";
    /**上传头像*/
    public static final String UPLOADAVATAR = "";
    /**删除头像*/
    public static final String AVATAR_DALETE = "";
    /**设置头像*/
    public static final String SET_AVATAR = "";
    /**解散群*/
    public static final String CHAT_GROUP_DISSOLVE = "";
    /**群邀请*/
    public static final String CHAT_GRUOP_ADD_LINK = "";
    /**创建群*/
    public static final String CHAT_GRUOP_CREATE = HTTP_IM+ "/im/gc/?api_key=";
    /**重置二维码*/
    public static final String CHAT_GROUP_RESET_QRCODE = "";
    /**获取好友信息*/
    public static final String GET_FRIENDINFO ="" ;
}
