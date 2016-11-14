package chat.manager;

import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;

import com.android.volley.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.util.ComparatorUtil;
import chat.common.util.file.FileOpreation;
import chat.common.util.network.HttpRequetUtil;
import chat.common.util.string.PingYinUtil;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ContactsBean;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.IMUserBase;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.group.bean.ChatGroupInfoResultBean;
import chat.session.group.bean.ChatGroupMemberBean;
import chat.session.group.bean.RemoteMemberBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

public class ChatLoadManager {
    /* 获取最近聊天 */
    public final static int CHAT_LOAD_SESSIONS = 1;
    /**
     * 搜索联系人
     */
    public final static int CHAT_LOAD_SEARCH_CONTACT = 2;
    /* 搜索群组 */
    public final static int CHAT_LOAD_SEARCH_GROUP = 3;
    /* 加载群成员 */
    public final static int CHAT_LOAD_GROUP_MEMEBERS = 4;
    /* 加载魔商列表 */
    public final static int CHAT_LOAD_LIST_MOBIZ = 5;
    /* 新朋友列表 */
    public final static int CHAT_LOAD_LIST_NEWFRIEND = 6;
    /* 执行消息回执 */
    public final static int CHAT_REBACK_MSG = 7;
    /* 加载消息内容 */
    public final static int CHAT_LOAD_LIST = 8;
    /**
     * 更新消息列表
     */
    public final static int CHAT_UPDATE_LIST = 9;
    /**
     * 消息重发
     */
    public final static int CHAT_RESEND = 10;
    /**
     * 消息更新
     */
    public final static int CHAT_UPDATE = 11;
    /**
     * 语音加载
     */
    public final static int CHAT_VOICE_LOAD = 12;
    /**
     * 群信息处理
     */
    public final static int CHAT_GROUP_INFO = 13;
    /**
     * 更新会话列表
     */
    public final static int CHAT_UDDATE_SESSIONS = 14;
    /* 执行消息回执 */
    public final static int CHAT_REBACK_MSGS = 15;
    /**
     * 更新好友
     */
    public final static int CHAT_UPDATE_FRIEND = 16;
    /**
     * 加载旧消息表数据
     */
    public final static int CHAT_LOAD_OLDER_DATA = 17;
    /**
     * 加载临时消息
     */
    public final static int CHAT_LOAD_TEMB_MSG = 18;
    private static ChatLoadManager instatnce;
    /**
     * 线程池
     */
    private ExecutorService loadPoor = Executors.newFixedThreadPool(10);
    private ChatLoadCallable<HashMap<String, Object>> sessionsCallable;
    private ChatLoadCallable<HashMap<String, Object>> groupMembersCallable;
    private ChatLoadCallable<List<MessageBean>> listOfMobizCallable;
    private ChatLoadCallable<List<ImUserBean>> listOfNewFriendCallable;
    private ChatLoadCallable<List<MessageBean>> chatMessagesCallable;
    private ChatLoadCallable<String> rbackMsgCallable;
    private ChatLoadCallable<String> rbackMsgsCallable;
    private ChatLoadCallable<String> reSendMsgCallable;
    private ChatLoadCallable<List<MessageBean>> updateAllNickNameByIdCallable;
    private ChatLoadCallable<String> updateMessageCallable;
    private ChatLoadCallable<MessageBean> updateVoiceCallable;
    private ChatLoadCallable<String> updateChatGroupInfoCallable;
    private ChatLoadCallable<String> updateSessionsCallable;
    private ChatLoadCallable<String> updateFriendsCallable;
    private ChatLoadCallable<String> loadOldDataCallable;
    private ChatLoadCallable<String> loadTembMsgCallable;
    /**
     * 更新好友
     *
     * @return
     */
    private WBaseModel getFansModel;

    public static ChatLoadManager getInstance() {
        if (instatnce == null)
            instatnce = new ChatLoadManager();
        return instatnce;
    }

    /**
     * @return ExecutorService
     * @Title: initPoor
     * @param:
     * @Description: 初始化线程池
     */
    private ExecutorService initPoor() {
        if (loadPoor == null)
            loadPoor = Executors.newFixedThreadPool(10);
        return loadPoor;
    }

