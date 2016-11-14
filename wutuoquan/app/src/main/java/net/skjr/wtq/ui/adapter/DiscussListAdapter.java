package net.skjr.wtq.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.model.account.DiscussListAccount;
import net.skjr.wtq.ui.activity.system.ReplyDiscussActivity;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/28 11:50
 * 描述	      评论列表适配器
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DiscussListAdapter extends BaseAdapter {

    private Activity mContext ;
    private List<DiscussListAccount.ListEntity> mList;
    private ImageView mBrands_img;
    private TextView mBrands_desc;
    private TextView mDiscuss_reply;

    public DiscussListAdapter(Activity context, List<DiscussListAccount.ListEntity> list) {
        mContext = context;
        mList = list;
    }
    @Override
    public int getCount() {
        return 14;
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
            view = View.inflate(mContext, R.layout.discuss_item, null);
            mDiscuss_reply = (TextView) view.findViewById(R.id.discuss_reply);
            mDiscuss_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ReplyDiscussActivity.class);
                    intent.putExtra("user","@番茄");
                    mContext.startActivity(intent);
                }
            });
        }
        return view;
    }
}
