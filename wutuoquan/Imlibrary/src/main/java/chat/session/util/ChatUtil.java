package chat.session.util; /**
 *
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.imlibrary.R;

import java.io.File;
import java.util.List;

import chat.base.IMClient;
import chat.card.bean.GiveCardBean;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.user.UserInfoHelp;
import chat.common.util.TextUtils;
import chat.common.util.file.FileOpreation;
import chat.common.util.output.ShowUtil;
import chat.common.util.storage.PreferencesHelper;
import chat.contact.bean.AvatarBean;
import chat.contact.bean.ContactBean;
import chat.contact.bean.ImageItem;
import chat.contact.bean.UserBean;
import chat.image.photo.utils.PickPhotoUtils;
import chat.manager.ChatContactManager;
import chat.manager.ChatGroupManager;
import chat.manager.ChatMessageManager;
import chat.manager.ChatSessionManager;
import chat.manager.ChatUploadManager;
import chat.manager.XmppSessionManager;
import chat.service.ChatService;
import chat.service.MessageInfoReceiver;
import chat.session.MessageBuilder;
import chat.session.activity.ChatActivity;
import chat.session.activity.ForwardMessageActivity;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.IMChatBaseBody;
import chat.session.bean.IMChatFileBody;
import chat.session.bean.IMChatGifBody;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.IMChatLocationBody;
import chat.session.bean.IMChatTextBody;
import chat.session.bean.IMChatVideoBody;
import chat.session.bean.IMRichCouponBody;
import chat.session.bean.IMRichDynamicBody;
import chat.session.bean.IMRichGoodsBody;
import chat.session.bean.IMRichNoticeBody;
import chat.session.bean.IMUserBody;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.bean.MsgSInviteBody;
import chat.session.bean.MsgSSBody;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.group.bean.ChatGroupBean;
import chat.session.group.bean.ChatGroupData;
import chat.session.group.bean.IMSGroupAdmins;
import chat.session.group.bean.IMSGroupBaseBean;
import chat.session.group.bean.IMSGroupMembers;
import chat.session.group.bean.IMSGroupUpdate;
import chat.session.observe.MessageClient;

/**
 * @Description  聊天工具类
 */
public class ChatUtil {
    public final static String ACTION_FOLLOW = "com.chat.follow";
    /**
     * 聊天分隔符:用于操作用户Id
     */
    public final static String CHAT_SPLIT_USERID = "_";
    /**
     * 聊天分隔符:用于操作店铺Id
     */
    public final static String CHAT_SPLIT_SHOPID = "@";
    public static final int REQUEST_ROOM = 100;
    public static final String CHAT_SERVICE_ID = "100001";
    public static final int PAGE_SIZE_CHAT = 20;
    /* 最大置顶数 */
    public static final int MAX_TOP = 5;
    private Context context;
    private static ChatUtil instance;
    private PreferencesHelper chatHelper;

    public ChatUtil(Context context) {
        this.context = context;
    }

    public static ChatUtil getInstance() {
        if (instance == null) {
            instance = new ChatUtil(IMClient.getInstance().getContext());
        }
        return instance;
    }
    public String getHeadUrl(UserBean bean) {
        List<AvatarBean> userAvatarList = bean.getData().getUserAvatarList();
        int size = userAvatarList.size();
        String headUrl = "";
        for (int i = 0; i < size; i++) {
            if (userAvatarList.get(i).getIsAvatar() == 1) {
                headUrl = userAvatarList.get(i).getAvatarUrl();
                break;
            }
        }
        return headUrl;
    }
    /**
     * 转发一个IMMessageBean 对象
     *
     * @param context
     * @param bean
     * @return
     */
    public static Intent transpond(Context context, MessageBean bean,
                                   boolean isNeedGroup) {
        Intent intent = new Intent();
        intent.setClass(context,ForwardMessageActivity.class);
        intent.putExtra("isNeedGroup", isNeedGroup);
        intent.putExtra(ChatActivity.TRANSPOND_TYPE, 1);
        intent.putExtra(ChatActivity.MESSAGEBEAN, bean);
        return intent;
    }
    /**
     * 保存图片到相册 IMMessageBean类型
     *
     * @param context
     * @return
     */
    public static boolean savePhoto(Context context, MessageBean messageBean) {
        IMChatImageBody imageBody = messageBean.getAttachment();
        String url;
        url = imageBody.getLocalUrl();
        if (url == null || url.length() == 0) {
            url = imageBody.getAttr1();
            if (TextUtils.getString(url).length() > 0
                    && !url.startsWith("http:")) {
                url = URLConfig.getDomainUrl(Constant.DOMAIN_IMAGE_TYPE) + url;
            }
            File imageFile = IMClient.getInstance()
                    .findInImageLoaderCache(url);
            if (imageFile != null && imageFile.exists())
                url = imageFile.getAbsolutePath();
        }
        String path = Environment.getExternalStorageDirectory().toString()
                + "/Moxian";
        File path1 = new File(path);
        if (!path1.exists()) {
            path1.mkdirs();
        }
        File file = new File(path1, messageBean.getMsgCode() + ".jpg");
        boolean copyFile = FileOpreation.copyFile(url, file.getAbsolutePath());
        if (copyFile)
            Toast.makeText(
                    context.getApplicationContext(),
                    context.getString(R.string.chat_textview_savefile_tip)
                            + file.getAbsolutePath(), Toast.LENGTH_SHORT)
                    .show();
        PickPhotoUtils.getInstance().takeResult(context, null, file);
        /**
         * 刷新文件夹
         */
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        return copyFile;
    }

