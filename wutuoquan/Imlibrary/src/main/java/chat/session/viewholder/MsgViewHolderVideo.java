package chat.session.viewholder;

import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.imlibrary.R;

import java.io.IOException;

import chat.common.util.ToolsUtils;
import chat.common.util.output.ShowUtil;
import chat.image.photo.views.MsgThumbImageView;
import chat.media.PlayVideoActiviy;
import chat.session.bean.IMChatVideoBody;
import video.itguy.wxlikevideo.views.ScalableVideoView;

/**
 * 消息子布局：视频
 */
public class MsgViewHolderVideo extends MsgViewHolderBase {
    private RelativeLayout rl_message_item_video;//视频播放容器
    private ScalableVideoView sv_message_item_video;//视频播放控件
    private ImageView cover_message_item_video;//视频覆盖层
    private LinearLayout play_message_item_video;
    private MsgThumbImageView thumb_message_item_video;
    private IMChatVideoBody audioBody;
    @Override
    protected int getContentResId() {
        return R.layout.im_message_item_video;
    }

    @Override
    protected void inflateContentView() {
        rl_message_item_video=findViewById(R.id.rl_message_item_video);
        sv_message_item_video=findViewById(R.id.sv_message_item_video);
        cover_message_item_video=findViewById(R.id.cover_message_item_video);
        play_message_item_video=findViewById(R.id.play_message_item_video);
        thumb_message_item_video=findViewById(R.id.thumb_message_item_video);
    }

    @Override
    protected void bindContentView() {
        contentContainer.setBackgroundResource(0);
        ViewGroup.LayoutParams layoutParams = rl_message_item_video.getLayoutParams();
        Resources resources=context.getResources();
        int padding=2*resources.getDimensionPixelSize(R.dimen.personal_image_padding);//頁面边距
        int avatarWith=2*resources.getDimensionPixelSize(R.dimen.avatar_size_in_session);//头像大小
        int withScreen= ShowUtil.getScreenSize(context, ShowUtil.ScreenEnum.WIDTH);
        int with=withScreen-avatarWith-padding- ToolsUtils.dip2px(context,20);//20表示视频与周边控件的距离总和
        layoutParams.width=with;
        layoutParams.height= with*150/250;
        rl_message_item_video.setLayoutParams(layoutParams);
        cover_message_item_video.setBackgroundResource(isReceivedMessage()?R.drawable.bg_cover_reciver:R.drawable.bg_cover_send);
        sv_message_item_video.setVisibility(View.VISIBLE);
        thumb_message_item_video.setVisibility(View.GONE);
        audioBody = message.getAttachment();
        try {
            sv_message_item_video.setDataSource(audioBody.getLocalUrl());
            sv_message_item_video.setLooping(true);
            sv_message_item_video.prepare();
            sv_message_item_video.setVolume(0, 0);
            sv_message_item_video.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        if(sv_message_item_video!=null) sv_message_item_video.start();
    }
    public void stop(){
        if(sv_message_item_video!=null)sv_message_item_video.stop();
    }
    @Override
    protected void onItemClick() {
        super.onItemClick();
        if(sv_message_item_video==null||audioBody==null)
            return;
        sv_message_item_video.pause();
        context.startActivity(new Intent(context, PlayVideoActiviy.class).putExtra(PlayVideoActiviy.KEY_FILE_PATH, audioBody.getLocalUrl()));
    }
}
