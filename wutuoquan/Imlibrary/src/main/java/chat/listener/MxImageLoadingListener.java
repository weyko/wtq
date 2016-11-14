package chat.listener;

import android.view.View;

import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * 图片加载监听器
 * 
 * MxImageLoadingListener
 * 
 * @author weyko 2015年4月20日上午9:47:55
 *
 */
public class MxImageLoadingListener implements ImageLoadingProgressListener {

	@Override
	public void onProgressUpdate(String imageUri, View view, int current,
			int total) {
//		MxProgressImageView imageView = (MxProgressImageView) view;
//		int progress = 0;
//		if (total != 0) {
//			progress = current / total;
//		}
//		imageView.updateProgress(progress);
//		ShowUtil.log(view.getContext(), "current=" + current + " total="
//				+ total + " progress=" + progress);
	}
}
