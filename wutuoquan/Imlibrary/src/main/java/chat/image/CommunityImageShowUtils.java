package chat.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.imlibrary.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import chat.base.IMClient;
import chat.image.activity.ShowBigPhoto;

/**
 * 多图显示工具类
 */
public class CommunityImageShowUtils {
	private Context context;
	private int width;
	private int imagePadding = 0;
	private int forwardSherk = 0;
	private SparseArray<CommunityImageParams> paramV;
	private DisplayImageOptions options;
	private int screenWidth;

	public CommunityImageShowUtils(Context context, boolean isDynamic) {
		this.context = context;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		paramV = new SparseArray<CommunityImageParams>();
		imagePadding = dip2px(4);// 图片之间的间距
		if(isDynamic){
			width = (screenWidth - dip2px(10 * 3+50)); // 屏幕宽度-(页面左右边距+头像宽度)
		} else{
			width = (screenWidth - dip2px(10 * 2)) - dip2px(60); //如果是逛逛关注的动态，则宽度需要多减少头像部分的宽度
		}
		forwardSherk = dip2px(10);// 转发的显示整体会有缩进间距
		initImageOptions();
	}

	/**
	 * 显示转发图片
	 * 
	 * @param root
	 *            父容器
	 * @param images
	 *            图片
	 * @param listener
	 *            图片点击监听
	 */
	public void showForwardImage(LinearLayout root, List<String> images,
			OnClickListener listener) {
		showImage(root, images, listener, true);
	}

	/**
	 * 显示图片
	 * 
	 * @param root
	 *            父容器
	 * @param images
	 *            图片
	 */
	public void showImage(LinearLayout root, List<String> images) {
		showImage(root, images, null);
	}

	/**
	 * 显示图片
	 * 
	 * @param root
	 *            父容器
	 * @param images
	 *            图片
	 * @param listener
	 *            图片点击监听
	 */
	public void showImage(LinearLayout root, List<String> images,
			OnClickListener listener) {
		showImage(root, images, listener, false);
	}

	/**
	 * 清理内存
	 */
	public void clearMemory() {
		paramV.clear();
		System.gc();
	}

	private void showImage(LinearLayout root, List<String> images,
			OnClickListener listener, boolean isForward) {
		if (root != null) {
			root.removeAllViews();
			System.gc();
			root.setDrawingCacheEnabled(false);
			int size = images.size();
			if (size > 0) {
				CommunityImageParams params = getImageParams(size, isForward);
				for (int i = 0; i < params.lines; i++) {
					LinearLayout line = new LinearLayout(context);
					line.setOrientation(LinearLayout.HORIZONTAL);
					LayoutParams lineParams = new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					if (i > 0) {
						lineParams.setMargins(0, imagePadding, 0, 0);
					}
					line.setLayoutParams(lineParams);
					for (int j = 0; j < params.numsPerLine
							&& (i * params.numsPerLine + j) < size; j++) {
						int pos = i * params.numsPerLine + j;
						String imageUrl = images.get(pos);
						ImageView imageView = new ImageView(context);
						LayoutParams layoutParams = new LayoutParams(
								params.showWidth, params.showHeight);
						if (j < 2) {
							layoutParams.setMargins(0, 0, imagePadding, 0);
						}
						imageView.setLayoutParams(layoutParams);
						imageView.setScaleType(ScaleType.CENTER_CROP);
						imageView.setTag(imageUrl);
						if (listener != null) {
							imageView.setOnClickListener(listener);
						} else {
							imageView.setOnClickListener(new CommonDynamicPhotoClickListener(
											context, images, pos));
						}
						line.addView(imageView);
						IMClient.sImageLoader.displayThumbnailImage(imageUrl, imageView, params.showWidth, params.showHeight);
					}
					root.addView(line);
				}
			}
		}
	}
	private void initImageOptions() {
		if (options == null) {
			options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.default_head)
					.showImageOnFail(R.drawable.default_head).cacheInMemory(true)
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new RoundedBitmapDisplayer(5))
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
		}
	}

	private CommunityImageParams getImageParams(int size, boolean isForward) {
		int key = generateKey(size, isForward);
		CommunityImageParams item = paramV.get(key);
		if (item == null) {
			item = new CommunityImageParams();
			int tempWidth = width;
			if (isForward) {
				tempWidth = width - 2 * forwardSherk;
			}
			if (key == 1 || key == 10) {
				item.lines = 1;
				item.numsPerLine = 1;
				item.showWidth = tempWidth * 2 / 3;
				item.showHeight = item.showWidth;
			} else if ((key>=2&&key<=3) || (key>=11&&key<=12)) {
				item.lines = 1;
				item.numsPerLine = 3;
				item.showWidth = (tempWidth - 2 * imagePadding) / 3;
				item.showHeight = item.showWidth;
			} else if ((key==4) || (key==13)) {
				item.lines = 2;
				item.numsPerLine = 2;
				item.showWidth = (tempWidth - 2 * imagePadding) / 3;
				item.showHeight = item.showWidth;
			}else if ((key>=5&&key<=6) || (key>=14&&key<=15)) {
				item.lines = 2;
				item.numsPerLine = 3;
				item.showWidth = (tempWidth - 2 * imagePadding) / 3;
				item.showHeight = item.showWidth;
			} else {
				if( (key>=7&&key<=9)||(key>=16&&key<=18)){
					item.lines = 3;
					item.numsPerLine = 3;
					item.showWidth = (tempWidth - 2 * imagePadding) / 3;
					item.showHeight = item.showWidth;
				}
			}
			paramV.put(key, item);
		}
		return item;
	}

	private int generateKey(int size, boolean isForward) {
		int key = 0;
		if (isForward) {
			key=9+size;
		} else {
			key=size;
		}
		return key;
	}

	private class CommunityImageParams {
		public int showWidth;
		public int showHeight;
		public int lines = 0;
		public int numsPerLine = 0;
	}

	private int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	public class CommonDynamicPhotoClickListener implements OnClickListener{
		private ArrayList<String> images=new ArrayList<String>();
		private Context context;
		private int pos=0;
		public CommonDynamicPhotoClickListener(Context context, List<String> data, int pos){
			images.addAll(data);
			this.pos = pos;
			this.context = context;
		}
		@Override
		public void onClick(View arg0) {
			if(context!=null&&images!=null){
				Intent intent=new Intent();
				intent.setClass(context, ShowBigPhoto.class);
				intent.putStringArrayListExtra("data", images);
				intent.putExtra("pos", pos);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}
	}
}