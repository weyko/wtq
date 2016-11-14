package net.skjr.wtq.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.account.MyInvestAccount;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/22 10:52
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyInvestListAdapter extends BaseAdapter {
    private Activity mContext;
    private List<MyInvestAccount.ListEntity> mList;
    public MyInvestListAdapter(Activity context, List<MyInvestAccount.ListEntity> list) {
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
            view = View.inflate(mContext, R.layout.my_invest_item, null);
            holder.title = (TextView) view.findViewById(R.id.my_invest_title);
            holder.state = (TextView) view.findViewById(R.id.my_invest_state);
            holder.num = (TextView) view.findViewById(R.id.my_invest_num);
            holder.share = (TextView) view.findViewById(R.id.my_invest_share);
            holder.money = (TextView) view.findViewById(R.id.my_invest_money);
            holder.date = (TextView) view.findViewById(R.id.my_invest_date);
            holder.img = (ImageView) view.findViewById(R.id.my_invest_img);
            holder.type = (ImageView) view.findViewById(R.id.my_invest_type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MyInvestAccount.ListEntity listEntity = mList.get(i);
        ImageLoaderUtils.displayImage(mContext, listEntity.stockImg, holder.img);
        /**
         * 显示不同的状态，预热中，筹资中。。。
         * @param
         */
        switch (listEntity.stockStatus) {
            case Consts.YURE:
                holder.type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wtzd_yrz));
                break;
            case Consts.CHOUZI:
                holder.type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wtzd_czz));
                break;
            case Consts.FENHONG:
                holder.type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_fenhong));
                break;
            case Consts.JIESAN:
                holder.type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_jiesan));
                break;
        }
        holder.title.setText(listEntity.stockTitle);
        holder.num.setText(listEntity.buyFraction+"份");
        holder.money.setText(listEntity.tenderMoney+"万元");
        holder.share.setText(listEntity.buyShares+"%");
        holder.date.setText(listEntity.addDatetime);

        return view;
    }

    class ViewHolder {
        TextView title;
        TextView state;
        TextView num;
        TextView share;
        TextView money;
        TextView date;
        ImageView img;
        ImageView type;
    }
}
