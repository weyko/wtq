package chat.session.audio;

import android.content.Context;
import android.widget.Toast;

import com.imlibrary.R;

import java.util.List;

import chat.common.util.storage.StorageUtil;
import chat.manager.ChatMessageManager;
import chat.media.BaseAudioControl;
import chat.media.Playable;
import chat.session.adapter.common.ImAdapter;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgListenEnum;
import chat.session.enums.MsgTypeEnum;

public class MessageAudioControl extends BaseAudioControl<MessageBean> {
    private static MessageAudioControl mMessageAudioControl = null;

    private boolean mIsNeedPlayNext = false;

    private ImAdapter mAdapter = null;

    private MessageBean mItem = null;

    private MessageAudioControl(Context context) {
        super(context, true);
    }

    public static MessageAudioControl getInstance(Context context) {
        if (mMessageAudioControl == null) {
            synchronized (MessageAudioControl.class) {
                if (mMessageAudioControl == null) {
                    mMessageAudioControl = new MessageAudioControl(context);
                }
            }
        }

        return mMessageAudioControl;
    }

    @Override
    protected void setOnPlayListener(Playable playingPlayable, BaseAudioControl.AudioControlListener audioControlListener) {
        this.audioControlListener = audioControlListener;

        BasePlayerListener basePlayerListener = new BasePlayerListener(currentAudioPlayer, playingPlayable) {

            @Override
            public void onInterrupt() {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                super.onInterrupt();
                cancelPlayNext();
            }

            @Override
            public void onError(String error) {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                super.onError(error);
                cancelPlayNext();
            }

            @Override
            public void onCompletion() {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                resetAudioController(listenerPlayingPlayable);

                boolean isLoop = false;
                if (mIsNeedPlayNext) {
                    if (mAdapter != null && mItem != null) {
                        isLoop = playNextAudio(mAdapter, mItem);
                    }
                }

                if (!isLoop) {
                    if (audioControlListener != null) {
                        audioControlListener.onEndPlay(currentPlayable);
                    }

//                    playSuffix();
                }
            }
        };

        basePlayerListener.setAudioControlListener(audioControlListener);
        currentAudioPlayer.setOnPlayListener(basePlayerListener);
    }

    @Override
    public MessageBean getPlayingAudio() {
        if (isPlayingAudio() && AudioMessagePlayable.class.isInstance(currentPlayable)) {
            return ((AudioMessagePlayable) currentPlayable).getMessage();
        } else {
            return null;
        }
    }

    @Override
    public void startPlayAudioDelay(long delayMillis,MessageBean message,AudioControlListener audioControlListener, int audioStreamType) {
        startPlayAudio(message, audioControlListener, audioStreamType, true, delayMillis);
    }

    //连续播放时不需要resetOrigAudioStreamType
    private void startPlayAudio( MessageBean message,AudioControlListener audioControlListener,
            int audioStreamType,boolean resetOrigAudioStreamType,long delayMillis) {
        if (StorageUtil.isExternalStorageExist()) {
            if (startAudio(new AudioMessagePlayable(message), audioControlListener, audioStreamType, resetOrigAudioStreamType, delayMillis)) {
                // 将未读标识去掉,更新数据库
                if (isUnreadAudioMessage(message)) {
                    message.setIsListen(MsgListenEnum.listened.getValue());
                }
            }
        } else {
            Toast.makeText(mContext, R.string.sdcard_not_exist_error, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean playNextAudio(ImAdapter tAdapter, MessageBean messageItem) {
        List<?> list = tAdapter.getItems();
        int index = 0;
        int nextIndex = -1;
        //找到当前已经播放的
        for (int i = 0; i < list.size(); ++i) {
            MessageBean item = (MessageBean) list.get(i);
            if (item.equals(messageItem)) {
                index = i;
                break;
            }
        }
        //找到下一个将要播放的
        for (int i = index; i < list.size(); ++i) {
            MessageBean item = (MessageBean) list.get(i);
            MessageBean message = item;
            if (isUnreadAudioMessage(message)) {
                nextIndex = i;
                break;
            }
        }

        if (nextIndex == -1) {
            cancelPlayNext();
            return false;
        }
        MessageBean message = (MessageBean) list.get(nextIndex);
        IMChatAudioBody attach = message.getAttachment();
        if (mMessageAudioControl != null && attach != null) {
            if (message.getIsListen() != 1) {
                cancelPlayNext();
                return false;
            }
            //更新语音收听状态
			if (message.getIsListen() != MsgListenEnum.listened.getValue()) {
                message.setIsListen(MsgListenEnum.listened.getValue());
                ChatMessageManager.getInstance().updateMessageListenStatus(message.getSessionId(),"",message.getMsgCode());
			}
            //不是直接通过点击ViewHolder开始的播放，不设置AudioControlListener
            //notifyDataSetChanged会触发ViewHolder刷新，对应的ViewHolder会把AudioControlListener设置上去
            //连续播放 1.继续使用playingAudioStreamType 2.不需要resetOrigAudioStreamType
            mMessageAudioControl.startPlayAudio(message, null, getCurrentAudioStreamType(), false, 0);
            mItem = (MessageBean) list.get(nextIndex);
            tAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private void cancelPlayNext() {
        setPlayNext(false, null, null);
    }

    public void setPlayNext(boolean isPlayNext, ImAdapter adapter, MessageBean item) {
        mIsNeedPlayNext = isPlayNext;
        mAdapter = adapter;
        mItem = item;
    }

    public void stopAudio() {
        super.stopAudio();
    }

    public boolean isUnreadAudioMessage(MessageBean message) {
        if ((message.getMsgType() == MsgTypeEnum.audio)
                && message.getDirect() == MsgDirectionEnum.In
                && message.getIsListen() != MsgListenEnum.listened.getValue()) {
            return true;
        } else {
            return false;
        }
    }
}
