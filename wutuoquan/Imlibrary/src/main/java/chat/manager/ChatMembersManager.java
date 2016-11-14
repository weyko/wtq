package chat.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import chat.common.util.output.ShowUtil;
import chat.contact.bean.ContactBean;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.ChatGroupInfoResultBean;
import chat.session.group.bean.ChatGroupMemberBean;
import chat.session.group.bean.GroupMemberBaseBean;
import chat.session.group.bean.IMSGroupMembers;
import chat.session.group.bean.SparseArrayList;
import chat.session.util.IMTypeUtil;

/**
 * @ClassName: ChatMembersManager
 * @Description: 群组成员管理类
 */
public class ChatMembersManager extends AbstractChatDBManager {
	private static ChatMembersManager instance;
	/** 对象锁 */
	Object mLock = new Object();

	public ChatMembersManager() {
	}

	public static ChatMembersManager getInstance() {
		if (instance == null)
			instance = new ChatMembersManager();
		return instance;
	}

	/**
	 * 更新群组成员到数据库
	 */
	public long insertGroupMember(String roomId, ChatGroupMemberBean member) {
		if (member == null || TextUtils.isEmpty(member.getUserId() + "")) {
			return -1L;
		}
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.OWN_GROUP_ID, roomId);
			values.put(GroupMembersColumn.ISBAN, member.getIsban());
			values.put(GroupMembersColumn.W_ID, "" + member.getUserId());
			ShowUtil.log("weyko","insertGroupMember2--->userId==============="+member.getUserId());
			values.put(GroupMembersColumn.DOMAIN, member.getMtalkDomain());
			if (member.getAvatar() != null) {
				values.put(GroupMembersColumn.AVATAR, member.getAvatar());
			}
			if (member.getNickName() != null) {
				values.put(GroupMembersColumn.NICKNAME, member.getNickName());
			}
			values.put(GroupMembersColumn.ROLE, member.getRoleType());
			values.put(GroupMembersColumn.REMARK, member.getRemark());
			values.put(GroupMembersColumn.ISENABLE, 1);
			if (isExitMember(member.getRoomId() + "", member.getUserId() + "")) {
				return getInstance().sqliteDB().update(
						DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
						values,
						GroupMembersColumn.W_ID + "='" + member.getUserId()
								+ "' and " + GroupMembersColumn.OWN_GROUP_ID
								+ "='" + roomId + "'", null);
			}
			long rowId = getInstance().sqliteDB().insert(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, null, values);
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
	 * 更新群组成员到数据库
	 */
	public long insertGroupMember(String roomId, ContactBean member) {
		if (member == null) {
			return -1L;
		}
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.OWN_GROUP_ID, roomId);
			values.put(GroupMembersColumn.W_ID, "" + member.getFriendID());
			if (member.getUserImg() != null) {
				values.put(GroupMembersColumn.AVATAR, member.getUserImg());
			}
			if (member.getUserNickname() != null) {
				values.put(GroupMembersColumn.NICKNAME, member.getUserNickname());
			}
			values.put(GroupMembersColumn.ROLE, member.getRole());
			values.put(GroupMembersColumn.REMARK, member.getRemarkName());
			values.put(GroupMembersColumn.ISENABLE, 1);
			if (isExitMember(roomId,String.valueOf(member.getFriendID()))) {
				return getInstance().sqliteDB().update(
						DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
						values,
						GroupMembersColumn.W_ID + "='" + member.getFriendID()
								+ "' and " + GroupMembersColumn.OWN_GROUP_ID
								+ "='" + roomId + "'", null);
			}
			long rowId = getInstance().sqliteDB().insert(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, null, values);
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
	 * 更新群组成员到数据库
	 */
	public long insertGroupMember(String roomId, GroupMemberBaseBean member) {
		if (member == null || TextUtils.isEmpty(member.getUserId() + "")) {
			return -1L;
		}
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.OWN_GROUP_ID, roomId);
			String mxId = member.getUserId();
			if (mxId.contains("@"))
				mxId = mxId.substring(0, mxId.indexOf("@"));
			values.put(GroupMembersColumn.W_ID, mxId);
			ShowUtil.log("weyko","insertGroupMember3--->userId==============="+mxId);
			if (member.getPhotoUrl() != null) {
				values.put(GroupMembersColumn.AVATAR, member.getPhotoUrl());
			}
			if (member.getNickName() != null) {
				values.put(GroupMembersColumn.NICKNAME, member.getNickName());
			}
			values.put(GroupMembersColumn.DOMAIN, member.getMtalkDomain());
			values.put(GroupMembersColumn.ROLE, member.getRole());
			values.put(GroupMembersColumn.ISENABLE, 1);
			if (isExitMember(roomId, mxId)) {
				return getInstance().sqliteDB().update(
						DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
						values,
						GroupMembersColumn.W_ID + "='" + mxId + "' and "
								+ GroupMembersColumn.OWN_GROUP_ID + "='"
								+ roomId + "'", null);
			}
			long rowId = getInstance().sqliteDB().insert(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, null, values);
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
	 * 更新群组成员头像到数据库
	 */
	public synchronized long updateGroupAvatar(String roomId, String userId,
			String avatar) {
		if (userId == null || avatar == null) {
			return -1L;
		}
		if (userId.contains("@"))
			userId = userId.substring(0, userId.indexOf("@"));
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.AVATAR, avatar);
			return getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
					values,
					GroupMembersColumn.W_ID + "='" + userId + "' and "
							+ GroupMembersColumn.OWN_GROUP_ID + "='" + roomId
							+ "'", null);
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
	 * 更新群组成员角色到数据库
	 */
	public synchronized long updateGroupRole(String roomId, int roleType,
			String userId) {
		if (TextUtils.isEmpty(userId)) {
			return -1L;
		}
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.ROLE, roleType);
			return getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
					values,
					GroupMembersColumn.W_ID + "='" + userId + "' and "
							+ GroupMembersColumn.OWN_GROUP_ID + "='" + roomId
							+ "'", null);
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
	 * 更新群组成员角色到数据库
	 */
	public synchronized long updateMyNickName(String roomId, String mxId,
			String nickname) {
		if (roomId == null || mxId == null || nickname == null) {
			return -1L;
		}
		if (mxId.contains("@"))
			mxId = mxId.substring(0, mxId.indexOf("@"));
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.NICKNAME, nickname);
			return getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
					values,
					GroupMembersColumn.W_ID + "='" + mxId + "' and "
							+ GroupMembersColumn.OWN_GROUP_ID + "='" + roomId
							+ "'", null);
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
	 * @Title: updateGroupRoleForList
	 * @param:
	 * @Description: 批量更新群成员角色
	 * @return void
	 */
	public void updateGroupRoleForList(String roomId,
			List<ChatGroupMemberBean> members) {
		if (members == null || members.size() == 0) {
			return;
		}
		for (ChatGroupMemberBean member : members) {
			updateGroupRole(roomId, member.getRoleType(),
					String.valueOf(member.getUserId()));
		}
	}

	/**
	 * 更新群组成员到数据库
	 */
	public long insertGroupMember(ChatGroupInfoResultBean.ChatGroupInfoData.MembersData member, String roomId) {
		if (member == null || TextUtils.isEmpty(member.getUserId() + "")) {
			return -1L;
		}
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.OWN_GROUP_ID, roomId);
			values.put(GroupMembersColumn.W_ID, "" + member.getUserId());
			ShowUtil.log("weyko","insertGroupMember1--->userId==============="+member.getUserId());
			values.put(GroupMembersColumn.ROLE, member.getRole());
			if (member.getPhotoUrl() != null) {
				values.put(GroupMembersColumn.AVATAR, member.getPhotoUrl());
			}
			if (member.getNickName() != null) {
				values.put(GroupMembersColumn.NICKNAME, member.getNickName());
			}
			values.put(GroupMembersColumn.DOMAIN, member.getMtalkDomain());
			values.put(GroupMembersColumn.ISENABLE, 1);
			if (isExitMember(roomId, member.getUserId() + "")) {
				return getInstance().sqliteDB().update(
						DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
						values,
						GroupMembersColumn.W_ID + "='" + member.getUserId()
								+ "' and " + GroupMembersColumn.OWN_GROUP_ID
								+ "='" + roomId + "'", null);
			}
			long rowId = getInstance().sqliteDB().insert(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, null, values);
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
	 * 更新群组成员到数据库
	 */
	public long updateGroupMember(ChatGroupInfoResultBean.ChatGroupInfoData.MembersData member, String roomId) {
		if (member == null || TextUtils.isEmpty(member.getUserId() + "")) {
			return -1L;
		}
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(GroupMembersColumn.OWN_GROUP_ID, roomId);
			values.put(GroupMembersColumn.W_ID, "" + member.getUserId());
			if (member.getPhotoUrl() != null) {
				values.put(GroupMembersColumn.AVATAR, member.getPhotoUrl());
			}
			if (member.getNickName() != null) {
				values.put(GroupMembersColumn.NICKNAME, member.getNickName());
			}
			values.put(GroupMembersColumn.ISENABLE, 1);
			if (isExitMember(roomId, member.getUserId() + "")) {
				return getInstance().sqliteDB().update(
						DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
						values,
						GroupMembersColumn.W_ID + "='" + member.getUserId()
								+ "' and " + GroupMembersColumn.OWN_GROUP_ID
								+ "='" + roomId + "'", null);
			}
			long rowId = getInstance().sqliteDB().insert(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, null, values);
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
	 * 更新群组成员
	 * @param members
	 */
	public ArrayList<Long> insertGroupMembers(String roomId,
		SparseArray<ContactBean> members) {
		ArrayList<Long> rows = new ArrayList<Long>();
		if (members == null) {
			return rows;
		}
		int size=members.size();
		for(int i=0;i<size;i++){
			long row = insertGroupMember(roomId, members.valueAt(i));
			if (row != -1) {
				rows.add(row);
			}
		}
		return rows;
	}

	/**
	 * 更新群组成员
	 */
	public ArrayList<Long> insertGroupMembers(IMSGroupMembers data) {
		ArrayList<Long> rows = new ArrayList<Long>();
		if (data == null) {
			return rows;
		}
		List<GroupMemberBaseBean> members = data.getMemberList();
		for (GroupMemberBaseBean member : members) {
			long row = insertGroupMember(data.getRoomId(), member);
			if (row != -1) {
				rows.add(row);
			}
		}
		return rows;
	}

	/**
	 * 更新群组成员
	 */
	public ArrayList<Long> insertGroupMembers(String roomId,
			ChatGroupInfoResultBean bean) {
		ArrayList<Long> rows = new ArrayList<Long>();
		if (bean == null) {
			return rows;
		}
//		removeAll(roomId);
		List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> members = bean.getData().get(0).getMemberList();
		for (ChatGroupInfoResultBean.ChatGroupInfoData.MembersData member : members) {
			long row = insertGroupMember(member, bean.getData().get(0).getId()
					+ "");
			if (row != -1) {
				rows.add(row);
			}
		}
		return rows;
	}

	/**
	 * 通过事务批量更新群组成员
	 * 
	 * @param members
	 * @return
	 */
	public ArrayList<Long> insertGroupMembersByTran(String roomId,
			List<ChatGroupMemberBean> members) {

		ArrayList<Long> rows = new ArrayList<Long>();
		if (members == null) {
			return rows;
		}
		try {
			synchronized (getInstance().mLock) {
				getInstance().sqliteDB().beginTransaction();
				for (ChatGroupMemberBean member : members) {
					try {
						long row = insertGroupMember(roomId, member);
						if (row != -1) {
							rows.add(row);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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
	 * @Title: getGroupMembers
	 * @param:
	 * @Description: 通过群组Id获取成员列表
	 * @return ArrayList<ChatGroupMemberBean>
	 */
	public ArrayList<ChatGroupMemberBean> getGroupMembers(String groupId,
			int pageIndex, int pageSize) {
		Map<String, ChatGroupMemberBean> maps = new LinkedHashMap<String, ChatGroupMemberBean>();
		ArrayList<ChatGroupMemberBean> members = new ArrayList<ChatGroupMemberBean>();
		String sql = SELECT + ALL + FROM
				+ DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
				+ GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + groupId
				+ EQ_STR_RIGHT + AND + GroupMembersColumn.ISENABLE + "=1"
				+ ORDER + GroupMembersColumn.ROLE + DESC + " limit "
				+ (pageIndex - 1) * pageSize + "," + pageSize;
		Cursor cursor = null;
		try {
			cursor = getInstance().sqliteDB().rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					ChatGroupMemberBean groupMember = new ChatGroupMemberBean();
					String userId = cursor.getString(cursor
							.getColumnIndex(GroupMembersColumn.W_ID));
					if (userId.contains("@"))
						userId = userId.substring(0, userId.indexOf("@"));
					groupMember.setUserId(Long.valueOf(userId));
					groupMember.setRoomId(Long.valueOf(cursor.getString(cursor
							.getColumnIndex(GroupMembersColumn.OWN_GROUP_ID))));
					groupMember.setIsban(cursor.getInt(cursor
							.getColumnIndex(GroupMembersColumn.ISBAN)));
					groupMember.setRoleType(cursor.getInt(cursor
							.getColumnIndex(GroupMembersColumn.ROLE)));
					groupMember.setMtalkDomain(cursor.getString(cursor
							.getColumnIndex(GroupMembersColumn.DOMAIN)));
					groupMember.setRemark(cursor.getString(cursor
							.getColumnIndex(GroupMembersColumn.REMARK)));
					groupMember.setNickName(cursor.getString(cursor
							.getColumnIndex(GroupMembersColumn.NICKNAME)));
					groupMember.setAvatar(cursor.getString(cursor
							.getColumnIndex(GroupMembersColumn.AVATAR)));
					maps.put(userId, groupMember);
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
			members.addAll(maps.values());
		}
		maps = null;
		System.gc();
		return members;
	}

	/**
	 * @Title: getMemberOfGroup
	 * @param:
	 * @Description: 获取成员信息
	 * @return ImUserBean
	 */
	public ImUserBean getMemberOfGroup(String roomId, String userId) {
		ImUserBean imUserBean = new ImUserBean();
		String sql = SELECT + ALL + FROM
				+ DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
				+ GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + roomId
				+ EQ_STR_RIGHT + AND + GroupMembersColumn.W_ID + EQ_STR_LEFT
				+ userId + EQ_STR_RIGHT;
		Cursor rawQuery = null;
		try{
			rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
			if (rawQuery != null) {
				while (rawQuery.moveToNext()) {
					String mxid = rawQuery.getString(rawQuery
							.getColumnIndex(GroupMembersColumn.W_ID));
					if (mxid.contains("@"))
						mxid = mxid.substring(0, mxid.indexOf("@"));
					imUserBean.setMxId(mxid);
					imUserBean.setAvatar(rawQuery.getString(rawQuery
							.getColumnIndex(GroupMembersColumn.AVATAR)));
					imUserBean.setName(rawQuery.getString(rawQuery
							.getColumnIndex(GroupMembersColumn.NICKNAME)));
					imUserBean.setMtalkDomain(rawQuery.getString(rawQuery
							.getColumnIndex(GroupMembersColumn.DOMAIN)));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if (rawQuery != null) {
				rawQuery.close();
				rawQuery = null;
			}
		}
		return imUserBean;
	}

	/**
	 * @Title: getMemberNameOfGroup
	 * @param:
	 * @Description: 获取成员昵称
	 * @return ImUserBean
	 */
	public String getMemberNameOfGroup(String roomId, String userId) {
		String sql = SELECT + GroupMembersColumn.NICKNAME + FROM
				+ DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
				+ GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + roomId
				+ EQ_STR_RIGHT + AND + GroupMembersColumn.W_ID + EQ_STR_LEFT
				+ userId + EQ_STR_RIGHT;
		Cursor rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
		if (rawQuery != null) {
			while (rawQuery.moveToNext()) {
				return rawQuery.getString(rawQuery
						.getColumnIndex(GroupMembersColumn.NICKNAME));
			}
		}
		if (rawQuery != null) {
			rawQuery.close();
			rawQuery = null;
		}
		return "";
	}

	/**
	 * @Title: getAdminsOfGroup
	 * @param:
	 * @Description: 获取群管理员人数
	 * @return ImUserBean
	 */
	public int getAdminsOfGroup(String roomId) {
		getMembersInfoOfGroup(roomId);
		String sql = SELECT + GroupMembersColumn.ROLE + FROM
				+ DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
				+ GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + roomId
				+ EQ_STR_RIGHT + AND + GroupMembersColumn.ROLE + EQ_INT
				+ IMTypeUtil.RoleType.ADMINS;
		Cursor rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
		int admins = 0;
		if (rawQuery != null) {
			while (rawQuery.moveToNext()) {
				return rawQuery.getCount();
			}
		}
		if (rawQuery != null) {
			rawQuery.close();
			rawQuery = null;
		}
		return admins;
	}

	/**
	 * @Title: getMembersOfGroup
	 * @param:
	 * @Description: 获取群成员人数
	 * @return ImUserBean
	 */
	public int getMembersOfGroup(String roomId) {
		String sql = SELECT + GroupMembersColumn.ID + FROM
				+ DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
				+ GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + roomId
				+ EQ_STR_RIGHT + AND + GroupMembersColumn.ISENABLE + EQ_INT + 1;
		Cursor rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
		int members = 0;
		if (rawQuery != null) {
			members = rawQuery.getCount();
		}
		if (rawQuery != null) {
			rawQuery.close();
			rawQuery = null;
		}
		return members;
	}

	/**
	 * @Title: getMembersInfoOfGroup
	 * @param:
	 * @Description: 获取群成员人数
	 * @return ImUserBean
	 */
	public int getMembersInfoOfGroup(String roomId) {
		String sql = SELECT + GroupMembersColumn.ROLE + FROM
				+ DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
				+ GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + roomId
				+ EQ_STR_RIGHT + AND + GroupMembersColumn.ISENABLE + EQ_INT + 1;
		Cursor rawQuery = null;
		try {
			rawQuery = getInstance().sqliteDB().rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int members = 0;
		if (rawQuery != null) {
			while (rawQuery.moveToNext()) {
				Log.d("weyko", "rawQuery===role-----" + rawQuery.getInt(0));
				members++;
			}
		}
		if (rawQuery != null) {
			rawQuery.close();
			rawQuery = null;
		}
		return members;
	}

	/**
	 * @Title: removeMembers
	 * @param:
	 * @Description: 移除成员
	 * @return ArrayList<Long>
	 */
	public int removeMembers(String roomId, SparseArrayList<ContactBean> members) {
		int size = 0;
		try {
			StringBuilder memberIds = new StringBuilder();
			int memberSize=members.size();
			for(int i=0;i<memberSize;i++){
				if (i > 0)
					memberIds.append(" or ");
				memberIds.append(GroupMembersColumn.W_ID + EQ_STR_LEFT
						+ String.valueOf(members.valueAt(i).getFriendID()) + EQ_STR_RIGHT);
			}
			memberIds.append(" and " + GroupMembersColumn.OWN_GROUP_ID
					+ EQ_STR_LEFT + roomId + EQ_STR_RIGHT);
			ContentValues values = new ContentValues();
			values.put(GroupMembersColumn.ISENABLE, 0);
			size = getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, values,
					memberIds.toString(), null);
		} catch (Exception e) {
		}
		return size;
	}
	/**
	 * @Title: removeMembers
	 * @param:
	 * @Description: 移除成员
	 * @return ArrayList<Long>
	 */
	public int removeMembersForSGroup(String roomId, List<GroupMemberBaseBean> members) {
		int size = 0;
		try {
			StringBuilder memberIds = new StringBuilder();
			int temp = 0;
			for (GroupMemberBaseBean bean : members) {
				if (temp > 0)
					memberIds.append(" or ");
				memberIds.append(GroupMembersColumn.W_ID + EQ_STR_LEFT
						+ ("" + bean.getUserId()) + EQ_STR_RIGHT);
				temp++;
			}
			memberIds.append(" and " + GroupMembersColumn.OWN_GROUP_ID
					+ EQ_STR_LEFT + roomId + EQ_STR_RIGHT);
			ContentValues values = new ContentValues();
			values.put(GroupMembersColumn.ISENABLE, 0);
			size = getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, values,
					memberIds.toString(), null);
		} catch (Exception e) {
		}
		return size;
	}

	/**
	 * @Title: removeMember
	 * @param:
	 * @Description: 移除成员
	 * @return ArrayList<Long>
	 */
	public int removeMember(String roomId, String userId) {
		int size = 0;
		try {
			String caseSql = GroupMembersColumn.W_ID + EQ_STR_LEFT + userId
					+ EQ_STR_RIGHT + AND + GroupMembersColumn.OWN_GROUP_ID
					+ EQ_STR_LEFT + roomId + EQ_STR_RIGHT;
			ContentValues values = new ContentValues();
			values.put(GroupMembersColumn.ISENABLE, 0);
			size = getInstance().sqliteDB().update(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS, values, caseSql,
					null);
		} catch (Exception e) {
		}
		return size;
	}

	/**
	 * @Title: removeAll
	 * @param:
	 * @Description: 移除所有成员
	 * @return ArrayList<Long>
	 */
	public int removeAll(String roomId) {
		int size = 0;
		try {
			size = getInstance().sqliteDB().delete(
					DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS,
					GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + roomId
							+ EQ_STR_RIGHT, null);
		} catch (Exception e) {
		}
		return size;
	}

	/**
	 * @Title: isExitMember
	 * @param:
	 * @Description: 群组成员是否存在
	 * @return boolean
	 */
	public boolean isExitMember(String groupId, String mxId) {
		String sql = SELECT + GroupMembersColumn.W_ID + FROM
				+ DatabaseHelper.TABLE_NAME_GRUOP_MEMBERS + WHERE
				+ GroupMembersColumn.W_ID + EQ_STR_LEFT + mxId + EQ_STR_RIGHT
				+ AND + GroupMembersColumn.OWN_GROUP_ID + EQ_STR_LEFT + groupId
				+ EQ_STR_RIGHT;
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

	public static void registerGroupObserver(OnMessageChange observer) {
		getInstance().registerObserver(observer);
	}

	public static void unregisterGroupObserver(OnMessageChange observer) {
		getInstance().unregisterObserver(observer);
	}

	public static void notifyGroupChanged(String session) {
		getInstance().notifyChanged(session);
	}

	public static void reset() {
		getInstance().release();
	}
}
