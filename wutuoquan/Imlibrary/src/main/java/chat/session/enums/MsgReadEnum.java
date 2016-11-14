package chat.session.enums;

/**
 * Description:消息是否已读
 * Created  by: weyko on 2016/4/5.
 */
public enum MsgReadEnum {
    unread(0),
    readed(1);

    private int value;

    private MsgReadEnum(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static MsgReadEnum fromInt(int var0) {
        MsgReadEnum[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MsgReadEnum var4;
            if((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return unread;
    }
}
