package chat.session.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
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

import java.util.HashMap;

import chat.base.IMClient;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.ContactBean;
import chat.image.DisplayImageConfig;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.SparseArrayList;
import chat.session.util.IMTypeUtil;

/**
 * @ClassName: MemberAdapter
 * @Description: 群成员适配器
 * @author weyko
 *
 */
public class MemberAdapter extends BaseAdapter implements SectionIndexer,
		Filterable {
	private Context mContext;
	private SparseArray<ContactBean> mDatas;
	private SparseArray<ContactBean> mDisplayDatas;// 过滤后显示的数据
	private MyFilter mFilter;
	private SparseArrayList<ContactBean> members;
	private Handler handler;
	/** 选择成员的索引 */
	private HashMap<String, String> selects;
	private boolean isSetManager = false;
	private boolean isSetOwner = false;
	private int limit=-1;
	
	@SuppressLint("UseSparseArrays")
	public MemberAdapter(Context context, SparseArrayList<ContactBean> datas) {
		this.mContext = context;
		this.mDatas = datas;
		this.mDisplayDatas = datas;
		selects = new HashMap<String, String>();
		members = new SparseArrayList<ContactBean>();
	}

	public void setLimit(int limit){
		this.limit = limit;
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public SparseArrayList<ContactBean> getMemebers() {
		if (members == null)
			members = new SparseArrayList<ContactBean>();
		return members;
	}

	/**
	 * @Title: setIsSetManager
	 * @param:
	 * @Description: 是否为设置管理员
	 * @return void
	 */
	public void setIsSetManager(boolean isSetManager) {
		this.isSetManager = isSetManager;
	}

	/**
	 * @Title: isSetOwner
	 * @param:
	 * @Description: 是否为设置群主
	 * @return void
	 */
	public void isSetOwner(boolean isSetOwner) {
		this.isSetOwner = isSetOwner;
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
			int size = mDisplayDatas.size();
			for (int i = 0; i < size; i++) {
				if (fansBean.getMxId().equals(String.valueOf(mDisplayDatas.get(i).getFriendID())))
					members.put(i,mDisplayDatas.get(i));
			}
			handler.sendMessage(handler.obtainMessage(10, members));
			notifyDataSetChanged();
		}
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final ContactBean item = mDisplayDatas.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_friend, null);
			viewHolder = new ViewHolder();
			viewHolder.indexTv = (TextView) convertView
					.findViewById(R.id.letterIndex);
			viewHolder.avatarIv = (ImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.select_item_friend = (TextView) convertView
					.findViewById(R.id.select_item_friend);
			viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name);
			viewHolder.chatIv = (ImageView) convertView.findViewById(R.id.chat);
			viewHolder.img_line = (ImageView) convertView
					.findViewById(R.id.img_line);
			viewHolder.line2_item_friend = (ImageView) convertView
					.findViewById(R.id.line2_item_friend);
			viewHolder.img_line_paddleft = (ImageView) convertView
					.findViewById(R.id.img_line_paddleft);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String letterIndex = item.getCatalog();
		if (position == 0) {
			viewHolder.indexTv.setVisibility(View.VISIBLE);
			viewHolder.line2_item_friend.setVisibility(View.VISIBLE);
			viewHolder.indexTv.setText(letterIndex);
		} else {
			String lastLetterIndex = mDisplayDatas.get(position - 1)
					.getCatalog();
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
					.getCatalog();
			if (nextLetterIndex.equals(letterIndex)) {
				isShowSplit = true;
			}
		}
		viewHolder.img_line.setVisibility(isShowSplit ? View.GONE
				: View.VISIBLE);
		viewHolder.img_line_paddleft.setVisibility(isShowSplit ? View.VISIBLE
				: View.GONE);
		if (item.getUserImg() != null && item.getUserImg().length() > 0) {
			IMClient.sImageLoader.displayThumbnailImage(item.getUserImg(),
					viewHolder.avatarIv,
					DisplayImageConfig.userLoginItemImageOptions,DisplayImageConfig.headThumbnailSize,DisplayImageConfig.headThumbnailSize);
		} else {
			viewHolder.avatarIv.setImageResource(R.drawable.default_head);
		}
		final String nickname = item.getShowName();
		viewHolder.nameTv.setText(nickname);// 有备注显示备注，没有就显示名字（这里备注改成昵称可能会更好点,因为可能会需要添加额外的备注）

		viewHolder.chatIv.setVisibility(View.INVISIBLE);
		viewHolder.select_item_friend.setVisibility(View.VISIBLE);
		final boolean isSelected = selects.containsKey(String.valueOf(item.getFriendID()))
				|| (isSetManager && item.getRole() == IMTypeUtil.RoleType.ADMINS);
		if (isSetManager) {
			convertView.setEnabled(item.getRole() != IMTypeUtil.RoleType.ADMINS);
		}
		viewHolder.select_item_friend.setBackgroundResource(isSelected ? (!convertView.isEnabled() ? R.drawable.ic_group_checked
						: R.drawable.ic_checkbox_checked)
						: R.drawable.ic_checkbox_nor);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isSelected) {
					members.remove(members.indexOfValue(item));
					selects.remove(String.valueOf(item.getFriendID()));
				} else {
					if (limit<=0 || selects.size()<limit ){
						if (isSetOwner) {
							members.clear();
							selects.clear();
						}
						members.put(members.size(),item);
						selects.put(String.valueOf(item.getFriendID()), String.valueOf(item.getFriendID()));
					}else{
						//
						ShowUtil.showToast(mContext, R.string.admin_limit);
					}
				}
				if (handler != null) {
					handler.sendMessage(handler.obtainMessage(10, members));
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
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
		TextView select_item_friend;
		/** 分割线 */
		ImageView img_line;
		/** 分割线 */
		ImageView line2_item_friend;
		/** 分割线 */
		ImageView img_line_paddleft;
	}

	// 获取某个字母的position
	@SuppressLint("DefaultLocale")
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < mDisplayDatas.size(); i++) {
			String l = mDisplayDatas.get(i).getCatalog();
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
				SparseArray<ContactBean> newValues = new SparseArray<ContactBean>();
				int size=mDatas.size();
				for (int i=0;i<size;i++) {
					ContactBean bean = mDatas.get(mDatas.keyAt(i));
					String s = bean.getCatalog();
					if (s != null && s.contains(constraintString)) {
						newValues.put(i,bean);
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
			/*mDisplayDatas = (List<ChatGroupMemberBean>) results.values;
			notifyDataSetChanged();*/
		}
	}
}
