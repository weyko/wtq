/**
 * 
 */
package chat.common.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @Description 网络连接状态监听类
 */
public class ConnectStateUtil {

	/**
	 * 判断网络是否连接或正在连接
	 * 
	 * @return
	 */
	public static boolean isConnected(Context context) {
		NetworkInfo networkInfo = initInfo(context);
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
				return true;
		}
		return false;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @return
	 */
	public static boolean isAvailable(Context context) {
		NetworkInfo networkInfo = initInfo(context);
		if (networkInfo != null)
			return networkInfo.isAvailable();
		return false;
	}

	/**
	 * 是否为wifi模式
	 * 
	 * @return
	 */
	public static boolean isWifiMode(Context context) {
		NetworkInfo networkInfo = initInfo(context);
		if (networkInfo != null
				&& ConnectivityManager.TYPE_WIFI == networkInfo.getType())
			return true;
		return false;
	}

	/**
	 * 是否为移动mo模式
	 * 
	 * @return
	 */
	public static boolean isMobileMode(Context context) {
		NetworkInfo networkInfo = initInfo(context);
		if (networkInfo != null
				&& ConnectivityManager.TYPE_MOBILE == networkInfo.getType())
			return true;
		return false;
	}

	/**
	 * 获取网络连接类型
	 * 
	 * @return
	 */
	public static int getConnectType(Context context) {
		NetworkInfo networkInfo = initInfo(context);
		if (networkInfo == null)
			return -1;
		return networkInfo.getType();
	}

	/**
	 * 初始化网络连接对象
	 * 
	 * @param context
	 * @return
	 */
	private static NetworkInfo initInfo(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		return networkInfo;
	}

	/**
	 * 检查当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */

	public static boolean isNetworkAvailable(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
//					System.out.println(i + "===状态==="
//							+ networkInfo[i].getState());
//					System.out.println(i + "===类型==="
//							+ networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
