package chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.imlibrary.R;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.util.download.ChatDownLoadFile;
import chat.common.util.output.ShowUtil;
import chat.manager.ChatMessageManager;
import chat.manager.XmppSessionManager;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgStatusEnum;
import chat.session.util.ChatUtil;

public class ChatService extends Service {

	public static final int ACTION_UPLOAD_FILE = 0;// 上传文件
	public static final int ACTION_SEND_MSG = 1;// 发送消息体
	public static final int ACTION_UPMSG_STATUS = 2;// 发送已读回执
	public static final int ACTION_GET_MSG = 3;// 获取消息体ok
	public static final int ACTION_GET_FILE = 4;// 获取文件

	public final int FAILED_UPDATA = 5;// 失败更新数据库
	private ExecutorService pool = null;
	private HashMap<String,String> downList=null;
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	private Handler handler;

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (handler == null)
			handler = new Handler();
		if (intent != null) {
			int action = intent.getIntExtra("action", -1);
			boolean isReSend = intent.getBooleanExtra("isReSend", false);
			MessageBean message = (MessageBean) intent
					.getSerializableExtra("IMMessageBean");

			if (action == ACTION_SEND_MSG) {// 开始上传消息体
				String content = intent.getStringExtra("content");
				if (!isReSend) {
					Intent itent = new Intent();
					itent.setAction(MessageInfoReceiver.ACTION);
					itent.putExtra("type", MessageInfoReceiver.EVENT_UPDATE_READ_NUMBERS);
					sendBroadcast(itent);
				}
				XmppSessionManager.getInstance().sendMessage(
						message.getTo(), content, message);
			} else if (action == ACTION_UPMSG_STATUS) {// 发送已读回执
			} else if (action == ACTION_GET_FILE) {
				downloadVoice(message);
			}
		}
	}
	
	private void downloadVoice(MessageBean message){
		if (pool == null) {
			pool = Executors.newFixedThreadPool(10);
		}
		IMChatAudioBody audioBody= message.getAttachment();
		String downFileUrl = audioBody.getAttr1();
		if (downFileUrl != null && downFileUrl.length()>0) {
			if (!downFileUrl.startsWith("http:")) {
				downFileUrl = URLConfig.getDomainUrl(Constant.DOMAIN_MEDIA_TYPE) + downFileUrl;
				if (downList==null){
					downList = new HashMap<String,String>();
				}
				if(message.getMsgCode()!=null&&!downList.containsKey(message.getMsgCode())){
					downList.put(message.getMsgCode(), downFileUrl);
					DownloadTask downloadTask = new DownloadTask(message);
					downloadTask.executeOnExecutor(pool);
				}
			}
		}else{
			message.setSession(getString(R.string.voice_fail));
			message.setMsgStatus(MsgStatusEnum.fail);
			ChatMessageManager.getInstance().updateMessage(message);
			Log.d("weyko", "语音下载失败:空的下载地址");
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (downList != null) {
			downList.clear();
			downList = null;
		}
		if (pool != null) {
			pool.shutdownNow();
		}
		pool = null;
		System.gc();
	}

	/**
	 * 下载声音文件
	 * 
	 * @author ami
	 * 
	 */
	private class DownloadTask extends AsyncTask<Void, Void, String> {
		private String fileName;
		private String url;
		private MessageBean messageBean;
		private String from;
		private String to;

		public DownloadTask(MessageBean messageBean) {
			this.messageBean = messageBean;
			to = messageBean.getTo();
			if (to.indexOf("@") != -1) {
				to = to.substring(0, to.indexOf("@"));
			}
			from = messageBean.getFrom();
			if (from.indexOf("@") != -1) {
				from = from.substring(0, from.indexOf("@"));
			}
			this.fileName = messageBean.getMsgCode() + ".amr";
			IMChatAudioBody audioBody= messageBean.getAttachment();
			this.url = audioBody.getAttr1();
			if (url != null && !url.startsWith("http:")) {
				url = URLConfig.getDomainUrl(Constant.DOMAIN_MEDIA_TYPE) + url;
			} else if (url == null) {
				url = "";
			}
			ShowUtil.log("weyko", "voice_url-----------" + url);
		}

		@Override
		protected String doInBackground(Void... params) {
			if (!fileName.contains(".amr")) {
				fileName += ".amr";
			}
			String locFileUrl = ChatDownLoadFile.downloadFile(
					Constant.VOICES_FOLDER + from + "_" + to + File.separator,
					fileName, url);
			return locFileUrl;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			downList.remove(messageBean.getMsgCode());
			IMChatAudioBody imChatVoiceBody = messageBean.getAttachment();
			if (result != null && !"".equals(result)) {
				imChatVoiceBody.setLocalUrl(result);
				messageBean.setSession(getString(R.string.voice));
				Log.d("weyko", "语音下载成功11");
				messageBean.setMsgStatus(MsgStatusEnum.success);
				ChatMessageManager.getInstance().updateMessage(messageBean);
			} else {
				messageBean.setSession(getString(R.string.voice_fail));
				messageBean.setMsgStatus(MsgStatusEnum.fail);
				ChatMessageManager.getInstance().updateMessage(messageBean);
				Log.d("weyko", "语音下载失败");
			}
			ChatUtil.sendUpdateNotify(
					IMClient.getInstance().getContext(),
					MessageInfoReceiver.EVENT_VOICE_LOADED,
					messageBean.getMsgCode() + "," + result + ","
							+ messageBean.getMsgStatus(), messageBean.getSessionId());
		}
	}
}