package chat.common.util.sys;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import chat.common.util.ToolsUtils;

/**
 * Description: 参考网上的方案，处理全屏模式下软键盘弹出的布局高度问题
 * Created  by: weyko on 2016/5/24.
 */
public class FullScreenUtil {

    private static FullScreenUtil instance;
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private FragmentActivity activity;

    public FullScreenUtil(FragmentActivity activity) {
        this.activity = activity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//兼容沉浸式状态栏设计
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent();
                }
            });
            frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        }
    }

    private static FullScreenUtil getInstance(FragmentActivity activity) {
        if (instance == null) {
            instance = new FullScreenUtil(activity);
        }
        return instance;
    }

    public static FullScreenUtil init(FragmentActivity activity) {
        return getInstance(activity);
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
//            if (heightDifference > (usableHeightSansKeyboard / 4)) {
//                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//            } else {
//                frameLayoutParams.height = usableHeightSansKeyboard;
//            }
            frameLayoutParams.height = usableHeightSansKeyboard - heightDifference+ ToolsUtils.getStatusBarHeight(activity);
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (instance != null) {
            activity = null;
            instance = null;
        }
    }
}
