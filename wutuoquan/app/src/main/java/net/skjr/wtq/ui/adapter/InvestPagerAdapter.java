package net.skjr.wtq.ui.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.skjr.wtq.common.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/21 15:29
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class InvestPagerAdapter extends PagerAdapter {
    private Activity mContext;
    private List<String> mList = new ArrayList();

    public InvestPagerAdapter(Activity context, ArrayList<String> list) {
        mContext = context;
        mList = list;
    }
    @Override
    public int getCount() {
        if(mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView iv = new ImageView(mContext);
        ImageLoaderUtils.displayImage(mContext, mList.get(position), iv);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
