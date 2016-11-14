package chat.common.util.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import chat.common.util.output.LogUtil;
import chat.manager.ChatOfflineManager;
import chat.manager.XmppServerManager;

/*
 * 监听网络状态变化
 */
public class NetWorkStateBroadcastReceiver extends BroadcastReceiver{
	private final String ALARM_ACTION="android.alarm.demo.action";
	private final String DATE_CHANGED_ACTION="android.intent.action.TIME_SET";
	private int timeCount=0;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(ALARM_ACTION)) {
			timeCount++;
			if (timeCount>=13){
				timeCount = 0;
			}
			XmppServerManager.checkIMConnectAndLogin();
			LogUtil.i("weyko", "---------alarm action----------");
		}else if(intent.getAction().equals(DATE_CHANGED_ACTION)){
			ChatOfflineManager.getInstance().getDotTimeWithServer();
		}else{
			LogUtil.i("weyko", "---------network changed action----------");
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		    if(activeInfo!=null){
		    	LogUtil.i("weyko", "---------has network----------");
		    	XmppServerManager.checkIMConnectAndLogin();
		    }else{
		    	//网络断开
		    	LogUtil.i("weyko", "---------no network----------");
		    	XmppServerManager.disConnection();
		    }
		}
	}
}