    /**
     * 保存图片到相册 List ImageItem类型
     *
     * @param context
     * @param selected
     */
    public static void savePhoto(Context context, List<ImageItem> selected) {
        for (int i = 0; i < selected.size(); i++) {
            ImageItem item = selected.get(i);
            String dir = Environment.getExternalStorageDirectory().toString();
            String path = Constant.IMAGES_FOLDER;
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            String convateUrl = "";
            if (item.imagePath.startsWith(dir)) {//处理本地图片和网络图片
                convateUrl = item.imagePath;
            } else {
                convateUrl = IMClient.sImageLoader
                        .convateUrl(item.imagePath);
            }
            File file = new File(path1, item.imageId + ".jpg");
            File findInImageLoaderCache = IMClient.getInstance()
                    .findInImageLoaderCache(convateUrl);
            if (file != null) {
                boolean copyFile = false;
                if (findInImageLoaderCache != null) {
                    copyFile = FileOpreation.copyFile(
                            findInImageLoaderCache.getAbsolutePath(),
                            file.getAbsolutePath());
                } else {//有些本地图片是不存储到第三方缓存路径的
                    if (!android.text.TextUtils.isEmpty(convateUrl)) {
                        File oldFile = new File(convateUrl);
                        if (oldFile != null && oldFile.exists()) {
                            copyFile = FileOpreation.copyFile(convateUrl,
                                    file.getAbsolutePath());
                        }
                    }
                }
                ShowUtil.showToast(
                        context,
                        copyFile ? context.getString(R.string.chat_photo_save_succ) : context
                                .getString(R.string.chat_photo_save_failed));
                /**
                 * 刷新文件夹
                 */
                if (copyFile) {
                    Intent intent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                }
            }
        }


    }

    /**
     * 从一个列表里删除另外一个列表的图片对象，刷新UI，并且从数据库删除消息题，并且删除文件，
     *
     * @param adapter
     * @param dataList
     * @param selected
     * @return
     */
    public static boolean removeImageItem(BaseAdapter adapter,
                                          List<ImageItem> dataList, List<ImageItem> selected) {
        boolean remove = false;
        for (int i = 0; i < selected.size(); i++) {
            ImageItem item = selected.get(i);
            remove = dataList.remove(item);
            ChatMessageManager.getInstance().deleteMessageById(item.imageId,
                    item.sessionId, item.shopId);
            if (item.imagePath != null)
                FileOpreation.delFile(item.imagePath);
            adapter.notifyDataSetChanged();

        }
        return remove;
    }

    /**
     * 从一个列表里删除另外一个列表的图片对象，刷新UI，并且从数据库删除消息题，并且删除文件，
     *
     * @param adapter
     * @param dataList
     * @param selected
     * @return
     */
    public static boolean removeImageItem(PagerAdapter adapter,
                                          List<ImageItem> dataList, List<ImageItem> selected) {
        boolean remove = false;
        for (int i = 0; i < selected.size(); i++) {
            ImageItem item = selected.get(i);
            remove = dataList.remove(item);
            ChatMessageManager.getInstance().deleteMessageById(item.imageId,
                    item.sessionId, item.shopId);
            if (item.imagePath != null)
                FileOpreation.delFile(item.imagePath);

            adapter.notifyDataSetChanged();

        }
        return remove;
    }

    /**
     * @return void
     * @Title: gotoChatRoom
     * @param:
     * @Description: 群聊
     */
    public static void gotoChatRoom(Activity activity, ChatGroupBean groupBean) {
        if (groupBean == null)
            return;
        ChatGroupData data = groupBean.getData();
        MessageBean sessionBean = new MessageBean();
        ImUserBean bean = new ImUserBean();
        bean.setName(data.getGroupName());
        bean.setMxId(String.valueOf(data.getId()));
        bean.setAvatar(data.getPhotoUrl());
        sessionBean.setImUserBean(bean);
        sessionBean.setChatWith(String.valueOf(data.getId()));
        sessionBean.setSessionType(SessionTypeEnum.GROUPCHAT);
        sessionBean.setSessionId(data.getId() + "@" + SessionTypeEnum.GROUPCHAT.getValue());
        sessionBean.setTo(String.valueOf(data.getId()));
        sessionBean.setChatGroupBean(groupBean);
        gotoChatRoom(activity, sessionBean);
    }

