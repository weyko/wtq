package chat.manager;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import chat.session.bean.MessageBean;

/**
 * @author weyko
 * @ClassName: ChatTembMsgManager
 * @Description: 聊天临时消息管理类
 */
public class ChatTembMsgManager extends AbstractChatDBManager {
    private static ChatTembMsgManager instance;

    public ChatTembMsgManager() {
    }

    public static ChatTembMsgManager getInstance() {
        if (instance == null)
            instance = new ChatTembMsgManager();
        return instance;
    }

    /**
     * @return long
     * @Title: insertTembData
     * @Description:插入临时消息到数据库
     */
    public synchronized long insertTembData(MessageBean messageBean) {
        if (messageBean == null) {
            return -1;
        }
        if (isExitMessage(messageBean.getMsgCode()))
            return 0;
        try {
            ContentValues values = new ContentValues();
            values.put(TembMsgColumn.SESSION_ID, messageBean.getSessionId());
            values.put(TembMsgColumn.MSG_CODE, messageBean.getMsgCode());
            return getInstance().sqliteDB().insert(
                    DatabaseHelper.TABLE_NAME_TEMB_MSG, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 查询临时消息
     *
     * @return
     */
    public List<MessageBean> getTembs(long tembId) {
        List<MessageBean> tembs = new ArrayList<MessageBean>();
        Cursor cursor = null;
        try {
            String caseSql = "";
            if (tembId > 0)
                caseSql = WHERE+TembMsgColumn.ID + " > " + tembId;
            String sql=SELECT+ALL+FROM+ DatabaseHelper.TABLE_NAME_TEMB_MSG+caseSql+" limit 0,5";
            cursor =sqliteDB().rawQuery(sql,null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MessageBean bean = new MessageBean();
                    bean.setMsgCode(cursor.getString(cursor.getColumnIndex(TembMsgColumn.MSG_CODE)));
                    bean.setSessionId(cursor.getString(cursor.getColumnIndex(TembMsgColumn.SESSION_ID)));
                    bean.setMsgTime(cursor.getInt(cursor.getColumnIndex(TembMsgColumn.ID)));
                    tembs.add(bean);
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
        return tembs;

    }

    /**
     * 删除临时消息
     *
     * @param msgCode
     * @return
     */
    public boolean deleteTembInfo(String msgCode) {
        String whereCase = TembMsgColumn.MSG_CODE + EQ_STR_LEFT + msgCode + EQ_STR_RIGHT;
        return sqliteDB().delete(DatabaseHelper.TABLE_NAME_TEMB_MSG, whereCase, null) > 0;
    }

    /**
     * 清除临时消息
     *
     * @return
     */
    public boolean clearTembInfo(String sessionId) {
        return sqliteDB().delete(DatabaseHelper.TABLE_NAME_TEMB_MSG, TembMsgColumn.SESSION_ID+EQ_STR_LEFT+sessionId+EQ_STR_RIGHT, null) > 0;
    }

    /**
     * @return boolean
     * @Title: isExitMessage
     * @param:
     * @Description: 判断消息是否存在
     */
    private boolean isExitMessage(String msgCode) {
        String sql = SELECT + TembMsgColumn.ID + FROM + DatabaseHelper.TABLE_NAME_TEMB_MSG + WHERE
                + TembMsgColumn.MSG_CODE + EQ_STR_LEFT + msgCode + EQ_STR_RIGHT;
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

    @Override
    protected void release() {
        super.release();
        instance = null;
    }

    public void registerGroupObserver(OnMessageChange observer) {
        getInstance().registerObserver(observer);
    }

    public void unregisterGroupObserver(OnMessageChange observer) {
        getInstance().unregisterObserver(observer);
    }

    public void notifyGroupChanged(String session) {
        getInstance().notifyChanged(session);
    }

    public void reset() {
        getInstance().release();
    }
}
