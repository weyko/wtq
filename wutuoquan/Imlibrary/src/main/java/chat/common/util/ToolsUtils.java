package chat.common.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolsUtils {

	/** 地球半径 */
	private static final double EARTH_RADIUS = 6378137.0;

	/** 
	* @Title: getCategoryName 
	* @param: context code
	* @Description: TODO(根据后台返回的 行业分类 code 返回不同语言的行业名称) 
	* @return String
	*/
	public static String getCategoryName(Context context, String str) {
		if (str==null) {
			str="";
		}
		int rInt = context.getResources().getIdentifier(str, "string", context.getPackageName());
		if (rInt == 0) {
			return str;
		}
		return context.getString(rInt);
	}
	
	
	/**
	 * @Title: isMobileNO
	 * @param:
	 * @Description: 电话号码 手机号码 等准确详细 正则表达式电话号码正则表达式
	 * @return boolean
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	/**
	 * @Title: isEmailString
	 * @param:
	 * @Description: 验证是否邮箱
	 * @return boolean
	 */
	public static boolean isEmailString(String email) {
		Pattern pattern = Pattern.compile(
				"[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		System.out.println(matcher.matches() + "+++");
		return matcher.matches();
	}
	public static boolean isConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mMobileNetworkInfo != null && mMobileNetworkInfo.isAvailable()) {
				return true;
			}
			if (mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	/**
	 * 获取应用的版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取应用的版本号
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "1.0";
		}
	}

	/**
	 * 获取未安装的APK信息
	 *
	 * @param context
	 * @param archiveFilePath
	 *            APK文件的路径。如：/sdcard/download/XX.apk
	 * @return versionCode
	 */
	public static int getUninatllApkVersionCode(Context context, String archiveFilePath) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
				return info.versionCode;
			}else{
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 获取应用的版本名称
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "v1.0";
		}
	}

	/**
	 * 版本号比较算法 版本vis1大于vis2返回1，相等返回0，小于返回-1
	 *
	 */
	public static int CompareVission(String vis1, String vis2) {
		if(vis1==null||vis1.length()==0||vis2==null||vis2.length()==0){
			return 0;
		}
		//去掉无用版本信息比如：1.2.3 qq plus
		if(vis1.indexOf(" ")>0){
			vis1=vis1.substring(0,vis1.indexOf(" "));
		}
		if(vis2.indexOf(" ")>0){
			vis2=vis2.substring(0,vis2.indexOf(" "));
		}
		String visOne[] = vis1.split("\\.");
		String visTwo[] = vis2.split("\\.");
		int compareLen = Math.min(visOne.length, visTwo.length);
		for (int i = 0; i < compareLen; i++) {
			int comPare=visOne[i].compareTo(visTwo[i]);
			if(comPare!=0){
				return comPare;
			}
		}
		if(visOne.length > visTwo.length){
			return 1;
		}
		return 0;
	}

	/**
	 * 获取手机底部smartBar 高度
	 * 
	 * @author weyko
	 * @return
	 */
	public static int getSmartBarHeight(FragmentActivity activity) {
		int sbar = 0;
		try {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int screenHeight = metrics.heightPixels;
			Rect rect = new Rect();
			activity.getWindow().getDecorView()
					.getWindowVisibleDisplayFrame(rect);
			sbar = screenHeight - rect.bottom;

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	private static long lastClickTime;   
    public static boolean isFastDoubleClick() {   
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;   
        if ( 0 < timeD && timeD < 5000) {//500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
            return true;      
        }      
        lastClickTime = time;      
        return false;      
    }   
	/**
	 * @DESCRIPTION 改变listview高度
	 */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
    	ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
  }
    
    /**
     * @DESCRIPTION 获取顶部状态栏高度
     * @AUTHOR Qingsong
     * @COMPANY 深圳魔线科技
     * @TIME 下午1:04:12
     */
    public static int getStatusBarHeight(Activity activity) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
        }
        return statusBarHeight;
	}
    /**
     * @param context 上下文
	 * @param pkgName 包名
	 * @return true:已安装  false:未安装
	 * @Description 检测手机上是否安装客户端
	 */
	public static boolean isAppInstalled(Context context, String pkgName){
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(pkgName, 0);
		} catch (Exception e) {
			info = null;
			e.printStackTrace();
		}
		if(info != null){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * @param context 上下文
	 * @param pkgName 包名
	 * @param className 需要打开的应用的activity(带包名)
	 * @Description 启动另外一个应用
	 */
	public static void launchActivity(Context context, String pkgName, String className) {
		ComponentName componentName = new ComponentName(pkgName, className);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(componentName);
		context.startActivity(intent);
	}

	/**
	 * 打开另外一个app
	 * @param context
	 * @param packageName
	 */
	public static void openApp(Context context, String packageName) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo pi = packageManager.getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = apps.iterator().next();
			if (ri != null ) {
				String pName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName cn = new ComponentName(pName, className);
				intent.setComponent(cn);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断网络是否有连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) { // 当前网络是连接的
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static String getCodeString(Context context, String str) {
		return getCategoryName(context, str);
	}

	/**
	 * 根据图片名称获得drwable
	 * @param context
	 * @param drawableName
	 * @return
	 */
	public static int getDrawableRid(Context context, String drawableName) {
		int rInt = context.getResources().getIdentifier(drawableName,
				"drawable", context.getPackageName());
		if (rInt == 0) {
			rInt = context.getResources().getIdentifier("icon_merchant",
					"drawable", context.getPackageName());
		}
		return rInt;
	}

	public static boolean checkNetworkConnected(Context context) {
		boolean isConnected = false;
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取NetworkInfo对象
		NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
		for (NetworkInfo info: networkInfo) {
			if (info.isConnected()) {
				isConnected = true;
				break;
			}
		}
		return isConnected;
	}
	public static int dip_To_px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
