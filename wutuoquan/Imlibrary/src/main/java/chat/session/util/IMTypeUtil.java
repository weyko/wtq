package chat.session.util;

/**
 * @ClassName: ChatTypeUtil
 * @Description: 消息类型工具类
 */
public class IMTypeUtil {
	/**
	 * 关注基本消息类型:1 关注 2好友
	 */
	public class FollowTy {
		/** 关注 */
		public static final int FOLLOWING = 1;
		/** 好友 */
		public static final int FOLLOWED = 2;
	}

	/**
	 * 发送的富消息类型
	 */
	public class RichTy {
		/** 商品消息 */
		public static final int GOODS = 1;
		/** 优惠券消息 */
		public static final int COUPON = 2;
		/** 动态消息 */
		public static final int DYNAMIC = 3;
		/** 公告消息 */
		public static final int NOTICE = 4;
		/** 兑换券消息 */
		public static final int EXCHANGE = 5;
		/** 优惠券通知 */
		public static final int COUPON_NOTICE = 6;
	}

	/**
	 * 接受的群系统类型
	 */
	public class SGroupTy {
		/** 默认值 (仅用于提示) */
		public static final int DEFAULT = 0;
		/** 创建群 */
		public static final int CREATE = 1;
		/** 新增群成员 */
		public static final int ADD_MEMBER = 2;
		/** 群基本属性更新(群昵称， 群图片，群主) */
		public static final int UPDATE_BASE = 3;
		/** 新增管理员列表 */
		public static final int ADD_ADMIN = 4;
		/** 取消管理员 */
		public static final int REMOVE_ADMIN = 5;
		/** 删除群成员 */
		public static final int REMOVE_MEMBER = 6;
		/** 群解散 */
		public static final int DISSOLVE = 7;
		/** 修改在群里的昵称 */
		public static final int UPDATE_MYNICKNAME = 8;

		public final static int QUIT_FROM_GROUP = 9;  //退出群组
		public final static int INVITE_FRIEND = 10;  //邀请好友链接
	}

	/**
	 * 接受的群系统类型
	 */
	public class STy {
		/** 邀请 */
		public static final int S_INVITE = 1;
	}
	public class FansStatus {
		/** 啥关系都没有 */
		public final static int NONE = 0;
		/** 我关注对方，对方没有关注我 */
		public final static int ONLY_ME_FOLLOW = 1;
		/** 对方关注了我，我没有关注对方 */
		public final static int ONLY_PEER_FOLLOW = 2;
		/** 双方关注 */
		public final static int FRIEND = 3;
		/** 黑名单 */
		public final static int BLACKLIST = 4;
	}
	/**
	 * @Description: 信箱类型
	 *
	 */
	public class BoxType {
		/** 单聊 */
		public final static int SINGLE_CHAT = 0;
		/** 群聊 */
		public final static int GROUP_CHAT = 1;
	}
	/**
	 * @Description: 用户角色
	 */
	public class RoleType {
		/** 群主 */
		public final static int OWNERS = 3;
		/** 管理员 */
		public final static int ADMINS = 2;
		/** 普通成员 */
		public final static int MEMBERS = 1;
		/** 逐出 */
		public final static int OUTCASTS = 0;
	}

	/**
	 * @Description: 会话类型
	 *
	 */
	public enum SessionType {
		/** 魔商用户组 */
		MOBIZ,
		/** 系统推送消息 */
		PUSH,
		/** 新朋友 */
		FRIEND, FansStatus;
	}
	/**
	 * @Description: 推送消息类型
	 */
	public class SSType {
		/* 新用户关注提醒文字消息 */
		public final static int FOLLOW = 4;
	}
}
