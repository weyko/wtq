package chat.common.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.Method;

import chat.common.util.output.LogUtil;


/**
 * 
 * 网络状态的帮助类
 *
 * 需要添加网络权限  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 */
public class Network {
	private Context context;
	private static final String TAG = Network.class.getSimpleName();

	public Network(Context context)
	{
		this.context = context;
	}
	
	public enum NetType {
		None(1),
		Mobile(2),
		Wifi(4),
		Other(8);
		NetType(int value) {
			this.value = value;
		}
		public int value;
	}

	/**
	 * 获取ConnectivityManager
	 */
	public ConnectivityManager getConnManager() {
		return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * 判断网络连接是否有效（此时可传输数据）。
	 * @return boolean 不管wifi，还是mobile net，只有当前在连接状态（可有效传输数据）才返回true,反之false。
	 */
	public boolean isConnected() {
		NetworkInfo net = getConnManager().getActiveNetworkInfo();
		return net != null && net.isConnected();
	}

	/**
	 * 判断有无网络正在连接中（查找网络、校验、获取IP等）。
	 * @return boolean 不管wifi，还是mobile net，只有当前在连接状态（可有效传输数据）才返回true,反之false。
	 */
	public boolean isConnectedOrConnecting() {
		NetworkInfo[] nets = getConnManager().getAllNetworkInfo();
		if (nets != null) {
			for (NetworkInfo net : nets) {
				if (net.isConnectedOrConnecting()) { return true; }
			}
		}
		return false;
	}

	/**
	 * 获取当前网络链接类型
	 * @return
	 */
	public NetType getConnectedType() {
		NetworkInfo net = getConnManager().getActiveNetworkInfo();
		if (net != null) {
			switch (net.getType()) {
				case ConnectivityManager.TYPE_WIFI :
					return NetType.Wifi;
				case ConnectivityManager.TYPE_MOBILE :
					return NetType.Mobile;
				default :
					return NetType.Other;
			}
		}
		return NetType.None;
	}

	/**
	 * 是否存在有效的WIFI连接
	 */
	public boolean isWifiConnected() {
		NetworkInfo net = getConnManager().getActiveNetworkInfo();
		return net != null && net.getType() == ConnectivityManager.TYPE_WIFI && net.isConnected();
	}

	/**
	 * 是否存在有效的移动连接
	 * @return boolean
	 */
	public boolean isMobileConnected() {
		NetworkInfo net = getConnManager().getActiveNetworkInfo();
		return net != null && net.getType() == ConnectivityManager.TYPE_MOBILE && net.isConnected();
	}

	/**
	 * 检测网络是否为可用状态
	 */
	public boolean isAvailable() {
		return isWifiAvailable() || (isMobileAvailable() && isMobileEnabled());
	}

	/**
	 * 判断是否有可用状态的Wifi，以下情况返回false：
	 *  1. 设备wifi开关关掉;
	 *  2. 已经打开飞行模式；
	 *  3. 设备所在区域没有信号覆盖；
	 *  4. 设备在漫游区域，且关闭了网络漫游。
	 *  
	 * @return boolean wifi为可用状态（不一定成功连接，即Connected）即返回ture
	 */
	public boolean isWifiAvailable() {
		NetworkInfo[] nets = getConnManager().getAllNetworkInfo();
		if (nets != null) {
			for (NetworkInfo net : nets) {
				if (net.getType() == ConnectivityManager.TYPE_WIFI) { return net.isAvailable(); }
			}
		}
		return false;
	}

	/**
	 * 判断有无可用状态的移动网络，注意关掉设备移动网络直接不影响此函数。
	 * 也就是即使关掉移动网络，那么移动网络也可能是可用的(彩信等服务)，即返回true。
	 * 以下情况它是不可用的，将返回false：
	 *  1. 设备打开飞行模式；
	 *  2. 设备所在区域没有信号覆盖；
	 *  3. 设备在漫游区域，且关闭了网络漫游。
	 * 
	 * @return boolean
	 */
	public boolean isMobileAvailable() {
		NetworkInfo[] nets = getConnManager().getAllNetworkInfo();
		if (nets != null) {
			for (NetworkInfo net : nets) {
				if (net.getType() == ConnectivityManager.TYPE_MOBILE) { return net.isAvailable(); }
			}
		}
		return false;
	}

	/**
	 * 设备是否打开移动网络开关
	 * @return boolean 打开移动网络返回true，反之false
	 */
	public boolean isMobileEnabled() {
		try {
			Method getMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
			getMobileDataEnabledMethod.setAccessible(true);
			return (Boolean) getMobileDataEnabledMethod.invoke(getConnManager());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 反射失败，默认开启
		return true;
	}

	/**
	 * 打印当前各种网络状态
	 * @param context
	 * @return boolean
	 */
	public static boolean printNetworkInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo in = connectivity.getActiveNetworkInfo();
			LogUtil.i(TAG, "-------------$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$-------------");
			LogUtil.i(TAG, "getActiveNetworkInfo: " + in);
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					// if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
					LogUtil.i(TAG, "NetworkInfo[" + i + "]isAvailable : " + info[i].isAvailable());
					LogUtil.i(TAG, "NetworkInfo[" + i + "]isConnected : " + info[i].isConnected());
					LogUtil.i(TAG, "NetworkInfo[" + i + "]isConnectedOrConnecting : " + info[i].isConnectedOrConnecting());
					LogUtil.i(TAG, "NetworkInfo[" + i + "]: " + info[i]);
					// }
				}
				LogUtil.i(TAG, "\n");
			} else {
				LogUtil.i(TAG, "getAllNetworkInfo is null");
			}
		}
		return false;
	}

}