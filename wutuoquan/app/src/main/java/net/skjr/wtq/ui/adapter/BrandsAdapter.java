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
import net.skjr.wtq.model.account.BrandsAccount;

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
public class BrandsAdapter extends BaseAdapter {

    private Activity mContext ;
    private List<BrandsAccount.ListEntity> mList;
    private ImageView mBrands_img;
    private TextView mBrands_desc;

    public BrandsAdapter(Activity context, List<BrandsAccount.ListEntity> list) {
        mContext = context;
        mList = list;
    }
    @Override
    public int getCount() {
        return 4;
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
            view = View.inflate(mContext, R.layout.brands_item, null);
            mBrands_img = (ImageView) view.findViewById(R.id.brands_img);
            mBrands_desc = (TextView) view.findViewById(R.id.brands_desc);
        }
            if(!TextUtils.isEmpty(mList.get(i).simpleImg)) {
                ImageLoaderUtils.displayImage(mContext, mList.get(i).simpleImg, mBrands_img);
            }
                mBrands_desc.setText(mList.get(i).synopsis);
        return view;
    }
}
