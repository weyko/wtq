package net.skjr.wtq.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.skjr.wtq.R;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/22 10:52
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyCollectedListAdapter extends BaseAdapter {
    private Context mContext;
    public MyCollectedListAdapter(Context context) {
        mContext = context;
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
         view = View.inflate(mContext, R.layout.project_item_collect, null);
        }
        return view;
    }
}
