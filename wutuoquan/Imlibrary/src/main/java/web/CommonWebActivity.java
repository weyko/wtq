package web;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.imlibrary.R;

import java.io.File;

import chat.common.util.ToolsUtils;
import chat.dialog.CustomBaseDialog;
import chat.view.ProgressWebView;

import static com.imlibrary.R.style.BaseDialog;

/**
* @Description: 通用内置浏览器类
*
 */
public class CommonWebActivity extends FragmentActivity implements
		OnClickListener{
	public static final String WEB_TITLE="TITLE";
	public static final String WEB_URL="URL";
	public static final String IS_REGISTER_AGREEMENT = "is_register_agreement";
	private ProgressWebView browser=null;
	private String url = null;
	private String title = null;
	private TextView mTitleTv;
	private ImageView mBackTv;
	private RelativeLayout contentView=null;
	private boolean openDownLoad=false;
	private CustomBaseDialog dialog=null;
	private boolean isRegisterAgreement = true;
	//private String curUrl=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentParams();
		contentView = (RelativeLayout)getLayoutInflater().inflate(R.layout.activity_web, null);
		setContentView(contentView);
		initView();
		initEvents();
		initData();
	}
	
	
	
	private void getIntentParams(){
		Intent intent=getIntent();
		if(intent!=null){
			url = intent.getStringExtra(WEB_URL);
			title = intent.getStringExtra(WEB_TITLE);
			isRegisterAgreement = intent.getBooleanExtra(IS_REGISTER_AGREEMENT, true);
			if(url==null||url.length()<=0){
				doFinish();
				return;
			}
		}else{
			doFinish();
			return;
		}
	}
	
	private void doFinish(){
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(contentView!=null&&browser!=null){
			contentView.removeView(browser);
			//browser.clearCache(true);
			browser.removeAllViews();
			browser.destroy();
		}
		System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();
		browser.reload();
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		browser.stopLoading();
	}

	protected void initView() {
		browser = (ProgressWebView) findViewById(R.id.webview);
		mTitleTv = (TextView) findViewById(R.id.titleText);
		mBackTv = (ImageView) findViewById(R.id.back);
	}

	protected void initEvents() {
		mBackTv.setOnClickListener(this);
		if(title!=null&&title.length()>0){
			mTitleTv.setText(title);
		}
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//curUrl = url;
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
	}

	//内部类  
	private class WebViewDownLoadListener implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent,
									String contentDisposition, String mimetype, long contentLength) {
				Uri uri = Uri.parse(url);
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	            startActivity(intent); 
	            CommonWebActivity.this.finish();
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	protected void initData() {
		if(title!=null&&title.length()>0){
			mTitleTv.setText(title);
		}else{
			mTitleTv.setText("");
		}
		WebSettings setting = browser.getSettings();
		if (setting != null) {
			setting.setJavaScriptEnabled(true);//支持js
			if(!isRegisterAgreement && ToolsUtils.isNetworkAvailable(this)){
				setting.setUseWideViewPort(true);
				setting.setLoadWithOverviewMode(true);
				setting.setAppCacheEnabled(true);
				setting.setLoadsImagesAutomatically(true);//支持自动加载图片
				setting.setSupportZoom(true);
				setting.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
				setting.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
				setting.setBuiltInZoomControls(true);
				setting.setDisplayZoomControls(false);
				browser.requestFocusFromTouch();
			}
		}
		if (url!=null && url.length()>0) {
			browser.loadUrl(url);
		}
		browser.setDownloadListener(new WebViewDownLoadListener());
	}

	@Override
	public void onClick(View v) {
		if (browser.canGoBack()) {
			browser.goBack();
		} else {
			doFinish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (browser.canGoBack()){
				browser.goBack();
				return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private void showDialog(final String filePath){
		if (dialog==null){
			dialog = CustomBaseDialog.getDialog(this, getString(R.string.open_file_tip), getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dissDialog();
				}
			}, getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openFile(filePath);
					dissDialog();
				}
			});
		}
		dialog.show();
	}
	
	private void dissDialog(){
		if (dialog!=null){
			dialog.dismiss();
			dialog = null;
		}
		this.finish();
	}
	
	private void openFile(String filePath){
		File file = new File(filePath);
		Intent intent = DownLoaderUtils.getFileIntent(file);
		startActivity(intent);
	}
}