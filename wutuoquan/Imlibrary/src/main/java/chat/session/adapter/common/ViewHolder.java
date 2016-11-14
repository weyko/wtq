package chat.session.adapter.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import chat.base.IMClient;
import chat.image.DisplayImageConfig;

public class ViewHolder {
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	private ViewHolder(Context context, ViewGroup parent, int layoutId,
					   int position) {
		this.mViews = new SparseArray<View>();
		mPosition=position;
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	/**
	 * 拿到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder) convertView.getTag();
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 设置点击事件
	 * 
	 * @Title: setonClick
	 * @param:
	 * @Description: 设置点击事件
	 * @return void
	 */
	public void setonClick(int viewId, OnClickListener mClickListener) {
		View view = getView(viewId);
		view.setOnClickListener(mClickListener);
	}
	
	public  void setBag(int viewId,int color)
	{
		View view = getView(viewId);
		view.setBackgroundResource(color);
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @return
	 */
	public ViewHolder setImageByUrl(int viewId, String url) {
		//增加默认的Options，使图片不缓存到内存
		IMClient.getInstance().sImageLoader.displayImage(url,(ImageView)getView(viewId), DisplayImageConfig.mNoCacheOptionsoptions);
		return this;
	}

	//为ImageView指定圆角大小
	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param cornerRadiusPixels
	 * @return
	 */
	public ViewHolder setImageByUrl(int viewId, String url,
			int cornerRadiusPixels) {
		IMClient.sImageLoader
				.displayImage(url,(ImageView) getView(viewId),DisplayImageConfig
				.getListOfUserPupupWindowDisplayImageOptions(cornerRadiusPixels));
		return this;
	}

	/**
	 * @Title: setAvatarImageByUrl
	 * @param:
	 * @Description: 设置头像
	 * @return ViewHolder
	 */
	public ViewHolder setAvatarImageByUrl(int viewId, String url) {
		IMClient.sImageLoader.displayThumbnailImage(url,
				(ImageView) getView(viewId),
				DisplayImageConfig.userLoginItemImageOptions, DisplayImageConfig.headThumbnailSize,
				DisplayImageConfig.headThumbnailSize);
		return this;
	}

	/**
	 * 可以指定图片设置
	 * 
	 * @Title: setAvatarImageByUrl
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return ViewHolder
	 */
	public ViewHolder setAvatarImageByUrl(int viewId, String url,
			DisplayImageOptions mDisplayImageOptions) {
		IMClient.sImageLoader.displayThumbnailImage(url,
				(ImageView) getView(viewId), mDisplayImageOptions,DisplayImageConfig.headThumbnailSize,DisplayImageConfig.headThumbnailSize);
		return this;
	}

	/**
	 * 可以指定图片设置
	 *
	 * @Title: setImageByUrl
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return ViewHolder
	 */
	public ViewHolder setImageByUrl(int viewId, String url,
										  DisplayImageOptions mDisplayImageOptions) {
		IMClient.sImageLoader.displayImage(url,
				(ImageView) getView(viewId), mDisplayImageOptions);
		return this;
	}

	public int getPosition() {
		return mPosition;
	}

}
