package chat.image.photo.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class PickPhotoUtils {
private static final String TAG = "PickPhotoUtil";
	private static PickPhotoUtils instance;
	public static PickPhotoUtils getInstance() {
		if (instance == null) {
			instance = new PickPhotoUtils();
		}
		return instance;
	}
	public interface PickPhotoCode {
		/**
		 * 基数
		 */
		static final int BASE_CAMERA = 200;
		/**
		 * 拍照
		 */
		public static final int PICKPHOTO_TAKE = BASE_CAMERA + 1;
		/**
		 * 裁剪
		 */
		public static final int PICKPHOTO_CUTTING = BASE_CAMERA + 2;
		/**
		 * 相册
		 */
		public static final int PICKPHOTO_LOCAL = BASE_CAMERA + 3;
	}
	
	private PickPhotoUtils() {}	
	
	private boolean isStorageState() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 拍照选取
	 * @param mContext
	 */
	@SuppressWarnings("unused")
	public String takePhoto(Context mContext, String userName, String path) {
		if (isStorageState()) {
			File imgFile = new File(path); //拍完照后的照片的输出路径(目前该文件还是不存在的)
			if (null != imgFile) {
				Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
				// 调用系统拍照
				try{
					((Activity)mContext).startActivityForResult(mIntent,
							PickPhotoCode.PICKPHOTO_TAKE);
				}catch(ActivityNotFoundException e){
					e.printStackTrace();
				}
				return imgFile.getAbsolutePath();
			}
			else {
				Toast.makeText(mContext, "创建图片对象有误", Toast.LENGTH_LONG).show();
			}			
		} else {
			Toast.makeText(mContext, "未找到SD卡", Toast.LENGTH_LONG).show();
		}	
		return null;
	}
	
	/**
	 * 拍照后获取uri
	 * @param mContext
	 * @param data
	 * @param imgFile
	 * @return
	 */
	public String takeResult(Context mContext, Intent data, File imgFile) {
		String mUri = null;
		Uri uri = null;
		if (data != null) {
			uri = data.getData();
			Log.e("yz", "---uri---" + uri);
			if (uri != null) {
				mUri = uri.toString();
			}
		}
		if (mUri == null || "".equals(mUri)) {
			mUri = insert(mContext, imgFile);
			Log.e("yz", "---mUri---" + mUri);
		}
		return mUri;
	}

	/**
	 * 拍照时图片的数据库操?BR>
	 * 主动调用主要是防止拍照是，系统没有主动保存数据库，回调查询该照片不为?
	 * 
	 * @param mContext
	 *            当前对象
	 * @param obj
	 *            文件对象
	 */
	private String insert(Context mContext, Object obj) {
		if (mContext == null || obj == null) {
			return "";
		}
		String insertImageUri = null;
		String filePath = null;
		if (obj instanceof String) {
			filePath = (String) obj;
		} else if (obj instanceof File) {
			filePath = obj.toString();
		}
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			return "";
		}
		String fileName = substring(filePath);
		Bitmap bitmap = null;
		try {
			/*long length = getFileLength(file);
			if (length < 100 * 1024) {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(
						filePath));
			}else if (length < 1024 * 1024) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig=Config.RGB_565;
				bitmap = BitmapFactory.decodeFile(filePath, options);
			} else {
				bitmap = compressBitmap(filePath);
			}*/
			bitmap = compressBitmap(filePath);
			insertImageUri = MediaStore.Images.Media.insertImage(
					mContext.getContentResolver(), bitmap, fileName, fileName);
			String _id = substring(insertImageUri);
			if (_id != null && !"".equals(_id)) {
				return insertImageUri;
			}
		} catch (Exception e) {
			insertImageUri = null;
			e.printStackTrace();
		} catch (OutOfMemoryError e){
			insertImageUri = null;
			e.printStackTrace();
		} finally {
			if (null != bitmap) {
				bitmap.recycle();
			}
		}
		return insertImageUri;
	}	
	
	private String substring(String str) {
		if (str == null || "".equals(str)) {
			return null;
		}
		return str.substring(str.lastIndexOf("/") + 1);
	}

	private Bitmap compressBitmap(String filePath) {
		if (filePath == null || filePath.length() == 0) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		if(options.outWidth<=200&&options.outHeight<=200){
			options.inSampleSize = 1;
		}else if(options.outWidth<=400&&options.outHeight<=400){
			options.inSampleSize = 2;
		}else{
			options.inSampleSize = 4;
		}
		try {
			options.inJustDecodeBounds = false;
			Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
			return bmp == null ? null : bmp;
		} catch (OutOfMemoryError err) {
			return null;
		}
	}

	/**
	 * 
	 * 获取文件的大?
	 * @param obj
	 * @return long
	 */
	private long getFileLength(Object obj) {
		if (obj == null || "".equals(obj)) {
			return 0;
		}
		File file;
		if (obj instanceof String) {
			file = new File(obj.toString());
		} else if (obj instanceof File) {
			file = new File(obj.toString());
		} else {
			return 0;
		}
		if (/*file != null && */file.isFile()) {
			return file.length();
		}
		return 0;
	}	
	
	/**
	 * 使用Uri返回对应的bitmap对象
	 * @param uri
	 */
	public Bitmap getBitmapFromUri (Context context, Uri uri) {
		String scheme = uri.getScheme();
		String pathName = "";
		if ("file".equalsIgnoreCase(scheme)) {
			pathName = uri.getPath();
			
		}
		else if ("content".equalsIgnoreCase(scheme)) {
	        String[] filePathColumn = {MediaStore.Images.Media.DATA};  
	        Cursor cursor = context.getContentResolver().query(uri,  
	                filePathColumn, null, null, null);  
	        cursor.moveToFirst();  
	        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
	        pathName = cursor.getString(columnIndex);  
	        cursor.close();
		}
		return BitmapFactory.decodeFile(pathName);		
	}
}
