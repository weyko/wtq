package chat.session.viewholder;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.IMClient;
import chat.common.util.ToolsUtils;
import chat.common.util.sys.ScreenUtil;
import chat.media.Playable;
import chat.session.audio.MessageAudioControl;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgListenEnum;
import chat.session.enums.MsgStatusEnum;
/**
 * 语音子布局
 */
public class MsgViewHolderAudio extends MsgViewHolderBase {

    public static final int CLICK_TO_PLAY_AUDIO_DELAY = 500;
    public static int MAX_AUDIO_TIME_SECOND = 120;
    private TextView durationLabel;
    private View containerView;
    private View unreadIndicator;
    private ImageView animationView;
    private ImageView message_item_audio_reload;

    private MessageAudioControl audioControl;
    private MessageAudioControl.AudioControlListener onPlayListener = new MessageAudioControl.AudioControlListener() {

        @Override
        public void updatePlayingProgress(Playable playable, long curPosition) {
            curPosition=curPosition/1000;
            if (curPosition > playable.getDuration()) {
                return;
            }
            if(curPosition==0)
                curPosition=1;
            updateTime(curPosition);
        }

        @Override
        public void onAudioControllerReady(Playable playable) {
            play();
        }

        @Override
        public void onEndPlay(Playable playable) {
            updateTime(playable.getDuration());

            stop();
        }
    };

    public static int getAudioMaxEdge() {
        return (int) (0.6 * ScreenUtil.getScreenMin(IMClient.getInstance().getContext()));
    }

    public static int getAudioMinEdge() {
        return (int) (0.1875 * ScreenUtil.getScreenMin(IMClient.getInstance().getContext()));
    }

    @Override
    protected int getContentResId() {
        return R.layout.im_message_item_audio;
    }

    @Override
    protected void inflateContentView() {
        durationLabel = findViewById(R.id.message_item_audio_duration);
        containerView = findViewById(R.id.message_item_audio_container);
        unreadIndicator = findViewById(R.id.message_item_audio_unread_indicator);
        animationView = findViewById(R.id.message_item_audio_playing_animation);
        message_item_audio_reload = findViewById(R.id.message_item_audio_reload);
        audioControl = MessageAudioControl.getInstance(IMClient.getInstance().getContext());
    }

    @Override
    protected void bindContentView() {
        layoutByDirection();

        refreshStatus();

        controlPlaying();
    }

    @Override
    protected void onItemClick() {
        if (audioControl != null) {
            if (message.getIsListen() != MsgListenEnum.listened.getValue()) {
                // 将未读标识去掉,更新数据库
                unreadIndicator.setVisibility(View.GONE);
            }
            audioControl.startPlayAudioDelay(CLICK_TO_PLAY_AUDIO_DELAY, message, onPlayListener);
            audioControl.setPlayNext(true, adapter, message);
        }
    }

