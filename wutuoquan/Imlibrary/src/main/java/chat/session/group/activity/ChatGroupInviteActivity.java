package chat.session.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.user.UserInfoHelp;
import chat.common.util.output.ShowUtil;
import chat.image.DisplayImageConfig;
import chat.manager.ChatGroupManager;
import chat.manager.ChatMembersManager;
import chat.session.group.bean.ChatGroupBean;
import chat.session.group.bean.ChatGroupData;
import chat.session.group.bean.ChatGroupInfoResultBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.session.util.PageToastUtil;
import chat.view.CircleImageView;
import chat.view.SettingItemView;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * @ClassName: ChatGroupInviteActivity
 * @Description: 群聊邀请页面
 * @author weyko
 *
 */
public class ChatGroupInviteActivity extends BaseActivity implements
		OnClickListener {
	/** 主显示界面 */
	private View main_group_invite;
	/** 其它提示界面 */
	private View view_toast_group_invite;
	/* 标题 */
	private TextView titleText;
	/* 顶部提示 */
	private TextView top_hint_group_invite;
	/* 备注 */
	private TextView remark_group_invite;
	/** 群头像 */
	private CircleImageView avatar_group_invite;
	/** 查看按钮 */
	private TextView look_group_invite;
	/** 群成员 */
	private SettingItemView members_group_invite;
	/** 默认展示群成员容器 */
	private LinearLayout ll_group_members_invite;
	private List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> groupMembers;
	private PageToastUtil pageUtil;
	/** 群id */
	private String roomId;
	private String qrcode;
	private String inviteKey;
	public static final String ROOMID = "roomid";
	public static final String KEY = "key";
	public static final String KEY_INVITE = "invite";
	public static final String GROUPINFO = "groupInfo";
	private int roleType;
	private ChatGroupInfoResultBean bean;
	private ChatGroupInfoResultBean.ChatGroupInfoData groupInfoData;
	private int memebers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentParams();
		setContentView(R.layout.activity_group_invite);
		initEvents();
		initData();
		getGroupInfo();
	}

	private void getIntentParams() {
		Intent intent = this.getIntent();
		if (intent != null) {
			roomId = intent.getStringExtra(ROOMID);
			qrcode = intent.getStringExtra(KEY);
			inviteKey = intent.getStringExtra(KEY_INVITE);
		}
	}
	@Override
	protected void initData() {
		pageUtil = new PageToastUtil(this);
		titleText.setText(getString(R.string.chat_group_invite_title));
	}

	@Override
	protected void initView() {
		titleText = (TextView) this.findViewById(R.id.titleText);
		main_group_invite = this.findViewById(R.id.main_group_invite);
		view_toast_group_invite = this
				.findViewById(R.id.view_toast_group_invite);
		top_hint_group_invite = (TextView) this
				.findViewById(R.id.top_hint_group_invite);
		look_group_invite = (TextView) this
				.findViewById(R.id.look_group_invite);
		remark_group_invite = (TextView) this
				.findViewById(R.id.remark_group_invite);
		members_group_invite = (SettingItemView) this
				.findViewById(R.id.members_group_invite);
		avatar_group_invite = (CircleImageView) this
				.findViewById(R.id.avatar_group_invite);
		ll_group_members_invite = (LinearLayout) this
				.findViewById(R.id.ll_group_members_invite);
	}

	@Override
	protected void initEvents() {
		look_group_invite.setOnClickListener(this);
		members_group_invite.setOnClickListener(this);
		ll_group_members_invite.setOnClickListener(this);
		this.findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		if (i == R.id.look_group_invite) {
			if (roleType > 0) {
				gotoRoom();
			} else {
				showLoading();
				if (!TextUtils.isEmpty(qrcode)) {
					addMember();
				} else {
					invite(inviteKey);
				}
			}

		} else if (i == R.id.members_group_invite || i == R.id.ll_group_members_invite) {
			if (groupInfoData == null)
				return;
			Intent intent = new Intent(this, MoreGroupMembersActivity.class);
			intent.putExtra("roomId", roomId);
			intent.putExtra("roleType", roleType);
			intent.putExtra("members", memebers);
			intent.putExtra("isOnlyLook", true);
			startActivity(intent);

		} else if (i == R.id.back) {
			reBack();

		} else {
		}
	}

	/**
	 * @Title: gotoRoom`1
	 * @param:
	 * @Description: 进入聊天室
	 * @return void
	 */
	private void gotoRoom() {
		ChatGroupBean groupBean = new ChatGroupBean();
		ChatGroupData chatGroupData = new ChatGroupData();
		chatGroupData.setId(groupInfoData.getId());
		chatGroupData.setGroupName(groupInfoData.getGroupName());
		chatGroupData.setPhotoUrl(groupInfoData.getPhotoUrl());
		groupBean.setData(chatGroupData);
		ChatUtil.gotoChatRoom(this, groupBean);
	}

	/**
	 * @Title: getMembers
	 * @param:
	 * @Description: 获取群信息
	 * @return void
	 */
	public void getGroupInfo() {
		WBaseModel<ChatGroupInfoResultBean> mode = new WBaseModel<ChatGroupInfoResultBean>(
				this, ChatGroupInfoResultBean.class);
		HashMap<String, Object> parmas = new HashMap<String, Object>();
		if (qrcode != null)
			parmas.put("qrcode", qrcode);
		else if (inviteKey != null) {
			parmas.put("key", inviteKey);
		}
		mode.httpJsonRequest(Method.GET,
				String.format(URLConfig.CHAT_GRUOP_INFO, roomId), parmas,
				new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						if (data != null) {
							if (data instanceof ChatGroupInfoResultBean) {
								bean = (ChatGroupInfoResultBean) data;
								if (bean.isResult()) {
									if (bean.getData().size() == 0)
										return;
									groupInfoData = bean.getData().get(0);
									memebers = groupInfoData.getNowCnt();
									setUserInfo(groupInfoData);
								}
							} else if (data instanceof WBaseBean) {
								WBaseBean bean = (WBaseBean) data;
								if ("mx11080025".equals(bean.getCode())) {
									main_group_invite.setVisibility(View.GONE);
									pageUtil.showToast(view_toast_group_invite,
											PageToastUtil.PageMode.INVITE_INVALID);
								}
							}
						} else {
							ShowUtil.showToast(ChatGroupInviteActivity.this,
									getString(R.string.warning_service_error));
						}
						dissmisLoading();
					}
				});
	}

	/**
	 * @Title: setHeader
	 * @param:
	 * @Description: 设置顶部消息
	 * @return void
	 */
	protected void setUserInfo(ChatGroupInfoResultBean.ChatGroupInfoData data) {
		if (data == null)
			return;
		if (TextUtils.isEmpty(data.getPhotoUrl())) {
			avatar_group_invite.setImageResource(R.drawable.ic_default_group);
		} else {
			IMClient.sImageLoader.displayThumbnailImage(
					data.getPhotoUrl(), avatar_group_invite,
					DisplayImageConfig.userLoginItemImageOptions,
					DisplayImageConfig.headThumbnailSize,
					DisplayImageConfig.headThumbnailSize);
		}
		remark_group_invite.setText(data.getGroupName());
		roleType = data.getRoleType();
		int topRId = 0, btnRId = 0;
		if (roleType > 0) {
			topRId = R.string.chat_group_invite_accept_hint;
			btnRId = R.string.chat_group_invite_look;
			look_group_invite.setVisibility(View.VISIBLE);
		} else {
			if (data.getMaxCnt() <= data.getNowCnt()) {
				topRId = R.string.chat_group_invite_later_hint;
				look_group_invite.setVisibility(View.GONE);
			} else {
				btnRId = R.string.chat_group_invite_join_hint;
				look_group_invite.setVisibility(View.VISIBLE);
			}
		}
		if (topRId > 0)
			top_hint_group_invite.setText(getString(topRId));
		if (btnRId > 0)
			look_group_invite.setText(getString(btnRId));
		setMembers(data);
	}

	/**
	 * @Title: setMembers
	 * @param:
	 * @Description: 设置群成员
	 * @return void
	 */
	private void setMembers(ChatGroupInfoResultBean.ChatGroupInfoData data) {
		groupMembers = data.getMemberList();
		if (groupMembers == null)
			return;
		int size = groupMembers.size();
		members_group_invite.setRight(memebers
				+ getString(R.string.chat_group_invite_group_members));
		ll_group_members_invite.removeAllViews();
		for (int i = 0; i < size; i++) {
			if (i == 5)
				break;
			View view = LayoutInflater.from(this).inflate(
					R.layout.item_members, null);
			ImageView icon = (ImageView) view
					.findViewById(R.id.avatar_members_item);
			ImageView tag_members_item = (ImageView) view
					.findViewById(R.id.tag_members_item);
			IMClient.sImageLoader.displayThumbnailImage(groupMembers
					.get(i).getPhotoUrl(), icon,
					DisplayImageConfig.userLoginItemImageOptions,
					DisplayImageConfig.headThumbnailSize,
					DisplayImageConfig.headThumbnailSize);
			view.findViewById(R.id.name_members_item).setVisibility(View.GONE);
			switch (groupMembers.get(i).getRole()) {
			case IMTypeUtil.RoleType.OWNERS:
				tag_members_item.setVisibility(View.VISIBLE);
				tag_members_item
						.setImageResource(R.drawable.ic_head_group_ower);
				break;
			case IMTypeUtil.RoleType.ADMINS:
				tag_members_item.setVisibility(View.VISIBLE);
				tag_members_item
						.setImageResource(R.drawable.ic_head_group_admin);
				break;
			case IMTypeUtil.RoleType.MEMBERS:
				tag_members_item.setVisibility(View.GONE);
				break;
			default:
				tag_members_item.setVisibility(View.GONE);
				break;
			}
			ll_group_members_invite.addView(view);
		}
	}

	/**
	 * 邀请
	 * @param key
	 */
	private void invite(String key) {
		WBaseModel<WBaseBean> mode = new WBaseModel<WBaseBean>(this,
				WBaseBean.class);
		HashMap<String, Object> parmas = new HashMap<String, Object>();
		parmas.put("key", key);
		mode.httpJsonRequest(Method.POST,
				String.format(URLConfig.CHAT_GRUOP_ADD_LINK, roomId), parmas,
				new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						dismissLoadingDialog();
						if (data != null) {
							if (data instanceof WBaseBean) {
								WBaseBean baseBean = (WBaseBean) data;
								doResultOfJoinGroup(baseBean);
							}
						} else {
							ShowUtil.showToast(ChatGroupInviteActivity.this,
									getString(R.string.warning_service_error));
						}
						dissmisLoading();
					}

				});

	}

	/**
	 * @Title: addMembers
	 * @param:
	 * @Description: 加入群成员
	 * @return void
	 */
	private void addMember() {
		WBaseModel<WBaseBean> mode = new WBaseModel<WBaseBean>(this,
				WBaseBean.class);
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("role", IMTypeUtil.RoleType.MEMBERS);
		map.put("userId", IMClient.getInstance().getSSOUserId());
		map.put("mtalkDomain", URLConfig.getServerName());
		list.add(map);
		HashMap<String, Object> parmas = new HashMap<String, Object>();
		parmas.put("needToInvite", 0);
		parmas.put("memberList", list);
		mode.httpJsonRequest(Method.POST,
				String.format(URLConfig.CHAT_GRUOP_INFO, roomId), parmas,
				new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						dismissLoadingDialog();
						if (data != null) {
							if (data instanceof WBaseBean) {
								WBaseBean baseBean = (WBaseBean) data;
								doResultOfJoinGroup(baseBean);
							}
						} else {
							ShowUtil.showToast(ChatGroupInviteActivity.this,
									getString(R.string.warning_service_error));
						}
					}
				});
	}

	/**
	 * @Title: doResultOfJoinGroup
	 * @param:
	 * @Description: 执行加入群之后的操作
	 * @return void
	 */
	private void doResultOfJoinGroup(WBaseBean baseBean) {
		if (baseBean.isResult()) {
			top_hint_group_invite
					.setText(getString(R.string.chat_group_invite_accept_hint));
			look_group_invite
					.setText(getString(R.string.chat_group_invite_look));
			memebers++;
			members_group_invite.setRight(memebers
					+ getString(R.string.chat_group_invite_group_members));
			roleType = 1;
			ChatGroupInfoResultBean.ChatGroupInfoData.MembersData mineInfo = new ChatGroupInfoResultBean.ChatGroupInfoData.MembersData();
			try {
				mineInfo.setUserId(Long.parseLong(IMClient.getInstance()
						.getSSOUserId()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			mineInfo.setMtalkDomain(URLConfig.getServerName());
			mineInfo.setNickName(UserInfoHelp.getInstance().getNickname());
			mineInfo.setRole(IMTypeUtil.RoleType.MEMBERS);
			groupInfoData.getMemberList().add(mineInfo);
			bean.getData().get(0).getMemberList().add(mineInfo);
			ChatGroupBean chatGroupBean=new ChatGroupBean();
			ChatGroupData data=new ChatGroupData();
			data.setId(groupInfoData.getId());
			data.setGroupName(groupInfoData.getGroupName());
			data.setMaxCnt(groupInfoData.getMaxCnt());
			data.setNowCnt(groupInfoData.getNowCnt());
			data.setRoleType(groupInfoData.getRoleType());
			data.setCreateTime(groupInfoData.getCreateTime());
			data.setCreatorId(groupInfoData.getCreatorId());
			data.setPhotoUrl(groupInfoData.getPhotoUrl());
			chatGroupBean.setData(data);
			ChatGroupManager.getInstance().insertGroup(chatGroupBean, false);
			ChatMembersManager.getInstance().insertGroupMembers(roomId, bean);
			ChatGroupManager.getInstance().setIsBookTolist(roomId, 0);// 默认设置为1了，应该为0
			setMembers(groupInfoData);
			gotoRoom();
		}
		if ("mx11080022".equals(bean.getCode())) {
			top_hint_group_invite
					.setText(getString(R.string.chat_group_invite_later_hint));
			look_group_invite.setVisibility(View.GONE);
		}
		showResutToast(baseBean.getCode());
	}

	@Override
	public void onBackPressed() {
		reBack();
		super.onBackPressed();
	}

	private void reBack() {
		this.finish();

	}
}
