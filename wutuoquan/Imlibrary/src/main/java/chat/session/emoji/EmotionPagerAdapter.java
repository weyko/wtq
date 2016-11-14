package chat.session.emoji;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class EmotionPagerAdapter extends PagerAdapter {

	private List<View> mViews;

	public EmotionPagerAdapter(List<View> mViews) {
		this.mViews = mViews;
	}

	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		if(container instanceof ViewPager){
			((ViewPager) container).removeView(mViews.get(position));
		}
	}

	@Override
	public Object instantiateItem(View container, int position) {
		if(container instanceof ViewGroup) {
			((ViewGroup) container).addView(mViews.get(position));
		}
		return mViews.get(position);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

}