    private void layoutByDirection() {
        context = IMClient.getInstance().getContext();
        if (isReceivedMessage()) {
            setGravity(animationView, Gravity.LEFT | Gravity.CENTER_VERTICAL);
            setGravity(durationLabel, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            containerView.setPadding(ToolsUtils.dip2px(context, 6), ToolsUtils.dip2px(context, 3), ToolsUtils.dip2px(context, 6), ToolsUtils.dip2px(context, 3));
            animationView.setBackgroundResource(R.drawable.im_audio_animation_list_left);
            durationLabel.setTextColor(Color.BLACK);
        } else {
            setGravity(animationView, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            setGravity(durationLabel, Gravity.LEFT | Gravity.CENTER_VERTICAL);
            unreadIndicator.setVisibility(View.GONE);
            containerView.setPadding(ToolsUtils.dip2px(context, 6), ToolsUtils.dip2px(context, 3), ToolsUtils.dip2px(context, 6), ToolsUtils.dip2px(context, 3));
            animationView.setBackgroundResource(R.drawable.im_audio_animation_list_right);
            durationLabel.setTextColor(Color.WHITE);
        }
    }

    private void refreshStatus() {// 消息状态
        IMChatAudioBody attachment = message.getAttachment();
        MsgStatusEnum status = message.getMsgStatus();
        if (isReceivedMessage()) {
            // alert button
            alertButton.setVisibility(View.GONE);
            // progress bar indicator
            progressBar.setVisibility(View.GONE);
            // unread indicator
            unreadIndicator.setVisibility(message.getIsListen() ==MsgListenEnum.listened.getValue() ? View.GONE : View.VISIBLE);
            //reload button
            message_item_audio_reload.setVisibility(TextUtils.isEmpty(attachment.getAttr1())? View.VISIBLE: View.GONE);
        } else {
            // alert button
            alertButton.setVisibility(status == MsgStatusEnum.fail? View.VISIBLE: View.GONE);
            // progress bar indicator
            progressBar.setVisibility(status == MsgStatusEnum.sending? View.VISIBLE: View.GONE);
            // unread indicator
            unreadIndicator.setVisibility(View.GONE);
            //reload button
            message_item_audio_reload.setVisibility(View.GONE);
        }
    }

    private void controlPlaying() {
        final IMChatAudioBody msgAttachment = message.getAttachment();
        long duration = msgAttachment.getAttr2();
        setAudioBubbleWidth(duration);

        if (!isMessagePlaying(audioControl, message)) {
            if (audioControl.getAudioControlListener() != null
                    && audioControl.getAudioControlListener().equals(onPlayListener)) {
                audioControl.changeAudioControlListener(null);
            }

            updateTime(duration);
            stop();
        } else {
            audioControl.changeAudioControlListener(onPlayListener);
            play();
        }
    }

    private void setAudioBubbleWidth(long seconds) {
        int currentBubbleWidth = calculateBubbleWidth(seconds, MAX_AUDIO_TIME_SECOND);
        ViewGroup.LayoutParams layoutParams = containerView.getLayoutParams();
        layoutParams.width = currentBubbleWidth;
        containerView.setLayoutParams(layoutParams);
    }

    private int calculateBubbleWidth(long seconds, int MAX_TIME) {
        int maxAudioBubbleWidth = getAudioMaxEdge();
        int minAudioBubbleWidth = getAudioMinEdge();

        int currentBubbleWidth;
        if (seconds <= 0) {
            currentBubbleWidth = minAudioBubbleWidth;
        } else if (seconds > 0 && seconds <= MAX_TIME) {
            currentBubbleWidth = (int) ((maxAudioBubbleWidth - minAudioBubbleWidth) * (2.0 / Math.PI)
                    * Math.atan(seconds / 10.0) + minAudioBubbleWidth);
        } else {
            currentBubbleWidth = maxAudioBubbleWidth;
        }

        if (currentBubbleWidth < minAudioBubbleWidth) {
            currentBubbleWidth = minAudioBubbleWidth;
        } else if (currentBubbleWidth > maxAudioBubbleWidth) {
            currentBubbleWidth = maxAudioBubbleWidth;
        }

        return currentBubbleWidth;
    }

    private void updateTime(long seconds) {
        if (seconds >= 0) {
            durationLabel.setText(seconds + "\"");
        } else {
            durationLabel.setText("");
        }
    }

    protected boolean isMessagePlaying(MessageAudioControl audioControl, MessageBean message) {
        if (audioControl.getPlayingAudio() != null && audioControl.getPlayingAudio().isTheSame(message)) {
            return true;
        } else {
            return false;
        }
    }

    private void play() {
        if (animationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
            animation.start();
        }
    }

    private void stop() {
        if (animationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
            animation.stop();
            animation.selectDrawable(2);
        }
    }

    @Override
    public void reclaim() {
        super.reclaim();
        if (audioControl.getAudioControlListener() != null
                && audioControl.getAudioControlListener().equals(onPlayListener)) {
            audioControl.changeAudioControlListener(null);
        }
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
