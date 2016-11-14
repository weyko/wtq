package net.skjr.wtq.ui.activity.system;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.application.MyPreference;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.ui.h5.H5Action;
import net.skjr.wtq.ui.h5.H5Excutor;
import net.skjr.wtq.ui.h5.H5Parser;
import net.skjr.wtq.ui.h5.WebViewUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 常规Web窗口
 */
public class WebActivity extends BaseToolbarActivity implements View.OnClickListener {

    private static final String TITLE_KEY = "title";
    private static final String URL_KEY = "url";
    private static final String LAYOUT = "layout";

    private String title;
    private String url;
    private int mLayout = R.layout.activity_web;

    private FrameLayout mWebContainer;
    private WebView webView;
    private ProgressBar progressBar;
    private TextView errorView;
    private boolean needClearHistory;
    private TextView mShop_car_edit;

    /**
     * 帮助方法，供外部调用打开Webview界面
     *
     * @param context
     * @param title
     * @param url
     */
    public static void open(Context context, String title, String url) {
        if (TextUtils.isEmpty(url))
            return;

        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(TITLE_KEY, title);
        intent.putExtra(URL_KEY, url);
        context.startActivity(intent);
    }
    /**
     * 帮助方法，供外部调用打开Webview界面
     *
     * @param context
     * @param title
     * @param url
     */
    public static void open(Context context, String title, String url, int layout) {
        if (TextUtils.isEmpty(url))
            return;

        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(TITLE_KEY, title);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(LAYOUT, layout);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        title = intent.getStringExtra(TITLE_KEY);
        url = intent.getStringExtra(URL_KEY);
        mLayout = intent.getIntExtra(LAYOUT, 0);
        if(mLayout == 0) {
            setContentView(R.layout.activity_web);
        } else {
            setContentView(mLayout);
        }
        initToolbar(title);

        findViews();
        initViews();

    }

    @Override
    public void onDestroy() {
        mWebContainer.removeAllViews();

        WebViewUtils.destroyWebView(this, webView);

        super.onDestroy();
    }

    // handling back button
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void findViews() {
        mWebContainer = (FrameLayout) findViewById(R.id.web_container);
        webView = (WebView) findViewById(R.id.webView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorView = (TextView) findViewById(R.id.errorView);
//        mShop_car_edit = (TextView) findViewById(R.id.shop_car_edit);
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                needClearHistory = true;
                errorView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                loadUrl();
            }
        });
        if(mShop_car_edit != null) {
            mShop_car_edit.setOnClickListener(this);
        }
    }

    private void initViews() {
        WebViewUtils.initWebView(webView);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }

                super.onProgressChanged(view, progress);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!H5Parser.isH5Call(url)) {
                    Log.e("action_h5",url);
                    Map<String,String> map = new HashMap<>();
                    map.put("token", MyPreference.getToken());

                    webView.loadUrl(url, map);
                } else {
                    Log.e("action_local",url);
                    H5Action action = H5Parser.parseUrl(url);
                    H5Excutor.excuteActionInActivity(WebActivity.this, action, webView);
                }
              return true;
            }


            @Override   //转向错误时的处理
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                view.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                errorView.setVisibility(View.VISIBLE);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

                if (needClearHistory) {
                    needClearHistory = false;
                    webView.clearHistory();//清除历史记录
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("onPageStarted",url);
                if(url.equals(Url.PAYBACK)) {

                    H5Action action = new H5Action();
                    action.code = H5Action.PAYMENT;
                    H5Excutor.excuteActionInActivity(WebActivity.this, action, webView);
                    return;

                } else if(url.equals(Url.RECHARGEBACK)) {

                    H5Action action = new H5Action();
                    action.code = H5Action.RECHARGE;
                    H5Excutor.excuteActionInActivity(WebActivity.this, action, webView);
                    return;
                } else {
                    super.onPageStarted(view, url, favicon);
                }
            }
        });

        loadUrl();
    }

    private void loadUrl() {
        //加载URL，同时传递header
        webView.loadUrl(url, WebViewUtils.getHeaderMap());
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.shop_car_edit:
//                if(mShop_car_edit.getText().equals("编辑")) {
//                    mShop_car_edit.setText("完成");
//                } else {
//                    mShop_car_edit.setText("编辑");
//                }
//                break;
//        }
    }
}
