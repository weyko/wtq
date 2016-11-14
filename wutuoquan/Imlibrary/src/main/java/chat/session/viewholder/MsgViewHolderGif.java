package chat.session.viewholder;

import com.imlibrary.R;

import chat.session.bean.IMChatGifBody;
import chat.session.util.ChatFaceUtils;
import pl.droidsonroids.gif.GifImageView;

/**
 *  消息子布局：动态表情
 */
public class MsgViewHolderGif extends MsgViewHolderBase {
    private ChatFaceUtils chatFaceUtils;
    @Override
    protected int getContentResId() {
        return R.layout.im_message_item_gif;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        GifImageView bodyGifView = findViewById(R.id.nim_message_item_gif_body);
        if(chatFaceUtils==null)
            chatFaceUtils=new ChatFaceUtils();
        IMChatGifBody attachment = message.getAttachment();
        chatFaceUtils.getGif(bodyGifView, context, attachment.getAttr1());
        contentContainer.setBackgroundResource(0);
    }
    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }
}