    /**
     * @return void
     * @Title: gotoChatRoom
     * @param:
     * @Description: 单聊
     */
    public static void gotoChatRoom(Activity activity, ContactBean data) {
        MessageBean sessionBean = new MessageBean();
        ImUserBean imUser = new ImUserBean();
        imUser.setMxId(String.valueOf(data.getFriendID()));
        String nickName = data.getShowName();
        imUser.setName(nickName);
        imUser.setAvatar(data.getUserImg());
        imUser.setFansStatus(data.getState());
        ChatContactManager.getInstance().insertContact(imUser);
        sessionBean.setChatWith(String.valueOf(data.getFriendID()));
        sessionBean.setSessionType(SessionTypeEnum.NORMAL);
        sessionBean.setSessionId(data.getFriendID() + "@" + SessionTypeEnum.NORMAL.getValue());
        sessionBean.setImUserBean(imUser);
        gotoChatRoom(activity, sessionBean);
    }

    /**
     * @return void
     * @Title: gotoChatRoom
     * @param:
     * @Description: 单聊
     */
    public static void gotoChatRoom(Activity activity, ImUserBean imUser) {
        MessageBean sessionBean = new MessageBean();
        ChatContactManager.getInstance().insertContact(imUser);
        sessionBean.setChatWith(imUser.getMxId());
        sessionBean.setTo(imUser.getMxId());
        sessionBean.setFrom(IMClient.getInstance().getSSOUserId());
        sessionBean.setSessionType(SessionTypeEnum.NORMAL);
        sessionBean.setImUserBean(imUser);
        sessionBean.setSessionId(imUser.getMxId() + "@" + sessionBean.getSessionType().getValue());
        gotoChatRoom(activity, sessionBean);
    }

