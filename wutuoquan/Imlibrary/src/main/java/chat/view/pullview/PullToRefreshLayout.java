package chat.view.pullview;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.Timer;
import java.util.TimerTask;

public class PullToRefreshLayout extends RelativeLayout {
	public static final String TAG = "PullToRefreshLayout";
	public static final int INIT = 0;
	public static final int RELEASE_TO_REFRESH = 1;
	public static final int REFRESHING = 2;
	public static final int RELEASE_TO_LOAD = 3;
	public static final int LOADING = 4;
	public static final int DONE = 5;
	private int state = INIT;
	private OnRefreshListener mListener;
	public static final int SUCCEED = 0;
	public static final int FAIL = 1;
	private float downY, lastY;
	public float pullDownY = 0;
	private float pullUpY = 0;
	private float refreshDist = 200;
	private float loadmoreDist = 200;

	private MyTimer timer;
	public float MOVE_SPEED = 8;
	private boolean isLayout = false;
	private boolean isTouch = false;
	private float radio = 2;
	private RotateAnimation rotateAnimation;
	private RotateAnimation refreshingAnimation;
	private View refreshView;
	private View pullView;
	private View refreshingView;
	private View refreshStateImageView;
	private TextView refreshStateTextView;
	private View loadmoreView;
	private View pullUpView;
	private View loadingView;
	private View loadStateImageView;
	private TextView loadStateTextView;
	private View pullableView;
	private int mEvents;
	private boolean canPullDown = true;
	private boolean canPullUp = true;

