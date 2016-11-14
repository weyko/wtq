package net.skjr.wtq.common;

/**
 * 从那个界面跳转而来
 * 用于某个流程被登录动作等打断后，再恢复的地点类型
 * 如：当切换到账户标签页时，如果没有登录，那么弹出登录窗，登录完成后，再跳回到账户标签页
 */
public class FromType {
    public static final int NONE = -1;

    public static final int MAIN = 99;

    public static final int TAB_MAIN = 0;

    public static final int TAB_CUSTOM = 1;

    public static final int TAB_ACCOUNT = 2;

    public static final int TAB_MORE = 3;

    public static final int LOGIN = 11;

    public static final int REGISTER = 12;

    /**
     * 修改手势界面
     */
    public static final int MODIFY_GESUTURE = 21;

    /**
     * 手势登录界面
     */
    public static final int UNLOCK_GESTURE = 22;

}
