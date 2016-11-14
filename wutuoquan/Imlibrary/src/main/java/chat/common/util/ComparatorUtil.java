package chat.common.util;

import java.util.Comparator;

import chat.contact.bean.ContactBean;
import chat.session.bean.IMUserBase;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.group.bean.ChatGroupMemberBean;
import chat.session.group.bean.GroupBean;

/**
 * @ClassName: ComparatorUtil
 * @Description: 排序工具类
 */
public class ComparatorUtil {
	private static ComparatorUtil instance;

	public ComparatorUtil() {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
	}

	/**
	 * @Title: getInstance
	 * @param:
	 * @Description: 获取单例化对象
	 * @return ComparatorUtil
	 */
	public static ComparatorUtil getInstance() {
		if (instance == null)
			instance = new ComparatorUtil();
		return instance;
	}

	/**
	 * @Title: getAdminComparator
	 * @param:
	 * @Description: 获取群成员权限排序类
	 * @return AdminComparator
	 */
	public AdminComparator getAdminComparator() {
		return new AdminComparator();
	}

	/**
	 * @Title: getMemberComparator
	 * @param:
	 * @Description: 获取群成员名称排序类
	 * @return MemberComparator
	 */
	public MemberComparator getMemberComparator() {
		return new MemberComparator();
	}

	/**
	 * @Title: getChatComparator
	 * @param:
	 * @Description:置顶排序类
	 * @return ChatComparator
	 */
	public ChatTopComparator getChatComparator() {
		return new ChatTopComparator();
	}

	/**
	 * @Title: ChatCatalogComparator
	 * @param:
	 * @Description:名称排序类
	 * @return ChatComparator
	 */
	public ChatCatalogComparator getChatCatalogComparator() {
		return new ChatCatalogComparator();
	}

	/**
	 * @Title: getGroupNewMemberComparator
	 * @param:
	 * @Description: 获取群添加成员名称排序类
	 * @return GroupNewMemberComparator
	 */
	public GroupNewMemberComparator getGroupNewMemberComparator() {
		return new GroupNewMemberComparator();
	}

	/**
	 * @Title: getGroupBeanComparator
	 * @param:
	 * @Description: 获取好友分组排序类
	 * @return GroupBeanComparator
	 */
	public GroupBeanComparator getGroupBeanComparator() {
		return new GroupBeanComparator();
	}

	/**
	 * @Title: getMessagesComparator
	 * @param:
	 * @Description: 获取消息内容排序类
	 * @return MessagesComparator
	 */
	public MessagesComparator getMessagesComparator() {
		return new MessagesComparator();
	}

	/**
	 * @ClassName: AdminComparator
	 * @Description: 群成员按权限排序
	 * @author weyko zhong.xiwei@moxiangroup.com
	 * @Company moxian
	 * @date 2015年9月7日 下午7:35:24
	 *
	 */
	class AdminComparator implements Comparator<ChatGroupMemberBean> {
		@Override
		public int compare(ChatGroupMemberBean arg0, ChatGroupMemberBean arg1) {
			int roleType0 = arg0.getRoleType();
			int roleType1 = arg1.getRoleType();
			if (roleType0 > roleType1) {
				return -1;
			} else if (roleType0 < roleType1) {
				return 1;
			}
			return 0;
		}
	}

	/**
	 * @ClassName: ContactBean
	 * @Description: 群成员按名称排序
	 * @date 2015年9月7日 下午7:35:24
	 */
	class MemberComparator implements Comparator<ContactBean> {
		@Override
		public int compare(ContactBean arg0, ContactBean arg1) {
			String a = arg0.getCatalog();
			String b = arg1.getCatalog();
			int flag = a.compareTo(b);
			if (flag == 0) {
				return a.compareTo(b);
			} else {
				return flag;
			}
		}
	}

	/**
	 * 
	 * 置顶排序类 ChatComparator
	 * 
	 * @author weyko 2015年4月15日下午7:45:47
	 *
	 */
	class ChatTopComparator implements Comparator<MessageBean> {
		@Override
		public int compare(MessageBean arg0, MessageBean arg1) {
			int isTop0 = arg0.getIsTop();
			int isTop1 = arg1.getIsTop();
			// 判断是否有置顶
			if(isTop0 < isTop1){
				return 1;
			}else if(isTop0 == isTop1){
				if (arg0.getMsgTime() < arg1.getMsgTime()) {
					return 1;
				}else{
					return -1;
				}
			}else{
				return -1;
			}
		}
	}

	/**
	 * 
	 * 名称排序类 ChatCatalogComparator
	 * 
	 * @author weyko 2015年4月15日下午7:45:47
	 *
	 */
	class ChatCatalogComparator implements Comparator<IMUserBase> {
		@Override
		public int compare(IMUserBase lhs, IMUserBase rhs) {
			String a = lhs.getCatalog();
			String b = rhs.getCatalog();
			int flag = a.compareTo(b);
			if (flag == 0) {
				return a.compareTo(b);
			} else {
				return flag;
			}
		}
	}

	/**
	 * 
	 * 群添加成员名称排序类 GroupNewMemberComparator
	 * 
	 * @author weyko 2015年4月15日下午7:45:47
	 *
	 */
	class GroupNewMemberComparator implements Comparator<ImUserBean> {
		@Override
		public int compare(ImUserBean lhs, ImUserBean rhs) {
			String a = lhs.getPingyin();
			String b = rhs.getPingyin();
			int flag = a.compareTo(b);
			if (flag == 0) {
				return a.compareTo(b);
			} else {
				return flag;
			}
		}
	}

	/**
	 * 
	 * 群添加成员名称排序类 GroupNewMemberComparator
	 * 
	 * @author weyko 2015年4月15日下午7:45:47
	 *
	 */
	class GroupBeanComparator implements Comparator<GroupBean> {
		@Override
		public int compare(GroupBean lhs, GroupBean rhs) {
			String a = lhs.getPinYin();
			String b = rhs.getPinYin();
			int flag = a.compareTo(b);
			if (flag == 0) {
				return a.compareTo(b);
			} else {
				return flag;
			}
		}
	}

	/**
	 * 
	 * 消息内容排序类 MessagesComparator
	 * 
	 * @author weyko 2015年4月15日下午7:45:47
	 *
	 */
	class MessagesComparator implements Comparator<MessageBean> {
		@Override
		public int compare(MessageBean lhs, MessageBean rhs) {
			if (lhs.getMsgTime() > rhs.getMsgTime()) {
				return 1;
			} else if (lhs.getMsgTime() < rhs.getMsgTime()) {
				return -1;
			}
			return 0;
		}
	}

}
