package chat.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.base.IMClient;
import chat.common.util.TextUtils;
import chat.common.util.output.ShowUtil;
import chat.service.MessageInfoReceiver;
import chat.session.bean.MessageBean;
import chat.session.group.bean.ChatGroupBean;
import chat.session.group.bean.ChatGroupData;
import chat.session.group.bean.ChatGroupInfoResultBean;
import chat.session.group.bean.ChatGroupListBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;

/**
 * @author weyko zhong.xiwei@moxiangroup.com
 * @ClassName: ChatGroupManager
 */
public class ChatGroupManager extends AbstractChatDBManager {
    private static ChatGroupManager instance;
    /**
     * 对象锁
     */
    Object mLock = new Object();

    public ChatGroupManager() {
    }

    public synchronized static ChatGroupManager getInstance() {
        if (instance == null)
            instance = new ChatGroupManager();
        return instance;
    }

    /**
     * 更新群组到数据库
     *
     * @param group
     */
    public long insertGroup(ChatGroupBean group, boolean isFromList) {
        if (group == null)
            return -1L;
        ChatGroupData data = group.getData();
        if (data == null)
            return -1L;
        if (data.getId() == 0) {
            return -1L;
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            String groupId = data.getId() + "";
            values.put(GroupColumn.GROUP_ID, groupId);
            values.put(GroupColumn.GROUP_NAME, data.getGroupName());
            values.put(GroupColumn.GROUP_MAX, data.getMaxCnt());
            values.put(GroupColumn.GROUP_MEMBER_COUNTS, data.getNowCnt());
            values.put(GroupColumn.ROLE_TYPE, data.getRoleType());
            values.put(GroupColumn.GROUP_DATE_CREATED, group.getData()
                    .getCreateTime());
            values.put(GroupColumn.GROUP_PERMISSION, group.getPermission());
            values.put(GroupColumn.GROUP_OWNER, data.getCreatorId());
            values.put(GroupColumn.GROUP_AVATAR, data.getPhotoUrl());
            values.put(GroupColumn.GROUP_ISNOTICE, group.getIsNotice());
            values.put(GroupColumn.GROUP_ISTOP, group.getIsTop());
            values.put(GroupColumn.IS_CONTACT_MODE, group.getIsContactMode());
            if (!isFromList) {
                values.put(GroupColumn.GROUP_DECLARED, group.getDeclared());
                values.put(GroupColumn.GROUP_JOINED, group.getIsJoined());
            }
            if (isExitGroup(groupId)) {
                return getInstance().sqliteDB().update(
                        DatabaseHelper.TABLE_NAME_GRUOPS,
                        values,
                        GroupColumn.GROUP_ID + EQ_STR_LEFT + groupId
                                + EQ_STR_RIGHT, null);
            }
            long rowId = getInstance().sqliteDB().insert(
                    DatabaseHelper.TABLE_NAME_GRUOPS, null, values);
            return rowId;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }

        }
        return -1L;
    }

    /**
     * 批量更新群组
     *
     * @param imGroups
     * @param joined
     * @throws android.database.SQLException
     */
    public ArrayList<Long> insertGroupInfos(List<ChatGroupBean> imGroups,
                                            int joined) {

        ArrayList<Long> rows = new ArrayList<Long>();
        if (imGroups == null) {
            return rows;
        }
        try {
            synchronized (getInstance().mLock) {
                // Set the start transaction
                getInstance().sqliteDB().beginTransaction();

                // Batch processing operation
                for (ChatGroupBean imGroup : imGroups) {
                    try {
                        long row = insertGroup(imGroup, false);
                        if (row != -1) {
                            rows.add(row);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Set transaction successful, do not set automatically
                // rolls back not submitted.
                getInstance().sqliteDB().setTransactionSuccessful();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getInstance().sqliteDB().endTransaction();
        }
        return rows;
    }

    /**
     * 批量更新群组
     * @throws android.database.SQLException
     */
    public synchronized long insertGroupInfos(ChatGroupListBean groupListBean) {
        long row = 0;
        if (groupListBean == null) {
            return row;
        }
        HashMap<String, ChatGroupBean> groupListForOld = getGroupListForOld();
        try {
            // Set the start transaction
            getInstance().sqliteDB().beginTransaction();
            for (ChatGroupData data : groupListBean.getData()) {
                try {
                    ChatGroupBean imGroup = new ChatGroupBean();
                    imGroup.setData(data);
                    imGroup.setIsContactMode(data.getSaveToContacts());
                    if(groupListForOld != null){
                        ChatGroupBean chatGroupBean = groupListForOld.get(data
                                .getId() + "");
                        if (chatGroupBean != null) {
                            imGroup.setIsNotice(chatGroupBean.getIsNotice());
                            imGroup.setIsTop(chatGroupBean.getIsTop());
                        }
                        row = insertGroup(imGroup, true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Set transaction successful, do not set automatically
            // rolls back not submitted.
            getInstance().sqliteDB().setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getInstance().sqliteDB().endTransaction();
        }
        return row;
    }

    /**
     * 更新群组到数据库
     */
    public long insertGroupForMsg(MessageBean data) {
        if (data == null)
            return -1L;
        ContentValues values = null;
        try {
            values = new ContentValues();
            String groupId = data.getChatWith();
            if (TextUtils.getString(data.getImUserBean().getName()).length() > 0) {
                values.put(GroupColumn.GROUP_NAME, data.getImUserBean().getName());
            }
            if (TextUtils.getString(data.getImUserBean().getAvatar()).length() > 0)
                values.put(GroupColumn.GROUP_AVATAR, data.getImUserBean()
                        .getAvatar());
            values.put(GroupColumn.GROUP_ID, groupId);
            if (!getGroupIsValid(groupId))
                values.put(GroupColumn.GROUP_DATE_CREATED, data.getMsgTime());
            if (isExitGroup(groupId)) {
                return getInstance().sqliteDB().update(
                        DatabaseHelper.TABLE_NAME_GRUOPS,
                        values,
                        GroupColumn.GROUP_ID + EQ_STR_LEFT + groupId
                                + EQ_STR_RIGHT, null);
            }
            long rowId = getInstance().sqliteDB().insert(
                    DatabaseHelper.TABLE_NAME_GRUOPS, null, values);
            return rowId;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }

        }
        return -1L;
    }

    /**
     * 更新群组到数据库
     */
    public long updateGroup(ChatGroupInfoResultBean.ChatGroupInfoData data) {
        if (data == null)
            return -1L;
        if (data.getId() == 0) {
            return -1L;
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            String groupId = data.getId() + "";
            values.put(GroupColumn.GROUP_ID, groupId);
            values.put(GroupColumn.GROUP_NAME, data.getGroupName());
            values.put(GroupColumn.GROUP_MAX, data.getMaxCnt());
            values.put(GroupColumn.GROUP_MEMBER_COUNTS, data.getNowCnt());
            values.put(GroupColumn.ROLE_TYPE, data.getRoleType());
            values.put(GroupColumn.GROUP_OWNER, data.getCreatorId());
            values.put(GroupColumn.GROUP_PERMISSION, data.getVerifyType());
            values.put(GroupColumn.GROUP_ISPUBLIC, data.getPublicType());
            values.put(GroupColumn.GROUP_AVATAR, data.getPhotoUrl());
            values.put(GroupColumn.IS_CONTACT_MODE, data.getSaveToContacts());
            if (isExitGroup(groupId)) {
                return getInstance().sqliteDB().update(
                        DatabaseHelper.TABLE_NAME_GRUOPS,
                        values,
                        GroupColumn.GROUP_ID + EQ_STR_LEFT + groupId
                                + EQ_STR_RIGHT, null);
            }
            long rowId = getInstance().sqliteDB().insert(
                    DatabaseHelper.TABLE_NAME_GRUOPS, null, values);
            return rowId;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }

        }
        return -1L;
    }

    /**
     * 更新群组头像
     */
    public long updateGroupAvatar(String roomId, String avatar) {
        if (roomId == null || avatar == null)
            return -1L;
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupColumn.GROUP_AVATAR, avatar);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_GRUOPS, values,
                    GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }

        }
        return -1L;
    }

    /**
     * 更新群组昵称
     */
    public long updateGroupName(String roomId, String name) {
        if (roomId == null || name == null)
            return -1L;
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupColumn.GROUP_NAME, name);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_GRUOPS, values,
                    GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }

        }
        return -1L;
    }

    /**
     * 设置群无效
     */
    public long setGroupInValid(String roomId) {
        if (roomId == null)
            return -1L;
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupColumn.GROUP_DATE_CREATED, -1);
            return getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_GRUOPS, values,
                    GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }

        }
        return -1L;
    }

    /**
     * @return boolean
     * @Title: getGroupIsValid
     * @param:
     * @Description: 获取群是否有效
     */
    public boolean getGroupIsValid(String roomId) {
        String sql = SELECT + GroupColumn.GROUP_DATE_CREATED + FROM
                + DatabaseHelper.TABLE_NAME_GRUOPS + WHERE
                + GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT;
        Cursor rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
        if (rawQuery != null) {
            try {
                while (rawQuery.moveToNext()) {
                    return rawQuery.getLong(0) != -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * @return ArrayList<Long>
     * @Title: removeGroupById
     * @param:
     * @Description: 移除群
     */
    public int removeGroupById(String roomId) {
        int size = 0;
        try {
            size = getInstance().sqliteDB().delete(
                    DatabaseHelper.TABLE_NAME_GRUOPS,
                    GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * @return ArrayList<Long>
     * @Title: removeAll
     * @param:
     * @Description: 移除所有群
     */
    public int removeAll() {
        int size = 0;
        try {
            size = getInstance().sqliteDB().delete(
                    DatabaseHelper.TABLE_NAME_GRUOPS, null, null);
        } catch (Exception e) {
        }
        return size;
    }

    /**
     * 通过名称查询群组
     *
     * @return
     */
    public List<ChatGroupBean> getGroupsByName(String name) {
        ArrayList<ChatGroupBean> arrayList = new ArrayList<ChatGroupBean>();
        try {
            String sql = SELECT + ALL + FROM + DatabaseHelper.TABLE_NAME_GRUOPS
                    + WHERE + GroupColumn.GROUP_NAME + LIKE_LEFT + name
                    + LIKE_RIGHT + ORDER + GroupColumn.GROUP_DATE_CREATED
                    + DESC;
            Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ChatGroupBean group = new ChatGroupBean();
                    setChatGroupBeanFromCursor(cursor, group);
                    arrayList.add(group);
                }
                cursor.close();
            }
            cursor = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public String getNameOfUnNameGroup(String roomId) {
        String sql = SELECT + GroupMembersColumn.NICKNAME + FROM
                + DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
                + GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + roomId
                + EQ_STR_RIGHT;
        Cursor rawQuery = null;
        try {
            rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        if (rawQuery != null) {
            int index = 0;
            while (rawQuery.moveToNext()) {
                if (index > 2)
                    break;
                if (index > 0) {
                    builder.append(",");
                }
                builder.append(rawQuery.getString(0));
                index++;
            }
        }
        if (rawQuery != null) {
            rawQuery.close();
            rawQuery = null;
        }
        return builder.toString();
    }

    /**
     * @return String
     * @Title: getGroupNameById
     * @param:
     * @Description: 根据id查询群名称
     */
    public String getGroupNameById(String roomId) {
        String sql = SELECT + GroupColumn.GROUP_NAME + FROM
                + DatabaseHelper.TABLE_NAME_GRUOPS + WHERE
                + GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT;
        Cursor rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                return rawQuery.getString(0);
            }
        }
        if (rawQuery != null) {
            rawQuery.close();
            rawQuery = null;
        }
        return "";
    }

    /**
     * 通过名称查询群组
     *
     * @return
     */
    public ChatGroupBean getGroupBean(String roomId) {
        ChatGroupBean bean = new ChatGroupBean();
        Cursor cursor = null;
        try {
            String sql = SELECT + ALL + FROM + DatabaseHelper.TABLE_NAME_GRUOPS
                    + WHERE + GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId
                    + EQ_STR_RIGHT;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    setChatGroupBeanFromCursor(cursor, bean);
                }
                cursor.close();
            }
            cursor = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return bean;
    }

    /**
     * 查询群组列表
     *
     * @return
     */
    public List<ChatGroupBean> getGroupList() {
        ArrayList<ChatGroupBean> arrayList = new ArrayList<ChatGroupBean>();
        try {
            String sql = SELECT + ALL + FROM + DatabaseHelper.TABLE_NAME_GRUOPS
                    + WHERE + GroupColumn.IS_CONTACT_MODE + EQ_INT + 1 + ORDER
                    + GroupColumn.GROUP_DATE_CREATED + DESC;
            ShowUtil.log("weyko", "getGroupList_sql=" + sql);
            Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ChatGroupBean group = new ChatGroupBean();
                    setChatGroupBeanFromCursor(cursor, group);
                    Log.d("weyko", "group_info=" + group.toString());
                    arrayList.add(group);
                }
                cursor.close();
            }
            cursor = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * 查询群组列表
     *
     * @return
     */
    public HashMap<String, ChatGroupBean> getGroupListForOld() {
        HashMap<String, ChatGroupBean> arrayList = new HashMap<String, ChatGroupBean>();
        try {
            String sql = SELECT + ALL + FROM + DatabaseHelper.TABLE_NAME_GRUOPS
                    + WHERE + GroupColumn.IS_CONTACT_MODE + EQ_INT + 1 + ORDER
                    + GroupColumn.GROUP_DATE_CREATED + DESC;
            Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ChatGroupBean group = new ChatGroupBean();
                    setChatGroupBeanFromCursor(cursor, group);
                    arrayList.put(group.getData().getId() + "", group);
                }
                cursor.close();
            }
            cursor = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * 查询群组列表
     *
     * @return
     */
    public List<ChatGroupBean> getGroupListByName(String name) {
        ArrayList<ChatGroupBean> arrayList = new ArrayList<ChatGroupBean>();
        try {
            String sql = SELECT + ALL + FROM + DatabaseHelper.TABLE_NAME_GRUOPS
                    + WHERE + GroupColumn.GROUP_NAME + LIKE_LEFT + name
                    + LIKE_RIGHT + ORDER + GroupColumn.GROUP_DATE_CREATED
                    + DESC;
            ShowUtil.log("weyko", "getGroupList_sql=" + sql);
            Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ChatGroupBean group = new ChatGroupBean();
                    setChatGroupBeanFromCursor(cursor, group);
                    Log.d("weyko", "group_info=" + group.toString());
                    arrayList.add(group);
                }
                cursor.close();
            }
            cursor = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * @return void
     * @Title: setChatGroupBeanFromCursor
     * @param:
     * @Description: 填充群组数据
     */
    private void setChatGroupBeanFromCursor(Cursor cursor, ChatGroupBean group) {
        ChatGroupData data = group.getData();
        if (data == null) {
            data = new ChatGroupData();
        }
        try{
        data.setId(Long.valueOf(cursor.getString(cursor
                .getColumnIndex(GroupColumn.GROUP_ID))));
        String roomName = cursor.getString(cursor
                .getColumnIndex(GroupColumn.GROUP_NAME));
        if (android.text.TextUtils.isEmpty(roomName)) {
            data.setGroupName(getNameOfUnNameGroup(String.valueOf(group
                    .getData().getId())));
        } else {
            data.setGroupName(roomName);
        }
        data.setPhotoUrl(cursor.getString(cursor
                .getColumnIndex(GroupColumn.GROUP_AVATAR)));
        data.setRoleType(cursor.getInt(cursor
                .getColumnIndex(GroupColumn.ROLE_TYPE)));
        data.setNowCnt(cursor.getInt(cursor
                .getColumnIndex(GroupColumn.GROUP_MEMBER_COUNTS)));
        data.setPublicType(cursor.getInt(cursor
                .getColumnIndex(GroupColumn.GROUP_ISPUBLIC)));
        data.setVerifyType(cursor.getInt(cursor
                .getColumnIndex(GroupColumn.GROUP_PERMISSION)));
        data.setCreateTime(cursor.getLong(cursor
                .getColumnIndex(GroupColumn.GROUP_DATE_CREATED)));
        group.setIsNotice(cursor.getInt(cursor
                .getColumnIndex(GroupColumn.GROUP_ISNOTICE)));
        group.setIsTop(cursor.getInt(cursor
                .getColumnIndex(GroupColumn.GROUP_ISTOP)));
        group.setIsContactMode(cursor.getInt(cursor
                .getColumnIndex(GroupColumn.IS_CONTACT_MODE)));
        group.setData(data);
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    /**
     * @return void
     * @Title: checkGroup
     * @param:
     * @Description: 检查有没有群组，没有就插入
     */
    public void checkGroup(ChatGroupBean group, boolean isFromList) {
        if (!isExitGroup(group.getData().getId() + "")) {
            insertGroup(group, isFromList);
        }
    }

    /**
     * @return boolean
     * @Title: getIsNotify
     * @param:
     * @Description: 获取是否免打扰
     */
    public boolean getIsNotify(String roomId) {
        Cursor cursor = null;
        try {
            String sql = SELECT + GroupColumn.GROUP_ISNOTICE + FROM
                    + DatabaseHelper.TABLE_NAME_GRUOPS + WHERE
                    + GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId
                    + EQ_STR_RIGHT;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if(cursor != null){
                while (cursor.moveToNext()) {
                    boolean isNotify = cursor.getInt(0) == 1;
                    cursor.close();
                    cursor = null;
                    return isNotify;
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
        return false;
    }

    /**
     * @return void
     * @Title: setIsNotify
     * @param:
     * @Description:设置是否免打扰
     */
    public boolean setIsNotify(String roomId, int isNotify) {
        if (roomId == null)
            return false;
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupColumn.GROUP_ISNOTICE, isNotify);
            int len = getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_GRUOPS, values,
                    GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT,
                    null);
            if (len > 0) {
                ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
                        MessageInfoReceiver.EVENT_UPDATE_SETTING, roomId, roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
            }
            return len > 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
        return false;
    }

    /**
     * @return boolean
     * @Title: getIsTop
     * @param:
     * @Description: 获取是否置顶
     */
    public boolean getIsTop(String roomId) {
        Cursor cursor = null;
        try {
            String sql = SELECT + GroupColumn.GROUP_ISTOP + FROM
                    + DatabaseHelper.TABLE_NAME_GRUOPS + WHERE
                    + GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId
                    + EQ_STR_RIGHT;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if(cursor != null){
                while (cursor.moveToNext()) {
                    boolean isTop = cursor.getInt(0) == 1;
                    cursor.close();
                    cursor = null;
                    return isTop;
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
        return false;
    }

    /**
     * @return void
     * @Title: setIsNotify
     * @param:
     * @Description:设置是否置顶
     */
    public boolean setIsTop(String roomId, int isTop) {
        if (roomId == null)
            return false;
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupColumn.GROUP_ISTOP, isTop);
            int len = getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_GRUOPS, values,
                    GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT,
                    null);
            if (len > 0) {
                ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
                        MessageInfoReceiver.EVENT_UPDATE_SETTING, roomId,roomId+"@"+ IMTypeUtil.BoxType.GROUP_CHAT);
            }
            return len > 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
        return false;
    }

    /**
     * @return boolean
     * @Title: getIsBookTolist
     * @param:
     * @Description: 获取是否保存到通讯录
     */
    public boolean getIsBookTolist(String roomId) {
        Cursor cursor = null;
        try {
            String sql = SELECT + GroupColumn.IS_CONTACT_MODE + FROM
                    + DatabaseHelper.TABLE_NAME_GRUOPS + WHERE
                    + GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId
                    + EQ_STR_RIGHT;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    boolean isBookTolist = cursor.getInt(0) == 1;
                    cursor.close();
                    cursor = null;
                    return isBookTolist;
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
        return false;
    }

    /**
     * @return void
     * @Title: setIsNotify
     * @param:
     * @Description:设置是否保存到通讯录
     */
    public boolean setIsBookTolist(String roomId, int isBook) {
        if (roomId == null)
            return false;
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(GroupColumn.IS_CONTACT_MODE, isBook);
            int len = getInstance().sqliteDB().update(
                    DatabaseHelper.TABLE_NAME_GRUOPS, values,
                    GroupColumn.GROUP_ID + EQ_STR_LEFT + roomId + EQ_STR_RIGHT,
                    null);
            Log.w("weyko", "setIsBookTolist---------len=" + len);
            return len > 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
        return false;
    }

    /**
     * 群组是否存在
     *
     * @param groupId
     * @return
     */
    public boolean isExitGroup(String groupId) {
        String sql = SELECT + GroupColumn.GROUP_ID + FROM
                + DatabaseHelper.TABLE_NAME_GRUOPS + WHERE
                + GroupColumn.GROUP_ID + EQ_STR_LEFT + groupId + EQ_STR_RIGHT;
        Cursor cursor = null;
        try {
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
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
        destroy();
    }
}
