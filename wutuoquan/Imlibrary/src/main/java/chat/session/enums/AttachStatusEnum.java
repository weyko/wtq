package chat.session.enums;

/**
 * Description: 转换状态
 * Created  by: weyko on 2016/4/5.
 */
public enum  AttachStatusEnum {
    def(0),
    transferring(1),
    transferred(2),
    fail(3);

    private int value;

    private AttachStatusEnum(int var3) {
        this.value = var3;
    }

    public static AttachStatusEnum statusOfValue(int var0) {
        AttachStatusEnum[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            AttachStatusEnum var4;
            if((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return def;
    }

    public final int getValue() {
        return this.value;
    }
}
