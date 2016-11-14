package chat.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * SideBar的子类，区别在于每个字母的间距不同
 *
 */
public class SubSideBar extends SideBar {

	private int oldHeight=0;
	
	public SubSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	//根据控件的实际大小计算m_nItemHeight
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (heightMeasureSpec>0&&l!=null&&l.length>0){
			if (oldHeight!=getHeight()){
				oldHeight = getHeight();
				m_nItemHeight = oldHeight / l.length;
				invalidate();
			}
		}
	}
}