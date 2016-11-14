package chat.image;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

	/**
	 * convert Bitmap to byte array
	 */
	public static byte[] bitmapToByte(Bitmap b) {
		ByteArrayOutputStream o = null;
		byte[] bytes = null;
		try {
			o = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.PNG, 100, o);
			bytes = o.toByteArray();
		}finally {
		try{
			if (o!=null) {
				o.close();
			}

		} catch (IOException e) {

			e.printStackTrace();

		}
	}
		return bytes;
	}




	/**
	 * convert byte array to Bitmap
	 */
	public static Bitmap byteToBitmap(byte[] b) {
		return (b == null || b.length == 0) ? null : BitmapFactory
				.decodeByteArray(b, 0, b.length);
	}

	/**
	 * 把bitmap转换成Base64编码String
	 */
	public static String bitmapToString(Bitmap bitmap) {
		return Base64.encodeToString(bitmapToByte(bitmap), Base64.DEFAULT);
	}

	/**
	 * convert Drawable to Bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		return drawable == null ? null : ((BitmapDrawable) drawable)
				.getBitmap();
	}

	/**
	 * convert Bitmap to Drawable
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		return bitmap == null ? null : new BitmapDrawable(bitmap);
	}
	
	/**
	 * convert Bitmap to Drawable
	 */
	public static Drawable bitmapToDrawable(Resources mResources, Bitmap bitmap) {
		return bitmap == null ? null : new BitmapDrawable(mResources, bitmap);
	}

	/**
	 * scale image
	 */
	public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
		return scaleImage(org, (float) newWidth / org.getWidth(),
				(float) newHeight / org.getHeight());
	}

	/**
	 * scale image
	 */
	public static Bitmap scaleImage(Bitmap org, float scaleWidth,
			float scaleHeight) {
		if (org == null) {
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(),
				matrix, true);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap) {
		int height = bitmap.getHeight();
		int width = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xff424242);
		// paint.setColor(Color.TRANSPARENT);
		canvas.drawCircle(width / 2, height / 2, width / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap createBitmapThumbnail(Bitmap bitMap,
			boolean needRecycle) {
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		// 设置想要的大小
		int newWidth = 120;
		int newHeight = 120;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
				matrix, true);
		if (needRecycle)
			bitMap.recycle();
		return newBitMap;
	}

	public static boolean saveBitmap(Bitmap bitmap, File file) {
		if (bitmap == null)
			return false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean saveBitmap(Bitmap bitmap, String absPath) {
		return saveBitmap(bitmap, new File(absPath));
	}

	/**
	 * 计算图片的缩放值 如果图片的原始高度或者宽度大与我们期望的宽度和高度，我们需要计算出缩放比例的数值。否则就不缩放。
	 * heightRatio是图片原始高度与压缩后高度的倍数， widthRatio是图片原始宽度与压缩后宽度的倍数。
	 * inSampleSize就是缩放值 ，取heightRatio与widthRatio中最小的值。
	 * inSampleSize为1表示宽度和高度不缩放，为2表示压缩后的宽度与高度为原来的1/2(图片为原1/4)。
	 *
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions(尺寸) larger than or equal to
			// the requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 根据路径获得图片并压缩返回bitmap用于显示
	 *
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath, int w, int h) {
		final BitmapFactory.Options options = new BitmapFactory.Options();

		// 该值设为true那么将不返回实际的bitmap不给其分配内存空间而里面只包括一些解码边界信息即图片大小信息
		options.inJustDecodeBounds = true;// inJustDecodeBounds设置为true，可以不把图片读到内存中,但依然可以计算出图片的大小
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, w, h);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;// 重新读入图片，注意这次要把options.inJustDecodeBounds
											// 设为 false
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);// BitmapFactory.decodeFile()按指定大小取得图片缩略图
		return bitmap;
	}

	public static Intent buildGalleryPickIntent(Uri saveTo, int aspectX,
			int aspectY, int outputX, int outputY, boolean returnData) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("output", saveTo);
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", returnData);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		return intent;
	}

	public static Intent buildImagePickIntent(Uri uriFrom, Uri uriTo,
			int aspectX, int aspectY, int outputX, int outputY,
			boolean returnData) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uriFrom, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("output", uriTo);
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", returnData);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		return intent;
	}

	public static Intent buildCaptureIntent(Uri uri) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		return intent;
	}

	/**
	 * 转换成带圆角的图片
	 *
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static File saveBitmapToSDCard(Bitmap bmp, String strPath) {
		if (null != bmp && null != strPath && !strPath.equalsIgnoreCase("")) {
			try {
				File file = new File(strPath.substring(0,
						strPath.lastIndexOf("/")));
				if (!file.exists()) {
					file.mkdirs();
				}
				file = new File(strPath);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = bitampToByteArray(bmp);
				fos.write(buffer);
				fos.close();
				return file;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static byte[] bitampToByteArray(Bitmap bitmap) {
		byte[] array = null;
		try {
			if (null != bitmap) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
				array = os.toByteArray();
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return array;
	}

	/**
	 * 旋转图片
	 *
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	* @Title: getBackgroundDrawable
	* @param:
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @return Drawable
	 */
	public static Drawable getDecodeStreamDrawable(Resources mResources, int id)
	{
		Drawable mDrawable = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			InputStream is = mResources.openRawResource(id);
			Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
			mDrawable = bitmapToDrawable(mResources, bm);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return mDrawable;
	}

	/**
	 * 以能根据分辨率的方式来读取本地资源的图片
	* @Title: getBackgroundDrawable
	* @param:
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @return Drawable
	 */
	public static Drawable getDecodeResourceDrawable(Resources mResources, int id)
	{
		Drawable mDrawable = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			Bitmap bm = BitmapFactory.decodeResource(mResources, id, opt);
			mDrawable = bitmapToDrawable(mResources, bm);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return mDrawable;
	}
	
    /**
     * 图片如果是放在hdpi下面，这样如果是遇到高密度手机， 系统会按照
     * scale = (float) targetDensity / density 把图片放到几倍，这样会使得在高密度手机上经常会发生OOM。
     *
     * 这个方法用来解决在如果密度大于hdpi（240）的手机上，decode资源文件被放大scale，内容浪费的问题。
     * @param resources
     * @param id
     * @return
     */
   /* public static Bitmap decodeResource(Resources resources, int id) {

        int densityDpi = resources.getDisplayMetrics().densityDpi;
        Bitmap bitmap;
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ALPHA_8;
        if (densityDpi > DisplayMetrics.DENSITY_HIGH) {
            opts.inTargetDensity = value.density;
            bitmap = BitmapFactory.decodeResource(resources, id, opts);
        }else{
            bitmap = BitmapFactory.decodeResource(resources, id);
        }

        return bitmap;
    }*/
}
