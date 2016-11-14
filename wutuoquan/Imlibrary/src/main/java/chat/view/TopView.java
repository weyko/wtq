package chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.imlibrary.R;

import java.util.LinkedList;
import java.util.List;

public class TopView extends ScrollView {
	private static final String STICKY = "sticky";
	private View mCurrentStickyView;
	private Drawable mShadowDrawable;
	private List<View> mStickyViews;
	private int mStickyViewTopOffset;
	private int defaultShadowHeight = 10;
	private float density;
	private boolean redirectTouchToStickyView;
	private ScrollBarListener scrollBarListener;
	/**
	 * 当点击Sticky的时候，实现某些背景的渐变
	 */
	private Runnable mInvalidataRunnable = new Runnable() {

		@Override
		public void run() {
			if (mCurrentStickyView != null) {
				int left = mCurrentStickyView.getLeft();
				int top = mCurrentStickyView.getTop();
				int right = mCurrentStickyView.getRight();
				int bottom = getScrollY()
						+ (mCurrentStickyView.getHeight() + mStickyViewTopOffset);

				invalidate(left, top, right, bottom);
			}

			postDelayed(this, 16);

		}
	};

	public TopView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TopView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		try {
			mShadowDrawable = context.getResources().getDrawable(
					R.drawable.sticky_shadow_default);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mStickyViews = new LinkedList<View>();
		density = context.getResources().getDisplayMetrics().density;
	}
	/**
	 * 设置顶部栏状态监听接口
	 * 
	 * @param scrollBarListener
	 */
	public void setScrollBarListener(ScrollBarListener scrollBarListener) {
		this.scrollBarListener = scrollBarListener;
	}
	/**
	 * 找到设置tag的View
	 * 
	 * @param viewGroup
	 */
	private void findViewByStickyTag(ViewGroup viewGroup) {
		int childCount = ((ViewGroup) viewGroup).getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = viewGroup.getChildAt(i);

			if (getStringTagForView(child).contains(STICKY)) {
				mStickyViews.add(child);
			}

			if (child instanceof ViewGroup) {
				findViewByStickyTag((ViewGroup) child);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			findViewByStickyTag((ViewGroup) getChildAt(0));
		}
		showStickyView();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		showStickyView();
	}

	/** 
	      *  
	      */
	private void showStickyView() {
		View curStickyView = null;
		View nextStickyView = null;

		for (View v : mStickyViews) {
			int topOffset = v.getTop() - getScrollY();

			if (topOffset <= 0) {
				if (curStickyView == null
						|| topOffset > curStickyView.getTop() - getScrollY()) {
					curStickyView = v;
					if (scrollBarListener != null) {
						scrollBarListener.onTopBarStateChange(true);
					}
				}
			} else {
				if (nextStickyView == null
						|| topOffset < nextStickyView.getTop() - getScrollY()) {
					nextStickyView = v;
					if (scrollBarListener != null) {
						scrollBarListener.onTopBarStateChange(false);
					}
				}
			}
		}

		if (curStickyView != null) {
			mStickyViewTopOffset = nextStickyView == null ? 0 : Math.min(
					0,
					nextStickyView.getTop() - getScrollY()
							- curStickyView.getHeight());
			mCurrentStickyView = curStickyView;
			post(mInvalidataRunnable);
		} else {
			mCurrentStickyView = null;
			removeCallbacks(mInvalidataRunnable);
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		removeCallbacks(mInvalidataRunnable);
	}

	private String getStringTagForView(View v) {
		Object tag = v.getTag();
		return String.valueOf(tag);
	}

	/**
	 * 将sticky画出来
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mCurrentStickyView != null) {
			// 先保存起来
			canvas.save();
			// 将坐标原点移动到(0, getScrollY() + mStickyViewTopOffset)
			canvas.translate(0, getScrollY() + mStickyViewTopOffset);

			if (mShadowDrawable != null) {
				int left = 0;
				int top = mCurrentStickyView.getHeight() + mStickyViewTopOffset;
				int right = mCurrentStickyView.getWidth();
				int bottom = top + (int) (density * defaultShadowHeight + 0.5f);
				mShadowDrawable.setBounds(left, top, right, bottom);
				mShadowDrawable.draw(canvas);
			}

			canvas.clipRect(0, mStickyViewTopOffset,
					mCurrentStickyView.getWidth(),
					mCurrentStickyView.getHeight());

			mCurrentStickyView.draw(canvas);

			// 重置坐标原点参数
			canvas.restore();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			redirectTouchToStickyView = true;
		}

		if (redirectTouchToStickyView) {
			redirectTouchToStickyView = mCurrentStickyView != null;

			if (redirectTouchToStickyView) {
				redirectTouchToStickyView = ev.getY() <= (mCurrentStickyView
						.getHeight() + mStickyViewTopOffset)
						&& ev.getX() >= mCurrentStickyView.getLeft()
						&& ev.getX() <= mCurrentStickyView.getRight();
			}
		}

		if (redirectTouchToStickyView) {
			ev.offsetLocation(
					0,
					-1
							* ((getScrollY() + mStickyViewTopOffset) - mCurrentStickyView
									.getTop()));
		}
		return super.dispatchTouchEvent(ev);
	}

	private boolean hasNotDoneActionDown = true;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (redirectTouchToStickyView) {
			ev.offsetLocation(0,
					((getScrollY() + mStickyViewTopOffset) - mCurrentStickyView
							.getTop()));
		}

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			hasNotDoneActionDown = false;
		}

		if (hasNotDoneActionDown) {
			MotionEvent down = MotionEvent.obtain(ev);
			down.setAction(MotionEvent.ACTION_DOWN);
			super.onTouchEvent(down);
			hasNotDoneActionDown = false;
		}

		if (ev.getAction() == MotionEvent.ACTION_UP
				|| ev.getAction() == MotionEvent.ACTION_CANCEL) {
			hasNotDoneActionDown = true;
		}
		return super.onTouchEvent(ev);
	}

	public interface ScrollBarListener {
		/**
		 * 顶部栏状态改变回调方法
		 * 
		 * @param isTopMode
		 *            是否为置顶模式
		 */
		public void onTopBarStateChange(boolean isTopMode);
	}
}
