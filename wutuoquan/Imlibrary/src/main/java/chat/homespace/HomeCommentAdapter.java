package chat.homespace;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import com.imlibrary.R;

import java.util.List;

import chat.base.IMClient;
import chat.common.util.output.ShowUtil;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.emoji.EmojiManager;
import chat.view.ActionSheet;

/**
 * Description: 评论列表适配器
 */
public class HomeCommentAdapter extends CommonAdapter<HomeSpaceBean.CommentBean> {

    private Context context;
    private StringBuilder builder = new StringBuilder();
    private SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
    private int currentPos;

    public HomeCommentAdapter(Context ct, List<HomeSpaceBean.CommentBean> mDatas, int itemLayoutId, int pos) {
        super(ct, mDatas, itemLayoutId);
        this.context = ct;
        this.currentPos = pos;
    }
    public void setPosition(int pos){
        this.currentPos = pos;
    }
    @Override
    public void convert(ViewHolder helper, HomeSpaceBean.CommentBean item) {
        TextView textview = helper.getView(R.id.comment_text_view);
        builder.delete(0, builder.length());
        if (item.getReplayName()!=null&&item.getName()!=null) {
            builder.append("<a style=\"text-decoration:none;\" href='publish_user'>" + item.getName() + "</a>");
            builder.append("<a style=\"text-decoration:none;\" href='text'>" + context.getString(R.string.rep_text) + "</a>");
            builder.append("<a style=\text-decoration:none;\" href='reply_user'>" + item.getReplayName() + "</a>");
            builder.append("<a style=\"text-decoration:none;\" href='text'>" + ": " + item.getComment() + "</a>");
        } else {
            if (item.getName() != null) {
                builder.append("<a style=\text-decoration:none;\" href='publish_user'>" + item.getName() + "</a>");
                builder.append("<a style=\"text-decoration:none;\" href='text'>" + ": " + item.getComment() + "</a>");
            }
        }
        textview.setText(Html.fromHtml(builder.toString()));
        String text = textview.getText().toString().trim();
        int len = text.length();
        textview.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spannable = (Spannable) textview.getText();
        URLSpan[] urlspan = spannable.getSpans(0, len, URLSpan.class);
        spanBuilder.clear();
        spanBuilder.clearSpans();
        spanBuilder.append(text);
        for (int i = 0; i < urlspan.length; i++) {
            URLSpan url = urlspan[i];
            CommentClickSpan urlSpan = new CommentClickSpan(context, url.getURL(), textview, item, currentPos, getPosition(item));
            try {
                spanBuilder.setSpan(urlSpan, spannable.getSpanStart(url), spannable.getSpanEnd(url), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                spanBuilder.setSpan(urlSpan, spannable.getSpanStart(url), spannable.getSpanEnd(url) - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        EmojiManager.getInstance().getSmileText(context, textview, text, 0.55f, spanBuilder);
    }
    /** 评论点击事件 */
    public class CommentClickSpan extends ClickableSpan implements ActionSheet.MenuItemClickListener, CommentUtil.OnCommentClickListener {

        private Context mContext;
        private String content;
        private HomeSpaceBean.CommentBean data;
        private TextView textView;
        private int currPos;
        private int commentPosition;
        private ActionSheet menuView;
        public CommentClickSpan(Context context, String cont, TextView tv, HomeSpaceBean.CommentBean item, int pos, int commPos) {
            this.mContext = context;
            this.content = cont;
            this.data = item;
            this.textView = tv;
            this.currPos = pos;
            this.commentPosition = commPos;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            if (content.equals("publish_user")) {
                ds.setColor(mContext.getResources().getColor(R.color.color_activity_blue_bg));
            } else if (content.equals("reply_user")) {
                ds.setColor(mContext.getResources().getColor(R.color.color_activity_blue_bg));
            } else {
                ds.setColor(mContext.getResources().getColor(R.color.text_color_mark));
            }
        }

        @Override
        public void onClick(View widget) {
            if (content.equals("publish_user")) {
                gotoPersonCenter(data.getName());
                textView.setClickable(false);
            } else if (content.equals("reply_user")) {
                gotoPersonCenter(data.getReplayName());
                textView.setClickable(false);
            } else {
                // 回复
                String friendId = data.getUid();
                if (friendId.equals(IMClient.getInstance().getSSOUserId())) {
                    showDeleteCommentMenu();
                } else {
                    CommentUtil.getInstance(context).openCommentDialog(textView,this);
                }
            }
        }

        /**
         * 跳转到个人中心
         * @param uid
         */
        private void gotoPersonCenter(String uid) {
            ShowUtil.showToast(context,"我是"+uid);
        }
        /** 删除评论菜单 */
        public void showDeleteCommentMenu() {
            mContext.setTheme(R.style.ActionSheetStyleIOS7);
            menuView = new ActionSheet(mContext);
            menuView.setCancelButtonTitle(mContext.getString(R.string.cancel));
            menuView.addItems(mContext.getString(R.string.chat_textview_menu_delete));
            menuView.setItemClickListener(this);
            menuView.setCancelableOnTouchMenuOutside(true);
            menuView.showMenu();
        }

        @Override
        public void onActionSheetItemClick(int itemPosition) {
            if (itemPosition == 0) {
                //删除评论
            }
        }

        @Override
        public void onCommentClick(View view) {

        }
    }
}
