package chat.session.util;

/**
 * @ClassName:RelationType.java
 * @Description:好友关系
 */
public enum RelationType {

	/**
	 * 陌生人
	 */
	none, /**
	 * 粉丝，对方关注我，我没关注对方
	 */
	follower, /**
	 * 已关注,我关注对方，对方没关注我
	 */
	following, /**
	 * 朋友
	 */
	friend, /**
	 * 黑名单
	 */
	blacklist, /**
	 * 备注
	 */
	remark,
	/** 取消黑名单 */
	outblack
}
