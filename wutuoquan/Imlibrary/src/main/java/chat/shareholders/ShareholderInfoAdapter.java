package chat.shareholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.imlibrary.R;

import java.util.List;

import chat.base.IMClient;
import chat.image.DisplayImageConfig;

/**
 * Description:
 * Created  by: weyko on 2016/5/26.
 */
public class ShareholderInfoAdapter extends BaseExpandableListAdapter {
    private ShareholderInfoBean bean;
    private Context context;

    public ShareholderInfoAdapter(Context context) {
        this.context = context;
    }

    public void setData(ShareholderInfoBean bean) {
        this.bean = bean;
    }

    @Override
    public int getGroupCount() {
        return bean == null || bean.getGroups() == null ? 0 : bean.getGroups().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (bean == null)
            return 0;
        List<ShareholderInfoBean.GroupBean> groups = bean.getGroups();
        return groups == null || groups.get(groupPosition).getChilds() == null ? 0 : groups.get(groupPosition).getChilds().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return bean == null || bean.getGroups() == null ? null : bean.getGroups().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (bean == null)
            return null;
        List<ShareholderInfoBean.GroupBean> groups = bean.getGroups();
        return groups == null || groups.get(groupPosition).getChilds() == null ? null : groups.get(groupPosition).getChilds().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_shareholders, null);
        }
        TextView viewById = (TextView) convertView.findViewById(R.id.title_item_group_shareholders);
        viewById.setCompoundDrawablesWithIntrinsicBounds(0, 0, isExpanded ? R.drawable.ic_unfold : R.drawable.ic_shrinkage, 0);
        viewById.setText(bean.getGroups().get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_child_shareholders, null);
            viewHolder.avatar_item_child_shareholders = (ImageView) convertView.findViewById(R.id.avatar_item_child_shareholders);
            viewHolder.name_item_child_shareholders = (TextView) convertView.findViewById(R.id.name_item_child_shareholders);
            viewHolder.post_item_child_shareholders = (TextView) convertView.findViewById(R.id.post_item_child_shareholders);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ShareholderInfoBean.ChildBean child = bean.getGroups().get(groupPosition).getChilds().get(childPosition);
        viewHolder.name_item_child_shareholders.setText(child.getName());
        viewHolder.post_item_child_shareholders.setText(child.getPost());
        IMClient.sImageLoader.displayThumbnailImage(child.getAvater(),
                viewHolder.avatar_item_child_shareholders,
                DisplayImageConfig.userLoginItemImageOptions,
                DisplayImageConfig.headThumbnailSize,
                DisplayImageConfig.headThumbnailSize);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        private ImageView avatar_item_child_shareholders;
        private TextView name_item_child_shareholders, post_item_child_shareholders;
    }
}
