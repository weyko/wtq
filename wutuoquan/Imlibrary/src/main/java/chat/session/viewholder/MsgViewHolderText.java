package chat.session.viewholder;

import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.imlibrary.R;

import chat.session.emoji.MoonUtil;

import static java.security.AccessController.getContext;

/**
 *  消息子布局：文本
 */
public class MsgViewHolderText extends MsgViewHolderBase {

    @Override
    protected int getContentResId() {
        return R.layout.im_message_item_text;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        TextView bodyTextView = findViewById(R.id.nim_message_item_text_body);
        bodyTextView.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
        MoonUtil.identifyFaceExpression(bodyTextView.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }
    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return message.getSession();
    }
}
