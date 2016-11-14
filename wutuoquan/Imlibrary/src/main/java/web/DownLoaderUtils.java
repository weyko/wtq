package web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class DownLoaderUtils {

	public static Intent getFileIntent(File file) {
		Uri uri = Uri.fromFile(file);
		String type = getMIMEType(file);
		Log.i("tag", "type=" + type);
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, type);
		return intent;
	}
	      
	@SuppressLint("DefaultLocale")
	public static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = "";
		if(fName.length() > 0){
			end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
		}
		/* 依扩展名的类型决定MimeType */
		if (end.equals("pdf")) {
			type = "application/pdf";
		} else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio/*";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video/*";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image/*";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			/* 如果无法直接打开，就跳出软件列表给用户选择 */
			type = "*/*";
		}
		return type;
	}
}