package chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
  * @Description: 禁用滚动，以嵌入Scrollview
  *
 */
public class DisableScrollListView extends ListView {

	public DisableScrollListView(Context context, AttributeSet attrs,
								 int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DisableScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DisableScrollListView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
