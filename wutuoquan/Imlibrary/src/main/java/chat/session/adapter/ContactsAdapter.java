package chat.session.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.base.IMClient;
import chat.common.util.TextUtils;
import chat.image.DisplayImageConfig;
import chat.manager.ChatContactManager;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.RemoteContactsBean;
import chat.session.util.ChatUtil;

/**
  * @ClassName: ContactsAdapter
  * @Description: 联系人适配器
  * @author weyko
 */
public class ContactsAdapter extends BaseAdapter implements SectionIndexer,
		Filterable {
	private Activity mContext;
	private List<ImUserBean> mDatas;
	private List<ImUserBean> mDisplayDatas;// 过滤后显示的数据
	private MyFilter mFilter;
	private boolean isChatMode = true;
	private RemoteContactsBean addMembesBean;
	private Handler handler;
	/** 选择成员的索引 */
	private HashMap<String, String> selects;

	private ChatUtil chatUtil;

	@SuppressLint("UseSparseArrays")
	public ContactsAdapter(Activity context, List<ImUserBean> datas) {
		this.mContext = context;
		this.mDatas = datas;
		this.mDisplayDatas = datas;
		chatUtil = new ChatUtil(context);
		selects = new HashMap<String, String>();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public int getCount() {
		return mDisplayDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDisplayDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/***
	 * 设置是否可以聊天
	 * 
	 * @param isChatMode
	 */
	public void setChatMode(boolean isChatMode) {
		this.isChatMode = isChatMode;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final ImUserBean item = mDisplayDatas.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_friend, null);
			viewHolder = new ViewHolder();
			viewHolder.indexTv = (TextView) convertView
					.findViewById(R.id.letterIndex);
			viewHolder.avatarIv = (ImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.select_item_friend = (ImageView) convertView
					.findViewById(R.id.select_item_friend);
			viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name);
			viewHolder.chatIv = (ImageView) convertView.findViewById(R.id.chat);
			viewHolder.line2_item_friend = (ImageView) convertView
					.findViewById(R.id.line2_item_friend);
			viewHolder.img_line = (ImageView) convertView
					.findViewById(R.id.img_line);
			viewHolder.img_line_paddleft = (ImageView) convertView
					.findViewById(R.id.img_line_paddleft);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String letterIndex = item.getPingyin();
		if (position == 0) {
			viewHolder.indexTv.setVisibility(View.VISIBLE);
			viewHolder.line2_item_friend.setVisibility(View.VISIBLE);
			viewHolder.indexTv.setText(letterIndex);
		} else {
			String lastLetterIndex = mDisplayDatas.get(position - 1)
					.getPingyin();
			if (letterIndex.equals(lastLetterIndex)) {
				viewHolder.indexTv.setVisibility(View.GONE);
				viewHolder.line2_item_friend.setVisibility(View.GONE);
			} else {
				viewHolder.indexTv.setVisibility(View.VISIBLE);
				viewHolder.line2_item_friend.setVisibility(View.VISIBLE);
				viewHolder.indexTv.setText(letterIndex);
			}
		}
		boolean isShowSplit = false;
		if (position + 1 < getCount()) {
			String nextLetterIndex = mDisplayDatas.get(position + 1)
					.getPingyin();
			if (nextLetterIndex.equals(letterIndex)) {
				isShowSplit = true;
			}
		}
		viewHolder.img_line.setVisibility(isShowSplit ? View.GONE
				: View.VISIBLE);
		viewHolder.img_line_paddleft.setVisibility(isShowSplit ? View.VISIBLE
				: View.GONE);
		if (item.getAvatar() != null && item.getAvatar().length() > 0) {
			IMClient.sImageLoader.displayThumbnailImage(item.getAvatar(),
					viewHolder.avatarIv,
					DisplayImageConfig.userLoginItemImageOptions,DisplayImageConfig.headThumbnailSize,DisplayImageConfig.headThumbnailSize);
		} else {
			viewHolder.avatarIv.setImageResource(R.drawable.default_head);
		}
		final String nickname = TextUtils.getString(item.getRemark()).length() == 0 ? item
				.getName() : item.getRemark();
		viewHolder.nameTv.setText(nickname);// 有备注显示备注，没有就显示名字（这里备注改成昵称可能会更好点,因为可能会需要添加额外的备注）
		if (isChatMode) {
			viewHolder.chatIv.setVisibility(View.VISIBLE);
			viewHolder.select_item_friend.setVisibility(View.GONE);
			viewHolder.chatIv.setTag(item);
			viewHolder.chatIv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v.getTag() instanceof ImUserBean) {
						ImUserBean it = (ImUserBean) v.getTag();
						String loginID = IMClient.getInstance()
								.getSSOUserId();
						if (!loginID.equals(it.getMxId())) {
							String toID =it.getMxId();
							ImUserBean user = ChatContactManager.getInstance().getImUserBean(toID);
							user.setName(it.getName());
							user.setAvatar(it.getAvatar());
							user.setGender(it.getGender());
							user.setMxId(toID);
							ChatUtil.gotoChatRoom(mContext, user);
						}
					}
				}
			});
		} else {
			viewHolder.chatIv.setVisibility(View.INVISIBLE);
			viewHolder.select_item_friend.setVisibility(View.VISIBLE);
			final boolean isSelected = selects.containsKey(item.getMxId());
			viewHolder.select_item_friend
					.setImageResource(isSelected ? R.drawable.ic_checkbox_checked
							: R.drawable.ic_checkbox_nor);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (isSelected) {
						addMembesBean.remove(item);
						selects.remove(item.getMxId());
					} else {
						addMembesBean.add(item);
						selects.put(item.getMxId(), item.getMxId());
					}
					if (handler != null) {
						handler.sendMessage(handler.obtainMessage(10,
								addMembesBean));
					}
					notifyDataSetChanged();
				}
			});
		}
		return convertView;
	}

	public RemoteContactsBean getAddMembesBean() {
		return addMembesBean;
	}

	/**
	 * @Title: setSelect
	 * @param:
	 * @Description: 设置选中项
	 * @return void
	 */
	public void setSelect(ImUserBean fansBean) {
		if (!selects.containsKey(fansBean.getMxId())) {
			selects.put(fansBean.getMxId(), fansBean.getMxId());
			addMembesBean.add(fansBean);
			handler.sendMessage(handler.obtainMessage(10, addMembesBean));
			notifyDataSetChanged();
		}
	}

	public void setAddMembesBean(RemoteContactsBean addMembesBean) {
		if(addMembesBean==null)
			addMembesBean=new RemoteContactsBean();
		this.addMembesBean = addMembesBean;
		addMembesBean.initSelects(selects);
		notifyDataSetChanged();
	}

	static class ViewHolder {
		/** 字母索引 */
		TextView indexTv;
		/** 好友名称 */
		TextView nameTv;
		/** 好友头像 */
		ImageView avatarIv;
		/** 聊天图标 */
		ImageView chatIv;
		/** 选择图标 */
		ImageView select_item_friend;
		ImageView line2_item_friend;
		ImageView img_line;
		ImageView img_line_paddleft;
	}

	// 获取某个字母的position
	@SuppressLint("DefaultLocale")
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < mDisplayDatas.size(); i++) {
			String l = mDisplayDatas.get(i).getPingyin();
			if (l != null && l.length() > 0) {
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	public boolean onLoadClass(Class<?> clazz) {
		return false;
	}

	@Override
	public Filter getFilter() {
		return mFilter == null ? new MyFilter() : mFilter;
	}

	class MyFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (constraint == null || constraint.length() == 0) {
				results.values = mDatas;
				results.count = mDatas.size();
			} else {
				String constraintString = constraint.toString();
				ArrayList<ImUserBean> newValues = new ArrayList<ImUserBean>();
				for (ImUserBean bean : mDatas) {
					String s = bean.getPingyin();
					if (s != null && s.contains(constraintString)) {
						newValues.add(bean);
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
		}
	}
}
