package chat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * 广播接收器
 *
 */
public class MessageInfoReceiver extends BroadcastReceiver {
	public final static String ACTION = "chat.view.receiver.sendmessage";
	/**登录服务器事件*/
	public final static int EVENT_LOGIN_SUCCESS = 1109;
	/**连接变化事件*/
	public final static int EVENT_CONNECTION_CHANGED = 1110;
	/**更新消息*/
	public final static int EVENT_UPDATE_CHAT = 1111;
	/**更新消息发送状态*/
	public final static int EVENT_UPDATE_SEND_STATUE = 1112;
	/** 消息回执*/
	public final static int EVENT_CHAT_READ = 1114;
	/** 语音加载完成**/
	public final static int EVENT_VOICE_LOADED = 1115;
	/**更新群成员昵称*/ 
	public final static int EVENT_UPDATE_GROUP_MEMBER_NICKNAME = 1116;
	/**更新消息数*/
	public final static int EVENT_UPDATE_READ_NUMBERS = 1117;
	/**更新设置*/ 
	public final static int EVENT_UPDATE_SETTING = 1118;
	/** 踢下线通知**/
	public final static int EVENT_LOGINOUT = 401;
	/**更新群信息*/
	public final static int EVENT_GROUPINFO = 402;
	/**清空信息*/
	public final static int EVENT_CLEAR = 403;
	/**卡券消息*/
	public final static int EVENT_CARD = 405;
	/**token失效*/
	public final static int EVENT_TOKEN_INVALID = 406;
	/**更新商家*/
	public final static int EVENT_UPDATE_MOBIZ = 407;
	/**更新除了商家之外的单个会话*/
	public final static int EVENT_UPDATE_SESSION_ONLY = 408;
	/**删除图片之后,更新消息列表*/
	public final static int EVENT_UPDATE_AFTER_DELETE_PIC= 410;
	/**更新群信息*/
	public final static int EVENT_UPDATE_GROUPINFO= 411;

	private static List<MessageReciveObserver> registerReceiverHandlerList = new ArrayList<MessageReciveObserver>();

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (arg1.getExtras() != null) {
			int type = arg1.getExtras().getInt("type");
			String msgCode = arg1.getExtras().getString("msgCode");
			long time = arg1.getExtras().getLong("time");
			String sessionId = arg1.getExtras().getString("sessionId");
			String url = arg1.getExtras().getString("url");
			String nickname = arg1.getExtras().getString("nickname");
			String roomId = arg1.getExtras().getString("roomId");
			int sendStatus = arg1.getIntExtra("sendStatus", 404);
			for (int i = 0; i < registerReceiverHandlerList.size(); i++) {
				MessageReciveObserver handler = registerReceiverHandlerList
						.get(i);
				if (handler != null) {
					handler.handleReciveMessage(type, msgCode,sendStatus,sessionId,time,url,nickname,roomId);
				}
			}
		}
	}

	public static void registerReceiverHandler(
			MessageReciveObserver msgReviceObsever) {
		if (registerReceiverHandlerList == null) {
			registerReceiverHandlerList = new ArrayList<MessageReciveObserver>();
		}
		if (registerReceiverHandlerList.contains(msgReviceObsever)) {
			return;
		}
		registerReceiverHandlerList.add(msgReviceObsever);
	}

	public static void unregisterReceiverHandler(
			MessageReciveObserver msgReviceObsever) {
		if (registerReceiverHandlerList != null) {
			registerReceiverHandlerList.remove(msgReviceObsever);
		}
	}

	public static void unregisterAllHandler() {
		if (registerReceiverHandlerList != null) {
			registerReceiverHandlerList.clear();
		}
	}
}
