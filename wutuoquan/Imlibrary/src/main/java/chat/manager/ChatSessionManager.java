package chat.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import chat.base.IMClient;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.bean.MsgSSBody;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.group.bean.ChatGroupBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;

/**
 * @author weyko zhong.xiwei@moxiangroup.com
 * @ClassName: ChatSessionManager
 * @Description: 会话消息数据库管理
 * @Company moxian
 * @date 2015年8月17日 下午6:43:59
 */
public class ChatSessionManager extends AbstractChatDBManager {
    private static ChatSessionManager instance;
    private ChatSessionManager() {
        super();
    }

    public static ChatSessionManager getInstance() {
        if (instance == null) {
            instance = new ChatSessionManager();
        }
        return instance;
    }
    /***
     * 从本地数据库组装消息
     */
    private MessageBean getMessageBeanFromCursor(Cursor cursor) {
        if (cursor == null)
            return null;
        MessageBean session = new MessageBean();
        session.setSessionId(cursor.getString(cursor
                .getColumnIndex(SessionColumn.SESSION_ID)));
        session.setMsgCode(cursor.getString(cursor
                .getColumnIndex(SessionColumn.MSG_CODE)));
        session.setChatWith(cursor.getString(cursor
                .getColumnIndex(SessionColumn.CONTACT_ID)));
        session.setTo(session.getChatWith());
        session.setFrom(IMClient.getInstance().getSSOUserId());
        session.setSession(cursor.getString(cursor
                .getColumnIndex(SessionColumn.SESSION)));
        session.setSessionType(SessionTypeEnum.fromInt(cursor.getInt(cursor
                .getColumnIndex(SessionColumn.SESSION_TYPE))));
        session.setDirect(cursor.getInt(cursor
                .getColumnIndex(SessionColumn.DIRECT)));
        session.setMsgStatus(MsgStatusEnum.fromInt(cursor.getInt(cursor
                .getColumnIndex(SessionColumn.MSG_STATUS))));
        session.setIsRead(cursor.getInt(cursor
                .getColumnIndex(SessionColumn.READ_STATUS)));
        session.setMsgTime(cursor.getLong(cursor
                .getColumnIndex(SessionColumn.MSG_TIME)));
        session.setMsgType(cursor.getInt(cursor
                .getColumnIndex(SessionColumn.MESSAGE_TYPE)));
        session.setIsTop(cursor.getInt(cursor
                .getColumnIndex(SessionColumn.IS_TOP)));
        session.setUnReads(cursor.getInt(cursor
                .getColumnIndex(SessionColumn.UNREAD_NUM)));
       if (session.getSessionType() == SessionTypeEnum.GROUPCHAT) {
            ChatGroupBean chatGroupBean = ChatGroupManager
                    .getInstance().getGroupBean(
                            session.getChatWith());
            session.setChatGroupBean(chatGroupBean);
        }else {
           String sessionId = session.getSessionId();
           ImUserBean imUserBean = ChatContactManager
                   .getInstance().getImUserBean(
                           session.getChatWith());
           if (IMTypeUtil.SessionType.FRIEND.name().equals(sessionId)) {
               String title=IMClient.getInstance().getContext().getString( R.string.chat_list_newfriend_title);
               imUserBean.setName(title);
           }
           session.setImUserBean(imUserBean);
       }
        return session;
    }

    /**
     * @return ArrayList<SessionBean>
     * @Title: getSessions
     * @Description: 获取会话室列表
     */
    public ArrayList<MessageBean> getSessions(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            tableName = DatabaseHelper.TABLE_NAME_SESSION;
        }
        ArrayList<MessageBean> arrayList = new ArrayList<MessageBean>();
        HashMap<String, MessageBean> maps = new HashMap<String, MessageBean>();
        Cursor cursor = null;
        try {
            String sql = SELECT + ALL + FROM
                    + tableName + ORDER
                    + SessionColumn.MSG_TIME + DESC;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MessageBean session = getMessageBeanFromCursor(cursor);
                    String key = DatabaseHelper.TABLE_NAME_SHOP.equals(tableName) ? "_" + "" : "";
                    if (maps.containsKey(session.getSessionId() + key)) {
                        MessageBean old = maps.get(session.getSessionId() + key);
                        if (old.msgTime < session.msgTime) {// 重复的会话，比较时间，取最新的
                            maps.put(session.getSessionId() + key, session);
                        }
                    } else {
                        maps.put(session.getSessionId() + key, session);
                    }
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

        if (maps.size() > 0) {
            arrayList.addAll(maps.values());
        }

        return arrayList;
    }

