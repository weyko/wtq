package chat.session.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.util.ComparatorUtil;
import chat.common.util.TextUtils;
import chat.common.util.ToolsUtils;
import chat.contact.bean.ContactBean;
import chat.image.DisplayImageConfig;
import chat.manager.ChatContactManager;
import chat.session.activity.ChatSearchActvity;
import chat.session.adapter.ContactsAdapter;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.RemoteContactsBean;
import chat.session.group.bean.RemoteMemberBean;
import chat.session.util.PageToastUtil;
import chat.view.SubSideBar;

/**
 * @Description: 联系人列表
 * @author weyko
 *
 */
public class ChatGroupContactsActivity extends BaseActivity implements
		OnClickListener {
	public static final int REQUEST_MEMBERS = 10;
	/* 添加 */
	public static final int REQUEST_ADD = 12;
	/* 邀请 */
	public static final int REQUEST_ASK = 13;
	/** 请求Key */
	public static final String REQUEST_KEY = "request_key";
	/** 请求Key */
	public static final String ADD_MEMBER = "add_member";
	/** 请求类型 */
	public int requestMode = REQUEST_ADD;
	/* 标题 */
	private TextView title;
	/** 取消 */
	private TextView next;
	private View searchArea_members;
	private ListView mListView;
	/** 快速选择栏 */
	private SubSideBar mSideBar;
	private List<ImUserBean> mDatas;
	private ContactsAdapter mAdapter;
	public static boolean reflush = false;
	private RemoteContactsBean membesBean;
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
	private ArrayList<String> membersId;
	private RemoteMemberBean remoteMemberBean;

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
		requestMode = getIntent().getIntExtra(REQUEST_KEY, requestMode);
		if (requestMode == REQUEST_ASK)
			membersId = getIntent().getStringArrayListExtra(ADD_MEMBER);
		else
			membesBean = (RemoteContactsBean) getIntent().getSerializableExtra(
					RemoteContactsBean.MEMBES);
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
//		mListView.setMode(Mode.PULL_DOWN_TO_REFRESH);
		mSideBar = (SubSideBar) this.findViewById(R.id.sideBar_members);
		menu_select_members = this.findViewById(R.id.menu_select_members);
		menu_select_members.setVisibility(View.VISIBLE);
		count_select_members = (TextView) this
				.findViewById(R.id.count_select_members);
		ok_select_members = (TextView) this
				.findViewById(R.id.ok_select_members);
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
		toastUtil = new PageToastUtil(this);
		mDatas = new ArrayList<ImUserBean>();
		int titleId = R.string.chat_group_create_add_title;
//		titleId = R.string.chat_group_create_add_title;
		mDatas = ChatContactManager.getInstance().getContacts();
		int count = 0;
		if (requestMode == REQUEST_ASK) {
			Iterator<ImUserBean> iterator = mDatas.iterator();
			while (iterator.hasNext()) {
				ImUserBean bean = iterator.next();
				if (membersId.contains(bean.getMxId())) {
					iterator.remove();
				}
			}
		} else if (requestMode == REQUEST_ADD) {
			if (membesBean != null) {
				count = membesBean.getSize();
			}
			updateSeleteView(membesBean);
		}
		count_select_members.setText(TextUtils.getStringFormat(
				ChatGroupContactsActivity.this, R.string.chat_group_search_selected,
				count));
		if (mDatas.size() == 0) {
			toastUtil.showToast(view_toast_members, PageToastUtil.PageMode.CONTACT_EMPTY);
			if (requestMode == REQUEST_ASK) {
				this.findViewById(R.id.view_search_members).setVisibility(
						View.GONE);
				toastUtil.setHint(R.string.chat_group_invite_members_nomore);
			}
		}
		title.setText(getString(titleId));
		this.findViewById(R.id.back).setVisibility(View.GONE);
		next.setTextColor(getResources().getColor(R.color.text_color_title));
		next.setText(getString(R.string.cancel));
		Collections.sort(mDatas, ComparatorUtil.getInstance()
				.getGroupNewMemberComparator());
		mAdapter = new ContactsAdapter(this, mDatas);
		mAdapter.setAddMembesBean(membesBean);
		mListView.setAdapter(mAdapter);
		mAdapter.setHandler(selectHandler);
		mAdapter.setChatMode(false);
		mSideBar.setListView(mListView);
		if (remoteMemberBean == null)
			remoteMemberBean = new RemoteMemberBean();
		Iterator<ImUserBean> iterator = mDatas.iterator();
		while (iterator.hasNext()) {
			ImUserBean bean = iterator.next();
			ContactBean memberBean = new ContactBean();
			try {
				memberBean.setFriendID(Integer.valueOf(bean.getMxId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			memberBean.setUserImg(bean.getAvatar());
			memberBean.setUserNickname(bean.getName());
			memberBean.setRemarkName(bean.getRemark());
			remoteMemberBean.add(memberBean);
		}
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
			intent.putExtra(ChatSearchActvity.SEARCH_REMOTE, remoteMemberBean);
			startActivityForResult(intent, REQUEST_MEMBERS);

		} else if (i == R.id.ok_select_members) {
			doBack(false);

		} else if (i == R.id.next) {
			doBack(true);

		} else {
		}
	}

	Comparator<ContactBean> comparator = new Comparator<ContactBean>() {
		@Override
		public int compare(ContactBean lhs, ContactBean rhs) {
			String a = lhs.getCatalog();
			String b = rhs.getCatalog();
			int flag = a.compareTo(b);
			if (flag == 0) {
				return a.compareTo(b);
			} else {
				return flag;
			}
		}
	};

	/**
	 * @Title: doBack
	 * @param:
	 * @Description: 执行回滚
	 * @return void
	 */
	public void doBack(boolean isCancal) {
		Intent intent = new Intent();
		intent.putExtra("isCancal", isCancal);
		intent.putExtra(RemoteContactsBean.MEMBES, mAdapter.getAddMembesBean());
		setResult(RESULT_OK, intent);
		this.finish();
		
	}

	@Override
	public void onBackPressed() {
		doBack(true);
		super.onBackPressed();
	}

	private Handler selectHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 10) {
				RemoteContactsBean addMembesBean = (RemoteContactsBean) msg.obj;
				if (addMembesBean != null) {
					membesBean = addMembesBean;
					updateSeleteView(addMembesBean);
				}
			}
		}

	};

	/**
	 * @Title: updateSeleteView
	 * @param:
	 * @Description: 更新底部选择菜单
	 * @return void
	 */
	private void updateSeleteView(RemoteContactsBean addMembesBean) {
		boolean enabled = addMembesBean.getSize() > 0;
		count_select_members.setText(TextUtils.getStringFormat(
				ChatGroupContactsActivity.this, R.string.chat_group_search_selected,
				addMembesBean.getSize()));
		ok_select_members.setBackgroundColor(getResources().getColor(
				enabled ? R.color.main_color : R.color.text_color_no_click));
		ok_select_members.setEnabled(enabled);
		addItem(addMembesBean);
	}

	private void addItem(RemoteContactsBean addMembesBean) {
		if (addMembesBean == null)
			return;
		contain_select_members.removeAllViews();
		int size = addMembesBean.getSize();
		int margin = ToolsUtils.dip2px(this, 8);
		int with = ToolsUtils.dip2px(this, 50);
		for (int i = 0; i < size; i++) {
			if (i == 5)
				break;
			ImUserBean fansBean = addMembesBean.getMembsers().get(i);
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