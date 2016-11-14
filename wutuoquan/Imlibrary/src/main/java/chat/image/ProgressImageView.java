package chat.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.imlibrary.R;

import chat.common.util.output.ShowUtil;

public class ProgressImageView extends ImageView {
	private Paint paintFrame;// 浮层画笔
	private Paint paintText;// 文字进度画笔
	private int MAX_SIZE_TEXT = 30;// 文字进度字体大小
	private int centerWidth;// 宽度中心
	private int centerHeight;// 高度度中心
	private int viewHeight;// 控件高度
	private String progressEndText = "%";
	private int progress = 0;
	private boolean isProgressMode = false;// 设置是否进度模式
	private Rect rectProgress;
	private int tranColor;// 透明背景
	private int halfTranColor;// 半透明背景

	public ProgressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		// 初始化背景色
		tranColor = context.getResources().getColor(R.color.tm);
		halfTranColor = context.getResources().getColor(R.color.b_tm);
		// 初始化进度浮层画笔
		paintFrame = new Paint();
		paintFrame.setColor(halfTranColor);
		// 初始化文字进度画笔
		MAX_SIZE_TEXT = (int) context.getResources().getDimension(
				R.dimen.text_size_title);
		paintText = new Paint();
		paintText.setTextSize(MAX_SIZE_TEXT);
		paintText.setColor(Color.WHITE);
		rectProgress = new Rect();
	}

	public void initSize(int widthMeasureSpec, int heightMeasureSpec) {
		// 初始化宽高中心
		this.centerWidth = widthMeasureSpec / 2 - MAX_SIZE_TEXT;// 除去字体大小
		this.centerHeight = heightMeasureSpec / 2 + MAX_SIZE_TEXT;// 除去字体大小
		rectProgress.left = 0;
		rectProgress.right = widthMeasureSpec;
		rectProgress.top = 0;
		rectProgress.bottom = heightMeasureSpec;
		viewHeight = heightMeasureSpec;
	}

	public void updateProgress(int progress) {
		this.progress = progress;
		rectProgress.bottom = viewHeight - viewHeight * progress / 100;
	}

	/**
	 * 设置是否为显示进度模式
	 * 
	 * @param isProgressMode
	 */
	public void setIsProgressMode(boolean isProgressMode) {
		this.isProgressMode = isProgressMode;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initSize(getMeasuredWidth(), getMeasuredHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!isProgressMode)
			return;
		ShowUtil.log(getContext(), "=progress" + progress + " centerWidth="
				+ centerWidth + " centerHeight=" + centerHeight);
		String progressText = progress + progressEndText;
		if (progress >= 100 || progress == 0) {
			progressText = "";
			paintFrame.setColor(tranColor);
		} else {
			paintFrame.setColor(halfTranColor);
		}
		canvas.drawRect(rectProgress, paintFrame);
		canvas.drawText(progressText, centerWidth, centerHeight, paintText);
	}

	private ImageUploadListener imageUploadListener;

	public void setImageUploadListener(ImageUploadListener imageUploadListener) {
		this.imageUploadListener = imageUploadListener;
	}

	/**
	 * 监听图片上传的接口
	 * 
	 * ImageUploadListener
	 * 
	 * @author weyko 2015年4月20日上午12:11:10
	 *
	 */
	public interface ImageUploadListener {
		public void onProgressUpdate(int progress);
	}
}
