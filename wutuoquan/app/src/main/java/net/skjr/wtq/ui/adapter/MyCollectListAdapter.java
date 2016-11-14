package net.skjr.wtq.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.account.MyCollectListAccount;

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
public class MyCollectListAdapter extends BaseAdapter {
    private Activity mContext;
    private List<MyCollectListAccount.ListEntity> mList;
    public MyCollectListAdapter(Activity context, List<MyCollectListAccount.ListEntity> list) {
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
            view = View.inflate(mContext, R.layout.project_item_collect, null);
            holder.img1 = (ImageView) view.findViewById(R.id.my_publish_img);
            holder.img2 = (ImageView) view.findViewById(R.id.my_publish_state);
            holder.title = (TextView) view.findViewById(R.id.my_publish_title);
            holder.type = (TextView) view.findViewById(R.id.my_publish_type);
            holder.desc = (TextView) view.findViewById(R.id.my_publish_desc);
            holder.money = (TextView) view.findViewById(R.id.my_publish_money);
            holder.investor = (TextView) view.findViewById(R.id.my_publish_investors);
            holder.progress_num = (TextView) view.findViewById(R.id.my_publish_progress_num);
            holder.days = (TextView) view.findViewById(R.id.my_publish_days);
            holder.progress = (ProgressBar) view.findViewById(R.id.my_publish_progress);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        MyCollectListAccount.ListEntity listEntity = mList.get(i);
        ImageLoaderUtils.displayImage(mContext, listEntity.stockImg, holder.img1);
        /**
         * 显示不同的状态，预热中，筹资中。。。
         * @param
         */
        switch (listEntity.stockStatus) {
            case Consts.YURE:
                holder.img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wtzd_yrz));
                break;
            case Consts.CHOUZI:
                holder.img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wtzd_czz));
                break;
            case Consts.FENHONG:
                holder.img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_fenhong));
                break;
            case Consts.JIESAN:
                holder.img2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_jiesan));
                break;
        }
        holder.title.setText(listEntity.stockTitle);
        holder.type.setText(listEntity.stockTypeName);
        holder.desc.setText(listEntity.sketch);
        holder.money.setText(listEntity.stockMoneyFormat+"万");
        holder.investor.setText(listEntity.support+"人");
        holder.progress_num.setText(listEntity.speed+"%");
        holder.progress.setProgress(listEntity.speed);
        return view;
    }

    class ViewHolder {
        ImageView img1 ;
        ImageView img2 ;
        TextView title ;
        TextView type ;
        TextView desc;
        TextView money ;
        TextView investor ;
        TextView progress_num ;
        TextView days;
        ProgressBar progress ;
    }
}
