package net.skjr.wtq.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.model.account.MyAssetAccount;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/11 19:07
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class TradeRecordAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyAssetAccount.AccountListEntity> mList;

    public TradeRecordAdapter(Context context, List<MyAssetAccount.AccountListEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        if(mList == null) {
            return 0;
        } else if(mList.size() > 3) {
            return 3;
        } else {
            return mList.size();
        }
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
        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.trade_record_item, null);
            holder.tv1 = (TextView) view.findViewById(R.id.record_name);
            holder.tv2 = (TextView) view.findViewById(R.id.record_money);
            holder.tv3 = (TextView) view.findViewById(R.id.record_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
            holder.tv1.setText(mList.get(i).businessName);
            holder.tv2.setText(mList.get(i).availableMoneyAdd+"");
            holder.tv3.setText(mList.get(i).addDatetime);
        return view;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
    }
}
