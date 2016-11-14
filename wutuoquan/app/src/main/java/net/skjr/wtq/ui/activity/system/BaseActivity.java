package net.skjr.wtq.ui.activity.system;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.umeng.analytics.MobclickAgent;

import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyApp;
import net.skjr.wtq.core.rx.RxManager;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.ui.ToastUtils;

import rx.Subscription;


/**
 * BaseActivity
 */
public class BaseActivity extends AppCompatActivity {

    protected KProgressHUD hud;
    private RxManager rxManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    protected void setStatusBar() {
        //袋鼠贷设计成白色工具栏,如果再开启通知栏透明,会导致通知栏的白色字体看不到,所以取消
        //StatusBarUtils.setColor(this, getResources().getColor(R.color.actionbar_color));

        //StatusBarUtils.setTranslucent(this);
    }

    /**
     * 获取当前Activity的名称
     *
     * @return
     */
    protected String getCurrentActivityName(){
        String contextString = this.toString();
        String name = "";
        try {
            name = contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
        }catch (Exception ex){
            L.e(ex.getLocalizedMessage());
        }

        return name;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //JPushInterface.onResume(this);

        //友盟统计分析API
        MobclickAgent.onResume(this);

        AppController.getInstance().currentActivity = getCurrentActivityName();
        AppController.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //JPushInterface.onPause(this);

        //友盟统计分析API
        MobclickAgent.onPause(this);

        AppController.activityPaused();
    }

    @Override
    public void onDestroy() {

        //退出时需要销毁进度框，如果有的话
        dismissProgressDialog();

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
        startActivity(new Intent(this, activity));
    }


    /**
     * 添加Rx订阅
     *
     * @param subscription
     */
    protected void addSubscription(Subscription subscription) {
        if(rxManager == null)
            rxManager = new RxManager();

        rxManager.addSubscription(subscription);
    }

    protected MyApp getMyApp() {
        return AppController.getInstance().getMyApp();
    }

    /**
     * 显示Toast
     *
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.show(getApplicationContext(), message);
    }

    /**
     * 显示弹出式提示框
     *
     * @param message
     */
    protected void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {


                   }
               })
               .show();
    }

    public void showProgressDialog() {
        showProgressDialog("");
    }

    /**
     * 显示进度框
     *
     * @param message 显示的提示文字
     */
    public void showProgressDialog(String message) {
        if (hud == null) {
            hud = KProgressHUD.create(this)
                              .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                              .setCancellable(true);
        }

        if (!TextUtils.isEmpty(message)) {
            hud.setLabel(message);
        }

        hud.show();
    }

    public void dismissProgressDialog() {
        if (hud != null)
            hud.dismiss();
    }


}
