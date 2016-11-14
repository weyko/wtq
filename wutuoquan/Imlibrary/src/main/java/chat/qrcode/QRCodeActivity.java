package chat.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.imlibrary.R;

/**
 * 
* @Description: 扫一扫（二维码识别）-支持Carame取景和手机内置图片
 */
public class QRCodeActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener, QRCodeObserver {
	/**扫一扫结果 key值  通过Intent向外部发送识别结果*/
	public static final String QR_CODE_RESULT="scan_result"; 
	public static final int REQUEST_CODE_HANDLE_CONTENT=115;
	/**二维码识别及相机控制View*/
	private QRCodeReaderView readView;
	/**二维码扫描动画View*/
	private QRScanView scanView;
	private ImageView back;
	//private TextView title;
	private LinearLayout photo;
	private LinearLayout carame_light;
	private String content=null;
	private boolean startResult=false;
	private boolean light=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);
		initView();
		initEvents();
		initData();
	}
	
	private void initData(){
		if (!QRCodeTools.checkCameraHardware(this)) {
			Toast.makeText(this, getString(R.string.open_camera_failed),
				     Toast.LENGTH_SHORT).show();
		}
		Resources resources = getResources();
		/**设置扫描框内部色彩*/
		scanView.setScanMaskColor(resources.getColor(R.color.qr_mask_black));
		/**设置扫描框边框色彩*/
		scanView.setScanFrameColor(resources.getColor(R.color.qr_frame_color));
		int width=(int)resources.getDimension(R.dimen.qrcode_scan_size_width);
		/**设置扫描框宽高*/
		scanView.setScanFrameSize(width, width);
	}

	private void initView() {
		readView = (QRCodeReaderView)findViewById(R.id.qrcoder_view);
		scanView = (QRScanView)findViewById(R.id.scan_view);
		//title = (TextView)findViewById(R.id.titleText);
		back = (ImageView)findViewById(R.id.back);
		photo = (LinearLayout)findViewById(R.id.photo);
		carame_light = (LinearLayout)findViewById(R.id.carame_light);
		if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
			carame_light.setVisibility(View.VISIBLE);//支持闪光灯
		}else{
			carame_light.setVisibility(View.GONE);//不支持闪光灯
			System.out.println("----------not support camera flash!!!--------------");
		}
	}
	
	private void initEvents(){
		readView.setOnQRCodeReadListener(this);
		readView.setDecoderView(this,scanView);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectPhotos();
			}
		});
		carame_light.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				light = !light;
				if(readView!=null) {
					readView.setLightEnable(light);
				}
			}
		});
	}
	
	private void selectPhotos(){
		Intent intent = new Intent(this, QRCodeHandleActivity.class);
		intent.putExtra("scanPhoto", true);
		this.startActivityForResult(intent,REQUEST_CODE_HANDLE_CONTENT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null) {
			if (REQUEST_CODE_HANDLE_CONTENT == requestCode) {
				setResult(RESULT_OK, data);
				finish();
			}
		}
	}
	/**
	 * 处理扫描结果
	 */
	@Override
	public void onQRCodeRead(String text, PointF[] points) {
		System.out.println("QR scan Result:"+text);
		if(!QRCodeActivity.this.isFinishing()) {
			if (!startResult) {
				startResult = true;
				content = text;
				QRCodeResultAnalysis.analysisAndHandleMXQRCodeResult(this, content, this);
			}
		}
	}
	
	// Called when your device have no camera
	@Override
	public void cameraNotFound() {
		scanView.cameraOpenFailed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			/**关闭相机*/
			if (readView!=null){
				readView.closeCameraDriver();
			}
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		System.gc();
	}
	
	@Override
	public void finish() {
		super.finish();
	}
	
	/**
	 * Called when there's no QR codes in the camera preview image
	 */
	@Override
	public void QRCodeNotFoundOnCamImage() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			readView.startPreview();
		}catch(RuntimeException e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			readView.stopPreview();
		}catch(RuntimeException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onAnalysisFinished() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onResetScan() {
		// TODO Auto-generated method stub
		startResult = false;
	}
}