package chat.session.emoji;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.imlibrary.R;

import chat.base.IMClient;
import chat.common.util.ToolsUtils;

/**
 * 每屏显示的贴图
 */
public class StickerAdapter extends BaseAdapter {

    private Context context;
    private StickerCategory category;
    private int startIndex;

    public StickerAdapter(Context mContext, StickerCategory category, int startIndex) {
        this.context = mContext;
        this.category = category;
        this.startIndex = startIndex;
    }

    public int getCount() {//获取每一页的数量
        int count = category.getStickers().size() - startIndex;
        count = Math.min(count, EmoticonView.STICKER_PER_PAGE);
        return count;
    }

    @Override
    public Object getItem(int position) {
        return category.getStickers().get(startIndex + position);
    }

    @Override
    public long getItemId(int position) {
        return startIndex + position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        StickerViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_emotion, null);
            viewHolder = new StickerViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img);
            viewHolder.ll_bg_emotion = (LinearLayout) convertView.findViewById(R.id.ll_bg_emotion);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StickerViewHolder) convertView.getTag();
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.imageView
                .getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = ToolsUtils.dip2px(context, 68);
        layoutParams.height = ToolsUtils.dip2px(context, 68);
        viewHolder.imageView.setLayoutParams(layoutParams);
        viewHolder.ll_bg_emotion.setBackgroundResource(0);
        int index = startIndex + position;
        if (index >= category.getStickers().size()) {
            return convertView;
        }
        StickerItem sticker = category.getStickers().get(index);
        if (sticker == null) {
            return convertView;
        }
        IMClient.sImageLoader.displayImage(StickerManager.getInstance().getStickerBitmapUri(sticker.getCategory()
                , sticker.getName()), viewHolder.imageView, StickerManager.getInstance().getStickerImageOptions(ToolsUtils.dip2px(IMClient.getInstance().getContext(), 64)));
        return convertView;
    }

    class StickerViewHolder {
        public ImageView imageView;
        LinearLayout ll_bg_emotion;
    }
}