    public static void gotoChatRoom(Activity activity, MessageBean sessionBean) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.SESSION_DATA, sessionBean);
        activity.startActivityForResult(intent, REQUEST_ROOM);
    }

    public static void gotoChatRoom(Activity activity, MessageBean sessionBean,
                                    GiveCardBean giveCardBean) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("toID", "" + sessionBean.getChatWith());
        intent.putExtra(ChatActivity.SESSION_DATA, sessionBean);
        intent.putExtra(ChatActivity.CHAT_CARD, giveCardBean);
        activity.startActivityForResult(intent, REQUEST_ROOM);
    }
    /**
     * @return void
     * @Title: insertMessage
     * @param:
     * @Description: 插入一条新消息
     */
    public long insertMessage(MessageBean messageBean) {
        if (messageBean == null)
            return -1;
        if (messageBean == null)
            return -1;
        if (messageBean.getSessionType() != SessionTypeEnum.NORMAL) {
            boolean  needUpdateGrouopName = false;
            boolean isUpdateGroup = false;
            if (SessionTypeEnum.SGROUP==messageBean.getSessionType()) {
                if (messageBean.getMsgType().getValue() == IMTypeUtil.SGroupTy.UPDATE_BASE) {
                    IMSGroupBaseBean sBaseBean= messageBean.getAttachment();
                    String executorId =sBaseBean.getExecutorId();
                    if (!IMClient.getInstance().getSSOUserId()
                            .equals(executorId)) {
                        isUpdateGroup = true;
                        needUpdateGrouopName = true;
                    }
                }
            }
            if (isUpdateGroup) {//更新群的基本信息
                if (needUpdateGrouopName)
                    ChatGroupManager.getInstance().updateGroupName(messageBean.getChatWith(), messageBean.getImUserBean().getName());
            }
        } else {
            if (SessionTypeEnum.SGROUP!=messageBean.getSessionType()) {// 过滤提示类消息
                ImUserBean imUserBean = messageBean.getImUserBean();
                if (SessionTypeEnum.SS==messageBean.getSessionType()) {
                    if (messageBean.getMsgType().getValue() != IMTypeUtil.SSType.FOLLOW) {
                        imUserBean.setMxId(CHAT_SERVICE_ID);
                        messageBean.setSessionId(CHAT_SERVICE_ID + "@"
                                + IMTypeUtil.BoxType.SINGLE_CHAT);
                        messageBean.setChatWith(CHAT_SERVICE_ID);
                        messageBean.setFrom(CHAT_SERVICE_ID);
                        imUserBean.setName(IMClient.getInstance().getContext()
                                .getString(R.string.chat_service));
                    }
                } else if (SessionTypeEnum.S==messageBean.getSessionType()) {
                    if (messageBean.getMsgType().getValue() == IMTypeUtil.STy.S_INVITE) {
                        messageBean.setSession(messageBean.getSession());
                    }
                }
//                if (SessionTypeEnum.FOLLOW!=messageBean.getSessionType())
//                    ChatContactManager.getInstance().insertContact(imUserBean);
            }
        }
        long size = -1;
        String session = messageBean.getSession();
        String sessionId = messageBean.getSessionId();
        if (IMTypeUtil.SessionType.FRIEND.name().equals(sessionId)) {
            session = messageBean.getImUserBean().getName() + session;
            size = ChatSessionManager.getInstance()
                    .insertSession(messageBean, session) ? 1 : -1;
        } else {
            size = ChatMessageManager.getInstance().insertMessage(messageBean);
            if (size > 0) {
                if (IMTypeUtil.SessionType.FRIEND.name().equals(sessionId)) {
                    session = messageBean.getImUserBean().getName() + session;
                }
                ChatSessionManager.getInstance()
                        .insertSession(messageBean, session);
            }
        }
        return size;
    }

    public static void initSSImUserBean(ImUserBean imUserBean, MsgSSBody ssBody) {
        imUserBean.setMxId(ssBody.getField5());
        imUserBean.setAvatar(ssBody.getField4());
        imUserBean.setName(ssBody.getField6());
        try {
            imUserBean.setGender(Integer.valueOf(ssBody.getField3()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            imUserBean.setBirthday(Long.valueOf(ssBody.getField2()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @return void
     * @Title: paseBodyForChat
     * @param:
     * @Description: 解析chat类型【基本聊天消息】消息
     */
    public static void  paseBodyForChat(String body, MessageBean messageBean,
                                        int type, String fromId, String toId) {
        messageBean.setFrom(fromId);
        messageBean.setTo(toId);
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.fromInt(type);
        switch (msgTypeEnum) {
            case text:
                IMChatTextBody txtBody = JSON.parseObject(body,
                        IMChatTextBody.class);
                messageBean.setAttachment(txtBody);
                messageBean.setSession(txtBody.getAttr1());
                break;
            case gif:
                IMChatGifBody gifBody = JSON.parseObject(body,
                        IMChatGifBody.class);
                messageBean.setAttachment(gifBody);
                messageBean.setSession(IMClient.getInstance().getContext().getString(
                        R.string.gif));
                break;
            case image:
                IMChatImageBody imgBody = JSON.parseObject(body,
                        IMChatImageBody.class);
                if (toId.equals(IMClient.getInstance().getSSOUserId()))// 只有接收到的消息才设置null
                    imgBody.setLocalUrl(null);
                messageBean.setAttachment(imgBody);
                messageBean.setSession(IMClient.getInstance().getContext().getString(
                        R.string.picture));
                break;
            case audio:
                IMChatAudioBody voiceBody = JSON.parseObject(body,
                        IMChatAudioBody.class);
                messageBean.setAttachment(voiceBody);
                messageBean.setSession(IMClient.getInstance().getContext().getString(
                        R.string.voice));
                break;
            case location:
                IMChatLocationBody locationBody = JSON.parseObject(body,
                        IMChatLocationBody.class);
                messageBean.setAttachment(locationBody);
                messageBean.setSession(IMClient.getInstance().getContext().getString(
                        R.string.location_recv));
                break;
            case file:
                IMChatFileBody fileBody = JSON.parseObject(body,
                        IMChatFileBody.class);
                messageBean.setAttachment(fileBody);
                messageBean.setSession(fileBody.getAttr1());
                break;
            case video:
                IMChatVideoBody videoBody = JSON.parseObject(body,
                        IMChatVideoBody.class);
                messageBean.setAttachment(videoBody);
                messageBean.setSession(IMClient.getInstance().getContext().getString(
                        R.string.video));
            default:
                break;
        }
    }
    /***
     * @return void
     * @Title: paseBodyForRich
     * @param:
     * @Description: 解析rich类型【富消息】消息
     */
    public static void paseBodyForRich(String body, MessageBean messageBean,
                                       int type, String fromId, String toId) {
        messageBean.setFrom(fromId);
        messageBean.setTo(toId);
        messageBean.setSession("");
        switch (type) {
            case IMTypeUtil.RichTy.GOODS:
                IMRichGoodsBody goodsBody = JSON.parseObject(body,
                        IMRichGoodsBody.class);
                messageBean.setSession(IMClient.getInstance().getContext().getString(R.string.chat_goods_link) + goodsBody.getField3());
                messageBean.setAttachment(goodsBody);
                break;
            case IMTypeUtil.RichTy.COUPON:
            case IMTypeUtil.RichTy.EXCHANGE:
                IMRichCouponBody couponBody = JSON.parseObject(body,
                        IMRichCouponBody.class);
                messageBean.setAttachment(couponBody);
                messageBean.setSession(TextUtils.getStringFormat(IMClient.getInstance().getContext(), R.string.chat_rich_toast,
                        IMClient.getInstance().getContext().getString(R.string.chat_get_toast),
                        IMClient.getInstance().getContext().getString(type == IMTypeUtil.RichTy.COUPON ? R.string.card_text_coupons : R.string.card_text_exchange)));
                break;
            case IMTypeUtil.RichTy.DYNAMIC:
                IMRichDynamicBody dynamicBody = JSON.parseObject(body,
                        IMRichDynamicBody.class);
                messageBean.setAttachment(dynamicBody);
                break;
            case IMTypeUtil.RichTy.NOTICE:
                IMRichNoticeBody noticeBody = JSON.parseObject(body,
                        IMRichNoticeBody.class);
                messageBean.setAttachment(noticeBody);
                break;
            default:
                break;
        }
    }

    /***
     * @return void
     * @Title: paseBodyForSGroup
     * @param:
     * @Description: 解析群系统消息
     */
    public static void paseBodyForSGroup(String body,
                                                MessageBean messageBean, int type, String toId) {
        messageBean.setTo(toId);
        messageBean.setSession("");
        switch (type) {
            case IMTypeUtil.SGroupTy.CREATE:
                IMSGroupBaseBean createBean = JSON.parseObject(body,
                        IMSGroupBaseBean.class);
                messageBean.setFrom(createBean.getRoomId());
                messageBean.setAttachment(createBean);
                break;
            case IMTypeUtil.SGroupTy.UPDATE_BASE:
                IMSGroupUpdate groupUpdate = JSON.parseObject(body,
                        IMSGroupUpdate.class);
                messageBean.setFrom(groupUpdate.getRoomId());
                messageBean.setAttachment(groupUpdate);
                break;
            case IMTypeUtil.SGroupTy.ADD_ADMIN:
            case IMTypeUtil.SGroupTy.REMOVE_ADMIN:
                IMSGroupAdmins addAdmins = JSON.parseObject(body,
                        IMSGroupAdmins.class);
                messageBean.setFrom(addAdmins.getRoomId());
                messageBean.setAttachment(addAdmins);
                break;
            case IMTypeUtil.SGroupTy.ADD_MEMBER:
            case IMTypeUtil.SGroupTy.REMOVE_MEMBER:
                IMSGroupMembers addMembers = JSON.parseObject(body,
                        IMSGroupMembers.class);
                messageBean.setFrom(addMembers.getRoomId());
                messageBean.setAttachment(addMembers);
                break;
            case IMTypeUtil.SGroupTy.DISSOLVE:
                IMSGroupBaseBean baseBean = JSON.parseObject(body,
                        IMSGroupBaseBean.class);
                messageBean.setFrom(baseBean.getRoomId());
                messageBean.setAttachment(baseBean);
                break;
            case IMTypeUtil.SGroupTy.UPDATE_MYNICKNAME:
                IMSGroupBaseBean updateBean = JSON.parseObject(body,
                        IMSGroupBaseBean.class);
                messageBean.setFrom(updateBean.getRoomId());
                messageBean.setAttachment(updateBean);
                break;
            default:
                break;
        }
    }

    /***
     * @return void
     * @Title: paseBodyForInvite
     * @param:
     * @Description: 解析单系统消息
     */
    public static void paseBodyForS(String body, MessageBean messageBean,
                                    int type, String toId) {
        messageBean.setTo(toId);
        messageBean.setSession("");
        switch (type) {
            case IMTypeUtil.STy.S_INVITE:
                MsgSInviteBody inviteBody = JSON.parseObject(body,
                        MsgSInviteBody.class);
                messageBean.setFrom(inviteBody.getUserId());
                messageBean.setAttachment(inviteBody);
                messageBean.setSession(inviteBody.getRoomName());
                break;
            default:
                break;
        }
    }

    /**
     * @return void
     * @Title: getSessionForSGroup
     * @param:
     * @Description: 获取群系统消息会话内容
     */
    public static String getSessionForSGroup(Context context,
                                             MessageBean messageBean) {
        StringBuilder builder = new StringBuilder();
        String userId = IMClient.getInstance().getSSOUserId();
        Resources resources = context.getResources();
        switch (messageBean.getMsgType().getValue()) {
            case IMTypeUtil.SGroupTy.UPDATE_BASE:
                IMSGroupUpdate groupUpdate = messageBean.getAttachment();
                int template1 = groupUpdate.getTemplate();
                boolean isSelf = userId.equals(groupUpdate.getExecutorId());
                switch (template1) {
                    case 2:
                        if (isSelf) {
                            builder.append(TextUtils.getStringFormat(context,
                                    R.string.chat_group_template_2_1,
                                    groupUpdate.getRoomName()));
                        } else
                            builder.append(TextUtils.getStringFormat(context,
                                    R.string.chat_group_template_2,
                                    groupUpdate.getExecutorNickName(),
                                    groupUpdate.getRoomName()));
                        break;
                    case 3:
                        if (isSelf) {
                            builder.append(TextUtils.getStringFormat(context,
                                    R.string.chat_group_template_3_2,
                                    groupUpdate.getExecutorTo()));
                        } else {
                            if (!userId.equals(groupUpdate.getOwnerId()))
                                builder.append(TextUtils.getStringFormat(context,
                                        R.string.chat_group_template_3,
                                        groupUpdate.getExecutorNickName(),
                                        groupUpdate.getExecutorTo()));
                            else
                                builder.append(TextUtils.getStringFormat(context,
                                        R.string.chat_group_template_3_1,
                                        groupUpdate.getExecutorNickName(),
                                        groupUpdate.getExecutorTo()));
                        }

                        break;
                    case 4:
                        if (isSelf) {
                            builder.append(resources
                                    .getString(R.string.chat_group_template_4_1));
                        } else
                            builder.append(TextUtils.getStringFormat(context,
                                    R.string.chat_group_template_4,
                                    groupUpdate.getExecutorNickName()));
                        break;
                }

                break;
            case IMTypeUtil.SGroupTy.ADD_ADMIN:
                IMSGroupAdmins admins = messageBean.getAttachment();
                if (userId.equals(admins.getExecutorId())) {
                    builder.append(TextUtils.getStringFormat(context,
                            R.string.chat_group_template_5_1,
                            admins.getExecutorTo()));
                } else {
                    builder.append(TextUtils.getStringFormat(context,
                            R.string.chat_group_template_5, admins.getExecutorTo()));
                }
                break;
            case IMTypeUtil.SGroupTy.REMOVE_ADMIN:
                IMSGroupAdmins admins_remove = messageBean.getAttachment();
                if (userId.equals(admins_remove.getExecutorId())) {
                    builder.append(TextUtils.getStringFormat(context,
                            R.string.chat_group_template_10_1,
                            admins_remove.getExecutorTo()));
                } else {
                    builder.append(TextUtils.getStringFormat(context,
                            R.string.chat_group_template_10,
                            admins_remove.getExecutorNickName(),
                            admins_remove.getExecutorTo()));
                }
                break;
            case IMTypeUtil.SGroupTy.ADD_MEMBER:
                IMSGroupMembers members = messageBean.getAttachment();
                int template = members.getTemplate();
                boolean isSelfAdd = userId.equals(members.getExecutorId());
                if (template == 1) {
                    if (isSelfAdd) {
                        builder.append(TextUtils.getStringFormat(context,
                                R.string.chat_group_template_1_1,
                                members.getExecutorTo()));
                    } else {
                        builder.append(TextUtils.getStringFormat(context,
                                R.string.chat_group_template_1,
                                members.getExecutorNickName(),
                                members.getExecutorTo()));
                    }
                } else if (template == 6) {
                    if (isSelfAdd) {
                        builder.append(resources
                                .getString(R.string.chat_group_template_6_1));
                    } else {
                        builder.append(TextUtils.getStringFormat(context,
                                R.string.chat_group_template_6,
                                members.getExecutorNickName(),
                                members.getExecutorTo()));
                    }
                }
                break;
            case IMTypeUtil.SGroupTy.REMOVE_MEMBER:
                IMSGroupMembers membersRemove = messageBean.getAttachment();
                int template2 = membersRemove.getTemplate();
                boolean isSelfRemove = userId.equals(membersRemove.getExecutorId());
                if (template2 == 7) {
                    if (isSelfRemove) {
                        builder.append(resources
                                .getString(R.string.chat_group_template_7_1));
                    } else {
                        builder.append(TextUtils.getStringFormat(context,
                                R.string.chat_group_template_7,
                                membersRemove.getExecutorNickName()));
                    }
                } else if (template2 == 8) {
                    if (isSelfRemove) {
                        builder.append(TextUtils.getStringFormat(context,
                                R.string.chat_group_template_8_1,
                                membersRemove.getExecutorTo()));
                    } else {
                        builder.append(TextUtils.getStringFormat(context,
                                R.string.chat_group_template_8,
                                membersRemove.getExecutorNickName(),
                                membersRemove.getExecutorTo()));
                    }
                }
                break;
            case IMTypeUtil.SGroupTy.DISSOLVE:
                IMSGroupBaseBean groupDissolve = messageBean.getAttachment();
                if (userId.equals(groupDissolve.getExecutorId())) {
                    builder.append(resources
                            .getString(R.string.chat_group_template_9_1));
                } else {
                    builder.append(TextUtils.getStringFormat(context,
                            R.string.chat_group_template_9,
                            groupDissolve.getExecutorNickName()));
                }
                break;
            case IMTypeUtil.SGroupTy.UPDATE_MYNICKNAME:
                break;
            default:
                break;
        }
        return builder.toString();
    }
    /**
     * @return void
     * @Title: sendUpdateNotify
     * @param:
     * @Description: 发送更新通知
     */
    public static void sendUpdateNotify(Context context,
                                        int messageInfoReceiver, String msg, String sessionId) {
        Intent itent = new Intent();
        itent.setAction(MessageInfoReceiver.ACTION);
        itent.putExtra("type", messageInfoReceiver);
        itent.putExtra("msg", msg);
        itent.putExtra("sessionId", sessionId);
        context.sendBroadcast(itent);
    }

    /**
     * @return void
     * @Title: loadVoice
     * @param:
     * @Description: 加载语音
     */
    public static void loadVoice(Activity activity, MessageBean message) {
        Intent service = new Intent(activity, ChatService.class);
        service.putExtra("action", ChatService.ACTION_GET_FILE);
        service.putExtra("IMMessageBean", message);
        activity.startService(service);
    }

    /**
     * @return long
     * @Title: getSendMsgTime
     * @param:
     * @Description: 获取消息发送时的时间
     */
    public long getSendMsgTime() {
        return IMClient.getInstance().diffTime
                + System.currentTimeMillis();
    }
    /**
     * @return void
     * @Title: saveDestoryInfo
     * @param:
     * @Description: 存储销毁信息
     */
    public void saveDestoryInfo(String msgCode, String chatWidth) {
        SharedPreferences preferences = IMClient.getInstance().getContext()
                .getSharedPreferences(msgCode + "_destoryChat",
                        Context.MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.putString(msgCode, chatWidth);
        edit.commit();
    }

    /**
     * @return void
     * @Title: saveDestoryInfo
     * @param:
     * @Description: 存储销毁信息
     */
    public String getDestoryInfo(String msgCode) {
        SharedPreferences preferences = IMClient.getInstance().getContext()
                .getSharedPreferences(msgCode + "_destoryChat",
                        Context.MODE_PRIVATE);
        String result = preferences.getString(msgCode, null);
        return result;
    }

    /**
     * @return void
     * @Title: removeDestoryInfo
     * @param:
     * @Description: 清除销毁消息
     */
    public void removeDestoryInfo(String msgCode) {
        SharedPreferences preferences = IMClient.getInstance().getContext()
                .getSharedPreferences(msgCode + "_destoryChat",
                        Context.MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    /**
     * @return void
     * @Title: getToastMessage
     * @param:
     * @Description: 插入提示类消息
     */
    public long insertToastMessage(String chatWith, String session) {
        MessageBean tipMessage = MessageBuilder.createTipMessage(IMClient.getInstance().getSSOUserId(), SessionTypeEnum.SGROUP, session);
        tipMessage.setMsgStatus(MsgStatusEnum.success);
        tipMessage.setFrom(chatWith);
        tipMessage.setChatWith(chatWith);
        tipMessage.setSessionId(chatWith+"@"+tipMessage.getSessionType().getValue());
        return insertMessage(tipMessage);
    }

    /**
     * @return void
     * @Title: insertGreetingsMessage
     * @param:
     * @Description: 插入打招呼类消息
     */
    public MessageBean insertGreetingsMessage(String fromId, String toID,
                                              int direction, int followTy) {
        MessageBean textMessage = MessageBuilder.createTextMessage(toID, SessionTypeEnum.NORMAL, "");
        textMessage.setMsgType(followTy);
        textMessage.setMsgStatus(MsgStatusEnum.success);
        textMessage.setSessionType(SessionTypeEnum.FOLLOW);
        textMessage.setDirect(direction);
        textMessage.setFrom(fromId);
        long size = ChatMessageManager.getInstance().insertMessage(textMessage);
        if (size > 0)
            ChatSessionManager.getInstance().insertSession(textMessage,
                    textMessage.getSession());
        return textMessage;
    }

    /**
     * @return void
     * @Title: sendFollowMessage
     * @param:
     * @Description: 发送关注消息
     */
    public void sendFollowMessage(String userId) {
        if (userId == null)
            return;
        int fanState = ChatContactManager.getInstance().getFanstate(userId);
        switch (fanState) {
            case IMTypeUtil.FansStatus.FRIEND:
            case IMTypeUtil.FansStatus.ONLY_PEER_FOLLOW:
                fanState = IMTypeUtil.FollowTy.FOLLOWED;
                break;
            default:
                fanState = IMTypeUtil.FollowTy.FOLLOWING;
                break;
        }
        MessageBean message = insertGreetingsMessage(IMClient
                        .getInstance().getSSOUserId(), userId, MsgDirectionEnum.Out.getValue(),
                fanState);
        IMChatTextBody textBody= message.getAttachment();
        XmppSessionManager.getInstance().sendMessage(userId,textBody.getSendBody(),
                message);
        Intent intent = new Intent();
        intent.putExtra("messageBean", message);
        intent.setAction(ACTION_FOLLOW);
        IMClient.getInstance().getContext().sendBroadcast(intent);
    }

    /**
     * @return void
     * @Title: setMyBody
     * @param:
     * @Description: 设置我的用户信息
     */
    public void setMyBody(IMUserBody baseBody) {
        UserInfoHelp userInfo =  UserInfoHelp.getInstance(IMClient.getInstance().getContext());
        baseBody.setAvatar(userInfo.getAvarar());
        baseBody.setName(userInfo.getNickname());
        baseBody.setGender(userInfo.getSex());
    }

    /**
     * 设置语音播放是否为听筒模式
     *
     * @param isEarMode
     */
    public void setVoiceToEarMode(boolean isEarMode) {
        if (chatHelper == null) {
            chatHelper = new PreferencesHelper(IMClient.getInstance().getContext(), "chatdata" + IMClient.getInstance().getSSOUserId());
        }
        chatHelper.put("isEarMode", isEarMode);
    }

    /**
     * 获取语音是否为听筒模式
     *
     * @return
     */
    public boolean getVoiceIsEarMode() {
        if (chatHelper == null) {
            chatHelper = new PreferencesHelper(IMClient.getInstance().getContext(), "chatdata" + IMClient.getInstance().getSSOUserId());
        }
        return chatHelper.getBoolean("isEarMode");
    }

    /**
     * 设置对应版本的数据是否已经读写完成
     *
     * @param isReaded
     */
    public void setDataIsReaded(boolean isReaded) {
        if (chatHelper == null) {
            chatHelper = new PreferencesHelper(IMClient.getInstance().getContext(), "chatdata_" + ChatMessageManager.getInstance().getVersionCode() + "_" + IMClient.getInstance().getSSOUserId());
        }
        chatHelper.put("isReaded", isReaded);
    }

    /**
     * 获取对应版本的数据是否已经读写完成
     *
     * @return
     */
    public boolean getDataIsReaded() {
        if (chatHelper == null) {
            chatHelper = new PreferencesHelper(IMClient.getInstance().getContext(), "chatdata_" + ChatMessageManager.getInstance().getVersionCode() + "_" + IMClient.getInstance().getSSOUserId());
        }
        return chatHelper.getBoolean("isReaded");
    }
    /**
     * 单聊发送文本消息
     *
     * @param toID
     * @param content
     */
    public void sendText(String toID, String content) {
        MessageBean message= MessageBuilder.createTextMessage(toID,SessionTypeEnum.NORMAL,content);
        if (insertMessage(message,"")) {
            MessageClient.getInstance().post(message);
            sendChatMessage(message, message.getSendBody());
        }
    }
    private boolean insertMessage(MessageBean message,String msg) {
        if (message == null)
            return false;
        boolean isInerted = ChatMessageManager.getInstance().insertMessage(message) > 0;
        if (isInerted) {
            ChatSessionManager.getInstance().insertSession(message,
                    message.getSession());
        }
        return isInerted;
    }

    public synchronized void sendChatMessage(MessageBean message, String content) {
        if (message == null || content == null)
            return;
        /** 避免快速发送消息时服务阻塞线程，只有在发送语音的时候才启用服务 */
        XmppSessionManager.getInstance().sendMessage(
                message.getTo(), content, message);
    }
    /**
     * 消息重发
     *
     * @param imbean
     */
    public void resendMessage(final MessageBean imbean,
                              boolean isNotify,final ChatUploadManager.OnUploadListener onUploadListener) {
        if (imbean == null)
            return;
        if(isNotify)
        XmppSessionManager.getInstance().notifyMessageStateChange(imbean.getSessionId(), "",
                imbean.getMsgCode(),ChatUtil.getInstance().getSendMsgTime(), 0, 0, 0);
        if (imbean.getSessionType()==null) {
            XmppSessionManager.getInstance().notifyMessageStateChange(imbean.getSessionId(), "",
                    imbean.getMsgCode(),ChatUtil.getInstance().getSendMsgTime(), 0, -1, 0);
            return;
        }
        SessionTypeEnum sessionType=imbean.getSessionType();
        switch (sessionType) {
            case NORMAL:
            case GROUPCHAT:
                final MsgTypeEnum type = imbean.getMsgType();
                switch (type) {
                    //需要上传的消息
                    case  audio:
                    case image:
                    case video:
                        String url = "", content = "";
                        IMChatBaseBody baseBody=imbean.getAttachment();
                        url=baseBody.getAttr1();
                        content=baseBody.getSendBody();
                        if (!android.text.TextUtils.isEmpty(url)) {
                            sendChatMessage(imbean, content );
                        } else {
                            ChatUploadManager.getInstance().uploadFile(imbean,onUploadListener);
                        }
                        break;
                    case  gif:
                    case text:
                    case location:
                        IMChatTextBody txtBody= imbean.getAttachment();
                        sendChatMessage(imbean, txtBody.getSendBody());
                        break;
                }
                break;

            default:
                break;
        }

    }
    /**
     * 更新消息发送状态
     *
     * @param msgCode
     */
    public void notifyMessageSendState(String sessionId, final String msgCode, final int msgStatus) {
        ChatMessageManager.getInstance().updateMessageSendStatus(sessionId, "", msgCode,
                msgStatus);
        ChatSessionManager.getInstance().updateSessionSendStatus(msgCode,
                msgStatus);
        sendUpdateNotify(IMClient.getInstance().getContext(),MessageInfoReceiver.EVENT_UPDATE_CHAT,sessionId,sessionId);
    }
}
