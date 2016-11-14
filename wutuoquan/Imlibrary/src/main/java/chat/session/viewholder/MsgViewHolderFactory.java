package chat.session.viewholder;
import java.util.HashMap;

import chat.session.bean.IMChatAudioBody;
import chat.session.bean.IMChatGifBody;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.IMChatLocationBody;
import chat.session.bean.IMChatTipBody;
import chat.session.bean.IMChatVideoBody;
import chat.session.bean.ImAttachment;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgTypeEnum;

/**
 * 消息项展示ViewHolder工厂类。
 */
public class MsgViewHolderFactory {

    private static HashMap<Class<? extends ImAttachment>, Class<? extends MsgViewHolderBase>> viewHolders = new HashMap<>();

    private static Class<? extends MsgViewHolderBase> tipMsgViewHolder;

    static {
        // built in
        register(IMChatGifBody.class, MsgViewHolderGif.class);
        register(IMChatImageBody.class, MsgViewHolderPicture.class);
        register(IMChatAudioBody.class, MsgViewHolderAudio.class);
        register(IMChatVideoBody.class, MsgViewHolderVideo.class);
        register(IMChatLocationBody.class, MsgViewHolderLocation.class);
        register(IMChatTipBody.class, MsgViewHolderNotification.class);
    }

    public static void register(Class<? extends ImAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        viewHolders.put(attach, viewHolder);
    }

    public static void registerTipMsgViewHolder(Class<? extends MsgViewHolderBase> viewHolder) {
        tipMsgViewHolder = viewHolder;
    }

    public static Class<? extends MsgViewHolderBase> getViewHolderByType(MessageBean message) {

        if (message.getMsgType() == MsgTypeEnum.text) {
            return MsgViewHolderText.class;
        } else if (message.getMsgType() == MsgTypeEnum.tip) {
            return MsgViewHolderNotification.class;
        } else {
            Class<? extends MsgViewHolderBase> viewHolder = null;
            if (message.getAttachment() != null) {
                Class<? extends ImAttachment> clazz = message.getAttachment().getClass();
                while (viewHolder == null && clazz != null) {
                    viewHolder = viewHolders.get(clazz);
                    if (viewHolder == null) {
                        clazz = getSuperClass(clazz);
                    }
                }
            }
            return viewHolder == null ? MsgViewHolderUnknown.class : viewHolder;
        }
    }

    public static int getViewTypeCount() {
        // plus text and unknown
        return viewHolders.size() + 2;
    }

    public static Class<? extends ImAttachment> getSuperClass(Class<? extends ImAttachment> derived) {
        Class sup = derived.getSuperclass();
        if (sup != null && ImAttachment.class.isAssignableFrom(sup)) {
            return sup;
        } else {
            for (Class itf : derived.getInterfaces()) {
                if (ImAttachment.class.isAssignableFrom(itf)) {
                    return itf;
                }
            }
        }
        return null;
    }
}
