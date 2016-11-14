package chat.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.BaseActivity;
import web.CommonWebActivity;
/**
 * 
* @Description: 扫描结果处理
 */
public class QRCodeResultActivity extends BaseActivity implements OnClickListener{
	private ImageView back;
	private TextView qr_result;
	private TextView titleText;
	private TextView qr_result_close;
	private TextView qr_result_open;
	private String result=null;
	private boolean errorToback=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!checkError()){
			setContentView(R.layout.activity_qrcode_result);
			initEvents();
			initData();
		}
	}
	
	private void getIntentParams(){
		Intent intent=getIntent();
		if(intent!=null){
			result = intent.getStringExtra(QRCodeActivity.QR_CODE_RESULT);
			if(result==null||result.length()<=0){
				errorToback = true;
			}
		}else{
			errorToback = true;
		}
	}
	
	private boolean checkError(){
		getIntentParams();
		if(errorToback){
			doFinish();
			return true;
		}
		if(result.startsWith("http://www.moxian.com")||result.startsWith("http://setting.moxian.com")){
			openWeb();
			doFinish();
			return true;
		}
		return false;
	}
	
	@Override
	protected void initView() {
		back = (ImageView)findViewById(R.id.back);
		qr_result = (TextView)findViewById(R.id.qr_result);
		titleText = (TextView)findViewById(R.id.titleText);
		qr_result_close = (TextView)findViewById(R.id.qr_result_close);
		qr_result_open = (TextView)findViewById(R.id.qr_result_open);
	}

	@Override
	protected void initData(){
		titleText.setText(getText(R.string.qrcode_result));
		if(result!=null&&result.length()>0){
			qr_result.setText(result);
			if(result.startsWith("http://")||result.startsWith("https://")){
				qr_result_open.setVisibility(View.VISIBLE);
			}else{
				qr_result_open.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	protected void initEvents() {
		back.setOnClickListener(this);
		qr_result_close.setOnClickListener(this);
		qr_result_open.setOnClickListener(this);
	}

	private void doFinish(){
		finish();
	}
	
	private void openWeb(){
		Intent intent = new Intent();
		intent.setClass(this, CommonWebActivity.class);
		intent.putExtra(CommonWebActivity.WEB_URL, result);
		intent.putExtra(CommonWebActivity.WEB_TITLE, getText(R.string.qrcode_result));
		startActivity(intent);
	}
	
	@Override
	public void onClick(View arg0) {
		int i = arg0.getId();
		if (i == R.id.back || i == R.id.qr_result_close) {
			doFinish();

		} else if (i == R.id.qr_result_open) {
			openWeb();

		} else {
		}
	}
}