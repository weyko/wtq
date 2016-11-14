package net.skjr.wtq.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.model.account.BankListAccount;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/18 13:29
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class BankCardListAdapter extends BaseAdapter {
    private Context mContext;
    private List<BankListAccount.ListEntity> mList;
    private ImageView mCard_logo;
    private TextView mCard_bank_name;
    private TextView mCard_bank_num;
    private TextView mCard_getcash;
    private View mCard_ungetcash;

    public BankCardListAdapter(Context context, List<BankListAccount.ListEntity> list) {
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
            view = View.inflate(mContext, R.layout.bank_list_item, null);
            mCard_logo = (ImageView) view.findViewById(R.id.card_logo);
            mCard_bank_name = (TextView) view.findViewById(R.id.card_bank_name);
            mCard_bank_num = (TextView) view.findViewById(R.id.card_bank_num);
            mCard_getcash = (TextView) view.findViewById(R.id.card_getcash);
            mCard_ungetcash = view.findViewById(R.id.card_ungetcash);
        }
        BankListAccount.ListEntity listEntity = mList.get(i);
        mCard_bank_name.setText(listEntity.bank);
        mCard_bank_num.setText(listEntity.accountCard);

        if(listEntity.useMark) {
            mCard_getcash.setVisibility(View.VISIBLE);
            mCard_ungetcash.setVisibility(View.GONE);
        } else {
            mCard_getcash.setVisibility(View.GONE);
            mCard_ungetcash.setVisibility(View.VISIBLE);
        }
        switch (listEntity.bankCode) {
            case "pf":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pf));
                break;
            case "gd":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gd));
                break;
            case "gf":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gf));
                break;
            case "hx":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hx));
                break;
            case "jt":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.jt));
                break;
            case "ms":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ms));
                break;
            case "ns":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ns));
                break;
            case "pa":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pa));
                break;
            case "sh":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sh));
                break;
            case "xy":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.xy));
                break;
            case "zs":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.zs));
                break;
            case "zx":
                mCard_logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.zx));
                break;
        }

        return view;
    }

}
