package chat.session.viewholder;

import android.content.Intent;

import com.imlibrary.R;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.image.activity.ShowBigPhoto;
import chat.session.activity.ChatActivity;

/**
 *  消息子布局：图片
 */
public class MsgViewHolderPicture extends MsgViewHolderThumbBase {

    @Override
    protected int getContentResId() {
        return R.layout.im_message_item_picture;
    }
    @Override
    protected void onItemClick() {
        BaseActivity lastActivity = IMClient.getInstance().getLastActivity();
        if(lastActivity==null)
            return;
        Intent intent = new Intent(lastActivity, ShowBigPhoto.class);
        intent.putExtra(ChatActivity.MESSAGEBEAN, message);
        lastActivity.startActivityForResult(intent,
                ChatActivity.REQUEST_CODE_VIEW_PHOTO);
    }

    @Override
    protected String thumbFromSourceFile(String path) {
        return path;
    }
}
