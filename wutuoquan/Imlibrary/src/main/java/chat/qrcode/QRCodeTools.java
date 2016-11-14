package chat.qrcode;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

/**
 * 
* @Description: QR 扫描工具类
 */
public class QRCodeTools {
	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
		if (android.os.Build.MODEL.equals("vivo")) {//不检查vivo手机是否支持camera
			return true;
		}
		if(context==null){
			return false;
		}
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FRONT)) {
			// this device has a front camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}
	
	/**
	 * 扫描内置图片二维码
	 * @param bmp
	 * @param listener
	 * @return
	 */
	public static String qrReaderBitmap(Bitmap bmp,QRCodeReaderView.OnQRCodeReadListener listener) {
		String res=null;
		if (bmp != null) {
			/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] bmpSource = baos.toByteArray();*/
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			int[] bmpSource = new int[width*height]; 
			bmp.getPixels(bmpSource, 0, width, 0, 0, width, height); 
			/*PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
					bmpSource, width, height, 0, 0, width, height, false);*/
			LuminanceSource source = new RGBLuminanceSource(width, height,bmpSource);
			HybridBinarizer hybBin = new HybridBinarizer(source);
			BinaryBitmap bitmap = new BinaryBitmap(hybBin);
			QRCodeReader mQRCodeReader = new QRCodeReader();
			try {
				Result result = mQRCodeReader.decode(bitmap);
				res = result.getText();
				if (listener!=null /*&& result != null */&& result.getText() != null) {
					listener.onQRCodeRead(result.getText(), null);
				}
			} catch (ChecksumException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				// Notify QR not found
				if(listener!=null){
					listener.QRCodeNotFoundOnCamImage();
				}
			} catch (com.google.zxing.FormatException e) {
				e.printStackTrace();
			} finally {
				mQRCodeReader.reset();
				mQRCodeReader = null;
				bmpSource=null;
				System.gc();
			}
		}
		return res;
	}
	
	public static Bitmap decodeFile(final String path){
		Bitmap bmp=null;
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		int scale = 1;
		if (opts.outWidth > 1080) {
			scale = opts.outWidth / 1080;
		}
		if (opts.outHeight > 1080) {
			scale = opts.outHeight / 1080 > scale ? opts.outHeight / 1080
					: scale;
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		opts.inPreferredConfig = Config.RGB_565;
		try {
			bmp = BitmapFactory.decodeFile(path, opts);
		}catch (OutOfMemoryError e) {
			System.gc();
		}
		return bmp;
	}
}