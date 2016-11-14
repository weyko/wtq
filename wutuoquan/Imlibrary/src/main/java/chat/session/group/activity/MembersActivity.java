package chat.session.group.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.util.ComparatorUtil;
import chat.common.util.TextUtils;
import chat.common.util.ToolsUtils;
import chat.contact.bean.ContactBean;
import chat.dialog.CustomBaseDialog;
import chat.image.DisplayImageConfig;
import chat.session.activity.ChatSearchActvity;
import chat.session.adapter.MemberAdapter;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.ChatGroupMemberBean;
import chat.session.group.bean.RemoteContactsBean;
import chat.session.group.bean.RemoteMemberBean;
import chat.session.group.bean.SparseArrayList;
import chat.session.util.IMTypeUtil;
import chat.session.util.PageToastUtil;
import chat.view.SubSideBar;

/**
 * @ClassName: MembersActivity
 * @Description: 群成员列表
 * @author weyko
 */
public class MembersActivity extends BaseActivity implements OnClickListener {
	public static final int REQUEST_MEMBERS = 10;
	/** 请求Key */
	public static final String REQUEST_KEY = "request_key";
	/** 请求群最大人数Key */
	public static final String REQUEST_MAX_MEMEBERS = "request_max_memebers";
	/** 可设置管理员的数目Key */
	public static final String SET_ADMIN_LIMIT = "set_admin_limit";
	
