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
public class InvestYureAdapter extends BaseAdapter {
    private Activity mContext;
    private List<TabhomeAccount.YureListEntity> mList;
    public InvestYureAdapter(Activity context, List list) {
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
            view = View.inflate(mContext, R.layout.project_item_yure, null);
            holder.yure_img =  (ImageView) view.findViewById(R.id.yure_img);
            holder.yure_title = (TextView) view.findViewById(R.id.yure_title);
            holder.yure_area = (TextView) view.findViewById(R.id.yure_area);
            holder.yure_time = (TextView) view.findViewById(R.id.yure_time);
            holder.yure_type = (TextView) view.findViewById(R.id.yure_type);
            holder.yure_text_progress = (TextView) view.findViewById(R.id.yure_text_progress);
            holder.yure_progress = (ProgressBar) view.findViewById(R.id.yure_progress);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        TabhomeAccount.YureListEntity listEntity = mList.get(i);
        ImageLoaderUtils.displayImage(mContext, listEntity.stockImg,holder.yure_img);
        holder.yure_title.setText(listEntity.stockTitle);
        holder.yure_area.setText(listEntity.cityName+"");
        holder.yure_time.setText(listEntity.surplusTimeDay+"天");
        holder.yure_type.setText(listEntity.stockTypeName);
        holder.yure_text_progress.setText(listEntity.speed+"%");
        holder.yure_progress.setProgress(listEntity.speed);
        return view;
    }

    class ViewHolder {
        ImageView yure_img ;
        TextView yure_title ;
        TextView yure_area ;
        TextView yure_time ;
        TextView yure_type ;
        TextView yure_text_progress;
        ProgressBar yure_progress ;
    }
}
