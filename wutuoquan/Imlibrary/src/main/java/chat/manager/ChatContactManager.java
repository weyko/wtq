package chat.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import chat.base.IMClient;
import chat.common.util.output.LogUtil;
import chat.contact.bean.ContactBean;
import chat.service.MessageInfoReceiver;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.ChatGroupMemberBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;

/**
 * @ClassName: ChatContactManager
 * @Description: 聊天联系人管理类
 */
public class ChatContactManager extends AbstractChatDBManager {
	private static ChatContactManager instance;
	/** 对象锁 */
	Object mLock = new Object();

	public ChatContactManager() {
	}

	public static ChatContactManager getInstance() {
		if (instance == null)
			instance = new ChatContactManager();
		return instance;
	}

	/**
	 * 
	 * @Title: insertContact
	 * @param:
	 * @Description:插入联系人到数据库
	 * @return long
	 */
	public synchronized long insertContact(ImUserBean contact) {
		if (contact == null || TextUtils.isEmpty(contact.getMxId())) {
			return -1;
		}
		try {
			ContentValues values = contact.buildContentValues(true);
				if (!hasContact(contact.getMxId())) {
					return getInstance().sqliteDB().insert(
							DatabaseHelper.TABLE_NAME_CONTACT, null, values);
				}
				getInstance().sqliteDB().update(
						DatabaseHelper.TABLE_NAME_CONTACT,
						values,
						ContactsColumn.W_ID + EQ_STR_LEFT + contact.getMxId()
								+ EQ_STR_RIGHT, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 
	 * @Title: insertContact
	 * @param:
	 * @Description:插入联系人到数据库
	 * @return long
	 */
	public synchronized long insertContact(ImUserBean contact, boolean isGroup) {
		if (contact == null || TextUtils.isEmpty(contact.getMxId())) {
			return -1;
		}
		try {
			ContentValues values = contact.buildContentValues(isGroup);
			if (!hasContact(contact.getMxId())) {
				return getInstance().sqliteDB().insert(
						DatabaseHelper.TABLE_NAME_CONTACT, null, values);
			}
			getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT,
					values,
					ContactsColumn.W_ID + EQ_STR_LEFT + contact.getMxId()
							+ EQ_STR_RIGHT, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 
	 * @Title: insertContact
	 * @param:
	 * @Description:插入联系人到数据库
	 * @return long
	 */
	public synchronized long insertContact(ChatGroupMemberBean contact) {
		if (contact == null
				|| TextUtils.isEmpty(String.valueOf(contact.getUserId()))) {
			return -1;
		}
		try {
			ContentValues values = new ContentValues();
			values.put(ContactsColumn.W_ID, contact.getUserId());
			values.put(ContactsColumn.AVATAR, contact.getAvatar());
			values.put(ContactsColumn.NAME, contact.getNickName());
			values.put(ContactsColumn.SEX, contact.getSexType());
			values.put(ContactsColumn.REMARK, contact.getRemark());
			if (!hasContact(String.valueOf(contact.getUserId()))) {
				return getInstance().sqliteDB().insert(
						DatabaseHelper.TABLE_NAME_CONTACT, null, values);
			}
			getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT,
					values,
					ContactsColumn.W_ID + EQ_STR_LEFT + contact.getUserId()
							+ EQ_STR_RIGHT, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 批量插入联系人到数据库
	 * 
	 * @param contacts
	 * @return
	 */
	public ArrayList<Long> insertContactsTransaction(List<ImUserBean> contacts) {
		ArrayList<Long> rows = new ArrayList<Long>();
		try {
			getInstance().sqliteDB().beginTransaction();
			for (ImUserBean c : contacts) {
				long rowId = insertContact(c, false);
				if (rowId != -1L) {
					rows.add(rowId);
				}
			}
			getInstance().sqliteDB().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			getInstance().sqliteDB().endTransaction();
		}
		return rows;
	}

	/**
	 * 插入联系人列表到数据库
	 * 
	 * @param contacts
	 * @return
	 */
	public ArrayList<Long> insertContacts(List<ContactBean> contacts) {
		ArrayList<Long> rows = new ArrayList<Long>();
		if(contacts!=null){
			HashMap<String, ImUserBean> contactsForOld = getContactsForOld();
			int size = contacts.size();
			int sizeOld = contactsForOld.size();
			if (size >= sizeOld) {
				for (ContactBean c : contacts) {
					c.setState(IMTypeUtil.FansStatus.FRIEND);
					long rowId = insertContact(getImUserBeanByContact(c), false);
					if (rowId != -1L) {
						rows.add(rowId);
					}
				}
			} else {
				HashMap<String, ImUserBean> contactsForNew = new HashMap<String, ImUserBean>();
				for (ContactBean c : contacts) {
					ImUserBean imUserBean = contactsForOld.get(String.valueOf(c
							.getFriendID()));
					c.setState(IMTypeUtil.FansStatus.FRIEND);
					if (imUserBean != null) {
						contactsForOld.put(String.valueOf(c.getFriendID()),
								getImUserBeanByContact(c));
					}
					contactsForNew.put(String.valueOf(c.getFriendID()),
							getImUserBeanByContact(c));
				}
				Iterator<Entry<String, ImUserBean>> iterator = contactsForOld
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, ImUserBean> next = iterator.next();
					if (contactsForNew.get(next.getKey()) != null) {
						insertContact(contactsForNew.get(next.getKey()), false);
					} else {
						ImUserBean value = next.getValue();
						value.setFansStatus(IMTypeUtil.FansStatus.NONE);
						insertContact(value, false);
					}

				}
			}
		}
		return rows;
	}
	private ImUserBean getImUserBeanByContact(ContactBean contactBean){
		ImUserBean imUserBean=new ImUserBean();
		if(contactBean!=null){
			imUserBean.setMxId(String.valueOf(contactBean.getFriendID()));
		}else{
			imUserBean.setMxId("");
		}
		imUserBean.setRemark(contactBean.getRemarkName());
		imUserBean.setAvatar(contactBean.getUserImg());
		imUserBean.setName(contactBean.getUserNickname());
		return imUserBean;
	}
	/**
	 * 
	 * @Title: insertContact
	 * @param:
	 * @Description:更新联系人名称
	 * @return long
	 */
	public synchronized boolean updateNickName(String userId, String name) {
		if (userId == null || TextUtils.isEmpty(String.valueOf(userId))) {
			return false;
		}
		try {
			ContentValues values = new ContentValues();
			values.put(ContactsColumn.NAME, name);
			return getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT, values,
					ContactsColumn.W_ID + EQ_STR_LEFT + userId + EQ_STR_RIGHT,
					null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @Title: updateRemark
	 * @param:
	 * @Description:更新联系人备注
	 * @return long
	 */
	public synchronized boolean updateRemark(String userId, String remark) {
		if (userId == null || TextUtils.isEmpty(String.valueOf(userId))) {
			return false;
		}
		try {
			ContentValues values = new ContentValues();
			values.put(ContactsColumn.REMARK, remark);
			return getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT, values,
					ContactsColumn.W_ID + EQ_STR_LEFT + userId + EQ_STR_RIGHT,
					null) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @Title: updateFanState
	 * @param:
	 * @Description: 更新好友状态
	 * @return long
	 */
	public synchronized boolean updateFanState(String userId, int fansStatus) {
		if (userId == null || TextUtils.isEmpty(String.valueOf(userId))) {
			return false;
		}
		try {
			ContentValues values = new ContentValues();
			values.put(ContactsColumn.FANS_STATUS, fansStatus);
			int update = getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT, values,
					ContactsColumn.W_ID + EQ_STR_LEFT + userId + EQ_STR_RIGHT,
					null);
			LogUtil.d("weyko", "update-----------" + update);
			return update > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 查询联系人
	 * 
	 * @return
	 */
	public ImUserBean getImUserBean(String mxId) {
		ImUserBean imUserBean = new ImUserBean();
		Cursor cursor=null;
		try {
			cursor = getInstance().sqliteDB().query(
					DatabaseHelper.TABLE_NAME_CONTACT, null,
					ContactsColumn.W_ID + EQ_STR_LEFT + mxId + EQ_STR_RIGHT,
					null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					imUserBean.setValueFromCursor(cursor);
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
		return imUserBean;

	}

	/**
	 * 查询联系人
	 * 
	 * @return
	 */
	public ImUserBean getImUserBean(String mxId, String shopid) {
		ImUserBean imUserBean = new ImUserBean();
		Cursor cursor=null;
		try {
			cursor = getInstance().sqliteDB().query(
					DatabaseHelper.TABLE_NAME_CONTACT,
					null,
					ContactsColumn.W_ID + EQ_STR_LEFT + mxId + EQ_STR_RIGHT
							+ AND + ContactsColumn.SHOP_ID + EQ_STR_LEFT
							+ shopid + EQ_STR_RIGHT, null, null, null, null,
					null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					imUserBean.setValueFromCursor(cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return imUserBean;

	}

	/**
	 * 查询联系人关系
	 * 
	 * @return
	 */
	public int getFanstate(String mxId) {
		Cursor cursor = null;
		try {
			String sql = SELECT + ContactsColumn.FANS_STATUS + ","
					+ ContactsColumn.W_ID + FROM
					+ DatabaseHelper.TABLE_NAME_CONTACT + WHERE
					+ ContactsColumn.W_ID + EQ_STR_LEFT + mxId + EQ_STR_RIGHT;
			cursor = getInstance().sqliteDB().rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int fanstate=cursor.getInt(0);
					//if (cursor != null) {
						cursor.close();
						cursor = null;
					//}
					return fanstate;
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
		return IMTypeUtil.FansStatus.NONE;
	}

	/**
	 * 查询联系人列表
	 * 
	 * @return
	 */
	public ArrayList<ImUserBean> getContacts() {
		ArrayList<ImUserBean> contacts = new ArrayList<ImUserBean>();
		Cursor cursor = null;
		try {
			cursor = getInstance().sqliteDB().query(
					DatabaseHelper.TABLE_NAME_CONTACT,
					null,
					ContactsColumn.FANS_STATUS + "=" + IMTypeUtil.FansStatus.FRIEND + AND
							+ ContactsColumn.W_ID + " <>'"
							+ ChatUtil.CHAT_SERVICE_ID + "'", null, null, null,
					null, null);
			if (cursor != null && cursor.getCount() > 0) {
				contacts = new ArrayList<ImUserBean>();
				while (cursor.moveToNext()) {
					ImUserBean c = new ImUserBean();
					c.setValueFromCursor(cursor);
					contacts.add(c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return contacts;

	}

	/**
	 * 查询联系人列表
	 * 
	 * @return
	 */
	public HashMap<String, ImUserBean> getContactsForOld() {
		HashMap<String, ImUserBean> contacts = new HashMap<String, ImUserBean>();
		Cursor cursor = null;
		try {
			cursor = getInstance().sqliteDB().query(
					DatabaseHelper.TABLE_NAME_CONTACT, null,
					ContactsColumn.FANS_STATUS + "=" + IMTypeUtil.FansStatus.FRIEND, null,
					null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					ImUserBean c = new ImUserBean();
					c.setValueFromCursor(cursor);
					contacts.put(c.getMxId(), c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return contacts;

	}

	/**
	 * 查询联系人列表
	 * 
	 * @return
	 */
	public ArrayList<ChatGroupMemberBean> getContactsForMember() {
		ArrayList<ChatGroupMemberBean> contacts = new ArrayList<ChatGroupMemberBean>();
		Cursor cursor = null;
		try {
			cursor = getInstance().sqliteDB().query(
					DatabaseHelper.TABLE_NAME_CONTACT,
					null,
					ContactsColumn.FANS_STATUS + "=" + IMTypeUtil.FansStatus.FRIEND + AND
							+ ContactsColumn.W_ID + "<>'"
							+ ChatUtil.CHAT_SERVICE_ID + "'", null, null, null,
					null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					ChatGroupMemberBean member = new ChatGroupMemberBean();
					member.setUserId(Long.valueOf(cursor.getString(cursor
							.getColumnIndex(ContactsColumn.W_ID))));
					member.setAvatar(cursor.getString(cursor
							.getColumnIndex(ContactsColumn.AVATAR)));
					member.setSexType(cursor.getInt(cursor
							.getColumnIndex(ContactsColumn.SEX)));
					member.setNickName(cursor.getString(cursor
							.getColumnIndex(ContactsColumn.NAME)));
					member.setMtalkDomain(cursor.getString(cursor
							.getColumnIndex(ContactsColumn.DOMAIN)));
					contacts.add(member);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return contacts;

	}

	/**
	 * 查询联系人列表
	 * 
	 * @return
	 */
	public ArrayList<ImUserBean> getContactsByName(String name) {
		ArrayList<ImUserBean> contacts = null;
		String selfId = IMClient.getInstance().getSSOUserId();
		Cursor cursor = null;
		try {
			String sql = SELECT + ALL + FROM
					+ DatabaseHelper.TABLE_NAME_CONTACT + WHERE + "("
					+ ContactsColumn.NAME + LIKE_LEFT + name + LIKE_RIGHT
					+ " or " + ContactsColumn.REMARK + LIKE_LEFT + name
					+ LIKE_RIGHT + ")" + AND + ContactsColumn.FANS_STATUS + "="
					+ IMTypeUtil.FansStatus.FRIEND;
			cursor = getInstance().sqliteDB().rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				contacts = new ArrayList<ImUserBean>();
				while (cursor.moveToNext()) {
					// 过滤自己的联系人账号信息
					if (selfId.equals(cursor.getString(cursor
							.getColumnIndex(ContactsColumn.W_ID)))) {
						continue;
					}
					ImUserBean c = new ImUserBean();
					c.setValueFromCursor(cursor);
					contacts.add(c);
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
		return contacts;

	}

	/**
	 * 查询联系人列表
	 * 
	 * @return
	 */
	public ArrayList<ContactBean> getFansBeans() {
		ArrayList<ContactBean> contacts = new ArrayList<ContactBean>();
		String selfId = IMClient.getInstance().getSSOUserId();
		Cursor cursor = null;
		try {
			cursor = getInstance().sqliteDB().query(
					DatabaseHelper.TABLE_NAME_CONTACT, null,
					ContactsColumn.FANS_STATUS + "=" + IMTypeUtil.FansStatus.FRIEND, null,
					null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					// 过滤自己的联系人账号信息
					if (selfId.equals(cursor.getString(cursor
							.getColumnIndex(ContactsColumn.W_ID)))) {
						continue;
					}
					ImUserBean c = new ImUserBean();
					c.setValueFromCursor(cursor);
					contacts.add(c.getFansBean());
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
		Log.d("weyko", "count=" + contacts.size());
		return contacts;

	}

	/**
	 * @Title: setIsTop
	 * @param:
	 * @Description:设置是否置顶
	 * @return void
	 */
	public boolean setIsTop(String userId, int isTop) {
		if (userId == null)
			return false;
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(ContactsColumn.IS_TOP, isTop);
			int len = getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT, values,
					ContactsColumn.W_ID + EQ_STR_LEFT + userId + EQ_STR_RIGHT,
					null);
			if (len > 0) {
				ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
						MessageInfoReceiver.EVENT_UPDATE_SETTING, userId,userId+"@"+ IMTypeUtil.BoxType.SINGLE_CHAT);
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
	 * @Title: setIsTop
	 * @param:
	 * @Description:设置是否置顶
	 * @return void
	 */
	public boolean setIsTop(String userId, String shopId, int isTop) {
		if (userId == null)
			return false;
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(ContactsColumn.IS_TOP, isTop);
			int len = getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT,
					values,
					ContactsColumn.W_ID + EQ_STR_LEFT + userId + EQ_STR_RIGHT
							+ AND + ContactsColumn.SHOP_ID + EQ_STR_LEFT
							+ shopId + EQ_STR_RIGHT, null);
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
	 * @Title: setIsTop
	 * @param:
	 * @Description:设置是否免打扰
	 * @return void
	 */
	public boolean setIsForbid(String userId, int isForbid) {
		if (userId == null)
			return false;
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(ContactsColumn.IS_FORBID, isForbid);
			int len = getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_CONTACT, values,
					ContactsColumn.W_ID + EQ_STR_LEFT + userId + EQ_STR_RIGHT,
					null);
			if (len > 0) {
				ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
						MessageInfoReceiver.EVENT_UPDATE_SETTING, userId,userId+"@"+ IMTypeUtil.BoxType.SINGLE_CHAT);
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
	 * @Title: hasContact
	 * @param:
	 * @Description: 判断联系人是否存在
	 * @return boolean
	 */
	public boolean hasContact(String contactId) {
		String sql = SELECT + ContactsColumn.W_ID + FROM
				+ DatabaseHelper.TABLE_NAME_CONTACT + WHERE
				+ ContactsColumn.W_ID + EQ_STR_LEFT + contactId + EQ_STR_RIGHT;
		Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		return false;
	}

	public boolean hasContact(String contactId, String shopid) {
		String sql = SELECT + ContactsColumn.W_ID + FROM
				+ DatabaseHelper.TABLE_NAME_CONTACT + WHERE
				+ ContactsColumn.W_ID + EQ_STR_LEFT + contactId + EQ_STR_RIGHT
				+ AND + ContactsColumn.SHOP_ID + EQ_STR_LEFT + shopid
				+ EQ_STR_RIGHT;
		Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		return false;
	}

	/**
	 * @Title: isFriend
	 * @param:
	 * @Description: 判断是否好友
	 * @return boolean
	 */
	public boolean isFriend(String userId) {
		String sql = SELECT + ContactsColumn.W_ID + FROM
				+ DatabaseHelper.TABLE_NAME_CONTACT + WHERE
				+ ContactsColumn.W_ID + EQ_STR_LEFT + userId + EQ_STR_RIGHT
				+ AND + ContactsColumn.FANS_STATUS + "=" + IMTypeUtil.FansStatus.FRIEND;
		Cursor cursor = getInstance().sqliteDB().rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String mxId = cursor.getString(0);
				if (!TextUtils.isEmpty(mxId)) {
					return true;
				}
			}
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
	}
}
