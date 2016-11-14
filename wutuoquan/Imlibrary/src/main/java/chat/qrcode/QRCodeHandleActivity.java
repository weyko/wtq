package chat.qrcode;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;

import chat.base.BaseActivity;
import chat.dialog.CustomBaseDialog;
import chat.image.activity.PhotoWallActivity;

public class QRCodeHandleActivity extends BaseActivity implements QRCodeObserver {
	/**扫一扫结果 key值  通过Intent向外部发送识别结果*/
	public static final String QR_CODE_RESULT="scan_result"; 
	private ImageView back;
	/**扫描内置图片View*/
	private ImageView scanImageView;
	private TextView handle_tv;
	private Bitmap scanImage;
	private String content=null;
	private boolean startResult=false;
	private boolean scanPhoto=false;
	private CustomBaseDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentParams();
		setContentView(R.layout.activity_qrcode_handle);
		initEvents();
		initData();
	}
	protected void initData(){
		if (scanPhoto){
			selectPhotos();
		}
		dialog = CustomBaseDialog.getDialog(this, getString(R.string.qrcode_handle_no_content),
				getString(R.string.ok),new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				setResult(RESULT_OK);
				finish();
				
			}
		});
	}

	private void getIntentParams(){
		Intent intent = getIntent();
		if(intent!=null){
			scanPhoto = intent.getBooleanExtra("scanPhoto", false);
			content = intent.getStringExtra("content");
		}
	}
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		back = (ImageView)findViewById(R.id.back);
		scanImageView = (ImageView)findViewById(R.id.scan_image);
		handle_tv = (TextView)findViewById(R.id.handle_tv);
		QRScanView view=(QRScanView)findViewById(R.id.scan_view);
		view.closeScanAnimation();
		view.setScanMaskColor(this.getResources().getColor(R.color.qr_title_black_tm));
	}

	@Override
	protected void initEvents() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	private void selectPhotos(){
		Intent intent = new Intent(this, PhotoWallActivity.class);
		intent.putExtra(PhotoWallActivity.KEY_IS_SHOW_RECENTLY, true);// 是否显示最近图片
		intent.putExtra(PhotoWallActivity.KEY_LIMIT_RECENTLY_SHOW_NUM, 100);// 最新图片显示最大数目
		intent.putExtra(PhotoWallActivity.KEY_LIMIT_SELECTED_NUM, 1);// 可以的最大图片数目
		this.startActivityForResult(intent,
				PhotoWallActivity.REQUEST_CODE_SELECT_PHOTO);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			boolean finish=true;
			if (PhotoWallActivity.REQUEST_CODE_SELECT_PHOTO == requestCode && data != null) {
				ArrayList<String> pic_filse = data
						.getStringArrayListExtra(PhotoWallActivity.KEY_SELECTED_PHOTOS);
				if (pic_filse != null) {
					System.out.println(pic_filse);
					if (!pic_filse.isEmpty()) {
						scanImage = QRCodeTools.decodeFile(pic_filse.get(0));
						if(scanImage!=null){
							scanImageView.setVisibility(View.VISIBLE);
							scanImageView.setImageBitmap(scanImage);
							scanBitmap();
							System.gc();
							finish = false;
						}
					}
				}
			}
			if (finish){
				setResult(RESULT_OK);
				this.finish();
			}
		}
	}
	
	private void scanBitmap(){
		hander.postDelayed(new Runnable(){
			@Override
			public void run() {
				/**扫描内置图片*/
				content = QRCodeTools.qrReaderBitmap(scanImage,null);
				hander.sendEmptyMessage(1);
			}
		}, 2000);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler hander=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			handle_tv.setVisibility(View.INVISIBLE);
			if(content!=null&&content.length()>0){
				onQRCodeRead(content,null);
			}else{
				QRCodeNotFoundOnCamImage();
			}
		}
	};
	
	public void QRCodeNotFoundOnCamImage(){
		if (null != dialog && !dialog.isShowing())
			dialog.show();
	}
	
	public void onQRCodeRead(String text, PointF[] points) {
		System.out.println("QR scan Result:"+text);
		if(!startResult){
			startResult = true;
			content = text;
			QRCodeResultAnalysis.analysisAndHandleMXQRCodeResult(this, content,this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dialog = null;
		/**回收图片资源*/
		if(scanImage!=null&&!scanImage.isRecycled()){
			scanImage.recycle();
			scanImage = null;
		}
		System.gc();
	}
	
	@Override
	public void finish() {
		super.finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onAnalysisFinished() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onResetScan() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finish();
	}
}