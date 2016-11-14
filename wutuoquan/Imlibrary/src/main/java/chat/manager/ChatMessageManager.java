package chat.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.ImAttachment;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.bean.MsgSSBody;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;

/**
 * @author weyko zhong.xiwei@moxiangroup.com
 * @ClassName: ChatMessageManager
 */
public class ChatMessageManager extends AbstractChatDBManager {
    public static final String FILE = "file";
    public static final String MSGID = "Msg_Id";
    public static final String SESSIONID = "sessionid";
    private static ChatMessageManager instance;
    public ChatMessageManager() {
    }

    public static synchronized ChatMessageManager getInstance() {
        if (instance == null)
            instance = new ChatMessageManager();
        return instance;
    }
    /**
     * 更新消息到本地数据库
     * @param message 消息
     * @return 更新的消息ID
     */
    public synchronized long insertMessage(MessageBean message) {
        long row = -1L;
        try {
            if (!TextUtils.isEmpty(message.getSessionId())) {
                if (isExitMessage(message.getSessionId(), message.getMsgCode()))// 避免重复插入
                    return 0;
                ContentValues values = new ContentValues();
                try {
                    String sessionId = message.getSessionId();
                    values.put(MessageColumn.MSG_CODE, message.getMsgCode());
                    values.put(MessageColumn.SESSION_ID, sessionId);
                    values.put(MessageColumn.MSG_TIME, message.getMsgTime());
                    values.put(MessageColumn.CHAT_WITH, message.getChatWith());
                    values.put(MessageColumn.FROM, message.getFrom());
                    values.put(MessageColumn.TO, message.getTo());
                    values.put(MessageColumn.MSG_DIRECTION,
                            message.getDirect().getValue());
                    values.put(MessageColumn.READ_STATUS, message.getIsRead());
                    values.put(MessageColumn.SESSION_TYPE, message.getSessionType().getValue());
                    values.put(MessageColumn.MSG_TYPE, message.getMsgType().getValue());
                    values.put(MessageColumn.MSG_STATUS, message.getMsgStatus().getValue());
                    if (SessionTypeEnum.SS.name().equals(message.getSessionType())) {
                        ImAttachment body = message.getAttachment();
                        if (message.getMsgType().getValue() == IMTypeUtil.SSType.FOLLOW) {
                            values.put(MessageColumn.GROUP_TYPE,
                                    IMTypeUtil.SessionType.FRIEND.name());
                        }
                    }
                    values.put(MessageColumn.REMARK, message.getRemark());
                    putValuesForBody(message, values);
                    String tableName = getTableSessionName(sessionId, "");
                    if (!isTableExist(getInstance().sqliteDB(), tableName))//如果不存在,则动态创建表
                        createTableForMessage(getInstance().sqliteDB(), tableName);
                    row = getInstance().sqliteDB().insert(
                            tableName, null, values);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    values.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    /**
     * @return boolean
     * @Title: isExitMessage
     * @Description: 判断消息是否存在
     */
    private boolean isExitMessage(String sessionId, String msgCode) {
        String sql = SELECT + MessageColumn.ID + FROM
                + getTableSessionName(sessionId, "") + WHERE
                + MessageColumn.MSG_CODE + EQ_STR_LEFT + msgCode + EQ_STR_RIGHT;
        Cursor rawQuery = null;
        int count = 0;
        try {
            rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
            if (rawQuery != null) {
                count = rawQuery.getCount();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rawQuery != null) {
                rawQuery.close();
                rawQuery = null;
            }
        }
        return (count > 0) ? true : false;
    }

    /**
     * @return boolean
     * @Title: deleteMessageById
     * @Description: 根据Id删除内容
     */
    public synchronized boolean deleteMessageById(String msgCode,
                                                  String sessionId, String shopId) {
        boolean isDelete = getInstance().sqliteDB().delete(
                getTableSessionName(sessionId, shopId),
                MessageColumn.MSG_CODE + EQ_STR_LEFT + msgCode + EQ_STR_RIGHT,
                null) > 0;
        if (isDelete && sessionId != null) {
            ChatTembMsgManager.getInstance().deleteTembInfo(msgCode);
            int size = getSize(sessionId, shopId);
            if (size == 0) {
                ChatSessionManager.getInstance().updateMsg(sessionId, "");
            } else {
                ChatSessionManager.getInstance().updateSessionBySessionType(sessionId, sessionId, shopId);
            }
        }
        return isDelete;
    }

    /**
     * 销毁某个会话聊天信息
     */
    public synchronized boolean destoryChatMessage(String sessionId,
                                                   String shopId) {
        int result = -1;
        try {
            String caseSql = "";
            if (!TextUtils.isEmpty(shopId)) {
                caseSql = AND + MessageColumn.SHOP_ID + EQ_STR_LEFT + shopId
                        + EQ_STR_RIGHT;
            }
            result = getInstance().sqliteDB().delete(
                    getTableSessionName(sessionId, shopId),
                    MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                            + EQ_STR_RIGHT + caseSql, null);
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        int size = getSize(sessionId, shopId);
        if (size == 0) {
            ChatSessionManager.getInstance().deleteById(sessionId, "");
        } else
            ChatSessionManager.getInstance().updateSession(sessionId, shopId);
        return (result >= 0 ? true : false);
    }

    /**
     * 删除某个会话聊天信息
     */
    public synchronized boolean deleteChatTable(String sessionId, String shopId) {
        boolean isDeleted = false;
        isDeleted = deleteTable(sessionId, shopId);
        if (!isDeleted)
            return isDeleted;
        //清除临时消息
        ChatTembMsgManager.getInstance().clearTembInfo(sessionId);
        int size = getSize(sessionId, shopId);
        if (size == 0) {
            ChatSessionManager.getInstance().deleteById(sessionId, "");
        } else
            ChatSessionManager.getInstance().updateSession(sessionId, shopId);
        return isDeleted;
    }

    /**
     * 销毁某个会话某个方向的聊天信息
     */
    public boolean destoryChatMessage(String sessionId, String shopId, boolean isSendDir) {
        int result = -1;
        try {
            String caseDel = AND + MessageColumn.MSG_DIRECTION + " = 1";
            if (isSendDir)
                caseDel = "";
            result = getInstance().sqliteDB().delete(
                    getTableSessionName(sessionId, shopId),
                    MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                            + EQ_STR_RIGHT + caseDel, null);
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        if (result > 0) {
            //清除临时消息
            ChatTembMsgManager.getInstance().clearTembInfo(sessionId);
            int size = getSize(sessionId, shopId);
            if (size == 0) {
                ChatSessionManager.getInstance().updateMsg(sessionId, "");
            } else {
                ChatSessionManager.getInstance().updateSession(sessionId, shopId);
            }
        }
        return (result >= 0 ? true : false);
    }

    /**
     * @return void
     * @Title: putValuesForBody
     * @param:
     * @Description: 填充消息体
     */
    private void putValuesForBody(MessageBean message, ContentValues values) {
        ImAttachment attachment = message.getAttachment();
        values.put(MessageColumn.SESSION, message.getSession());
        values.put(MessageColumn.MSG_BODY, attachment == null ? "" : attachment.getSaveBody());
    }

    /**
     * @return List<MessageBean>
     * @Title: getMessages
     * @param:
     * @Description: 获取消息列表
     */
    public List<MessageBean> getMessages(String sessionId, String shopId, long lastTime) {
        List<MessageBean> beans = new ArrayList<MessageBean>();
        Cursor cursor = null;
        try {
            String caseStr = AND + MessageColumn.MSG_TIME + " < " + lastTime;
            String orderBy = ORDER + MessageColumn.MSG_TIME + " desc limit "
                    + 0 * ChatUtil.PAGE_SIZE_CHAT + ","
                    + ChatUtil.PAGE_SIZE_CHAT;
            String sql = SELECT + ALL + FROM
                    + getTableSessionName(sessionId, shopId) + WHERE
                    + MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT;
            if (lastTime > 0) {
                sql += caseStr;
            }
            sql += orderBy;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MessageBean messageBean = getMessageBean(cursor);
                    beans.add(messageBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return beans;
    }
    /**
     * @return MessageBean
     * @Title: getMessagesOfTemb
     * @param:
     * @Description: 获取临时消息
     */
    public MessageBean getMessagesOfTemb(String sessionId, String shopId, String msgCode) {
        MessageBean messageBean = null;
        Cursor cursor = null;
        try {
            String sql = SELECT + ALL + FROM
                    + getTableSessionName(sessionId, shopId) + WHERE
                    + MessageColumn.MSG_CODE + EQ_STR_LEFT + msgCode
                    + EQ_STR_RIGHT;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    messageBean = getMessageBean(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return messageBean;
    }

    /**
     * @Title: getRichBoy
     * @param:
     * @Description: 获取所有消息列表
     */
    public ImAttachment getRichBoy(String sessionId, String shopId, String msgCode) {
        Cursor cursor = null;
        try {
            String sql = SELECT + MessageColumn.FROM + "," + MessageColumn.TO + "," + MessageColumn.MSG_TYPE + "," + MessageColumn.MSG_BODY + FROM
                    + getTableSessionName(sessionId, shopId) + WHERE
                    + MessageColumn.MSG_CODE + EQ_STR_LEFT + msgCode
                    + EQ_STR_RIGHT;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String from = cursor.getString(cursor.getColumnIndex(MessageColumn.FROM));
                    String to = cursor.getString(cursor.getColumnIndex(MessageColumn.TO));
                    String body = cursor.getString(cursor.getColumnIndex(MessageColumn.MSG_BODY));
                    int type = cursor.getInt(cursor.getColumnIndex(MessageColumn.MSG_TYPE));
                    MessageBean mesageBean = new MessageBean();
                    ChatUtil.paseBodyForRich(body, mesageBean, type, from, to);
                    return mesageBean.getAttachment();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return null;
    }

    /**
     * @return List<MessageBean>
     * @Title: getMessages
     * @param:
     * @Description: 获取消息列表
     */
    public List<ImUserBean> getMessagesForNewFriend(String sessionType) {
        List<ImUserBean> beans = new ArrayList<ImUserBean>();
        Cursor cursor = null;
        try {
            String sql = SELECT + ALL + FROM
                    + DatabaseHelper.TABLE_NAME_NEW_FRIEND
                    + ORDER + MessageColumn.MSG_TIME + " desc ";
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String userId = cursor.getString(cursor.getColumnIndex(NewFriendsColumn.W_ID));
                    String msgCode = cursor.getString(cursor.getColumnIndex(NewFriendsColumn.MSG_CODE));
                    ImUserBean imUserBean = ChatContactManager.getInstance().getImUserBean(userId);
                    imUserBean.setMsgTime(cursor.getLong(cursor.getColumnIndex(NewFriendsColumn.MSG_TIME)));
                    beans.add(imUserBean);
                    updateMessageReadStatus(userId, userId, msgCode, 1);
                }
                ChatSessionManager.getInstance().updateSessionUnReadAll(
                        IMTypeUtil.SessionType.FRIEND.name());
                updateMessageAllReaded(IMTypeUtil.SessionType.FRIEND.name(), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return beans;
    }
    /**
     * @return MessageBean
     * @Description:获取消息体
     */
    private MessageBean getMessageBean(Cursor cursor) {
        MessageBean messageBean = new MessageBean();
        messageBean.setMsgCode(cursor.getString(cursor
                .getColumnIndex(MessageColumn.MSG_CODE)));
        messageBean.setSessionId(cursor.getString(cursor
                .getColumnIndex(MessageColumn.SESSION_ID)));
        messageBean.setMsgTime(cursor.getLong(cursor
                .getColumnIndex(MessageColumn.MSG_TIME)));
        messageBean.setChatWith(cursor.getString(cursor
                .getColumnIndex(MessageColumn.CHAT_WITH)));
        messageBean.setFrom(cursor.getString(cursor
                .getColumnIndex(MessageColumn.FROM)));
        messageBean.setTo(cursor.getString(cursor
                .getColumnIndex(MessageColumn.TO)));
        messageBean.setDirect(cursor.getInt(cursor
                .getColumnIndex(MessageColumn.MSG_DIRECTION)));
        messageBean.setIsRead(cursor.getInt(cursor
                .getColumnIndex(MessageColumn.READ_STATUS)));
        messageBean.setIsListen(cursor.getInt(cursor
                .getColumnIndex(MessageColumn.LISTEN_STATUS)));
        messageBean.setMsgStatus(MsgStatusEnum.fromInt(cursor.getInt(cursor
                .getColumnIndex(MessageColumn.MSG_STATUS))));
        messageBean.setSessionType(SessionTypeEnum.fromInt(cursor.getInt(cursor
                .getColumnIndex(MessageColumn.SESSION_TYPE))));
        messageBean.setMsgType(cursor.getInt(cursor
                .getColumnIndex(MessageColumn.MSG_TYPE)));
        messageBean.setRemark(cursor.getString(cursor
                .getColumnIndex(MessageColumn.REMARK)));
        messageBean.setSession(cursor.getString(cursor
                .getColumnIndex(MessageColumn.SESSION)));
        String body = cursor.getString(cursor
                .getColumnIndex(MessageColumn.MSG_BODY));
        if (messageBean.getSessionType() == SessionTypeEnum.NORMAL)
            messageBean.setImUserBean(ChatContactManager.getInstance()
                    .getImUserBean(messageBean.getChatWith()));
        else if (messageBean.getSessionType() == SessionTypeEnum.GROUPCHAT)
            messageBean.setImUserBean(ChatMembersManager.getInstance()
                    .getMemberOfGroup(messageBean.getChatWith(),
                            messageBean.getFrom()));
        parseBody(messageBean, body);
        return messageBean;
    }

    /**
     * @return List<MessageBean>
     * @Description: 获取图片消息列表
     */
    public List<MessageBean> getMessagesOfImage(String sessionId, String shopId) {
        List<MessageBean> beans = new ArrayList<MessageBean>();
        Cursor cursor = null;
        try {
            String sql = SELECT + ALL + FROM
                    + getTableSessionName(sessionId, shopId) + WHERE
                    + MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT + AND + MessageColumn.MSG_TYPE + "="
                    + MsgTypeEnum.image.getValue() + ORDER + MessageColumn.MSG_TIME + " asc";
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    beans.add(getMessageBean(cursor));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return beans;
    }

    /**
     * @return List<MessageBean>
     * @Description: 获取消息条数
     */
    public int getSize(String sessionId, String shopId) {
        Cursor cursor = null;
        try {
            String shopCase = sessionId.contains("_") ? AND + MessageColumn.SHOP_ID + EQ_STR_LEFT + shopId + EQ_STR_RIGHT : "";
            String sql = SELECT + "count(" + MessageColumn.ID + ")" + FROM
                    + getTableSessionName(sessionId, shopId) + WHERE
                    + MessageColumn.SESSION_TYPE + " <> "
                    + SessionTypeEnum.SGROUP.getValue() + " AND "
                    + MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT + shopCase;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int size = cursor.getInt(0);
                    cursor.close();
                    cursor = null;
                    return size;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return 0;
    }

    /**
     * @Description: 获取未读消息条数
     */
    public int getUnReadSize(String sessionId, String shopId) {
        Cursor cursor = null;
        try {
            String sql = SELECT + "count(" + MessageColumn.ID + ")" + FROM
                    + getTableSessionName(sessionId, shopId) + WHERE
                    + MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT + AND + MessageColumn.READ_STATUS + EQ_INT
                    + 0 + AND + MessageColumn.MSG_DIRECTION + EQ_INT + 1;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int unReadSize = cursor.getInt(0);
                    cursor.close();
                    cursor = null;
                    return unReadSize;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return 0;
    }
    /**
     * @Description:获取最后一条消息的会话
     */
    public synchronized MessageBean getSessionForUpdate(String sessionId, String shopId) {
        MessageBean messagebean = new MessageBean();
        messagebean.setSessionId(sessionId);
        Cursor cursor = null;
        try {
            String shopCase = sessionId.contains("_") ? AND + MessageColumn.SHOP_ID + EQ_STR_LEFT + shopId + EQ_STR_RIGHT : "";
            String sql = SELECT + ALL + FROM
                    + getTableSessionName(sessionId, shopId) + WHERE
                    + MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT + shopCase + ORDER + MessageColumn.MSG_TIME + DESC + " LIMIT 0,1";
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    messagebean.setMsgTime(cursor.getLong(cursor
                            .getColumnIndex(MessageColumn.MSG_TIME)));
                    messagebean.setIsRead(cursor.getInt(cursor
                            .getColumnIndex(MessageColumn.READ_STATUS)));
                    messagebean.setMsgCode(cursor.getString(cursor
                            .getColumnIndex(MessageColumn.MSG_CODE)));
                    messagebean.setMsgStatus(MsgStatusEnum.fromInt(cursor.getInt(cursor
                            .getColumnIndex(MessageColumn.MSG_STATUS))));
                    messagebean.setSession(cursor.getString(cursor
                            .getColumnIndex(MessageColumn.SESSION)));
                    messagebean.setMsgType(cursor.getInt(cursor
                            .getColumnIndex(MessageColumn.MSG_TYPE)));
                    messagebean.setSessionType(SessionTypeEnum.fromInt(cursor.getInt(cursor
                            .getColumnIndex(MessageColumn.SESSION_TYPE))));
                    messagebean.setChatWith(cursor.getString(cursor
                            .getColumnIndex(MessageColumn.CHAT_WITH)));
                    messagebean.setDirect(cursor.getInt(cursor
                            .getColumnIndex(MessageColumn.MSG_DIRECTION)));
                    break;
                }
            }
            messagebean.setUnReads(getUnReadSize(sessionId, shopId));
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return messagebean;
    }

    /**
     * 标记某条记录状态的发送状态,0:发送中，1：发送成功，-1，发送失败
     * @param msgCode 消息ID
     */
    public synchronized boolean updateMessageSendStatus(String sessionId, String shopId, String msgCode,
                                                        int sendStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageColumn.MSG_STATUS, sendStatus);
        try {
            boolean isUpdate = getInstance().sqliteDB().update(
                    getTableSessionName(sessionId, shopId),
                    contentValues,
                    MessageColumn.MSG_CODE + EQ_STR_LEFT + msgCode
                            + EQ_STR_RIGHT, null) > 0;
            if (isUpdate && sendStatus == -1)//处理临时消息
                ChatTembMsgManager.getInstance().deleteTembInfo(msgCode);
            return isUpdate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 标记某条记录状态的发送、是否已读状态
     * @param msg_code 消息ID
     */
    public synchronized boolean updateState(String sessionId, String shopId, String msg_code,
                                            int msgStatus, int readStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageColumn.MSG_STATUS, msgStatus);
        contentValues.put(MessageColumn.READ_STATUS, readStatus);
        try {
            boolean isUpdate = getInstance().sqliteDB().update(
                    getTableSessionName(sessionId, shopId),
                    contentValues,
                    MessageColumn.MSG_CODE + EQ_STR_LEFT + msg_code
                            + EQ_STR_RIGHT, null) > 0;
            if (isUpdate && msgStatus == -1)//处理临时消息
                ChatTembMsgManager.getInstance().deleteTembInfo(msg_code);
            return isUpdate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新消息
     */
    public synchronized boolean updateMessage(MessageBean message) {
        ContentValues values = new ContentValues();
        try {
            values.put(MessageColumn.MSG_CODE, message.getMsgCode());
            values.put(MessageColumn.SESSION_ID, message.getSessionId());
            values.put(MessageColumn.MSG_TIME, message.getMsgTime());
            values.put(MessageColumn.CHAT_WITH, message.getChatWith());
            values.put(MessageColumn.FROM, message.getFrom());
            values.put(MessageColumn.TO, message.getTo());
            values.put(MessageColumn.MSG_DIRECTION, message.getDirect().getValue());
            values.put(MessageColumn.READ_STATUS, message.getIsRead());
            values.put(MessageColumn.SESSION_TYPE, message.getSessionType().getValue());
            values.put(MessageColumn.MSG_TYPE, message.getMsgType().getValue());
            values.put(MessageColumn.MSG_STATUS, message.getMsgStatus().getValue());
            values.put(MessageColumn.REMARK, message.getRemark());
            putValuesForBody(message, values);
            boolean isUpdate = getInstance().sqliteDB().update(
                    getTableSessionName(message.getSessionId(), ""),
                    values,
                    MessageColumn.MSG_CODE + EQ_STR_LEFT + message.getMsgCode()
                            + EQ_STR_RIGHT, null) > 0;
            if (isUpdate && message.getMsgStatus().getValue() == -1)//处理临时消息
                ChatTembMsgManager.getInstance().deleteTembInfo(message.getMsgCode());
            return isUpdate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ChatSessionManager.getInstance().updateSession(message.getSessionId(), "");
        return false;
    }

    /**
     * 标记某条消息是否已读状态
     * @param msg_code 消息ID
     */
    public synchronized boolean updateMessageReadStatus(String sessionId, String shopId, String msg_code,
                                                        int msgStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageColumn.READ_STATUS, msgStatus);
        try {
            return getInstance().sqliteDB().update(
                    getTableSessionName(sessionId, shopId),
                    contentValues,
                    MessageColumn.MSG_CODE + EQ_STR_LEFT + msg_code
                            + EQ_STR_RIGHT, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return boolean
     * @Title: setMessageReadStatusByGroup
     * @Description: 根据消息组来更新所有消息为已读
     */
    public synchronized boolean setMessageReadStatusByGroup(String sessionId, String shopId, String groupType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageColumn.READ_STATUS, 1);
        try {
            return getInstance().sqliteDB().update(
                    getTableSessionName(sessionId, shopId),
                    contentValues,
                    MessageColumn.GROUP_TYPE + EQ_STR_LEFT + groupType
                            + EQ_STR_RIGHT, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 标记某条消息是否已收听状态
     * @param msg_code 消息ID
     */
    public synchronized boolean updateMessageListenStatus(String sessionId, String shopId, String msg_code) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageColumn.LISTEN_STATUS, 1);
        try {
            return getInstance().sqliteDB().update(
                    getTableSessionName(sessionId, shopId),
                    contentValues,
                    MessageColumn.MSG_CODE + EQ_STR_LEFT + msg_code
                            + EQ_STR_RIGHT, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新所有已读消息
     */
    public synchronized boolean updateMessageAllReaded(String sessionId, String shopid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageColumn.READ_STATUS, 1);

        try {
            String sqlCase = "";
            if (sessionId.equals(IMTypeUtil.SessionType.MOBIZ.name()))
                sqlCase = MessageColumn.SHOP_ID + EQ_STR_LEFT + shopid + EQ_STR_RIGHT;
            else {
                sqlCase = MessageColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                        + EQ_STR_RIGHT;
            }
            sqlCase += AND + MessageColumn.MSG_DIRECTION
                    + "=1";
            int result = getInstance().sqliteDB().update(
                    getTableSessionName(sessionId, shopid),
                    contentValues, sqlCase
                    , null);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取与某个人所有聊天图片的路径
     */
    public synchronized List<Map<String, String>> getPhotoPaths(String chat_with, String shopId) {
        List<MessageBean> singleChatMessages = getMessagesOfImage(chat_with, shopId);
        List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        String domainUrl = URLConfig.getDomainUrl(Constant.DOMAIN_IMAGE_TYPE);
        for (MessageBean bean : singleChatMessages) {
            if (bean.getMsgType() == MsgTypeEnum.image) {
                Map<String, String> hashMap = new HashMap<String, String>();
                if (bean.getAttachment() instanceof IMChatImageBody) {
                    IMChatImageBody imageBody = bean.getAttachment();
                    String localUrl = imageBody.getLocalUrl();
                    String uri = imageBody.getAttr1();
                    if (null != localUrl && localUrl.length() > 0) {
                        hashMap.put(FILE, localUrl);
                    } else {
                        if (null != uri && uri.length() > 0) {
                            if (!TextUtils.isEmpty(uri) && !uri.startsWith("http:")) {
                                uri = domainUrl + uri;
                            }
                            File file = IMClient.getInstance()
                                    .findInImageLoaderCache(uri);
                            if (file != null && file.exists()) {
                                hashMap.put(FILE, file.getAbsolutePath());
                            } else {
                                hashMap.put(FILE, uri);
                            }
                        }
                    }
                    hashMap.put(SESSIONID, bean.getSessionId());
                    hashMap.put(MSGID, bean.getMsgCode());
                    listMap.add(hashMap);
                }
            }

        }

        return listMap;

    }
    private void parseBody(MessageBean messageBean, String body) {
        if (messageBean.getSessionType() == null)
            return;
        int type = messageBean.getMsgType().getValue();
        String from = messageBean.getFrom();
        String to = messageBean.getTo();
        SessionTypeEnum sessionType = messageBean.getSessionType();
        switch (sessionType) {
            case NORMAL:
            case GROUPCHAT:
                ChatUtil.paseBodyForChat(body, messageBean, type, from, to);
                break;
            case RICH:
                ChatUtil.paseBodyForRich(body, messageBean, type, from, to);
                break;
            case SGROUP:
                ChatUtil.paseBodyForSGroup(body, messageBean, type, to);
                break;
            case S:
                ChatUtil.paseBodyForS(body, messageBean, type, to);
                break;
            case SS:
                MsgSSBody ssBody = JSON.parseObject(body, MsgSSBody.class);
                messageBean.setAttachment(ssBody);
                if (messageBean.getMsgType().getValue() == IMTypeUtil.SSType.FOLLOW) {
                    ChatUtil.initSSImUserBean(messageBean.getImUserBean(), ssBody);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取消息表的表名
     */
    private String getTableSessionName(String sessionId, String shopId) {
        String tableName = "";
        if (!TextUtils.isEmpty(sessionId)) {
            tableName = DatabaseHelper.TABLE_NAME_MESSAGE + "_" + sessionId;
            if (tableName.contains("@"))//这里需要处理下数据库表名不能有@特殊字符
                tableName = tableName.substring(0, tableName.indexOf("@"));
        }
        return tableName;
    }

    /**
     * 删除表
     */
    public boolean deleteTable(String sessionId, String shopId) {
        boolean isDeleted = false;
        String tableName = getTableSessionName(sessionId, shopId);
        String sql = "DROP TABLE IF EXISTS " + tableName;
        try {
            getInstance().sqliteDB().execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            isDeleted=true;
            return isDeleted;
        }
        try {
            getInstance().sqliteDB().rawQuery(SELECT+" ID "+FROM+ tableName,null);
        } catch (SQLException e) {
            e.printStackTrace();
            isDeleted=true;
        }
        return isDeleted;
    }

    @Override
    protected void release() {
        super.release();
        instance = null;
    }

    public void reset() {
        getInstance().release();
    }
}
