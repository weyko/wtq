package chat.session.enums;

/**
 * Description:消息是否已读
 * Created  by: weyko on 2016/4/5.
 */
public enum MsgListenEnum {
    unlisten(0),
    listened(1);

    private int value;

    private MsgListenEnum(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static MsgListenEnum fromInt(int var0) {
        MsgListenEnum[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MsgListenEnum var4;
            if((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return unlisten;
    }
}