    /**
     * @return HashMap<String,Object>
     * @Title: getSessions
     * @param:
     * @Description: 获取最近聊天记录
     */
    public HashMap<String, Object> getSessions(boolean needToSetCatalog,
                                               boolean hasGroup) {
        initPoor();
        if (sessionsCallable == null)
            sessionsCallable = new ChatLoadCallable<HashMap<String, Object>>(
                    CHAT_LOAD_SESSIONS);
        sessionsCallable.setNeedToSetCatalog(needToSetCatalog, hasGroup);
        Future<HashMap<String, Object>> submit = loadPoor
                .submit(sessionsCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return HashMap<String,Object>
     * @Title: getGroupMembers
     * @param:
     * @Description: 获取群成员
     */
    public HashMap<String, Object> getGroupMembers(String roomId,
                                                   int pageIndex, int pageSize) {
        initPoor();
        if (groupMembersCallable == null)
            groupMembersCallable = new ChatLoadCallable<HashMap<String, Object>>(
                    CHAT_LOAD_GROUP_MEMEBERS);
        groupMembersCallable.setRoomId(roomId, pageIndex, pageSize);
        Future<HashMap<String, Object>> submit = loadPoor
                .submit(groupMembersCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MessageBean> getListOfMobiz() {
        initPoor();
        if (listOfMobizCallable == null)
            listOfMobizCallable = new ChatLoadCallable<List<MessageBean>>(
                    CHAT_LOAD_LIST_MOBIZ);
        Future<List<MessageBean>> submit = loadPoor.submit(listOfMobizCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ImUserBean> getListOfNewFriend() {
        initPoor();
        if (listOfNewFriendCallable == null)
            listOfNewFriendCallable = new ChatLoadCallable<List<ImUserBean>>(
                    CHAT_LOAD_LIST_NEWFRIEND);
        Future<List<ImUserBean>> submit = loadPoor
                .submit(listOfNewFriendCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return List<MessageBean>
     * @Title: getChatMessages
     * @param:
     * @Description: 加载消息内容
     */
    public List<MessageBean> getChatMessages(String sessionId, String shopId, long lastTime) {
        initPoor();
        if (chatMessagesCallable == null)
            chatMessagesCallable = new ChatLoadCallable<List<MessageBean>>(
                    CHAT_LOAD_LIST);
        chatMessagesCallable.setSessionId(sessionId, shopId, lastTime);
        Future<List<MessageBean>> submit = loadPoor
                .submit(chatMessagesCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return
     * @Title: updateSessions
     * @param:
     * @Description: 更新会话列表
     */
    public boolean updateSessions(String sessionId, String shopId, List<MessageBean> list,
                                  boolean isUpdateNumbers) {
        initPoor();
        if (updateSessionsCallable == null)
            updateSessionsCallable = new ChatLoadCallable<String>(
                    CHAT_UDDATE_SESSIONS);
        updateSessionsCallable.setUpdateSessions(sessionId, shopId, list,
                isUpdateNumbers);
        Future<String> submit = loadPoor.submit(updateSessionsCallable);
        try {
            return submit.get().length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return String
     * @Title: rebackMsg
     * @param:
     * @Description: 执行消息回执
     */
    public String rebackMsg(BaseActivity activity, MessageBean bean) {
        if (activity == null || bean == null)
            return "";
        initPoor();
        if (rbackMsgCallable == null)
            rbackMsgCallable = new ChatLoadCallable<String>(CHAT_REBACK_MSG);
        rbackMsgCallable.setMessageBean(activity, bean);
        Future<String> submit = loadPoor.submit(rbackMsgCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @return String
     * @Title: rebackMsgs
     * @param:
     * @Description: 执行消息回执
     */
    public String rebackMsgs(BaseActivity activity,
                             List<MessageBean> beans, MessageBean sessionBean) {
        if (activity == null || beans == null || sessionBean == null)
            return "";
        initPoor();
        if (rbackMsgsCallable == null)
            rbackMsgsCallable = new ChatLoadCallable<String>(CHAT_REBACK_MSGS);
        rbackMsgsCallable.setRebackMsgs(activity, beans, sessionBean);
        Future<String> submit = loadPoor.submit(rbackMsgsCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void removeChatActivity() {
        if (rbackMsgCallable != null) {
            rbackMsgCallable.activity = null;
        }
        if (rbackMsgsCallable != null) {
            rbackMsgsCallable.activity = null;
        }
    }

    /**
     * @return void
     * @Title: reSendMsg
     * @param:
     * @Description: 消息重发
     */
    public void reSendMsg(MessageBean bean, boolean isNotify, ChatUploadManager.OnUploadListener onUploadListener) {
        if (bean == null)
            return;
        initPoor();
        if (reSendMsgCallable == null)
            reSendMsgCallable = new ChatLoadCallable<String>(CHAT_RESEND);
        reSendMsgCallable.setChatReSend(bean, isNotify, onUploadListener);
        Future<String> submit = loadPoor.submit(reSendMsgCallable);
        try {
            submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return String
     * @Title: updateAllNickNameById
     * @param:
     * @Description: 更新昵称
     */
    public List<MessageBean> updateAllNickNameById(List<MessageBean> beans,
                                                   String contactId, String nickName) {
        if (beans == null || contactId == null || nickName == null)
            return null;
        initPoor();
        if (updateAllNickNameByIdCallable == null)
            updateAllNickNameByIdCallable = new ChatLoadCallable<List<MessageBean>>(
                    CHAT_UPDATE_LIST);
        updateAllNickNameByIdCallable.setUpdateNickData(beans, contactId,
                nickName);
        Future<List<MessageBean>> submit = loadPoor
                .submit(updateAllNickNameByIdCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beans;
    }

    /**
     * @return String
     * @Title: updateMessages
     * @param:
     * @Description: 更新消息内容
     */
    public String updateMessage(List<MessageBean> beans, String msgCode,int sendStatus) {
        if (beans == null)
            return null;
        initPoor();
        if (updateMessageCallable == null)
            updateMessageCallable = new ChatLoadCallable<String>(
                    CHAT_UPDATE);
        updateMessageCallable.setUpdateMessage(beans, msgCode,"",
                sendStatus,-1);
        Future<String> submit = loadPoor.submit(updateMessageCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return String
     * @Title: updateVoice
     * @param:
     * @Description: 更新消息内容
     */
    public MessageBean updateVoice(List<MessageBean> beans,String msgCode,String url,int sendStatus) {
        if (beans == null)
            return null;
        initPoor();
        if (updateVoiceCallable == null)
            updateVoiceCallable = new ChatLoadCallable<MessageBean>(
                    CHAT_VOICE_LOAD);
        updateVoiceCallable.setUpdateMessage(beans, msgCode, url,sendStatus,-1);
        Future<MessageBean> submit = loadPoor.submit(updateVoiceCallable);
        try {
            return submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return String
     * @Title: updateChatGroupInfo
     * @param:
     * @Description: 更新群信息
     */
    public void updateChatGroupInfo(Handler handler, String roomId,
                                    ChatGroupInfoResultBean bean, ChatGroupInfoResultBean.ChatGroupInfoData chatGroupInfoData,
                                    boolean isLoadFromServer, String cacheGroupInfo, boolean isFirstPage) {
        initPoor();
        if (updateChatGroupInfoCallable == null)
            updateChatGroupInfoCallable = new ChatLoadCallable<String>(
                    CHAT_GROUP_INFO);
        updateChatGroupInfoCallable.setChatGroupUpdate(handler, roomId, bean,
                chatGroupInfoData, isLoadFromServer, cacheGroupInfo,
                isFirstPage);
        Future<String> submit = loadPoor.submit(updateChatGroupInfoCallable);
        try {
            submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return String
     * @Title: updateChatGroupInfo
     * @param:
     * @Description: 更新群信息
     */
    public boolean loadOldData() {
        initPoor();
        if (loadOldDataCallable == null)
            loadOldDataCallable = new ChatLoadCallable<String>(
                    CHAT_LOAD_OLDER_DATA);
        Future<String> submit = loadPoor.submit(loadOldDataCallable);
        try {
            submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @Title: loadTembMsg
     * @param:
     * @Description: 更新群信息
     */
    public boolean loadTembMsg(long tembId,Handler handler) {
        initPoor();
        if (loadTembMsgCallable == null)
            loadTembMsgCallable = new ChatLoadCallable<String>(
                    CHAT_LOAD_TEMB_MSG);
        loadTembMsgCallable.setTembId(tembId);
        loadTembMsgCallable.setHandler(handler);
        Future<String> submit = loadPoor.submit(loadTembMsgCallable);
        try {
            submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //add by h.j.huang 页面销毁时将handler置空解耦，防止内存泄露
    public void reSetChatGroupInfoHandler() {
        if (updateChatGroupInfoCallable != null) {
            updateChatGroupInfoCallable.setHandler(null);
        }
    }

    /**
     * 更新好友列表
     */
    public void updateFriends() {
        initPoor();
        if (updateFriendsCallable == null) {
            updateFriendsCallable = new ChatLoadCallable<String>(CHAT_UPDATE_FRIEND);
        }
        Future<String> submit = loadPoor.submit(updateFriendsCallable);
        try {
            submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载聊天旧数据
     */
    private String doLoadOldData() {
        return "success";
    }

    private String doLoadTembMsgs(long tembId,Handler handler) {
        List<MessageBean> tembs = ChatTembMsgManager.getInstance().getTembs(tembId);
        if (tembs != null && tembs.size() > 0) {
            for (MessageBean temb : tembs) {
                temb = ChatMessageManager.getInstance().getMessagesOfTemb(temb.getSessionId(), "", temb.getMsgCode());
                ChatUtil.getInstance().resendMessage(temb, false, null);
            }
            if(handler!=null){
                handler.sendMessageDelayed(handler.obtainMessage(1000,tembs.get(tembs.size() - 1).getMsgTime()),5*1000);
            }
        }else{
            handler=null;
        }
        return "success";
    }

    private String doUpdateFriends() {
        if (getFansModel == null) {
            getFansModel = new WBaseModel<ContactsBean>(IMClient.getInstance().getContext(), ContactsBean.class);
        }
        HashMap<String,Object>map=new HashMap<String,Object>();
        map.put("code","chat-getFriendsList");
        map.put("userID",27889);
        map.put("pageIndex",String.valueOf(0));
        map.put("pageSize",String.valueOf(100));
        getFansModel.httpRequest(Request.Method.POST, URLConfig.getUrlByMain(URLConfig.FANS),
                HttpRequetUtil.getJson(map), new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof ContactsBean) {
                            ContactsBean friendList = (ContactsBean) data;
                            if (friendList.isResult()) {
                                if (friendList.getData().getList().size() > 0) {
                                    ChatContactManager.getInstance().insertContacts(friendList.getData().getList());
                                    String cacheFriends = Constant.CACHE_COMMON_PATH + IMClient.getInstance().getSSOUserId() + "_friends.txt";
                                    FileOpreation.writeObjectToFile(friendList, cacheFriends);// 将对象写入缓存文件
                                }
                            }
                        }
                    }
                });
        return "success";
    }
    /**
     * @return String
     * @Title: doUpdateSessions
     * @param:
     * @Description: 更新会话列表
     */
    private String doUpdateSessions(String sessionId, String shopId,
                                    List<MessageBean> sessions, boolean isUpdateNumbers) {
        if (sessions == null || sessionId == null)
            return "";
        int size = sessions.size();
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (sessionId.equals(sessions.get(i).getSessionId())) {
                index = i;
                break;
            }
        }
        if (isUpdateNumbers) {
            if (index != -1) {
                sessions.get(index).setUnReads(0);
                return "success";
            }
        } else {
            MessageBean sessionBean = ChatSessionManager.getInstance()
                    .getSessionById(sessionId, shopId);
            if (sessionBean != null) {
                if (index != -1) {
                    sessions.set(index, sessionBean);
                } else {
                    sessions.add(sessionBean);
                }
                Collections.sort(sessions, ComparatorUtil.getInstance()
                        .getChatComparator());
                return "success";
            } else {
                if (index != -1) {
                    sessions.remove(index);
                    return "success";
                }
            }
        }
        return "";
    }

    public String doUpdateGroupInfo(Handler handler, String roomId,
                                    ChatGroupInfoResultBean bean, ChatGroupInfoResultBean.ChatGroupInfoData chatGroupInfoData,
                                    boolean isLoadFromServer, String cacheGroupInfo, boolean isFirstPage) {
        ArrayList<Long> insertGroupMembers = ChatMembersManager.getInstance()
                .insertGroupMembers(roomId, bean);
        if (isLoadFromServer) {
            if (handler != null && insertGroupMembers.size() > 0)
                handler.sendEmptyMessage(0);
        }
        if (isFirstPage) {
            ChatGroupManager.getInstance().updateGroup(chatGroupInfoData);
            FileOpreation.writeObjectToFile(bean, cacheGroupInfo);// 将对象写入缓存文件
        }
        return null;
    }

    /**
     * @return MessageBean
     * @Title: doUpdateVoice
     * @param:
     * @Description: 更新语音
     */
    public MessageBean doUpdateVoice(List<MessageBean> beans, String msgCode,String url,int statue) {
            Iterator<MessageBean> iterator = beans.iterator();
            while (iterator.hasNext()) {
                MessageBean bean = iterator.next();
                if (bean.getMsgCode().equals(msgCode)) {
                    ChatMessageManager.getInstance().updateMessageReadStatus(bean.getSessionId(), "",
                            msgCode, 1);
                    if (bean.getAttachment() != null) {
                        bean.setMsgStatus(MsgStatusEnum.fromInt(statue));
                        if (statue != MsgStatusEnum.fail.getValue()) {
                            IMChatAudioBody audioBody= bean.getAttachment();
                            audioBody.setLocalUrl(url);
                        }
                        return bean;
                    }
                }
            }
        return null;
    }

    /**
     * @return String
     * @Title: updateMessages
     * @param:
     * @Description: 更新消息内容
     */
    private List<MessageBean> updateMessages(List<MessageBean> beans,
                                             String contactId, String nickName) {
        if (beans == null)
            return null;
        Iterator<MessageBean> iterator = beans.iterator();
        while (iterator.hasNext()) {
            MessageBean bean = iterator.next();
            ImUserBean userBean = bean.getImUserBean();
            if (userBean == null || TextUtils.isEmpty(userBean.getMxId()))// 群系统消息无用户信息
                continue;
            if (userBean.getMxId().equals(contactId)) {
                userBean.setName(nickName);
            }
        }
        return beans;
    }

    /**
     * @return List<MessageBean>
     * @Title: getMessages
     * @param:
     * @Description: 获取消息内容
     */
    private List<MessageBean> getMessages(String sessionId, String shopId, long lastTime) {
        List<MessageBean> messages = ChatMessageManager.getInstance()
                .getMessages(sessionId, shopId, lastTime);
        Collections.sort(messages, ComparatorUtil.getInstance()
                .getMessagesComparator());
        return messages;
    }

    /**
     * @return String
     * @Title: doResendMessage
     * @param:
     * @Description: 消息重发
     */
    private String doResendMessage(MessageBean bean, boolean isNotify, ChatUploadManager.OnUploadListener onUploadListener) {
        if (bean != null) {
            ChatUtil.getInstance().resendMessage(bean, isNotify, onUploadListener);
        }
        return "";
    }

    /**
     * @Title: doUpdateMessage
     * @param:
     * @Description: 执行更新消息
     */
    public String doUpdateMessage(List<MessageBean> beans,String msgCode,int isReaded,int sendStatus,long time) {
        Iterator<MessageBean> iterator = beans.iterator();
        int index=-1;
        while (iterator.hasNext()) {
            index++;
            MessageBean bean = iterator.next();
            if (bean.getMsgCode().equals(msgCode)) {
                bean.setIsRead(isReaded);
                bean.setIsListen(isReaded);
                bean.setMsgStatus(MsgStatusEnum.fromInt(sendStatus));
                if (time > 0)
                    bean.setMsgTime(time);
                return  String.valueOf(index);
            }
        }
        return null;
    }

    /**
     * @return void
     * @Title: release
     * @param:
     * @Description: 释放资源
     */
    public void release() {
        if (sessionsCallable != null) {
            sessionsCallable = null;
        }
        if (groupMembersCallable != null)
            groupMembersCallable = null;
        if (listOfMobizCallable != null)
            listOfMobizCallable = null;
        if (listOfNewFriendCallable != null)
            listOfNewFriendCallable = null;
        if (chatMessagesCallable != null)
            chatMessagesCallable = null;
        if (rbackMsgCallable != null)
            rbackMsgCallable = null;
        if (reSendMsgCallable != null)
            reSendMsgCallable = null;
        if (updateAllNickNameByIdCallable != null)
            updateAllNickNameByIdCallable = null;
        if (updateMessageCallable != null)
            updateMessageCallable = null;
        if (updateChatGroupInfoCallable != null)
            updateChatGroupInfoCallable = null;
        if (updateSessionsCallable != null) {
            updateSessionsCallable = null;
        }
        if (loadOldDataCallable != null) {
            loadOldDataCallable = null;
        }
        if (loadTembMsgCallable != null) {
            loadTembMsgCallable.setHandler(null);
            loadTembMsgCallable = null;
        }
        if (loadPoor != null) {
            loadPoor.shutdown();
            loadPoor = null;
        }
    }

    public class ChatLoadCallable<T> implements Callable<T> {
        /* 用于传递群成员列表数据集 */
        private RemoteMemberBean remoteMembeBean;
        /* 加载类型 */
        private int loadMode = 0;
        /* 搜索关键字 */
        private String searchTxt;
        /* 群id */
        private String roomId;
        /**
         * 是否需要分类
         */
        private boolean needToSetCatalog;
        /**
         * 是否有群组
         */
        private boolean hasGroup;
        /* 消息体 */
        private MessageBean bean;
        /* 基类 */
        private BaseActivity activity;
        /* 会话id */
        private String sessionId;
        /* 最后一条记录的时间 */
        private long lastTime;
        /* 联系人id */
        private String contactId;
        /**
         * 昵称
         */
        private String nickName;
        /**
         * 消息集合
         */
        private List<MessageBean> beans;
        private int isReaded, sendStatus;
        private int pageIndex;
        private int pageSize;
        // chatgroupinfo
        private Handler handler;
        private ChatGroupInfoResultBean beanChatGroup;
        private ChatGroupInfoResultBean.ChatGroupInfoData chatGroupInfoData;
        private boolean isLoadFromServer;
        private String cacheGroupInfo;
        private boolean isFirstPage;

        //
        private List<MessageBean> sessions;
        private boolean isUpdateNumbers;
        private String shopId;
        private boolean isNotify;
        private ChatUploadManager.OnUploadListener onUploadListener;
        private long tembId;
        private String msgCode;
        private String url;
        private long time;
        public ChatLoadCallable(int loadMode) {
            this.loadMode = loadMode;
        }

        public void setUpdateSessions(String sessionId, String shopId,
                                      List<MessageBean> sessions, boolean isUpdateNumbers) {
            this.sessionId = sessionId;
            this.shopId = shopId;
            this.sessions = sessions;
            this.isUpdateNumbers = isUpdateNumbers;
        }

        public void setSessionId(String sessionId, String shopId, long lastTime) {
            this.sessionId = sessionId;
            this.lastTime = lastTime;
            this.shopId = shopId;
        }

        public void setMessageBean(BaseActivity activity, MessageBean bean) {
            this.activity = activity;
            this.bean = bean;
        }

        public void setRebackMsgs(BaseActivity activity,
                                  List<MessageBean> beans, MessageBean sessionBean) {
            this.activity = activity;
            this.beans = beans;
        }

        public void setChatReSend(MessageBean bean, boolean isNotify, ChatUploadManager.OnUploadListener onUploadListener) {
            this.bean = bean;
            this.isNotify = isNotify;
            this.onUploadListener = onUploadListener;
        }

        public void setNeedToSetCatalog(boolean needToSetCatalog,
                                        boolean hasGroup) {
            this.needToSetCatalog = needToSetCatalog;
            this.hasGroup = hasGroup;
        }

        public void setRoomId(String roomId, int pageIndex, int pageSize) {
            this.roomId = roomId;
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        public void setUpdateNickData(List<MessageBean> beans,
                                      String contactId, String nickName) {
            this.beans = beans;
            this.contactId = contactId;
            this.nickName = nickName;
        }

        public void setUpdateMessage(List<MessageBean> beans, String msgCode,String url,int sendStatus,long time) {
            this.beans = beans;
            this.msgCode = msgCode;
            this.url = url;
            this.sendStatus = sendStatus;
            this.time=time;
        }

        public void setChatGroupUpdate(Handler handler, String roomId,
                                       ChatGroupInfoResultBean beanChatGroup,
                                       ChatGroupInfoResultBean.ChatGroupInfoData chatGroupInfoData, boolean isLoadFromServer,
                                       String cacheGroupInfo, boolean isFirstPage) {
            this.handler = handler;
            this.roomId = roomId;
            this.beanChatGroup = beanChatGroup;
            this.chatGroupInfoData = chatGroupInfoData;
            this.isLoadFromServer = isLoadFromServer;
            this.cacheGroupInfo = cacheGroupInfo;
            this.isFirstPage = isFirstPage;
        }

        public void setHandler(Handler handler) {
            this.handler = handler;
        }
        public void setTembId(long tembId) {
            this.tembId=tembId;
        }
        @SuppressWarnings("unchecked")
        @Override
        public T call() throws Exception {
            switch (loadMode) {
                case CHAT_LOAD_SESSIONS:
                    return (T) loadSessions();
                case CHAT_LOAD_SEARCH_CONTACT:
                    return (T) loadContact();
                case CHAT_LOAD_SEARCH_GROUP:
                    return (T) ChatGroupManager.getInstance().getGroupListByName(
                            searchTxt);
                case CHAT_LOAD_GROUP_MEMEBERS:
                    return (T) loadMembers(roomId, pageIndex, pageSize);
                case CHAT_LOAD_LIST_NEWFRIEND:
                    return (T) ChatMessageManager.getInstance();
                case CHAT_LOAD_LIST:
                    return (T) getMessages(sessionId, shopId, lastTime);
                case CHAT_UPDATE_LIST:
                    return (T) updateMessages(beans, contactId, nickName);
                case CHAT_RESEND:
                    return (T) doResendMessage(bean, isNotify, onUploadListener);
                case CHAT_UPDATE:
                    return (T) doUpdateMessage(beans, msgCode, isReaded,
                            sendStatus,time);
                case CHAT_VOICE_LOAD:
                    return (T) doUpdateVoice(beans, msgCode, url,sendStatus);
                case CHAT_GROUP_INFO:
                    return (T) doUpdateGroupInfo(handler, roomId, beanChatGroup,
                            chatGroupInfoData, isLoadFromServer, cacheGroupInfo,
                            isFirstPage);
                case CHAT_UDDATE_SESSIONS:
                    return (T) doUpdateSessions(sessionId, shopId, sessions,
                            isUpdateNumbers);
                case CHAT_UPDATE_FRIEND:
                    return (T) doUpdateFriends();
                case CHAT_LOAD_OLDER_DATA:
                    return (T) doLoadOldData();
                case CHAT_LOAD_TEMB_MSG:
                    return (T) doLoadTembMsgs(tembId,handler);
                default:
                    break;
            }
            return null;
        }

        /**
         * @return HashMap<String,Object>
         * @Title: loadSessions
         * @param:
         * @Description: 加载所有会话
         */
        private HashMap<String, Object> loadSessions() {
            List<MessageBean> beans = ChatSessionManager.getInstance()
                    .getSessions("");
            HashMap<String, Object> map = new LinkedHashMap<String, Object>();
            List<IMUserBase> users = new ArrayList<IMUserBase>();
            int allTopCount = 0;
            for (MessageBean bean : beans) {
                if (needToSetCatalog) {
                    IMUserBase user = new IMUserBase();
                    String name = "", catalog = "";
                    if (bean.getSessionId().equals(
                            ChatUtil.CHAT_SERVICE_ID + "@1")
                            || bean.getSessionId().equals(
                            IMTypeUtil.SessionType.FRIEND.name())) {
                        continue;
                    }
                    if (bean.getSessionType() == SessionTypeEnum.GROUPCHAT) {
                        if (!hasGroup)
                            continue;
                        name = bean.getChatGroupBean().getData().getGroupName();
                        user.setAvatarUrl(bean.getChatGroupBean().getData()
                                .getPhotoUrl());
                    } else {
                        if (!hasGroup && bean.getSessionId().contains("_"))
                            continue;
                        name = bean.getImUserBean().getName();
                        user.setAvatarUrl(bean.getImUserBean().getAvatar());
                    }
                    if (!TextUtils.isEmpty(name)) {
                        catalog = PingYinUtil
                                .converterToFirstSpell(name.substring(0, 1))
                                .substring(0, 1).toUpperCase(Locale.CHINA);
                    } else {
                        catalog = "#";
                    }
                    user.setId(bean.getChatWith());
                    user.setName(name);
                    user.setCatalog(catalog);
                    user.setBoxType(bean.getSessionType().getValue());
                    user.setTime(bean.getMsgTime());
                    users.add(user);
                } else {
                    if (bean.getIsTop() == 1)
                        allTopCount++;
                }
            }
            if (needToSetCatalog) {
                map.put("data", users);
                Collections.sort(users, ComparatorUtil.getInstance()
                        .getChatCatalogComparator());
            } else {
                map.put("data", beans);
                map.put("allTopCount", allTopCount);
                Collections.sort(beans, ComparatorUtil.getInstance()
                        .getChatComparator());
            }

            return map;
        }

        /**
         * @return ArrayList<ImUserBean>
         * @Title: loadContact
         * @param:
         * @Description: 加载联系人
         */
        private ArrayList<ImUserBean> loadContact() {
            if (remoteMembeBean == null)
                return ChatContactManager.getInstance().getContactsByName(
                        searchTxt);
            else {
                SparseArray<ContactBean> membsers = remoteMembeBean
                        .getMembsers();
                ArrayList<ImUserBean> imUserBeans = new ArrayList<ImUserBean>();
                int size=membsers.size();
                for(int i=0;i<size;i++){
                    if (searchTxt.length() > 0) {
                        ContactBean bean = membsers.get(membsers.keyAt(i));
                        if (bean.getShowName().toLowerCase()
                                .contains(searchTxt.toLowerCase())) {
                            ImUserBean imUserBean = new ImUserBean();
                            imUserBean.setAvatar(bean.getUserImg());
                            imUserBean
                                    .setMxId(String.valueOf(bean.getFriendID()));
                            imUserBean.setName(bean.getUserNickname());
                            imUserBean.setRoleType(bean.getRole());
                            imUserBean.setRemark(bean.getRemarkName());
                            imUserBeans.add(imUserBean);
                        }
                    }
                }
                return imUserBeans;
            }
        }

        /**
         * @return HashMap<String,Object>
         * @Title: loadMembers
         * @param:
         * @Description: 加载群成员
         */
        private HashMap<String, Object> loadMembers(String roomId,
                                                    int pageIndex, int pageSize) {
            if (roomId == null)
                return null;
            HashMap<String, Object> map = new LinkedHashMap<String, Object>();
            ArrayList<ChatGroupMemberBean> groupMembers = ChatMembersManager
                    .getInstance().getGroupMembers(roomId, pageIndex, pageSize);
            StringBuilder memebers=new StringBuilder();
            map.put("nickname",
                    ChatMembersManager.getInstance()
                            .getMemberNameOfGroup(
                                    roomId,
                                    IMClient.getInstance()
                                            .getSSOUserId().trim()));
            map.put("admins", ChatMembersManager.getInstance()
                    .getAdminsOfGroup(roomId));
            map.put("members", ChatMembersManager.getInstance()
                    .getMembersOfGroup(roomId));
            map.put("data", groupMembers);
            for(ChatGroupMemberBean member:groupMembers){
                memebers.append(member.getUserId());
                memebers.append(",");
            }
            map.put("memberIds",memebers.toString());
            return map;
        }
    }
}
