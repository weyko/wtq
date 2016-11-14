package chat.common.fileupload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import chat.common.util.output.LogUtil;

/**
 * 图片压缩上传优化
 */
public class CompressImgUtil {
	public static final int compressedFilelimitS = 300*1024;// S级图片压缩后的限制，300K。
	public static final int compressedFilelimitSS = 10*1024*1024;// SS级图片压缩后的限制，5M。
	public static final int compressFileSizeLimit = 150000;// 文件大小超过150K，进行压缩处理。

	/**
	 * 处理文件（压缩等）
	 *
	 * @param path
	 * @return 文件
	 */
	public static File compressImg(final String path) {
		return compressImg(path,1280);
	}

	public static File compressImgMaxLimit(final String path) {
		return compressImg(path,640);
	}
	/**
	 * 处理文件（压缩等）
	 *
	 * @param path
	 * @return 文件
	 */
	private static File compressImg(final String path,int limitSize) {
		final File file = new File(path);
		long srcFile = file.length();
		if (srcFile < compressFileSizeLimit) {
			LogUtil.writeCommonLogtoFile("imagePath:" + path + " no compress--FileSize=" + srcFile);
			return file;
		}
		try {
			int quality = 70;// original
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int scale = 1;
			if (opts.outWidth > limitSize) {
				scale = opts.outWidth / limitSize;
			}
			if (opts.outHeight > limitSize) {
				scale = opts.outHeight / limitSize > scale ? opts.outHeight / limitSize
						: scale;
			}
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = scale;
			Bitmap bit = BitmapFactory.decodeFile(path, opts);
			if (bit != null) {
				NativeUtil.saveBitmap(bit, quality, file.getAbsolutePath(),
						true);
				bit.recycle();
				bit = null;
			}
			LogUtil.writeCommonLogtoFile("imagePath:"+path+"compress--OriginalFileSize="+srcFile + "compressFileSize="+file.length());
		} catch (UnsatisfiedLinkError e) {
			doOrgcompressBmp(file,path,limitSize);
		}catch (NoClassDefFoundError e){
			doOrgcompressBmp(file,path,limitSize);
		}catch (ExceptionInInitializerError e) {
			doOrgcompressBmp(file,path,limitSize);
		}catch(OutOfMemoryError e){
			if(limitSize>640){
				return compressImg(path,640);
			}
		}catch(Exception e){
			doOrgcompressBmp(file,path,limitSize);
		}
		return file;
	}

	private static void doOrgcompressBmp(File file,String path,int limitSize){
		LogUtil.writeCommonLogtoFile("imagePath:"+file.getAbsolutePath()+" compress--OriginalFileSize="+file.length());
		if (file.exists()) {
			Bitmap bmp = null;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int scale = 1;
			if (opts.outWidth > limitSize) {
				scale = opts.outWidth / limitSize;
			}
			if (opts.outHeight > limitSize) {
				scale = opts.outHeight / limitSize > scale ? opts.outHeight / limitSize
						: scale;
			}
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = scale;
			try {
				bmp = BitmapFactory.decodeFile(path, opts);
				if (bmp != null) {
					compressBmpToFile(bmp, file);
				}
				bmp = null;
				System.gc();
			} catch (OutOfMemoryError e1) {
				e1.printStackTrace();
				if(limitSize>640){
					doOrgcompressBmp(file,path,640);
				}
			}
		}
	}

	public static void compressBmpToFile(Bitmap bmp, File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 80;
		bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		if (baos.toByteArray().length > compressedFilelimitS) {
			baos.reset();
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 20;
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.writeCommonLogtoFile("imagePath:"+file.getAbsolutePath()+" compressFileSize="+file.length());
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
