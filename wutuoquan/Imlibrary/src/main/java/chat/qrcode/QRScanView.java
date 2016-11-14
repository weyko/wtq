package chat.qrcode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 
* @Description: 二维码扫描区动画绘制
*
 */
public final class QRScanView extends View {
	/*** 刷新界面的时间*/
	private static final long ANIMATION_DELAY = 50L;
	/*** 四个绿色边角对应的宽度*/
	private static final int CORNER_WIDTH = 4;
	/*** 扫描框中的中间线的宽度 */
	private static final int MIDDLE_LINE_WIDTH = 3;
	/** * 扫描框中的中间线的与扫描框左右的间隙*/
	private static final int MIDDLE_LINE_PADDING = 4;
	/*** 中间那条线每次刷新移动的距离*/
	private static final int SPEEN_DISTANCE = 7;
	/*** 四个绿色边角对应的长度*/
	private int ScreenRate;
	/*** 画笔对象的引用*/
	private Paint paint;
	/** * 中间滑动线的最顶端位置*/
	private int slideTop;
	private int frameColor=Color.parseColor("#61C0F0");
	private int maskColor=Color.parseColor("#44cccccc");
	boolean isFirst;
	
	private boolean scanAnimation=true;
	/**0-摄像扫一扫 1-扫内置图片*/
	private int scanType=0;
	
	private DisplayMetrics displayMetrics;
	private boolean cameraOpenSucess=true;
	private Rect frame=null;
	private Rect cFrame=null;
	
	public QRScanView(Context context) {
		super(context);
		init(context);
	}
	
	public QRScanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public QRScanView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		displayMetrics = context.getResources().getDisplayMetrics();
		ScreenRate = (int)(20 * displayMetrics.density);
		paint = new Paint();
		int width = dip2px(180);
		frame = new Rect(0,0,width,width);
		cFrame = new Rect(frame.left+CORNER_WIDTH, frame.top+CORNER_WIDTH,
				frame.right-CORNER_WIDTH, frame.bottom-CORNER_WIDTH);
	}
	
	public void closeScanAnimation(){
		scanAnimation = false;
	}
	
	public void setScanMaskColor(int color){
		maskColor = color;
	}
	
	public void setScanFrameColor(int color){
		frameColor = color;
	}
	
	public void setScanFrameSize(int width,int height){
		if(width>0&&height>0){
			frame = new Rect(0,0,width,height);
			cFrame = new Rect(frame.left+CORNER_WIDTH, frame.top+CORNER_WIDTH,
					frame.right-CORNER_WIDTH, frame.bottom-CORNER_WIDTH);
		}
	}
	
	private int dip2px(float dpValue) {
		final float scale = displayMetrics.density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public void cameraOpenFailed(){
		cameraOpenSucess = false;
	}

	/**
	 * 获取扫描类型
	 * @return scanType 0-摄像扫一扫 1-扫内置图片
	 */
	public int getScanType() {
		return scanType;
	}

	/**
	 * 设置扫描类型
	 * @param scanType 0-摄像扫一扫 1-扫内置图片
	 */
	public void setScanType(int scanType) {
		this.scanType = scanType;
	}


	public void onDraw(Canvas canvas) {
		if(scanType==0&&!cameraOpenSucess){
			return;
		}
		
		canvas.clipRect(frame);
		paint.setColor(maskColor);
		
		canvas.drawRect(cFrame, paint);
		/*canvas.drawRect(0, 0, displayMetrics.widthPixels, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, displayMetrics.widthPixels, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, displayMetrics.widthPixels, displayMetrics.heightPixels, paint);*/
		
		//画扫描框边上的角，总共8个部分
		paint.setColor(frameColor);
		canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
				frame.top + CORNER_WIDTH, paint);
		canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
				+ ScreenRate, paint);
		canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
				frame.top + CORNER_WIDTH, paint);
		canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
				+ ScreenRate, paint);
		canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
				+ ScreenRate, frame.bottom, paint);
		canvas.drawRect(frame.left, frame.bottom - ScreenRate,
				frame.left + CORNER_WIDTH, frame.bottom, paint);
		canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,
				frame.right, frame.bottom, paint);
		canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,
				frame.right, frame.bottom, paint);
		if(!scanAnimation){
			return;
		}
		//初始化中间线滑动的最上边和最下边
		if(!isFirst){
			isFirst = true;
			slideTop = cFrame.top;
		}
		//绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
		slideTop += SPEEN_DISTANCE;
		if(slideTop > cFrame.bottom){
			slideTop = cFrame.top;
		}
		canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2, paint);
		//只刷新扫描框的内容，其他地方不刷新
		postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
				frame.right, frame.bottom);
	}
}