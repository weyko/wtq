package chat.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class BitmapProvider {

	public static Bitmap getBitmap(Bitmap bitmap, int width, int height) {
		Bitmap bm = bitmap;
		if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
			bm = Bitmap.createScaledBitmap(bm, width, height, true);
			if (!bitmap.isRecycled()){
				bitmap.recycle();
			}
			bitmap=null;
		}
		return bm;

	}

	public static Bitmap getBitmap(Context context, int id) {
		InputStream is = context.getResources().openRawResource(id);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;
		Bitmap btp = BitmapFactory.decodeStream(is, null, options);
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return btp;

	}

	/*public static Bitmap getBitmap(Context context, int id, int width,
			int height) {

		return getBitmap(getBitmap(context, id), width, height);

	}*/

	/*public static Bitmap getBitmap(InputStream is) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;
		Bitmap t = BitmapFactory.decodeStream(is, null, options);
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

	public static Bitmap getBitmap(String path) {

		try {
			InputStream is = new FileInputStream(path);
			return getBitmap(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}*/
}
