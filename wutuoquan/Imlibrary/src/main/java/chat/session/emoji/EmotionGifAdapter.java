package chat.session.emoji;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.imlibrary.R;

import chat.common.util.ToolsUtils;
import chat.common.util.output.ShowUtil;
import chat.image.WImageLoader;
import chat.session.util.ChatFaceUtils;

public class EmotionGifAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private String[] mResId;
	int switchID;
	private ChatFaceUtils chatFaceTool = null;
	private int displayWith, displayHeight;
	private Context context;
	private WImageLoader imageLoader = null;
	public EmotionGifAdapter(Context context, WImageLoader imageLoader, String[] resId, int switchID) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mResId = resId;
		displayWith = ShowUtil.getScreenSize(context, ShowUtil.ScreenEnum.WIDTH);
		this.switchID = switchID;
		displayHeight = (int) context.getResources().getDimension(
				R.dimen.emotion_height);
		this.imageLoader = imageLoader;
	}

	public void setChatFaceUtils(ChatFaceUtils chatFaceTool) {
		this.chatFaceTool = chatFaceTool;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResId.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (position < mResId.length) {
			return mResId[position];
		} else {
			return null;
		}

	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_emotion, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.ll_bg_emotion = (LinearLayout) convertView.findViewById(R.id.ll_bg_emotion);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (switchID == 2) {
			LayoutParams layoutParams = (LayoutParams) holder.img
					.getLayoutParams();
			layoutParams.gravity = Gravity.CENTER;
			layoutParams.width = ToolsUtils.dip2px(context, 68);
			layoutParams.height = ToolsUtils.dip2px(context, 68);
			holder.img.setLayoutParams(layoutParams);
			holder.ll_bg_emotion.setBackgroundResource(0);
			if (chatFaceTool != null) {
				int pos = position < mResId.length ? position : mResId.length - 1;
				holder.img.setImageBitmap(imageLoader.decodeResource(context.getResources(),
						ChatFaceUtils.getImageResource(context, mResId[pos])));
			}
		} else if (switchID == 3) {
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView img;
		LinearLayout ll_bg_emotion;
	}
}
