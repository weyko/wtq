package chat.session.enums;

/**
 * Description:消息收发方向
 * Created  by: weyko on 2016/4/5.
 */
public enum MsgDirectionEnum {
    Out(0),
    In(1);

    private int value;

    private MsgDirectionEnum(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static MsgDirectionEnum fromInt(int var0) {
        MsgDirectionEnum[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MsgDirectionEnum var4;
            if((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return Out;
    }
}
