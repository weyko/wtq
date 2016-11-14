package chat.session.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.imlibrary.R;

import chat.image.WImageLoader;

public class EmotionImgAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private int[] mResId;
	private Context context;
	WImageLoader imageLoader = null;

	public void setImageLoader(WImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public EmotionImgAdapter(Context context, int[] resId) {
		mInflater = LayoutInflater.from(context);
		mResId = resId;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResId.length;
	}

	@Override
	public Integer getItem(int position) {
		// TODO Auto-generated method stub
		return mResId[position];
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.img.setBackgroundColor(0);
		int pos = position < mResId.length ? position : mResId.length - 1;
		if (imageLoader != null) {
			holder.img.setImageBitmap(imageLoader.decodeResource(
					context.getResources(), mResId[pos]));
		} else {
			holder.img.setImageResource(mResId[pos]);
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView img;
	}
}
