package chat.session.enums;

/**
 * Description:消息状态
 * Created  by: weyko on 2016/4/5.
 */
public enum MsgStatusEnum {
    fail(-1),
    sending(0),
    success(1),
    prepare(2);
    private int value;

    private MsgStatusEnum(int var3) {
        this.value = var3;
    }

    public static MsgStatusEnum fromInt(int var0) {
        MsgStatusEnum[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MsgStatusEnum var4;
            if((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return sending;
    }

    public final int getValue() {
        return this.value;
    }
    public final int getDoneValue() {
        return this.value+1;
    }
}