	private Context mContext;
	Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
					/ getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
			if (!isTouch) {
				if (state == REFRESHING && pullDownY <= refreshDist) {
					pullDownY = refreshDist;
					timer.cancel();
				} else if (state == LOADING && -pullUpY <= loadmoreDist) {
					pullUpY = -loadmoreDist;
					timer.cancel();
				}

			}
			if (pullDownY > 0)
				pullDownY -= MOVE_SPEED;
			else if (pullUpY < 0)
				pullUpY += MOVE_SPEED;
			if (pullDownY < 0) {
				pullDownY = 0;
				if(pullView!=null) {
					pullView.clearAnimation();
				}
				if (state != REFRESHING && state != LOADING)
					changeState(INIT);
				timer.cancel();
				requestLayout();
			}
			if (pullUpY > 0) {
				pullUpY = 0;
				if(pullUpView!=null){
					pullUpView.clearAnimation();
				}
				if (state != REFRESHING && state != LOADING)
					changeState(INIT);
				timer.cancel();
				requestLayout();
			}
			requestLayout();
			if (pullDownY + Math.abs(pullUpY) == 0)
				timer.cancel();
		}

	};
	private float mPullHeight = 0;
	private int fromChat = 0;

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}
	
	public void removeOnRefreshListener() {
		mListener = null;
	}
	
	

	public PullToRefreshLayout(Context context) {
		super(context);
		initView(context);

	}

	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		timer = new MyTimer(updateHandler);
		try{
			rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
					context, R.anim.reverse_anim);
			refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
					context, R.anim.rotating);
		}catch(NotFoundException e){
			
		}
		LinearInterpolator lir = null;
		if(rotateAnimation!=null){
			if(lir==null){
				lir = new LinearInterpolator();
			}
			rotateAnimation.setInterpolator(lir);
		}
		if(refreshingAnimation!=null){
			if(lir==null){
				lir = new LinearInterpolator();
			}
			refreshingAnimation.setInterpolator(lir);
		}
	}

	public void setHeight(Context context, int height) {
		if (height > 0) {
			mPullHeight = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, height, context.getResources()
							.getDisplayMetrics());
		}
	}

	private void hide() {
		timer.schedule(5);
	}

	public void refreshFinish(int refreshResult) {
		if (null == refreshingView) {
			// return;
			initView();
		}
		if (refreshingView == null)
			return;
		refreshingView.clearAnimation();
		refreshingView.setVisibility(View.GONE);
		switch (refreshResult) {
		case SUCCEED:
			refreshStateImageView.setVisibility(View.VISIBLE);
			if (fromChat == 0) {
				refreshStateTextView.setText(R.string.refresh_succeed);
			} else {
				refreshStateTextView.setText(R.string.load_succeed);
			}
			refreshStateImageView
					.setBackgroundResource(R.drawable.refresh_succeed);
			break;
		case FAIL:
		default:
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_fail);
			refreshStateImageView
					.setBackgroundResource(R.drawable.refresh_failed);
			break;
		}
		if (pullDownY > 0) {
			delayHandler.sendEmptyMessageDelayed(0, 1000);
		} else {
			changeState(DONE);
			hide();
		}
	}

	private Handler delayHandler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			changeState(DONE);
			hide();
		}
	};
	public void loadmoreFinish(int refreshResult) {
		if(loadingView!=null){
			loadingView.clearAnimation();
			loadingView.setVisibility(View.GONE);
		}
		switch (refreshResult) {
		case SUCCEED:
			// 加载成功
			if(loadStateImageView!=null){
				loadStateImageView.setVisibility(View.VISIBLE);
				loadStateImageView.setBackgroundResource(R.drawable.load_succeed);
			}
			if(loadStateTextView!=null){
				loadStateTextView.setText(R.string.load_succeed);
			}
			break;
		case FAIL:
		default:
			// 加载失败
			if(loadStateImageView!=null) {
				loadStateImageView.setVisibility(View.VISIBLE);
				loadStateImageView.setBackgroundResource(R.drawable.load_failed);
			}
			if(loadStateTextView!=null){
				loadStateTextView.setText(R.string.load_fail);
			}
			break;
		}
		if (pullUpY < 0) {
			delayHandler.sendEmptyMessageDelayed(0, 1000);
		} else {
			changeState(DONE);
			hide();
		}
	}

	public void setState(int formChat) {
		this.fromChat = formChat;
	}

	private void changeState(int to) {
		state = to;
		switch (state) {
		case INIT:
			refreshStateImageView.setVisibility(View.GONE);
			refreshStateImageView.setVisibility(View.GONE);
			if (fromChat == 0) {
				refreshStateTextView.setText(R.string.pull_to_refresh);
			} else {
				refreshStateTextView.setText(R.string.chat_refreshed);
			}
			if (loadStateImageView!=null) {
				loadStateImageView.setVisibility(View.GONE);
			}
			if (loadStateTextView!=null) {
				loadStateTextView.setText(R.string.pullup_to_load);
			}
			if(pullUpView!=null) {
				pullUpView.clearAnimation();
				pullUpView.setVisibility(View.VISIBLE);
			}
			break;
		case RELEASE_TO_REFRESH:
			// 释放刷新状态
			if (fromChat == 0) {
				refreshStateTextView.setText(R.string.release_to_refresh);
			} else {
				// 用于聊天界面下拉头部
				refreshStateTextView.setText(R.string.release_to_load);
			}
			if(pullView!=null) {
				pullView.startAnimation(rotateAnimation);
			}
			break;
		case REFRESHING:
			// 正在刷新状态
			if(pullView!=null) {
				pullView.clearAnimation();
				pullView.setVisibility(View.INVISIBLE);
			}
			if (refreshingView!=null) {
				refreshingView.setVisibility(View.VISIBLE);
				refreshingView.startAnimation(refreshingAnimation);
			}
			refreshStateTextView.setText(R.string.refreshing);
			break;
		case RELEASE_TO_LOAD:
			// 释放加载状态
			loadStateTextView.setText(R.string.release_to_load);
			if(pullUpView!=null) {
				pullUpView.startAnimation(rotateAnimation);
			}
			break;
		case LOADING:
			// 正在加载状态
			if(pullUpView!=null) {
				pullUpView.clearAnimation();
				pullUpView.setVisibility(View.INVISIBLE);
			}
			loadStateTextView.setText(R.string.loading);
			if(loadingView!=null){
				loadingView.setVisibility(View.VISIBLE);
				loadingView.startAnimation(refreshingAnimation);

			}
			break;
		case DONE:
			break;
		}
	}
	private void releasePull() {
		if (downY > mPullHeight) {
			canPullDown = true;
			canPullUp = true;
		} else {
			canPullDown = false;
			canPullUp = false;
		}
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			lastY = downY;
			timer.cancel();
			mEvents = 0;
			releasePull();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
			mEvents = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mEvents == 0) {
				if (pullDownY > 0
						|| (((Pullable) pullableView).canPullDown()
								&& canPullDown && state != LOADING)) {
					pullDownY = pullDownY + (ev.getY() - lastY) / radio;
					if (pullDownY < 0) {
						pullDownY = 0;
						canPullDown = false;
						canPullUp = true;
					}
					if (pullDownY > getMeasuredHeight())
						pullDownY = getMeasuredHeight();
					if (state == REFRESHING) {
						isTouch = true;
					}
				} else if (pullUpY < 0
						|| (((Pullable) pullableView).canPullUp() && canPullUp && state != REFRESHING)) {
					pullUpY = pullUpY + (ev.getY() - lastY) / radio;
					if (pullUpY > 0) {
						pullUpY = 0;
						canPullDown = true;
						canPullUp = false;
					}
					if (pullUpY < -getMeasuredHeight())
						pullUpY = -getMeasuredHeight();
					if (state == LOADING) {
						isTouch = true;
					}
				} else
					releasePull();
			} else
				mEvents = 0;
			lastY = ev.getY();
			radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
					* (pullDownY + Math.abs(pullUpY))));
			if (pullDownY > 0 || pullUpY < 0)
				requestLayout();
			if (pullDownY > 0) {
				if (pullDownY <= refreshDist
						&& (state == RELEASE_TO_REFRESH || state == DONE)) {
					changeState(INIT);
				}
				if (pullDownY >= refreshDist && state == INIT) {
					changeState(RELEASE_TO_REFRESH);
				}
			} else if (pullUpY < 0) {
				if (-pullUpY <= loadmoreDist
						&& (state == RELEASE_TO_LOAD || state == DONE)) {
					changeState(INIT);
				}
				if (-pullUpY >= loadmoreDist && state == INIT) {
					changeState(RELEASE_TO_LOAD);
				}

			}
			if ((pullDownY + Math.abs(pullUpY)) > 8) {
				ev.setAction(MotionEvent.ACTION_CANCEL);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
			{
				isTouch = false;
			}
			if (state == RELEASE_TO_REFRESH) {
				changeState(REFRESHING);
				if (mListener != null)
					mListener.onRefresh(this);
			} else if (state == RELEASE_TO_LOAD) {
				changeState(LOADING);
				if (mListener != null)
					mListener.onLoadMore(this);
			}
			hide();
		default:
			break;
		}
		super.dispatchTouchEvent(ev);
		return true;
	}

	/**
	 * @author chenjing 閼奉亜濮╁Ο鈩冨珯閹靛瀵氬鎴濆З閻ㄥ墖ask
	 * 
	 */
	private class AutoRefreshAndLoadTask extends
			AsyncTask<Integer, Float, String> {

		@Override
		protected String doInBackground(Integer... params) {
			while (pullDownY < 4 / 3 * refreshDist) {
				pullDownY += MOVE_SPEED;
				publishProgress(pullDownY);
				try {
					Thread.sleep(params[0]);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			changeState(REFRESHING);
			if (mListener != null)
				mListener.onRefresh(PullToRefreshLayout.this);
			hide();
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			if (pullDownY > refreshDist)
				changeState(RELEASE_TO_REFRESH);
			requestLayout();
		}

	}
	public void autoRefresh() {
		AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
		task.execute(20);
	}
	public void autoLoad() {
		pullUpY = -loadmoreDist;
		requestLayout();
		changeState(LOADING);
		if (mListener != null)
			mListener.onLoadMore(this);
	}

	private void initView() {
		if (refreshView == null)
			return;
		pullView = refreshView.findViewById(R.id.pull_icon);
		refreshStateTextView = (TextView) refreshView
				.findViewById(R.id.state_tv);
		refreshingView = refreshView.findViewById(R.id.refreshing_icon);
		refreshStateImageView = refreshView.findViewById(R.id.state_iv);
		pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
		loadStateTextView = (TextView) loadmoreView
				.findViewById(R.id.loadstate_tv);
		loadingView = loadmoreView.findViewById(R.id.loading_icon);
		loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLayout) {
			refreshView = getChildAt(0);
			pullableView = getChildAt(1);
			loadmoreView = getChildAt(2);
			isLayout = true;
			initView();
			refreshDist = ((ViewGroup) refreshView).getChildAt(0)
					.getMeasuredHeight();
			loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0)
					.getMeasuredHeight();
		}
		refreshView.layout(0,
				(int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
				refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
		pullableView.layout(0, (int) (pullDownY + pullUpY),
				pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
						+ pullableView.getMeasuredHeight());
		loadmoreView.layout(0,
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
				loadmoreView.getMeasuredWidth(),
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
						+ loadmoreView.getMeasuredHeight());
	}

	private static Timer ctimer=new Timer();
	
	class MyTimer {
		private Handler handler;
		private MyTask mTask;

		public MyTimer(Handler handler) {
			this.handler = handler;
			//timer = new Timer();
		}

		public void schedule(long period) {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			ctimer.schedule(mTask, 0, period);
		}

		public void cancel() {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		}

		class MyTask extends TimerTask {
			private Handler handler;

			public MyTask(Handler handler) {
				this.handler = handler;
			}

			@Override
			public void run() {
				handler.obtainMessage().sendToTarget();
			}

		}
	}

	@Override
    protected void onDetachedFromWindow() {  
        super.onDetachedFromWindow();
        timer.cancel();
	}
	public interface OnRefreshListener {
		void onRefresh(PullToRefreshLayout pullToRefreshLayout);
		void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
	}

}
