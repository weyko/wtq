package chat.image.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.imlibrary.R;

import java.io.File;
import java.util.List;

import chat.contact.bean.ImageItem;
import chat.image.IMImageLoader;
import chat.image.activity.ShowBigPhoto;
import chat.session.activity.ChatActivity;
import chat.session.bean.MessageBean;

public class ImageGridAdapter extends BaseAdapter {
    private Activity act;
    private List<ImageItem> dataList;
    /**
     * 标记 选择按钮是否打开
     */
    public boolean flag = false;
    private MessageBean bean;
    private IMImageLoader imageLoader;

    public ImageGridAdapter(Activity act, List<ImageItem> list, MessageBean bean) {
        this.act = act;
        dataList = list;
        this.bean = bean;
        imageLoader = new IMImageLoader();
        imageLoader.initConfig(act.getApplicationContext(), 0);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        }
        return count;
    }

    @Override
    public ImageItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        private ImageView iv;
        private ImageView selected;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(act, R.layout.item_image_grid, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.image);
            holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final ImageItem item = dataList.get(position);
        String imgageUrl = item.imagePath;
        File file = new File(imgageUrl);
        if (file.exists()) {// 本地图片
            imgageUrl = "file:///" + imgageUrl;
        }
        imageLoader.displayImage(imgageUrl, holder.iv);
        if (item.isSelected) {
            holder.selected.setImageResource(R.drawable.selector_checkbox_checked);
        } else {
            holder.selected.setImageResource(R.drawable.selector_checkbox_normal);
        }
        holder.selected.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doImageSelect(item, holder);
            }
        });

        holder.iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    doImageSelect(item, holder);
                    return;
                }
                Intent intent = new Intent(act, ShowBigPhoto.class);
                intent.putExtra(ChatActivity.MESSAGEBEAN, bean);
                intent.putExtra(ChatActivity.TRANSPOND_TYPE, position);
                if (position < dataList.size()) {
                    intent.putExtra(ChatActivity.TRANSPOND_PIC_PATH, dataList.get(position).imagePath);
                }
                act.startActivityForResult(intent, 1000);
                act.finish();
            }
        });
        if (flag)
            holder.selected.setVisibility(View.VISIBLE);
        else
            holder.selected.setVisibility(View.GONE);
        return convertView;
    }

    /**
     * 处理图片选择事物
     *
     * @param item
     * @param holder
     */
    private void doImageSelect(ImageItem item, Holder holder) {
        item.isSelected = !item.isSelected;
        if (item.isSelected) {
            holder.selected.setImageResource(R.drawable.selector_checkbox_checked);

        } else if (!item.isSelected) {
            holder.selected.setImageResource(R.drawable.selector_checkbox_normal);
        }
    }

    public void clearMemory() {
        imageLoader.clearMemory();
        imageLoader = null;
        System.gc();
    }
}