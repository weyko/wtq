package net.skjr.wtq.ui.h5;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.utils.CommonUtils;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.PhoneUtils;
import net.skjr.wtq.core.utils.common.StringUtils;
import net.skjr.wtq.ui.activity.system.BaseToolbarActivity;
import net.skjr.wtq.ui.activity.system.MainActivity;
import net.skjr.wtq.ui.activity.system.WebActivity;
import net.skjr.wtq.ui.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * 执行H5Action
 */
public class H5Excutor {
    public static void excuteActionInActivity(WebActivity activity, H5Action action, WebView webView) {
        if (activity == null || action == null)
            return;

        //先处理关闭当前界面的指令
        if (StringUtils.isEquals(action.code, H5Action.CLOSEPAGE)) {
            activity.finish();

            return;
        }

        excuteAction(activity, action, webView, false);
    }


    public static void excuteActionInFragment(BaseFragment fragment, H5Action action, WebView webView) {
        if (fragment == null || action == null)
            return;

        Activity activity = fragment.getActivity();
        if (!(activity instanceof BaseToolbarActivity)) {
            L.d("H5Excutor--->非BaseToolbarActivity");
            return;
        }

        WebActivity baseActivity = (WebActivity) activity;

        if (StringUtils.isEquals(action.code, H5Action.CLOSEPAGE)) {
            //fragment不处理

            return;
        }

        excuteAction(baseActivity, action, webView, true);
    }

    private static void excuteAction(WebActivity activity, H5Action action, WebView webView, boolean isFragment) {
        String code = action.code;

        //拨打电话
        if (StringUtils.isEquals(code, H5Action.CALLMOBILE)) {

            //TODO:6.0需要检查权限
            PhoneUtils.call(activity, action.phone);
            return;
        }

        //ajax
        if (StringUtils.isEquals(code, H5Action.AJAX)) {


            return;
        }

        //showProgress，转圈
        if (StringUtils.isEquals(code, H5Action.SHOWPROGRESS)) {
            activity.showProgressDialog();
            return;
        }

        //hideProgress，停止转圈
        if (StringUtils.isEquals(code, H5Action.HIDEPROGRESS)) {
            activity.dismissProgressDialog();
            return;
        }

        //showToast，toast
        if (StringUtils.isEquals(code, H5Action.SHOWTOAST)) {
            activity.showToast(action.msg);
            return;
        }
        /**
         * 连连支付
         */
        if(StringUtils.isEquals(code, H5Action.PAYMENT)) {
            activity.finish();
            /*Intent intent = new Intent(activity, SuccessActivity.class);
            intent.putExtra("successtext","支付成功");
            intent.putExtra("toolbartext","支付");
            activity.startActivity(intent);*/

            return;
        }

        if(StringUtils.isEquals(code, H5Action.RECHARGE)) {
            activity.finish();
            /*Intent intent = new Intent(activity, SuccessActivity.class);
            intent.putExtra("successtext","充值成功");
            intent.putExtra("toolbartext","充值");
            activity.startActivity(intent);*/
            EventBus.getDefault().post(new Event.RechargeCompleteEvent());
            return;
        }



        //gotoHome，跳转到首页
        if (StringUtils.isEquals(code, H5Action.GOTOHOME)) {
            //clear stack
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);

            //open home tab
            EventBus.getDefault().post(new Event.OpenMainTabEvent(0));

            if (action.close) {
                activity.finish();
            }

            return;
        }

        //popMsg，弹出对话框
        if (StringUtils.isEquals(code, H5Action.POPMSG)) {
            //0602:客户会先hide,再马上pop,这样会导致hide的location被pop覆盖,所以会导致只有最后的pop传递过来
            //为了解决这个问题,在pop前主动hide一次
            activity.dismissProgressDialog();

            processPopMsg(action.dataId, action.msg, action.type, activity, webView);
            return;
        }

        //-----------------popPage----------------------

        if (StringUtils.isEquals(code, H5Action.POPPAGE_GENERAL)) {
            openGeneralWebActivity(activity, action, webView, isFragment);

            return;
        }

    }

    /**
     * 处理popMsg，type:1确认 2确认取消
     *
     * @param msg
     * @param type
     */
    private static void processPopMsg(String dataId, String msg, int type, BaseToolbarActivity activity, final WebView webView) {
        final String okUrl = "javascript:appCallBack('popMsg','" +dataId +  "','ok');";
        final String cancelUrl = "javascript:appCallBack('popMsg','" +dataId +  "','cancel');";

        if (type == 1) {
            CommonUtils.showDialog(activity, msg, "确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ok
                    webView.loadUrl(okUrl);
                }
            });

            return;
        }

        if (type == 2) {
            CommonUtils.showDialog(activity, msg, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ok
                    webView.loadUrl(okUrl);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //cancel
                    webView.loadUrl(cancelUrl);
                }
            },"取消","确认");
        }
    }

    /**
     * 打开通用的WebActivity
     *
     * @param activity
     * @param action
     */
    private static void openGeneralWebActivity(BaseToolbarActivity activity, H5Action action, WebView webView, boolean isFragment) {
        String title = action.title;
        String fullUrl = action.url;

        //1:不关闭当前窗口，新开窗口；2：关闭当前窗口，新开窗口；3：在当前窗口打
        int type = action.closeType;

        L.d("H5Excutor.openGeneralWebActivity: title=" + title + ",url=" + fullUrl + ",type=" + type);
        switch (type) {
            case 1:
                WebActivity.open(activity, title, fullUrl);
                break;
            case 2:
                WebActivity.open(activity, title, fullUrl);
                activity.finish();
                break;
            case 3:
                if (TextUtils.isEmpty(fullUrl))
                    return;

                if (!isFragment) {
                    if (!TextUtils.isEmpty(title))
                        activity.setToolbarTitle(title);
                }

                webView.loadUrl(fullUrl, WebViewUtils.getHeaderMap());
                break;
            default:
                WebActivity.open(activity, title, fullUrl);
                break;
        }
    }
}
