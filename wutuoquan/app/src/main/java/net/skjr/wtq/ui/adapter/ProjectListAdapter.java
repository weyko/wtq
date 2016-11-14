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
import net.skjr.wtq.model.account.ProjectListAccount;

import java.util.List;

import static net.skjr.wtq.R.id.yure_progress;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/22 10:52
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectListAdapter extends BaseAdapter {
    private Activity mContext;
    private List<ProjectListAccount.ListEntity> mList;
    public ProjectListAdapter(Activity context, List<ProjectListAccount.ListEntity> list) {
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
    public int getItemViewType(int position) {
        if(mList.get(position).stockStatus == Consts.YURE) {
            return Consts.YURE;
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 50;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        ViewHolder1 holder1 = new ViewHolder1();
        ViewHolder2 holder2 = new ViewHolder2();
        if(view == null) {
            switch (type) {
                case Consts.YURE:
                    holder2 = new ViewHolder2();
                    view = View.inflate(mContext, R.layout.project_item_yure, null);
                    holder2.yure_img =  (ImageView) view.findViewById(R.id.yure_img);
                    holder2.yure_state =  (ImageView) view.findViewById(R.id.yure_state);
                    holder2.yure_title = (TextView) view.findViewById(R.id.yure_title);
                    holder2.yure_area = (TextView) view.findViewById(R.id.yure_area);
                    holder2.yure_time = (TextView) view.findViewById(R.id.yure_time);
                    holder2.yure_type = (TextView) view.findViewById(R.id.yure_type);
                    holder2.yure_text_progress = (TextView) view.findViewById(R.id.yure_text_progress);
                    holder2.yure_progress = (ProgressBar) view.findViewById(yure_progress);
                    view.setTag(holder2);
                    break;
                default:
                    holder1 = new ViewHolder1();
                    view = View.inflate(mContext, R.layout.project_item_chouzi, null);
                    holder1.chouzi_img =  (ImageView) view.findViewById(R.id.chouzi_img);
                    holder1.chouzi_title = (TextView) view.findViewById(R.id.chouzi_title);
                    holder1.chouzi_area = (TextView) view.findViewById(R.id.chouzi_area);
                    holder1.chouzi_money = (TextView) view.findViewById(R.id.chouzi_money);
                    holder1.chouzi_user = (TextView) view.findViewById(R.id.chouzi_user);
                    holder1.chouzi_day = (TextView) view.findViewById(R.id.chouzi_day);
                    holder1.chouzi_type = (TextView) view.findViewById(R.id.chouzi_type);
                    holder1.chouzi_text_progress = (TextView) view.findViewById(R.id.chouzi_text_progress);
                    holder1.chouzi_progress = (ProgressBar) view.findViewById(R.id.chouzi_progress);
                    holder1.project_state = (ImageView) view.findViewById(R.id.project_state);
                    view.setTag(holder1);

                    break;
            }

        } else {
            switch (type) {
                case Consts.YURE:
                    holder2 = (ViewHolder2) view.getTag();
                    break;
                default:
                    holder1 = (ViewHolder1) view.getTag();
                    break;
            }
        }
        ProjectListAccount.ListEntity listEntity = mList.get(i);
        /**
         * 显示不同的状态，预热中，筹资中。。。
         * @param
         */
        switch (listEntity.stockStatus) {
            case Consts.YURE:
                holder2.yure_state.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wtzd_yrz));
                break;
            case Consts.CHOUZI:
                holder1.project_state.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wtzd_czz));
                break;
            case Consts.FENHONG:
                holder1.project_state.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_fenhong));
                break;
            case Consts.JIESAN:
                holder1.project_state.setImageDrawable(mContext.getResources().getDrawable(R.drawable.state_jiesan));
                break;
        }
        switch (type) {
            case Consts.YURE:
                ImageLoaderUtils.displayImage(mContext, listEntity.stockImg,holder2.yure_img);
                holder2.yure_title.setText(listEntity.stockTitle);
                holder2.yure_area.setText(listEntity.cityName+"");
                holder2.yure_time.setText(listEntity.surplusTimeDay+"天");
                holder2.yure_type.setText(listEntity.stockTypeName);
                holder2.yure_text_progress.setText(listEntity.speed+"%");
                holder2.yure_progress.setProgress(listEntity.speed);
                break;
            default:
                ImageLoaderUtils.displayImage(mContext, listEntity.stockImg, holder1.chouzi_img);
                holder1.chouzi_title.setText(listEntity.stockTitle);
                holder1.chouzi_area.setText(listEntity.cityName+"");
                holder1.chouzi_money.setText(listEntity.stockMoneyFormat+"万");
                holder1.chouzi_user.setText(listEntity.support+"人");
                holder1.chouzi_day.setText(listEntity.surplusTimeDay+"天");
                holder1.chouzi_type.setText(listEntity.stockTypeName);
                holder1.chouzi_text_progress.setText(listEntity.speed+"%");
                holder1.chouzi_progress.setProgress(listEntity.speed);

                break;
        }

        return view;
    }

    class ViewHolder1 {
        ImageView chouzi_img ;
        ImageView project_state ;
        TextView chouzi_title ;
        TextView chouzi_area ;
        TextView chouzi_money;
        TextView chouzi_user ;
        TextView chouzi_day ;
        TextView chouzi_type ;
        TextView chouzi_text_progress;
        ProgressBar chouzi_progress ;
    }
    class ViewHolder2 {
        ImageView yure_img ;
        ImageView yure_state ;
        TextView yure_title ;
        TextView yure_area ;
        TextView yure_time ;
        TextView yure_type ;
        TextView yure_text_progress;
        ProgressBar yure_progress ;
    }


}
