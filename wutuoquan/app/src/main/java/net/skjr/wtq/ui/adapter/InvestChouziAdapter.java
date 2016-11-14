package net.skjr.wtq.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.account.TabhomeAccount;

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
public class InvestChouziAdapter extends BaseAdapter {
    private Activity mContext;
    private List<TabhomeAccount.ChouziListEntity> mList;
    public InvestChouziAdapter(Activity context, List list) {
        mContext = context;
        mList = list;
    }
    @Override
    public int getCount() {
        if (mList == null) {
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
         view = View.inflate(mContext, R.layout.project_item_chouzi, null);
            holder.chouzi_img =  (ImageView) view.findViewById(R.id.chouzi_img);
            holder.chouzi_title = (TextView) view.findViewById(R.id.chouzi_title);
            holder.chouzi_area = (TextView) view.findViewById(R.id.chouzi_area);
            holder.chouzi_money = (TextView) view.findViewById(R.id.chouzi_money);
            holder.chouzi_user = (TextView) view.findViewById(R.id.chouzi_user);
            holder.chouzi_day = (TextView) view.findViewById(R.id.chouzi_day);
            holder.chouzi_type = (TextView) view.findViewById(R.id.chouzi_type);
            holder.chouzi_text_progress = (TextView) view.findViewById(R.id.chouzi_text_progress);
            holder.chouzi_progress = (ProgressBar) view.findViewById(R.id.chouzi_progress);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        TabhomeAccount.ChouziListEntity listEntity = mList.get(i);
        ImageLoaderUtils.displayImage(mContext, listEntity.stockImg,holder.chouzi_img);
        holder.chouzi_title.setText(listEntity.stockTitle);
        holder.chouzi_area.setText(listEntity.cityName+"");
        holder.chouzi_money.setText(listEntity.stockMoneyFormat+"万");
        holder.chouzi_user.setText(listEntity.support+"人");
        holder.chouzi_day.setText(listEntity.surplusTimeDay+"天");
        holder.chouzi_type.setText(listEntity.stockTypeName);
        holder.chouzi_text_progress.setText(listEntity.speed+"%");
        holder.chouzi_progress.setProgress(listEntity.speed);
        return view;
    }

    class ViewHolder {
        ImageView chouzi_img ;
        TextView chouzi_title ;
        TextView chouzi_area ;
        TextView chouzi_money;
        TextView chouzi_user ;
        TextView chouzi_day ;
        TextView chouzi_type ;
        TextView chouzi_text_progress;
        ProgressBar chouzi_progress ;
    }
}
