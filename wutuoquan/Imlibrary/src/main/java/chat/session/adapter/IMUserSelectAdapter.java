package chat.session.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.List;

import chat.base.IMClient;
import chat.image.DisplayImageConfig;
import chat.session.bean.IMUserBase;

public class IMUserSelectAdapter extends BaseAdapter implements SectionIndexer,
		Filterable {
	private Context mContext;
	private List<IMUserBase> mDatas;
	private List<IMUserBase> mDisplayDatas;// 过滤后显示的数据
	private MyFilter mFilter;
	private boolean isMulSelect = false;

	public IMUserSelectAdapter(Context context, List<IMUserBase> datas,
							   boolean isMulSelect) {
		this.mContext = context;
		this.mDatas = datas;
		this.mDisplayDatas = datas;
		this.isMulSelect = isMulSelect;
	}

	public IMUserSelectAdapter(Context context, List<IMUserBase> datas) {
		this.mContext = context;
		this.mDatas = datas;
		this.mDisplayDatas = datas;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		IMUserBase item = mDisplayDatas.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_select_im_user, null);
			viewHolder = new ViewHolder();
			viewHolder.indexTv = (TextView) convertView
					.findViewById(R.id.letterIndex);
			viewHolder.avatarIv = (ImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name);
			viewHolder.selected = (ImageView) convertView
					.findViewById(R.id.selected);
			viewHolder.img_line = (ImageView) convertView
					.findViewById(R.id.img_line);
			viewHolder.img_line_left = (ImageView) convertView
					.findViewById(R.id.img_line_left);
			viewHolder.img_line_bottom = (ImageView) convertView
					.findViewById(R.id.img_line_bottom);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String letterIndex = item.getCatalog();
		if (position == 0) {
			viewHolder.indexTv.setVisibility(View.VISIBLE);
			viewHolder.indexTv.setText(letterIndex);
			viewHolder.img_line.setVisibility(View.VISIBLE);
		} else {
			String lastLetterIndex = mDisplayDatas.get(position - 1)
					.getCatalog();
			if (letterIndex.equals(lastLetterIndex)) {
				viewHolder.indexTv.setVisibility(View.GONE);
				viewHolder.img_line.setVisibility(View.GONE);
				viewHolder.img_line_bottom.setVisibility(View.GONE);
			} else {
				viewHolder.indexTv.setVisibility(View.VISIBLE);
				viewHolder.img_line.setVisibility(View.VISIBLE);
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
		viewHolder.img_line_bottom.setVisibility(!isShowSplit ? View.VISIBLE
				: View.GONE);
		viewHolder.img_line_left.setVisibility(isShowSplit ? View.VISIBLE
				: View.GONE);
		if (item.getAvatarUrl() != null && item.getAvatarUrl().length() > 0) {
			IMClient.sImageLoader.displayThumbnailImage(item.getAvatarUrl(),
					viewHolder.avatarIv, DisplayImageConfig.userLoginItemImageOptions,
					DisplayImageConfig.headThumbnailSize,DisplayImageConfig.headThumbnailSize);
		} else {
			viewHolder.avatarIv.setImageResource(R.drawable.default_head);
		}
		if (isMulSelect) {
			viewHolder.selected.setVisibility(View.VISIBLE);
			if (item.selected) {
				viewHolder.selected
						.setImageResource(R.drawable.selector_checkbox_checked);
			} else {
				viewHolder.selected
						.setImageResource(R.drawable.selector_checkbox_normal);
			}
		}
		viewHolder.nameTv.setText(item.getName());
		return convertView;
	}

	static class ViewHolder {
		/** 字母索引 */
		TextView indexTv;
		/** 好友名称 */
		TextView nameTv;
		/** 好友头像 */
		ImageView avatarIv;
		/** 是否选中 */
		ImageView selected;
		/** 分割线 */
		ImageView img_line;
		/** 分割线 */
		ImageView img_line_left;
		/** 分割线 */
		ImageView img_line_bottom;
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
				ArrayList<IMUserBase> newValues = new ArrayList<IMUserBase>();
				for (IMUserBase bean : mDatas) {
					String s = bean.getCatalog();
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
			/*mDisplayDatas = (List<IMUserBase>) results.values;
			notifyDataSetChanged();*/
		}
	}
}
