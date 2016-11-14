package chat.contact.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
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
import chat.contact.bean.ContactBean;
import chat.image.DisplayImageConfig;
import chat.image.IMImageLoader;
import chat.session.group.bean.SparseArrayList;

public class FansAdapter extends BaseAdapter implements SectionIndexer,
        Filterable {
    private Activity mContext;
    private List<ContactBean> mDatas;
    private List<ContactBean> mDisplayDatas;// 过滤后显示的数据
    private MyFilter mFilter;
    private IMImageLoader imageLoader = null;
    // 是否为聊天模式
    private boolean isChatMode = true;
    private SparseArrayList<ContactBean> selectFriends;
    private SparseArrayList<String> selectedMap;
    @SuppressLint("UseSparseArrays")
    public FansAdapter(Activity context, List<ContactBean> datas) {
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

    /**
     * @return void
     * @Title: setIsChatMode
     * @param:
     * @Description: 设置是否为聊天模式
     */
    public void setIsChatMode(boolean isChatMode) {
        this.isChatMode = isChatMode;
        if (!isChatMode) {
            selectFriends = new SparseArrayList<ContactBean>();
        }
    }

    public SparseArrayList<ContactBean> getSelectFriends() {
        return selectFriends;
    }

    public void setSelectedMembers(String selectedMembers) {
        if (selectedMap == null) {
            selectedMap = new SparseArrayList<String>();
        }
        if (selectedMembers != null) {
            String[] members = selectedMembers.split(",");
            int size = members.length;
            for (int i = 0; i < size; i++) {
                selectedMap.put(Integer.valueOf(members[i]), "");
            }
        }
    }

    /**
     * 清除选择
     */
    public void clearSelects() {
        if (selectFriends != null) {
            if (selectFriends.size() > 0)
                selectFriends.clear();
        }
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

        String letterIndex = item.getCatalog();
        if (TextUtils.isEmpty(letterIndex))
            letterIndex = "#";
        if (position == 0) {
            viewHolder.indexTv.setVisibility(View.VISIBLE);
            viewHolder.line2_item_friend.setVisibility(View.VISIBLE);
            viewHolder.indexTv.setText(letterIndex);
        } else {
            String lastLetterIndex = mDisplayDatas.get(position - 1)
                    .getCatalog();
            if (TextUtils.isEmpty(lastLetterIndex))
                lastLetterIndex = "#";

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
            if (TextUtils.isEmpty(nextLetterIndex))//add by weyko to fix NullPointException 2016.4.6
                nextLetterIndex = "#";
            if (nextLetterIndex.equals(letterIndex)) {
                isShowSplit = true;
            }
        }
        viewHolder.img_line.setVisibility(isShowSplit ? View.GONE
                : View.VISIBLE);
        viewHolder.img_line_paddleft.setVisibility(isShowSplit ? View.VISIBLE
                : View.GONE);

        if (item.getUserImg() != null && !item.getUserImg().equals("null")
                && item.getUserImg().length() > 0) {
            if (imageLoader != null) {
                imageLoader.displayThumbnailImage(item.getUserImg(),
                        viewHolder.avatarIv,
                        DisplayImageConfig.userLoginItemImageOptions,
                        DisplayImageConfig.headThumbnailSize,
                        DisplayImageConfig.headThumbnailSize);
            } else {
                IMClient.sImageLoader.displayThumbnailImage(
                        item.getUserImg(), viewHolder.avatarIv,
                        DisplayImageConfig.userLoginItemImageOptions,
                        DisplayImageConfig.headThumbnailSize,
                        DisplayImageConfig.headThumbnailSize);
            }
        } else {
            viewHolder.avatarIv.setImageResource(R.drawable.default_head);
        }
        String nickName = "";
        String remark = item.getRemarkName();
        if (!TextUtils.isEmpty(remark)) {
            nickName = remark;
        } else {
            String name = item.getUserNickname();
            nickName = name;
        }
        viewHolder.nameTv.setText(nickName);// 有备注显示备注，没有就显示名字（这里备注改成昵称可能会更好点,因为可能会需要添加额外的备注）
        viewHolder.chatIv.setTag(item);
        viewHolder.chatIv.setVisibility(!isChatMode ? View.INVISIBLE: View.VISIBLE);
        if (!isChatMode) {
            boolean isFocusable = true;//是否能操作
            if (selectedMap != null) {
                isFocusable = selectedMap.indexOfKey(item.getFriendID())<0;
            }
            viewHolder.select_item_friend.setVisibility(View.VISIBLE);
            viewHolder.select_item_friend.setBackgroundResource(isFocusable ? R.drawable.selector_chose_contact : R.drawable.ic_group_checked);
            if (isFocusable) {
                viewHolder.select_item_friend.setEnabled(selectFriends.indexOfKey(position) >= 0);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectFriends == null)
                            return;
                        if (selectFriends.indexOfKey(position) >= 0) {
                            selectFriends.remove(position);
                        } else {
                            selectFriends.put(position, item);
                        }
                        notifyDataSetChanged();
                    }
                });
            }else{
                convertView.setOnClickListener(null);
            }
        }
        return convertView;
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
    @Override
    public Filter getFilter() {
        return mFilter == null ? new MyFilter() : mFilter;
    }

    static class ViewHolder {
        /**
         * 字母索引
         */
        TextView indexTv;
        /**
         * 好友名称
         */
        TextView nameTv;
        /**
         * 好友头像
         */
        ImageView avatarIv;
        /**
         * 聊天图标
         */
        ImageView chatIv;
        /**
         * 选择图标
         */
        TextView select_item_friend;
        ImageView line2_item_friend;
        ImageView img_line;
        ImageView img_line_paddleft;
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
                ArrayList<ContactBean> newValues = new ArrayList<ContactBean>();
                for (ContactBean bean : mDatas) {
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
        }
    }
}
