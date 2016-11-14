package chat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.imlibrary.R;

import java.util.Timer;
import java.util.TimerTask;

import chat.image.BitmapProvider;

/**录音动画imageView*/
public class MicrophoneImageView extends ImageView {

	public MicrophoneImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MicrophoneImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public MicrophoneImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {

		this.paint = new Paint();
		this.paint.setColor(Color.rgb(0x06, 0x79, 0xab));
		this.paint.setAntiAlias(true);
		this.setVoicePercent(0);
		this.setVoiceBitmapResource(R.drawable.record_animate_04);
	}

	private double voicePercenet = 0;
	private int voiceBitmapId;
	//private Bitmap srcBitmap;
	private Bitmap bitmap;
	private Paint paint;

	private Handler handler = new Handler();

	public void setVoicePercent(double voicePercenet) {
		this.voicePercenet = voicePercenet;
		MicrophoneImageView.this.invalidate();

	}

	private Timer timer = new Timer();

	private double perChange = 0f;

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				if (voicePercenet >= 0d) {
					voicePercenet -= perChange;
					handler.post(new Runnable() {
						public void run() {
							invalidate();

						}
					});
				}

			}
		}, 0, 20);

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		timer.cancel();
	}

	public void setVoiceBitmapResource(int id) {
		this.voiceBitmapId = id;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		if (w > 0 && h > 0 && oldw == 0 && oldh == 0) {
			clearMemory();
			this.bitmap = BitmapProvider.getBitmap(getContext(), voiceBitmapId);
			this.bitmap = BitmapProvider.getBitmap(bitmap, w, h);
			System.gc();
			this.perChange = 0.01f;
		}
	}
	
	public void clearMemory(){
		if(bitmap!=null&&!bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
	}

	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		if (bitmap != null) {

			int h = getHeight();

			int w = getWidth();

			if (h > 0) {

				//水平动画
				float dx  = (float) (voicePercenet * w);
				float left = (w-dx)/2f;
				float right = (w+dx)/2f;
				canvas.clipRect(new RectF(left,0,right, h));
				/*
				 * 垂直动画
				float height = (float) (voicePercenet * h);
				float top = h - height;
				canvas.clipRect(new RectF(0, top, w, h));
				 */
				canvas.drawBitmap(bitmap, 0, 0, new Paint());
				
//				float height = (float) (voicePercenet * h);
//
//				float top = h - height;
//
//				canvas.clipRect(new RectF(0, top, w, h));
//
//				canvas.drawBitmap(bitmap, 0, 0, new Paint());

			}
		}
	}

}