    /**
     * @Description: 获取会话室列表
     */
    public synchronized MessageBean getSessionById(String sessionId, String shopId) {
        Cursor cursor = null;
        MessageBean session = null;
        try {
            String sql = SELECT + ALL + FROM
                    + DatabaseHelper.TABLE_NAME_SESSION + WHERE
                    + SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT + ORDER + SessionColumn.MSG_TIME + DESC;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    session = getMessageBeanFromCursor(cursor);
                    break;
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
        return session;
    }
    /**
     * @Description: 获取会话室ID
     */
    public synchronized Map<String, String> getSessionIds() {
        Cursor cursor = null;
        Map<String, String> idMap = new LinkedHashMap<String, String>();
        try {
            String sql = SELECT + SessionColumn.SESSION_ID + FROM
                    + DatabaseHelper.TABLE_NAME_SESSION;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    idMap.put(cursor.getString(0), cursor.getString(0));
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
        return idMap;
    }

    /**
     * 通过会话ID查找是否存在
     */
    public synchronized boolean hasSession(String sessionId, String tableName) {
        Cursor cursor = null;
        if (sessionId != null) {
            try {
                String sql = SELECT + "count(" + SessionColumn.ID + ")" + FROM
                        + tableName + WHERE
                        + SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                        + EQ_STR_RIGHT;
                cursor = getInstance().sqliteDB().rawQuery(sql, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        boolean hasSession = cursor.getInt(0) > 0;
                        return hasSession;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return false;
    }

    /**
     * 通过会话ID查找是否存在
     */
    public synchronized boolean hasSessionForNewFriend(String chatWith) {
        Cursor cursor = null;
        if (chatWith != null) {
            try {
                String sql = SELECT + "count(" + SessionColumn.ID + ")" + FROM
                        + DatabaseHelper.TABLE_NAME_NEW_FRIEND + WHERE
                        + NewFriendsColumn.W_ID + EQ_STR_LEFT + chatWith
                        + EQ_STR_RIGHT;
                cursor = getInstance().sqliteDB().rawQuery(sql, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        boolean hasSession = cursor.getInt(0) > 0;
                        return hasSession;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return false;
    }
    /**
     * 通过会话ID查找是否存在
     */
    public synchronized boolean hasSessionByMsgCode(String msgCode, String tableName) {
        Cursor cursor = null;
        if (msgCode != null) {
            try {
                String sql = SELECT + "count(" + SessionColumn.ID + ")" + FROM
                        + tableName + WHERE
                        + SessionColumn.MSG_CODE + EQ_STR_LEFT + msgCode
                        + EQ_STR_RIGHT;
                cursor = getInstance().sqliteDB().rawQuery(sql, null);
                if (cursor != null) {
                    boolean hasSession = false;
                    while (cursor.moveToNext()) {
                        hasSession = cursor.getInt(0) > 0;
                        return hasSession;
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
        }
        return false;
    }

    /**
     * 生成一个新的会话消息
     */
    public synchronized boolean insertSession(MessageBean msg, String content) {
        String sessionId = msg.getSessionId().trim();
        boolean isGroupMode = false, isNewFriends = false,isNewFriendInserted = false;
        if (SessionTypeEnum.SS == msg.getSessionType()) {
            int ty = msg.getMsgType().getValue();
            if (ty == IMTypeUtil.SSType.FOLLOW) {
                isNewFriends=isGroupMode = true;
                sessionId = SessionTypeEnum.FRIEND.name();
                msg.setChatWith(((MsgSSBody) msg.getAttachment()).getField5());
            }
        }
        if (isNewFriends) {//处理新朋友表
            if (hasSessionForNewFriend(msg.getChatWith())) {
                isNewFriendInserted = true;
                upDateDataForNewFriend(msg.getMsgTime(), msg.getChatWith());
            } else {
                isNewFriendInserted = insertDataForNewFriend(msg, DatabaseHelper.TABLE_NAME_NEW_FRIEND) > 0;
            }
            msg.setSessionType(SessionTypeEnum.SS);
        }
        if (hasSession(sessionId, DatabaseHelper.TABLE_NAME_SESSION)) {
            if (isGroupMode) {
                if (isNewFriends) {
                    updateSessionForNewFriends(getSessionById(SessionTypeEnum.FRIEND.name(), ""), msg.getImUserBean().getName() + msg.getSession());
                } else {
                    updateSessionByType(msg.getSessionId().trim(), sessionId, "");
                }
            } else {
                updateSession(msg.getSessionId().trim(), "");
            }
        } else {
            if (isNewFriends && !isNewFriendInserted) {//处理新朋友表
                return false;
            }
            inserDataForSession(msg, content, sessionId, DatabaseHelper.TABLE_NAME_SESSION);
        }
        return true;
    }

    /**
     * 插入会话数据内容
     */
    private long inserDataForSession(MessageBean msg, String content, String sessionId, String tableName) {
        long row = -1;
        ContentValues values = new ContentValues();
        try {
            values.put(SessionColumn.MSG_TIME, msg.getMsgTime());
            values.put(SessionColumn.SESSION_TYPE, msg.getSessionType().getValue());
            values.put(SessionColumn.SESSION, content);
            values.put(SessionColumn.DIRECT, msg.getDirect().getValue());
            values.put(SessionColumn.MSG_STATUS, msg.getMsgStatus().getValue());
            values.put(SessionColumn.READ_STATUS, msg.getIsRead());
            values.put(SessionColumn.MESSAGE_TYPE, msg.getMsgType().getValue());
            values.put(SessionColumn.CONTACT_ID, msg.getChatWith());
            values.put(SessionColumn.MSG_CODE, msg.getMsgCode());
            values.put(SessionColumn.SESSION_ID, sessionId);
            values.put(SessionColumn.UNREAD_NUM, msg.getDirect().getValue() == 0 ? 0
                    : 1);// 根据消息发送方，设置消息初始化值
            row = getInstance().sqliteDB().insertOrThrow(
                    tableName, null, values);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            values.clear();
            values = null;
        }
        return row;
    }

    /**
     * 更新新朋友会话数据内容
     */
    private long updateSessionForNewFriends(MessageBean msg, String content) {
        long row = -1;
        ContentValues values = new ContentValues();
        try {
            values.put(SessionColumn.MSG_TIME, msg.getMsgTime());
            values.put(SessionColumn.SESSION, content);
            values.put(SessionColumn.DIRECT, MsgDirectionEnum.In.getValue());
            values.put(SessionColumn.MSG_STATUS, 1);
            values.put(SessionColumn.MSG_TIME, ChatUtil.getInstance().getSendMsgTime());
            values.put(SessionColumn.MSG_CODE, msg.getMsgCode());
            values.put(SessionColumn.UNREAD_NUM, msg.getUnReads() + 1);
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLE_NAME_SESSION, values, SessionColumn.SESSION_ID + EQ_STR_LEFT + msg.getSessionId() + EQ_STR_RIGHT, null);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            values.clear();
            values = null;
        }
        return row;
    }

    /**
     * 插入新朋友数据内容
     */
    private long upDateDataForNewFriend(long msgTime, String chatWith) {
        long row = -1;
        ContentValues values = new ContentValues();
        try {
            values.put(NewFriendsColumn.MSG_TIME, msgTime);
            row = getInstance().sqliteDB().update(DatabaseHelper.TABLE_NAME_NEW_FRIEND, values, NewFriendsColumn.W_ID + EQ_STR_LEFT + chatWith + EQ_STR_RIGHT, null);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            values.clear();
            values = null;
        }
        return row;
    }

    /**
     * 插入新朋友数据内容
     */
    private long insertDataForNewFriend(MessageBean msg, String tableName) {
        long row = -1;
        ContentValues values = new ContentValues();
        try {
            values.put(NewFriendsColumn.MSG_TIME, msg.getMsgTime());
            values.put(NewFriendsColumn.W_ID, msg.getChatWith());
            values.put(NewFriendsColumn.MSG_CODE, msg.getMsgCode());
            values.put(SessionColumn.SESSION_ID, msg.getChatWith() + "@" + IMTypeUtil.BoxType.SINGLE_CHAT);
            row = getInstance().sqliteDB().insertOrThrow(
                    tableName, null, values);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            values.clear();
            values = null;
        }
        return row;
    }

    /**
     * @Description: 根据id删除会话
     */
    public boolean deleteById(String sessionId, String tableName) {
        if (TextUtils.isEmpty(tableName))
            tableName = DatabaseHelper.TABLE_NAME_SESSION;
        return getInstance().sqliteDB().delete(
                tableName,
                SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                        + EQ_STR_RIGHT, null) > 0;
    }
    /**
     * 更新置顶
     */
    public synchronized boolean updateTop(String sessionId, boolean isTop) {
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT;
            values.put(SessionColumn.IS_TOP, isTop ? 1 : 0);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return false;
    }

    /**
     * 更新置顶
     */
    public synchronized boolean updateTopForShop(String sessionId, boolean isTop) {
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT;
            values.put(SessionColumn.IS_TOP, isTop ? 1 : 0);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SHOP, values, where, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return false;
    }

    /**
     * 更新免打扰
     */
    public synchronized boolean updateForbid(String sessionId, int isForbid) {
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT;
            values.put(SessionColumn.IS_FORBID, isForbid);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return false;
    }

    /**
     * @Description: 判断会话是否免打扰
     */
    public synchronized boolean isForbid(String sessionId) {
        boolean isForbid = false;
        String[] columnsList = {SessionColumn.IS_FORBID};
        String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                + EQ_STR_RIGHT;
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().query(
                    DatabaseHelper.TABLE_NAME_SESSION, columnsList, where,
                    null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    isForbid = cursor.getInt(cursor
                            .getColumnIndex(SessionColumn.IS_FORBID)) == 1;
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
        return isForbid;
    }
    /**
     * @Title: getAllUnreads
     * @Description: 获取所有未读消息
     */
    public int getAllUnreadMsgs() {
        int count = 0;
        Cursor cursor = null;
        try {
            Map<String, String> idsMap = getSessionIds();
            Iterator<String> it = idsMap.keySet().iterator();
            while (it.hasNext()) {
                String sessionId = it.next();
                String sql = SELECT + SessionColumn.UNREAD_NUM + FROM
                        + DatabaseHelper.TABLE_NAME_SESSION + WHERE
                        + SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                        + EQ_STR_RIGHT  + ORDER + SessionColumn.ID + " asc ";
                cursor = getInstance().sqliteDB().rawQuery(sql, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        count += cursor.getInt(0);
                        break;
                    }
                }
            }
            idsMap = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
    /**
     *  weyko 更新会话
     */
    public synchronized long updateSession(String sessionId, String shopId) {
        ContentValues values = new ContentValues();
        try {
            MessageBean sessionBean = ChatMessageManager.getInstance()
                    .getSessionForUpdate(sessionId, shopId);
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT;
            putMsgValues(values, sessionBean);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }

    /***
     * 设置消息类容
     */
    private void putMsgValues(ContentValues values, MessageBean sessionBean) {
        values.put(SessionColumn.MSG_TIME, sessionBean.getMsgTime());
        values.put(SessionColumn.MSG_CODE, sessionBean.getMsgCode());
        values.put(SessionColumn.MSG_STATUS, sessionBean.getMsgStatus().getValue());
        values.put(SessionColumn.READ_STATUS, sessionBean.getIsRead());
        values.put(SessionColumn.UNREAD_NUM, sessionBean.getUnReads());
        values.put(SessionColumn.SESSION, sessionBean.getSession());
        values.put(SessionColumn.MESSAGE_TYPE, sessionBean.getMsgType().getValue());
        values.put(SessionColumn.DIRECT, sessionBean.getDirect().getValue());
        values.put(SessionColumn.SESSION_TYPE, sessionBean.getSessionType().getValue());
    }
    /**
     *  更新外层组会话
     */
    public synchronized long updateSessionBySessionType(String sessionType, String sessionId, String shopId) {
        ContentValues values = new ContentValues();
        try {
            MessageBean sessionBean = ChatMessageManager.getInstance()
                    .getSessionForUpdate(sessionId, shopId);
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionType+ EQ_STR_RIGHT;
            putMsgValues(values, sessionBean);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }

    /**
     *  weyko 更新会话
     */
    public synchronized long updateSessionOfTable(String sessionId, String tableName) {
        ContentValues values = new ContentValues();
        try {
            MessageBean sessionBean = ChatMessageManager.getInstance()
                    .getSessionForUpdate(sessionId, "");
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId + EQ_STR_RIGHT;
            putMsgValues(values, sessionBean);
            return getInstance().sqliteDB().update(
                    tableName, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }
    /**
     * 更新会话
     */
    public synchronized long updateMsg(String sessionId, String session) {
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId+ EQ_STR_RIGHT;
            values.put(SessionColumn.MSG_STATUS, 1);
            values.put(SessionColumn.DIRECT, 1);
            values.put(SessionColumn.MSG_TIME, ChatUtil.getInstance().getSendMsgTime());
            values.put(SessionColumn.SESSION_TYPE, SessionTypeEnum.SS.getValue());
            values.put(SessionColumn.SESSION,
                    chat.common.util.TextUtils.getString(session));
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }
    /**
     * 更新会话
     * @return
     */
    public synchronized long updateSessionByType(String sessionId, String sessionType, String shopId) {
        ContentValues values = new ContentValues();
        try {
            MessageBean sessionBean = ChatMessageManager.getInstance()
                    .getSessionForUpdate(sessionId, shopId);
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionType
                    + EQ_STR_RIGHT;
            putMsgValues(values, sessionBean);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }
    /**
     * 更新会话未读数为0
     * @return
     */
    public synchronized long updateSessionUnReadAll(String sessionId) {
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.SESSION_ID + EQ_STR_LEFT + sessionId
                    + EQ_STR_RIGHT;
            values.put(SessionColumn.UNREAD_NUM, 0);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }
    /**
     * 更新会话发送状态
     * @return
     */
    public synchronized long updateSessionSendStatus(String msgCode,int sendStatus) {
        if (!hasSessionByMsgCode(msgCode, DatabaseHelper.TABLE_NAME_SESSION)) {
            return 0;
        }
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.MSG_CODE + EQ_STR_LEFT + msgCode+ EQ_STR_RIGHT;
            values.put(SessionColumn.MSG_STATUS, sendStatus);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }

    /**
     * 更新会话已读状态
     * @return
     */
    public synchronized long updateSessionReadStatus(String msgCode,int readStatus) {
        if (!hasSessionByMsgCode(msgCode, DatabaseHelper.TABLE_NAME_SESSION)) {
            return 0;
        }
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.MSG_CODE + EQ_STR_LEFT + msgCode+ EQ_STR_RIGHT;
            values.put(SessionColumn.READ_STATUS, readStatus);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }
    /**
     * 更新会话状态
     * @return
     */
    public synchronized long updateSessionStatus(String msgCode,int readStatus, int msgStatus) {
        if (!hasSessionByMsgCode(msgCode, DatabaseHelper.TABLE_NAME_SESSION)) {
            return 0;
        }
        ContentValues values = new ContentValues();
        try {
            String where = SessionColumn.MSG_CODE + EQ_STR_LEFT + msgCode+ EQ_STR_RIGHT;
            values.put(SessionColumn.READ_STATUS, readStatus);
            values.put(SessionColumn.MSG_STATUS, msgStatus);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_SESSION, values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
        }
        return -1;
    }
    public void reset() {
        getInstance().release();
    }
    @Override
    protected void release() {
        super.release();
        instance = null;
    }
}
