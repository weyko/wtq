package chat.image;

import android.os.Handler;

import chat.base.IMClient;
import chat.common.util.output.ShowUtil;

public class ImageUploadEntity implements ProgressImageView.ImageUploadListener {
	private int progress;
	private ProgressImageView imageView;
	private Handler handlerUploadImage;
	private ImageUploadRunnable imageUploadRunnable;

	@Override
	public void onProgressUpdate(int progress) {
		this.progress = progress;
		ShowUtil.log(IMClient.getInstance().getContext(),
				"---------------imageView--->isNull ?" + (imageView == null)
						+ "----progress----" + progress);
		if (imageView != null) {
			imageView.setIsProgressMode(true);
			imageView.updateProgress(progress);
			if (handlerUploadImage != null && imageUploadRunnable != null)
				handlerUploadImage.post(imageUploadRunnable);
			if (progress == 100) {
				handlerUploadImage = null;
				imageUploadRunnable = null;
			}

		}
	}

	public int getProgress() {
		return progress;
	}

	public void setImageView(ProgressImageView imageView) {
		this.imageView = imageView;
		handlerUploadImage = new Handler();
		imageUploadRunnable = new ImageUploadRunnable(imageView);
	}

	class ImageUploadRunnable implements Runnable {
		private ProgressImageView imageView;

		public ImageUploadRunnable(ProgressImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		public void run() {
			if (imageView != null)
				imageView.invalidate();
		}

	}
}