	public int requestMode = ChatGroupInfoAcitivty.REQUEST_ADD;
	/* 标题 */
	private TextView title;
	/** 取消 */
	private TextView next;
	private View query_members, searchArea_members;
	private ListView mListView;
	/** 快速选择栏 */
	private SubSideBar mSideBar;
	private SparseArrayList<ContactBean> mDatas;
	private MemberAdapter mAdapter;
	public static boolean reflush = false;
	private String loginID;
	/* 用于传递列表数据集 */
	private RemoteMemberBean remoteMembeBean;
	/** 底部菜单栏 */
	private View menu_select_members;
	/** 已选择人数 */
	private TextView count_select_members;
	/** 确认选择 */
	private TextView ok_select_members;
	/** 提示页 */
	private View view_toast_members;
	/** 显示选择联系人的容器 */
	private LinearLayout contain_select_members;
	private PageToastUtil toastUtil;
	private boolean isSetOwner = false;
	/** 群最大人数 */
	private int maxMembers;
	private final int MAX_NUMS_ADMIN=5;
	private int curAdminNums=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_members);
		getIntentParam();
		initEvents();
		initData();
	}

	private void getIntentParam() {
		remoteMembeBean = (RemoteMemberBean) getIntent().getSerializableExtra(
				RemoteContactsBean.MEMBES);
		requestMode = getIntent().getIntExtra(REQUEST_KEY, requestMode);
		maxMembers = getIntent().getIntExtra(REQUEST_MAX_MEMEBERS, 0);
		if (requestMode== ChatGroupInfoAcitivty.REQUEST_SET_ADMIN){
			curAdminNums = getIntent().getIntExtra(SET_ADMIN_LIMIT, 0);
		}
	}

	@Override
	protected void initView() {
		title = (TextView) this.findViewById(R.id.titleText);
		next = (TextView) this.findViewById(R.id.next);
		mListView = (ListView) this
				.findViewById(R.id.refresh_list_members);
		this.findViewById(R.id.query_members).setOnClickListener(this);
		this.findViewById(R.id.searchArea_members).setOnClickListener(this);
		searchArea_members = this.findViewById(R.id.searchArea_members);
//		mListView.setOnRefreshListener(this);
//		mListView.setMode(Mode.DISABLED);
		mSideBar = (SubSideBar) this.findViewById(R.id.sideBar_members);
		mSideBar.setListView(mListView);
		menu_select_members = this.findViewById(R.id.menu_select_members);
		menu_select_members.setVisibility(View.VISIBLE);
		count_select_members = (TextView) this.findViewById(R.id.count_select_members);
		ok_select_members = (TextView) this.findViewById(R.id.ok_select_members);
		view_toast_members = this.findViewById(R.id.view_toast_members);
		contain_select_members = (LinearLayout) this
				.findViewById(R.id.contain_select_members);

		ViewTreeObserver vto = mSideBar.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				mSideBar.setItemHeight(mSideBar.getMeasuredHeight() / 27);
				return true;
			}
		});

	}

	@Override
	protected void initEvents() {
		searchArea_members.setOnClickListener(this);
		ok_select_members.setOnClickListener(this);
		next.setOnClickListener(this);
	}
	@Override
	protected void initData() {
		loginID = IMClient.getInstance().getSSOUserId();
		toastUtil = new PageToastUtil(this);
		mDatas = new SparseArrayList<ContactBean>();
		int titleId = 0;
		boolean isSetManager = false;
		if (remoteMembeBean != null)
			mDatas = remoteMembeBean.getMembsers();
		switch (requestMode) {
		case ChatGroupInfoAcitivty.REQUEST_ADD:
			titleId = R.string.chat_group_invite_new_title;
			break;
		case ChatGroupInfoAcitivty.REQUEST_DELETE:
			titleId = R.string.chat_group_member_delete;
			break;
		case ChatGroupInfoAcitivty.REQUEST_SET_ADMIN:
			titleId = R.string.chat_group_admin_title;
			isSetManager = true;
			break;
		case ChatGroupInfoAcitivty.REQUEST_SET_OWNER:
			titleId = R.string.chat_group_invite_members;
			isSetOwner = true;
			break;
		default:
			titleId = R.string.chat_group_invite_new_title;
			break;
		}
		if (mDatas.size() == 0) {
			toastUtil.showToast(view_toast_members, PageToastUtil.PageMode.CONTACT_EMPTY);
		} else {
			mSideBar.setListView(mListView);
		}
		if (isSetOwner)
			menu_select_members.setVisibility(View.GONE);
		else {
			count_select_members.setText(TextUtils.getStringFormat(
					MembersActivity.this, R.string.chat_group_search_selected,
					0));
		}
		title.setText(getString(titleId));
		this.findViewById(R.id.back).setVisibility(View.GONE);
		next.setTextColor(getResources().getColor(R.color.text_color_title));
		next.setText(getString(R.string.cancel));
		sortSparseArray(mDatas);
		mAdapter = new MemberAdapter(this, mDatas);
		if (requestMode== ChatGroupInfoAcitivty.REQUEST_SET_ADMIN){
			mAdapter.setLimit(MAX_NUMS_ADMIN-curAdminNums);
		}
		mAdapter.setIsSetManager(isSetManager);
		mAdapter.isSetOwner(isSetOwner);
		mListView.setAdapter(mAdapter);
		mAdapter.setHandler(selectHandler);
	}
	private SparseArray<ContactBean> sortSparseArray(SparseArray<ContactBean> datas){
		ArrayList<ContactBean> vals = new ArrayList<ContactBean>();
		int size=datas.size();
		for(int i =0; i < size;i++){
			vals.add(datas.valueAt(i));
		}
		Collections.sort(vals, ComparatorUtil.getInstance().getMemberComparator());
		datas.clear();
		size=vals.size();
		for (int i =0; i<size;i++){
			datas.put(i,vals.get(i));
		}
		return datas;
	}
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.query_members || i == R.id.searchArea_members) {
			Intent intent = new Intent(getApplicationContext(),
					ChatSearchActvity.class);
			intent.putExtra(ChatSearchActvity.SEARCH_MODE,
					ChatSearchActvity.SEARCH_CONTACT);
			intent.putExtra(ChatSearchActvity.SEARCH_ISEDIT, true);
			intent.putExtra(ChatSearchActvity.SEARCH_REMOTE, remoteMembeBean);
			startActivityForResult(intent, REQUEST_MEMBERS);

		} else if (i == R.id.ok_select_members) {
			int size = mAdapter.getMemebers().size();
			if (requestMode == ChatGroupInfoAcitivty.REQUEST_ADD
					&& maxMembers > 0 && size >= maxMembers) {
				showLimitDialog();
				return;
			}
			doBack(false);

		} else if (i == R.id.next) {
			doBack(true);

		} else {
		}
	}

	/**
	 * @Title: showLimitDialog
	 * @param:
	 * @Description: 弹出超限对话框
	 * @return void
	 */
	private void showLimitDialog() {
		CustomBaseDialog dialog = CustomBaseDialog.getDialog(this, null,
				getString(R.string.chat_group_invite_out_hint),
				getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}, this.getString(R.string.chat_group_invite_sure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doBack(false);
					}
				});
		dialog.setButton1Background(R.drawable.bg_button_dialog_1);
		dialog.setButton2Background(R.drawable.bg_button_dialog_2);
		dialog.show();
	}
	/**
	 * @Title: doBack
	 * @param:
	 * @Description: 执行回滚
	 * @return void
	 */
	public void doBack(boolean isCancal) {
		RemoteMemberBean memberBean = new RemoteMemberBean();
		memberBean.setMembsers(mAdapter.getMemebers());
		Intent intent = new Intent();
		intent.putExtra("isCancal", isCancal);
		intent.putExtra(RemoteMemberBean.MEMBES, memberBean);
		setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	public void onBackPressed() {
		doBack(true);
		super.onBackPressed();
	}

	@SuppressLint("HandlerLeak")
	private Handler selectHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 10) {
				@SuppressWarnings("unchecked")
				List<ChatGroupMemberBean> editBean = (List<ChatGroupMemberBean>) msg.obj;
				if (editBean != null) {
					if (isSetOwner) {
						if (editBean.size() > 0)
							setOwner(editBean.get(0).getNickName());
					} else {
						updateSeleteView(editBean);
					}
				}
			}
		}

	};

	/**
	 * @Title: setOwner
	 * @param:
	 * @Description: 设置群主
	 * @return void
	 */
	private void setOwner(String owner) {
		if (owner == null)
			return;
		CustomBaseDialog dialog = CustomBaseDialog.getDialog(this, TextUtils
				.getStringFormat(this, R.string.chat_group_owner_set_hint,
						owner), getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}, getString(R.string.chat_group_owner_set_sure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doBack(false);
					}
				});
		dialog.setButton1Background(R.drawable.bg_button_dialog_1);
		dialog.setButton2Background(R.drawable.bg_button_dialog_2);
		dialog.show();
	}

	/**
	 * @Title: updateSeleteView
	 * @param:
	 * @Description: 更新底部选择菜单
	 * @return void
	 */
	private void updateSeleteView(List<ChatGroupMemberBean> members) {
		if (members == null)
			return;
		List<ChatGroupMemberBean> editMembers = new ArrayList<ChatGroupMemberBean>();
		if (requestMode == ChatGroupInfoAcitivty.REQUEST_SET_ADMIN) {
			for (ChatGroupMemberBean member : members) {
				if (member.getRoleType() != IMTypeUtil.RoleType.ADMINS) {
					editMembers.add(member);
				}
			}
		} else {
			editMembers.addAll(members);
		}
		boolean enabled = editMembers.size() > 0;
		count_select_members.setText(TextUtils.getStringFormat(
				MembersActivity.this, R.string.chat_group_search_selected,
				editMembers.size()));
		ok_select_members.setBackgroundColor(getResources().getColor(
				enabled ? R.color.main_color : R.color.text_color_no_click));
		ok_select_members.setEnabled(enabled);
		if (enabled)
			addItem(editMembers);
		else {
			contain_select_members.removeAllViews();
		}
	};

	private void addItem(List<ChatGroupMemberBean> delteMembesBean) {
		if (delteMembesBean == null)
			return;
		contain_select_members.removeAllViews();
		int size = delteMembesBean.size();
		int margin = ToolsUtils.dip2px(this, 8);
		int with = ToolsUtils.dip2px(this, 50);
		for (int i = 0; i < size; i++) {
			ChatGroupMemberBean fansBean = delteMembesBean.get(i);
			ImageView item = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					with, with);
			item.setLayoutParams(params);
			params.leftMargin = margin;
			item.setImageResource(R.drawable.default_head);
			IMClient.sImageLoader.displayThumbnailImage(fansBean.getAvatar(),
					item, DisplayImageConfig.userLoginItemImageOptions,DisplayImageConfig.headThumbnailSize,DisplayImageConfig.headThumbnailSize);
			contain_select_members.addView(item);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 == RESULT_OK) {
			if (arg0 == REQUEST_MEMBERS) {
				ImUserBean bean = (ImUserBean) arg2
						.getSerializableExtra(ChatSearchActvity.SEARCH_ITEM_CONTACT);
				if (bean != null) {
					mAdapter.setSelect(bean);
				}
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
}