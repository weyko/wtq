package chat.view.pullview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.GridView;

import com.imlibrary.R;
public class PullableGridView extends GridView implements Pullable
{
	private boolean mCanPullDown = true;
	private boolean mCanPullUp = true;
	public PullableGridView(Context context)
	{
		super(context);
	}

	public PullableGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	public PullableGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.IPullable);
		int mode = a.getInt(R.styleable.IPullable_PullMode, 0);
		a.recycle();
		setPullToRefreshMode(mode);
	}
	
	public void setPullToRefreshMode(int mode)
	{
		switch (mode) {
			case Pullable.BOTH:// both
				mCanPullDown = true;
				mCanPullUp = true;
				break;
			case Pullable.TOP:// top
				mCanPullDown = true;
				mCanPullUp = false;
				break;
			case Pullable.BOTTOM:// bottom
				mCanPullDown = false;
				mCanPullUp = true;
				break;
			default:
				mCanPullDown = false;
				mCanPullUp = false;
				break;
			}
	}
	
	@Override
	public boolean canPullDown()
	{
		if (!mCanPullDown) {
			return false;
		}
		if (getCount() == 0)
		{
			// 没有item的时候也可以下拉刷新
			return true;
		} else if (getFirstVisiblePosition() == 0 && getChildAt(0)!=null
				&& getChildAt(0).getTop() >= 0)
		{
			// 滑到顶部了
			return true;
		} else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if (!mCanPullUp) {
			return false;
		}
		if (getCount() == 0)
		{
			// 没有item的时候也可以上拉加载
			return true;
		} else if (getLastVisiblePosition() == (getCount() - 1))
		{
			// 滑到底部了
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(
							getLastVisiblePosition()
									- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
				return true;
		}
		return false;
	}

}
