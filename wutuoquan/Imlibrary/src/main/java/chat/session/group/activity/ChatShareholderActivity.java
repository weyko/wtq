package chat.session.group.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.util.ToolsUtils;
import chat.common.util.output.ShowUtil;
import chat.image.DisplayImageConfig;
import chat.manager.ChatGroupManager;
import chat.session.activity.ChatSearchActvity;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.group.bean.ChatGroupBean;
import chat.session.group.bean.ChatGroupData;
import chat.session.group.bean.ChatGroupListBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.session.util.PageToastUtil;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * @ClassName: ChatShareholderActivity
 * @Description: 股东会
 * @author weyko
 */
public class ChatShareholderActivity extends BaseActivity implements
		OnClickListener, PullToRefreshLayout.OnRefreshListener {
	private static final int REQUEST_CREATE = 10;
	/** 空内容容器 */
	private View view_toast_group;
	private PullToRefreshLayout ph_list_group;
	/** 空内容容器 */
	private PullableListView pull_refresh_list;
	/** 提示页管理工具 */
	private PageToastUtil toastUtil;
	/** 群组集合 */
	private List<ChatGroupBean> list;
	private CommonAdapter<ChatGroupBean> adapter;
	/* 创建的数目 */
	private int countCreate;
	private boolean isFirst = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_group);
		showLoading();
		getGroups();
	}

	@Override
	public void onResume() {
		if (IMClient.isGroupChange) {
			IMClient.isGroupChange = false;
			if (!isFirst) {
				getGroups();
			}
		}
		super.onResume();
	}
	@Override
	protected void initData() {
		toastUtil = new PageToastUtil(this);
		pull_refresh_list.setPullToRefreshMode(Pullable.TOP);
		list = new ArrayList<ChatGroupBean>();
	}


	@Override
	protected void initView() {
		ph_list_group = (PullToRefreshLayout) this
				.findViewById(R.id.ph_list_group);
		view_toast_group = this.findViewById(R.id.view_toast_group);
		pull_refresh_list = (PullableListView) this
				.findViewById(R.id.pull_refresh_list_group);
	}
	@Override
	protected void initEvents() {
		ph_list_group.setOnRefreshListener(this);
	}

	@Override
	public void onClick(View arg0) {
		int i = arg0.getId();
		if (i == R.id.search_rl_group || i == R.id.searchArea_group) {
			Bundle bundle = new Bundle();
			bundle.putInt(ChatSearchActvity.SEARCH_MODE,
					ChatSearchActvity.SEARCH_GROUP);
			Intent intent = new Intent(IMClient.getInstance().getContext(),
					ChatSearchActvity.class);
			intent.putExtras(bundle);
			startActivity(intent);

		} else {
		}
	}

	/**
	 * @Title: setData
	 * @param:
	 * @Description: 设置数据
	 * @return void
	 */
	private void setData() {
		if (adapter == null) {
			adapter = new CommonAdapter<ChatGroupBean>(
					IMClient.getInstance().getContext(), list, R.layout.item_friend) {
				@Override
				public void convert(ViewHolder helper, final ChatGroupBean item) {
					final ChatGroupData data = item.getData();
					if (android.text.TextUtils.isEmpty(data.getPhotoUrl())) {
						((ImageView) helper.getView(R.id.avatar))
								.setImageResource(R.drawable.ic_default_group);
					} else
						IMClient.sImageLoader.displayThumbnailImage(
								data.getPhotoUrl(),
								(ImageView) helper.getView(R.id.avatar),
								DisplayImageConfig.userLoginItemImageOptions,
								new ImageLoadingListener() {
									@Override
									public void onLoadingStarted(String arg0,
											View arg1) {

									}

									@Override
									public void onLoadingFailed(String arg0,
																View arg1, FailReason arg2) {
										ImageView view = (ImageView) arg1;
										view.setImageResource(R.drawable.ic_default_group);
									}

									@Override
									public void onLoadingComplete(String arg0,
																  View arg1, Bitmap arg2) {

									}

									@Override
									public void onLoadingCancelled(String arg0,
											View arg1) {

									}
								}, DisplayImageConfig.headThumbnailSize,
								DisplayImageConfig.headThumbnailSize);
					helper.setText(R.id.name,
							data.getGroupName() + "(" + data.getNowCnt() + ")");
					int position = adapter.getPosition(item);
					int currRoleType = data.getRoleType();
					TextView letterIndex = helper.getView(R.id.letterIndex);
					if (position == 0) {
						helper.getView(R.id.line2_item_friend).setVisibility(
								View.VISIBLE);
						letterIndex.setVisibility(View.VISIBLE);
						setItemTitle(letterIndex, currRoleType);
					} else {
						int preOwner = list.get(position - 1).getData()
								.getRoleType();
						if (preOwner == currRoleType
								|| (/*preOwner != currRoleType
										&& */currRoleType != IMTypeUtil.RoleType.OWNERS && preOwner != IMTypeUtil.RoleType.OWNERS)) {
							helper.getView(R.id.line2_item_friend)
									.setVisibility(View.GONE);
							letterIndex.setVisibility(View.GONE);
						} else {
							helper.getView(R.id.line2_item_friend)
									.setVisibility(View.VISIBLE);
							letterIndex.setVisibility(View.VISIBLE);
							setItemTitle(letterIndex, data.getRoleType());
						}
					}
					helper.getView(R.id.chat).setVisibility(View.INVISIBLE);
					helper.getConvertView().setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									ChatUtil.gotoChatRoom(ChatShareholderActivity.this, item);
								}
							});
					boolean isShowSplit = false;
					if (position + 1 < list.size()) {
						int nextRoleType = list.get(position + 1).getData()
								.getRoleType();
						if (nextRoleType == currRoleType
								|| (/*nextRoleType != currRoleType
										&& */currRoleType != IMTypeUtil.RoleType.OWNERS && nextRoleType != IMTypeUtil.RoleType.OWNERS)) {
							isShowSplit = true;
						}
					}
					helper.getView(R.id.img_line_paddleft).setVisibility(
							isShowSplit ? View.VISIBLE : View.GONE);
					helper.getView(R.id.img_line).setVisibility(
							isShowSplit ? View.GONE : View.VISIBLE);
				}

			};
			pull_refresh_list.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * @Title: setTitle
	 * @param:
	 * @Description: 设置标题
	 * @return void
	 */
	public void setItemTitle(TextView letterIndex, int roleType) {
		if (roleType == IMTypeUtil.RoleType.OWNERS) {
			letterIndex.setText(getString(R.string.chat_group_create_self)
					+ "  " + countCreate);
		} else {
			letterIndex.setText(getString(R.string.chat_group_create_joined)
					+ "  " + (list.size() - countCreate));
		}
	}

	public class LoadAsyncTask extends
			AsyncTask<Boolean, String, List<ChatGroupBean>> {
		@Override
		protected List<ChatGroupBean> doInBackground(Boolean... arg0) {
			List<ChatGroupBean> list = ChatGroupManager.getInstance()
					.getGroupList();
			List<ChatGroupBean> listOwers = new ArrayList<ChatGroupBean>();
			List<ChatGroupBean> listJoins = new ArrayList<ChatGroupBean>();
			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (list.get(i).getData().getRoleType() == IMTypeUtil.RoleType.OWNERS) {
					listOwers.add(list.get(i));
				} else {
					listJoins.add(list.get(i));
				}
			}
			countCreate = listOwers.size();
			list.clear();
			list.addAll(listOwers);
			list.addAll(listJoins);
			return list;
		}

		@Override
		protected void onPostExecute(List<ChatGroupBean> result) {
			super.onPostExecute(result);
			if (result == null)
				return;
			list.clear();
			list.addAll(result);
			dissmisLoading();
			if (list.size() == 0) {
				ph_list_group.setVisibility(View.GONE);
				toastUtil.showToast(view_toast_group, PageToastUtil.PageMode.GROUP_EMPTY);
			} else {
				toastUtil.hidePage();
				ph_list_group.setVisibility(View.VISIBLE);
				setData();
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == this.RESULT_OK) {
			if (requestCode == REQUEST_CREATE
					|| requestCode == ChatUtil.REQUEST_ROOM) {
				boolean isCancal = data != null
						&& data.getBooleanExtra("isCancal", true);
				if (!isCancal) {
					getGroups();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @Title: getMembers
	 * @param:
	 * @Description: 获取群成员
	 * @return void
	 */
	public void getGroups() {
		WBaseModel<ChatGroupListBean> mode = new WBaseModel<ChatGroupListBean>(
				IMClient.getInstance().getContext(), ChatGroupListBean.class);
		HashMap<String, Object> parmas = new HashMap<String, Object>();
		mode.httpJsonRequest(Method.GET, URLConfig.CHAT_GRUOP_LIST, parmas,
				new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						isFirst = false;
						ph_list_group
								.refreshFinish(PullToRefreshLayout.SUCCEED);
						if (data != null) {
							if (data instanceof ChatGroupListBean) {
								ChatGroupManager.getInstance()
										.insertGroupInfos(
												(ChatGroupListBean) data);
							} else if (data instanceof WBaseBean) {
								ToolsUtils.getCategoryName(
										IMClient.getInstance().getContext(),
										((WBaseBean) data).getCode());
							}
						} else {
							ShowUtil.showToast(ChatShareholderActivity.this,getString(R.string.warning_service_error));
						}
						new LoadAsyncTask().execute();
					}
				});
	}

	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		countCreate = 0;
		getGroups();
	}

	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub

	}

}
