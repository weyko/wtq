package chat.common.util.output;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.imlibrary.R;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.Format;

import chat.base.IMClient;
import chat.volley.WBaseModel;

/**
 * 显示工具类
 */
public class ShowUtil {
	public static String currencyFormat(double doubleNun) {
		String str = ""+doubleNun;
		if (doubleNun==0) {
			return "0";
		}
		try {
			Format formater = null;
			if (doubleNun==(int)doubleNun) {
				formater=new DecimalFormat("#,###");
			}else {
				formater=new DecimalFormat("#,###.00");
			}
			str = formater.format(doubleNun);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 屏幕枚举 ScreenEnum
	 * 
	 * @author weyko 2015年3月19日上午10:33:21 包含属性WIDTH（宽）,HEIGHT（高）,DENSITY（密度）
	 */
	public enum ScreenEnum {
		WIDTH, HEIGHT, DENSITY
	}

	private final static boolean isDebug = true;// 设置是否为调试模式，是则打印日志信息
	private static final String TAG = "weyko";// 日志标签，过滤用

	/**
	 * 弹出提示框
	 * @param msg
	 *            提示内容
	 */
	public static void showToast(String msg) {
		if (android.text.TextUtils.isEmpty(msg))
			return;
		Toast.makeText(IMClient.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 * 弹出提示框
	 *
	 * @param context
	 * @param msg
	 *            提示内容
	 */
	public static void showToast(Context context, String msg) {
		if (context==null|| android.text.TextUtils.isEmpty(msg))
			return;
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出提示框
	 * 
	 * @param context
	 * @param msgResId
	 *            提示内容的资源id
	 */
	public static void showToast(Context context, int msgResId) {
		if (context == null)
			return;
		String msg="";
		try{
			msg=context.getResources().getString(msgResId);
		}catch(NotFoundException e){
			e.printStackTrace();
		}
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 打印日志
	 * 
	 * @param context
	 * @param msg
	 *            日志内容
	 */
	public static void log(Context context, String msg) {
		if (isDebug && context != null)
			Log.d(TAG, context.getClass().getName() + "--->" + msg);
	}

	public static void log(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}

	/**
	 * 打印日志
	 * 
	 * @param activity
	 * @param msg
	 *            日志内容
	 */
	public static void log(Activity activity, String msg) {
		if (isDebug && activity != null)
			Log.d(TAG, activity.getClass().getName() + "--->" + msg);
	}

	/**
	 * 获取屏幕大小信息
	 * 
	 * @param context
	 * @param screenEnum
	 *            获取所需类型数据.WIDTH:宽；HEIGHT：高；DENSITY：密度
	 * @return
	 */
	public static int getScreenSize(Context context, ScreenEnum screenEnum) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(metrics);
		switch (screenEnum) {
		case WIDTH:
			return metrics.widthPixels;
		case HEIGHT:
			return metrics.heightPixels;
		case DENSITY:
			return metrics.densityDpi;
		}
		return 0;
	}

	/**
	 * 获取屏幕大小信息
	 * 
	 * @param activity
	 * @param screenEnum
	 *            获取所需类型数据.WIDTH:宽；HEIGHT：高；DENSITY：密度
	 * @return
	 */
	public static int getScreenSize(Activity activity, ScreenEnum screenEnum) {
		WindowManager manager = activity.getWindowManager();
		DisplayMetrics metrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(metrics);
		switch (screenEnum) {
		case WIDTH:
			return metrics.widthPixels;
		case HEIGHT:
			return metrics.heightPixels;
		case DENSITY:
			return metrics.densityDpi;
		}
		return 0;
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStateBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbar;
	}

	/**
	 * 针对接口请求后异常提示
	 * 
	 * @param codeStr
	 *            Bean里面的code
	 */
	public static void showResutToast(Context context, String codeStr) {

		int rInt = context.getResources().getIdentifier(codeStr, "string",
				context.getPackageName());
		ShowUtil.showToast(context, rInt);
	}

	/**
	 * 获取服务器返回的code值，
	* @Title: getResutToastString 
	* @param: Context context
	* 		  String codeStr
	* @Description: 获取密码的密码问题，但是如果是自定义问题需要不能返回其它值
	* @return String
	 */
	public static String getResutToastString(Context context,String codeStr){
		int rInt = 0;
		try {
			rInt = context.getResources().getIdentifier(codeStr, "string",
					context.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (rInt == 0 || codeStr.equals(String.valueOf(rInt))) {
			return codeStr;
		}else{
			String mResourceString = "";
			try {
				mResourceString = context.getResources().getString(rInt);
			} catch (NotFoundException e) {
				e.printStackTrace();
				return codeStr;
			}
			return mResourceString;
		}
	
	}
	/**
	 * @Title: showSoftWindow
	 * @param:
	 * @Description: 显示软键盘
	 * @return void
	 */
	public static void showSoftWindow(Context context, View view) {
		if(context==null||view==null)
			return;
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

	}
	/**
	 * @Title: showSoftWindow
	 * @param:
	 * @Description: 显示软键盘
	 * @return void
	 */
	public static void hideSoftWindow(Context context, View view) {
		if(context==null||view==null)
			return;
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘  
	}
	/**
	 * @Title: showHttpRequestErrorResutToast
	 * @param:
	 * @Description: 统一根据网络错误码和接口返回的错误码提示错误信息
	 * @return void
	 */
	public static void showHttpRequestErrorResutToast(Context context,
													  int httpStatusCode, Object data) {
		Context ct = IMClient.getInstance().getContext();
		if (httpStatusCode == 200) {// 手机有网络，可以与服务器通信。
		} else if (httpStatusCode == WBaseModel.NO_NETWORK_CONNECTED) {// 手机无网络
			ShowUtil.showToast(ct, R.string.chat_textview_not_network);
		} else if (httpStatusCode == WBaseModel.UNKNOW_SERVER_ERROR) {// 手机有网络，但无法与服务器通信。
			ShowUtil.showToast(ct, R.string.warning_service_error);
		} else {// 手机有网络，但无法与服务器通信。
			ShowUtil.showToast(ct, R.string.warning_service_error);
		}
	}
}
