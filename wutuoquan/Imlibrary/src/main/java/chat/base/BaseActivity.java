package chat.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.imlibrary.R;

import chat.common.util.TextUtils;
import chat.common.util.output.Toastor;
import chat.dialog.DialogLoading;

public abstract class BaseActivity extends FragmentActivity {
    public Toastor mToastor = null;
    /**
     * 加载对话框
     */
    protected DialogLoading mLoadingDialog = null;
    /**
     * 软键盘管理
     */
    private InputMethodManager inputManager;

    protected abstract void initView();

    protected abstract void initEvents();

    protected abstract void initData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        mLoadingDialog = new DialogLoading(this);
        IMClient.getInstance().addActivity(this);
        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private void setWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        initEvents();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initEvents();
        initData();
    }

    /**
     * 针对接口请求后异常提示
     *
     * @param codeStr Bean里面的code
     */
    public void showResutToast(String codeStr) {
        int rInt = 0;
        if (TextUtils.getString(codeStr).length() > 0) {
            rInt = this.getResources().getIdentifier(codeStr, "string",
                    this.getPackageName());
        }
        if (rInt == 0) {
            rInt = R.string.server_string_null;
        }
        if (mToastor != null) {
            mToastor.showToast(rInt);
        }
    }

    public void showLoading() {
        if (null != mLoadingDialog) {
            mLoadingDialog.show();
        }
    }

    public void showLoading(String msg) {
        if (null != mLoadingDialog) {
            mLoadingDialog.show(msg);
        }
    }

    public void dissmisLoading() {
        if (null != mLoadingDialog) {
            mLoadingDialog.diss();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mLoadingDialog) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        IMClient.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /**
     * 隐藏屏幕上显示的软键盘
     *
     * @param v
     */
    public void hiddenSoftInput(View v) {
        if (v != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void setTitleView(RelativeLayout rootView) {
        if (rootView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rootView.setFitsSystemWindows(true);
            }
        }
    }
    protected boolean isCompatible(int apiLevel) {
        return android.os.Build.VERSION.SDK_INT >= apiLevel;
    }
}
