package net.skjr.wtq.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/26 9:28
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SelectAreaAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;

    public SelectAreaAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }
    @Override
    public int getCount() {
        return mList.size();
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
        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        tv.setPadding(10, 20, 10, 20);
        tv.setText(mList.get(i));
        return tv;
    }
}
