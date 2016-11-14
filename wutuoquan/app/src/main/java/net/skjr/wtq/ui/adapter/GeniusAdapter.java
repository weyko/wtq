package net.skjr.wtq.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.account.GeniusAccount;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/28 11:50
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GeniusAdapter extends BaseAdapter {

    private Activity mContext ;
    private List<GeniusAccount.ListEntity> mList;
    private ImageView mGenius_img;
    private TextView mGenius_name;
    private TextView mGenius_job;
    private TextView mGenius_desc;

    public GeniusAdapter(Activity context, List<GeniusAccount.ListEntity> list) {
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
        if(view == null) {
            view = View.inflate(mContext, R.layout.genius_item, null);
            mGenius_img = (ImageView) view.findViewById(R.id.genius_img);
            mGenius_name = (TextView) view.findViewById(R.id.genius_name);
            mGenius_job = (TextView) view.findViewById(R.id.genius_job);
            mGenius_desc = (TextView) view.findViewById(R.id.genius_desc);
        }
        if(!TextUtils.isEmpty(mList.get(i).powerfulImg)) {
            ImageLoaderUtils.displayImage(mContext, mList.get(i).powerfulImg, mGenius_img);
        }
        mGenius_name.setText(mList.get(i).name);
        mGenius_job.setText(mList.get(i).duties);
        mGenius_desc.setText(mList.get(i).synopsis);

        return view;
    }
}
