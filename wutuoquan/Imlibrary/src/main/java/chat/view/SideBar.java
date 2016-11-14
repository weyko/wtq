package chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.imlibrary.R;
public class SideBar extends View {
	protected char[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list = null;
	private TextView mDialogText = null;
	protected int m_nItemHeight;
	Context context;
	// 字体缩放比率
	private float rate = 1f;

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float scale = dm.density;
		int w = (int) (33 * scale + 0.5f);
		m_nItemHeight = dm.heightPixels / w;
		init();
	}

	public void setSideBarHeightPixels(int m_nItemHeight) {
		this.m_nItemHeight = m_nItemHeight;
		invalidate();
	}

	public int getSideBarHeightPixels() {
		return m_nItemHeight;
	}

	protected void init() {
		l = new char[] { '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z' };
	}

	public void setListView(ListView _list) {
		list = _list;
		if (_list.getAdapter() instanceof HeaderViewListAdapter) {
			ListAdapter adapter = ((HeaderViewListAdapter) _list.getAdapter())
					.getWrappedAdapter();
			if (adapter instanceof SectionIndexer) {
				sectionIndexter = (SectionIndexer) adapter;
			}
		} else {
			sectionIndexter = (SectionIndexer) _list.getAdapter();
		}
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public void setItemHeight(int ItemHeight) {
		this.m_nItemHeight = ItemHeight;
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (list == null) {
			return false;
		}
		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			setBackgroundResource(R.drawable.sidebar_bg);
			if (mDialogText != null) {
				mDialogText.setVisibility(View.VISIBLE);
				mDialogText.setText("" + l[idx]);
			}
			if (sectionIndexter == null) {
				if (!(list.getAdapter() instanceof SectionIndexer)) {
					return false;
				}
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(l[idx]);
			if (position == -1) {
				return true;
			}
			if (list != null) {
				list.setSelection(position);
			}
		} else {
			if (mDialogText != null) {
				mDialogText.setVisibility(View.INVISIBLE);
			}
			setBackgroundResource(0);
		}
		return true;
	}

	/**
	 * @Title: setTextSize
	 * @param:
	 * @Description: 设置字体大小缩放比率
	 * @return void
	 */
	public void setTextSizeRate(float rate) {
		this.rate = rate;
	}

	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(context.getResources().getColor(R.color.text_color_mark));
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float scale = dm.density;
		int w = 0;
		if (dm.widthPixels > 320) {
			w = (int) (18 * rate * scale + 0.5f);
		} else {
			w = (int) (12 * rate * scale + 0.5f);
		}
		paint.setTextSize(w);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < l.length; i++) {
			canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight
					+ (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}
}
