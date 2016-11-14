package chat.image.photo.utils;

import android.os.Environment;

public class Utility {
	public static boolean isSDcardOK() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static String getSDcardRoot() {
		if (isSDcardOK()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	/*public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int msgId) {
		Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
	}*/

	/** 获取字符串中某个字符串出现的次数。 */
	/*public static int countMatches(String res, String findString) {
		if (res == null) {
			return 0;
		}

		if (findString == null || findString.length() == 0) {
			throw new IllegalArgumentException(
					"The param findString cannot be null or 0 length.");
		}

		return (res.length() - res.replace(findString, "").length())
				/ findString.length();
	}*/

	/** 判断该文件是否是一个图片。 */
	public static boolean isImage(String fileName) {
		return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
				|| fileName.endsWith(".png");
	}
}