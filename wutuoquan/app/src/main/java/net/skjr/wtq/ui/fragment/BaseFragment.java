
package net.skjr.wtq.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyApp;
import net.skjr.wtq.core.rx.RxManager;
import net.skjr.wtq.core.utils.ui.ToastUtils;
import net.skjr.wtq.ui.activity.system.MainActivity;

import rx.Subscription;


/**
 * BaseFragment
 */
public class BaseFragment extends Fragment {

    private RxManager rxManager;
    public MainActivity mActivity ;

    protected MyApp getMyApp() {
        return AppController.getInstance().getMyApp();
    }

    /**
     * 显示Toast
     *
     * @param message
     */
    protected void showToast(String message) {
        ToastUtils.show(getActivity(), message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onPause() {
        super.onPause();

        AppController.activityPaused();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppController.activityResumed();
    }

    @Override
    public void onDestroy() {
        //取消rx订阅，如果有的话
        if(rxManager != null)
            rxManager.unsubscribe();

        super.onDestroy();
    }

    /**
     * 启动一个Activity
     *
     * @param activity
     */
    protected void startActivity(Class<? extends Activity> activity) {
        startActivity(new Intent(getActivity(), activity));
    }

    /**
     * 添加Rx订阅
     *
     * @param subscription
     */
    protected synchronized void addSubscription(Subscription subscription) {
        if(rxManager == null)
            rxManager = new RxManager();

        rxManager.addSubscription(subscription);
    }
}
