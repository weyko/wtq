package chat.session.enums;

/**
 * Description: 会话类型
 * Created  by: weyko on 2016/4/5.
 */
public enum  SessionTypeEnum {
    None(-1),
    /** 基本消息 */
    NORMAL(0),
    /** 群消息 */
    GROUPCHAT(1),
    /** 富消息 */
    RICH(2),
    /** 销毁消息 */
    DESTROY(3),
    /** 群系统消息 */
    SGROUP(4),
    /** 推送系统消息 */
    SS(5),
    /** 单系统消息 */
    S(6),
    /** 关注 **/
    FOLLOW(7),
    /**新朋友*/
    FRIEND(8);
    private int value;

    private SessionTypeEnum(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static SessionTypeEnum fromInt(int value) {
        SessionTypeEnum[] sessionTypes;
        int size = (sessionTypes = values()).length;
        for(int i = 0; i < size; ++i) {
            SessionTypeEnum var4;
            if((var4 = sessionTypes[i]).getValue() == value) {
                return var4;
            }
        }
        return NORMAL;
    }
}
