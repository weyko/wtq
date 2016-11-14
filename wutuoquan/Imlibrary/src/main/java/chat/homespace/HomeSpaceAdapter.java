package chat.homespace;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.List;

import chat.base.IMClient;
import chat.common.util.ToolsUtils;
import chat.common.util.time.DateUtils;
import chat.image.CommunityImageShowUtils;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.view.DisableScrollListView;
import chat.view.MoreTextView;

/**
 * 梦家园适配器
 */
public class HomeSpaceAdapter extends CommonAdapter<HomeSpaceBean> implements View.OnClickListener, CommentUtil.OnCommentClickListener {
    private int margin = 10;
    private HomeCommentAdapter commentAdapter;
    private List<HomeSpaceBean.CommentBean> blankComments;

    public HomeSpaceAdapter(List<HomeSpaceBean> mDatas) {
        super(IMClient.getInstance().getContext(), mDatas, R.layout.item_homespace);
        margin = ToolsUtils.dip2px(mContext, margin);
        blankComments=new ArrayList<HomeSpaceBean.CommentBean>();
    }

    public HomeSpaceAdapter(Context context, List mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, HomeSpaceBean item) {
        helper.setText(R.id.nickname_item_homespace, item.getName());
        MoreTextView content = helper.getView(R.id.content_item_homespace);
        content.setMaxShowLines(6);
        content.setDesc(item.getContent());
        content.setVisibility(TextUtils.isEmpty(item.getContent()) ? View.GONE : View.VISIBLE);
        helper.setText(R.id.time_item_homespace, DateUtils.getTimesTampString(mContext, item.getTime()));
        int picUrls = item.getPicUrls().size();
        LinearLayout imgs_item_homespace = helper.getView(R.id.imgs_item_homespace);
        imgs_item_homespace.setVisibility(picUrls > 0 ? View.VISIBLE : View.GONE);
        if (picUrls > 0) {
            CommunityImageShowUtils utils = new CommunityImageShowUtils(mContext, true);
            utils.showImage(imgs_item_homespace, item.getPicUrls(), this);
        }
        helper.getView(R.id.play_item_homesapce).setVisibility(picUrls == 1 ? View.VISIBLE : View.GONE);
        helper.getView(R.id.chat_item_homespace).setOnClickListener(this);
        DisableScrollListView comment_list_item_homespace = helper.getView(R.id.comment_list_item_homespace);
        //复用评论适配器
        ListAdapter adapter = comment_list_item_homespace.getAdapter();
        if (adapter != null && adapter instanceof HomeCommentAdapter) {
            commentAdapter = (HomeCommentAdapter) adapter;
        } else {
            commentAdapter = new HomeCommentAdapter(mContext, blankComments, R.layout.item_textview, 0);
            comment_list_item_homespace.setAdapter(commentAdapter);
        }
        commentAdapter.setList(item.getCommentList());
        commentAdapter.setPosition(getPosition(item));
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chat_item_homespace)
            CommentUtil.getInstance(mContext).openCommentDialog(v, this);
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        CommentUtil.getInstance(mContext).release();
    }

    @Override
    public void onCommentClick(View view) {
        CommentUtil.getInstance(mContext).reset();
    }
}
