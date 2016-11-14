package chat.session.enums;

import android.util.SparseArray;

/**
 * Description: 消息类型
 * Created  by: weyko on 2016/4/5.
 */
public enum MsgTypeEnum {
    /**
     * 发送的基本消息类型:1 文本 2 图片 3 语音 4 位置 5 gif表情 6文件 7 视频 8公告 9券
     */
    undef(-1, "Unknown"),
    text(1, "文本"),
    image(2, "图片"),
    audio(3, "语音"),
    location(4, "位置"),
    gif(5, "gif表情"),
    file(6, "文件"),
    video(7, "视频"),
    notification(8, "公告"),
    ticket(9, "券"),
    avchat(11, "音视频通话"),
    tip(10, "提醒消息"),
    custom(100, "自定义消息");

    private static SparseArray<MsgTypeEnum> map = new SparseArray<MsgTypeEnum>();

    static {
        for (MsgTypeEnum item : MsgTypeEnum.values()) {
            map.append(item.getValue(), item);
        }
    }

    final String sendMessageTip;
    private final int value;

    private MsgTypeEnum(int value, String sendMessageTip) {
        this.value = value;
        this.sendMessageTip = sendMessageTip;
    }

    public final int getValue() {
        return this.value;
    }

    public final String getSendMessageTip() {
        return this.sendMessageTip;
    }

    public static MsgTypeEnum fromInt(int value) {
        return MsgTypeEnum.map.get(value);
    }
}
