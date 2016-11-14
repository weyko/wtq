package net.skjr.wtq.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/23 11:14
 * 描述	      投资排行
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class InvestListAdapter extends BaseAdapter {
    private Context mContext ;
    public InvestListAdapter(Context context) {
        mContext = context;
    }
    @Override
    public int getCount() {
        return 20;
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
        ViewHolder holder ;
        if(view == null) {
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.wealthlist_item, null);
            holder.tv_num = (TextView) view.findViewById(R.id.list_num);
            holder.iv_head = (ImageView) view.findViewById(R.id.list_head);
            holder.tv_user = (TextView) view.findViewById(R.id.list_user);
            holder.tv_assets = (TextView) view.findViewById(R.id.list_assets);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
            if(i < 3) {
                if(i == 0) {
                    holder.tv_num.setBackground(mContext.getResources().getDrawable(R.drawable.cfb_1));
                    holder.tv_num.setText("");
                } else if(i == 1) {
                    holder.tv_num.setBackground(mContext.getResources().getDrawable(R.drawable.cfb_2));
                } else if(i == 2) {
                    holder.tv_num.setBackground(mContext.getResources().getDrawable(R.drawable.cfb_3));
                }
            } else {
                Log.e("text",i+1+"");
                holder.tv_num.setText(i+1+"");
            }
        return view;
    }
    class ViewHolder {
        TextView tv_num ;
        ImageView iv_head ;
        TextView tv_user ;
        TextView tv_assets ;
    }
}
