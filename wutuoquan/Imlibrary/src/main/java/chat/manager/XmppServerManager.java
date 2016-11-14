package chat.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.core.SASLXOauth2Mechanism;
import org.jivesoftware.smack.sasl.core.SCRAMSHA1Mechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.sasl.provided.SASLExternalMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.util.output.ShowUtil;
import chat.service.MessageInfoReceiver;
import chat.session.bean.MessageBean;
import chat.session.extension.ClientReceiptExtension;
import chat.session.extension.CommonExtension;
import chat.session.extension.MochatExtension;
import chat.session.extension.ServerReceiptExtension;
import chat.session.provider.ClientReceiptExtensionProvider;
import chat.session.provider.CommonExtensionProvider;
import chat.session.provider.MoProvider;
import chat.session.provider.ServerReceiptExtensionProvider;

/***
 * XMPP 链接登录，发送等
 */
public final class XmppServerManager implements PingFailedListener {
	private static XmppServerManager instance = null;
	private XMPPTCPConnection xmppConnection;
	private XMPPTCPConnectionConfiguration config;
	private static final String TAG_LOGIN = "MXXmpp";
	private boolean idle = true;
	private XmppSessionManager sessionManager;
	private Context context;
	private boolean isLogOut = false;
	private final int defaultPingInterval = 3*60;
	private PingManager pingManager = null;
	private ServerPingWithAlarmManager pingAlarmManager=null;
	private boolean canCheckLogin = false;
	private boolean pingFailed=false;
	private ExecutorService loginThreadPool = Executors.newFixedThreadPool(1);
	public XmppServerManager(XmppSessionManager sessionManager) {
		instance = this;
		this.context = IMClient.getInstance().getContext();
//		initSASLAuthentication();
		this.sessionManager = sessionManager;
		sessionManager.setXmppServerManager(this);
		initXmppConnection();
		canCheckLogin = true;
	}

	private void initSASLAuthentication() {
//		// TODO Auto-generated method stub
		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

		// 后台服务器验证
		SASLAuthentication.blacklistSASLMechanism(SASLMechanism.DIGESTMD5);
		SASLAuthentication.blacklistSASLMechanism(SASLMechanism.CRAMMD5);
		SASLAuthentication.blacklistSASLMechanism(SASLMechanism.EXTERNAL);
		SASLAuthentication.blacklistSASLMechanism(SASLMechanism.GSSAPI);
		SASLAuthentication.unregisterSASLMechanism(SASLXOauth2Mechanism.class.getName());
		SASLAuthentication.unregisterSASLMechanism(SASLDigestMD5Mechanism.class.getName());
		SASLAuthentication.unregisterSASLMechanism(SASLExternalMechanism.class.getName());
		SASLAuthentication.unregisterSASLMechanism(SCRAMSHA1Mechanism.class.getName());
		SASLAuthentication.unregisterSASLMechanism("org.jivesoftware.smack.sasl.javax.SASLGSSAPIMechanism");
		SASLAuthentication.unregisterSASLMechanism("org.jivesoftware.smack.sasl.javax.SASLCramMD5Mechanism");

	}

	/**
	 * 断开链接
	 */
	public static void disConnection() {
		if (instance != null) {
			instance.disConnectionXmpp();
		}
	}

	/**
	 * 关闭连接
	 */
	private void disConnectionXmpp() {
		try {
			ShowUtil.log(TAG_LOGIN, "---------disConnectionXmpp----------");
			if (xmppConnection != null) {
				if (pingManager!=null){
					pingManager.unregisterPingFailedListener(this);
					pingManager = null;
				}
				if(pingAlarmManager!=null){
					pingAlarmManager = null;
				}
				if (sessionManager != null) {
					xmppConnection.removeConnectionListener(sessionManager);
					sessionManager.clearListener();
					sessionManager.removeAllTimeOutRunnable();
				}
				if (xmppConnection.isConnected()){
					xmppConnection.disconnect();
				}
			}
			xmppConnection = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doExit() {
		isLogOut = true;
		canCheckLogin = false;
		ShowUtil.log(TAG_LOGIN, "---------doExit----------");
		disConnection();
		xmppConnection = null;
		if (sessionManager != null) {
			sessionManager.Exit();
		}
		if (loginThreadPool!=null){
			loginThreadPool.shutdownNow();
		}
		loginThreadPool = null;
	}

	public static void ExitService() {
		ShowUtil.log(TAG_LOGIN, "---------ExitService----------");
		if (instance != null) {
			instance.doExit();
		}
		instance = null;
		System.gc();
	}

	public static synchronized XmppServerManager getInstance(
			XmppSessionManager sessionManager) {
		if (instance == null) {
			instance = new XmppServerManager(sessionManager);
		}
		return instance;
	}

	private int portSelectIndex=0;

	private int getPort(){
		portSelectIndex = portSelectIndex%3;
		if (URLConfig.conditionFlag == 3 || URLConfig.conditionFlag == 2){
			if(portSelectIndex==0){
				return 80;
			}else if(portSelectIndex==1){
				return 443;
			}else{
				return 5222;
			}
		}else{
			return 5222;
		}
	}
	/*
	 * 初始化连接
	 */
	private void initXmppConnection() {
		try {
			if (xmppConnection == null) {
                int port=getPort();
                System.out.println("---------cur port="+port);
                config = XMPPTCPConnectionConfiguration
                        .builder()
                        .setHost(URLConfig.IM_HOST)
                        .setPort(port)
						.setSendPresence(false)//设置离线，处理离线消息
                        .setSecurityMode(
                                ConnectionConfiguration.SecurityMode.disabled)
                        .setServiceName(URLConfig.IM_HOST).setDebuggerEnabled(true)
                        .build();
                xmppConnection = new XMPPTCPConnection(config);
				//增加消息标签
                ProviderManager.addExtensionProvider(MochatExtension.delivery,
						MochatExtension.namespace, new MoProvider());
                ProviderManager.addExtensionProvider(ServerReceiptExtension.serverReceipt,
						ServerReceiptExtension.namespace, new ServerReceiptExtensionProvider());
                ProviderManager.addExtensionProvider(ClientReceiptExtension.clientReceipt,
						ClientReceiptExtension.namespace, new ClientReceiptExtensionProvider());
                ProviderManager.addExtensionProvider(CommonExtension.common,
						CommonExtension.namespace, new CommonExtensionProvider());
                initPingManger();
                if (xmppConnection != null) {
                    sessionManager.initializeConnection(xmppConnection);
                }
                ShowUtil.log(TAG_LOGIN, "---------initXmppConnection----------");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPingManger() {
		ShowUtil.log(TAG_LOGIN, "---------initPingManger----------");
		if (xmppConnection != null) {
			pingManager = PingManager.getInstanceFor(xmppConnection);
			PingManager.setDefaultPingInterval(defaultPingInterval);
			pingAlarmManager = ServerPingWithAlarmManager.getInstanceFor(xmppConnection);
			pingAlarmManager.setEnabled(true);
			ServerPingWithAlarmManager
					.onCreate(context.getApplicationContext());
			if (pingManager!=null){
				pingManager.registerPingFailedListener(this);
			}
			pingFailed=false;
			pingResponseTime = 0;
		}
	}

	private long pingResponseTime=0;

	public void setPingResponseTime(){
		pingResponseTime = System.currentTimeMillis();
		ShowUtil.log(TAG_LOGIN, "---------PingResponse----------");
	}

	public void checkPingResponseTime(){
		long curTime= System.currentTimeMillis();
		if(pingResponseTime>0){
			int diffTime=(int)((curTime-pingResponseTime)/1000);
			if(diffTime>(defaultPingInterval+20)){//ping 无响应
				pingFailed = true;
				ShowUtil.log(TAG_LOGIN, "---------checkPingResponseTime  pingFailed=true----------");
			}
		}
	}

	@Override
	public void pingFailed() {
		// TODO Auto-generated method stub
		// MoXianLog.writeLogtoFile(TAG_LOGIN,
		// "----------pingFailed-----------");
		ShowUtil.log(TAG_LOGIN, "---------pingFailed----------");
		pingFailed = true;
		if (!isLogOut) {
			authChanged();
		}
	}

	private final int SMACK_EXCEPTION_CODE=-1;
	/**
	 * 连接服务器
	 */
	private int doXmppConnect() {
		if (isLogOut) {
			return 0;
		}
		if (xmppConnection == null) {
			initXmppConnection();
		}
		if (xmppConnection != null && !xmppConnection.isConnected()) {
			try {
				ShowUtil.log(TAG_LOGIN,
						"---------xmppConnection connecting----------");
				xmppConnection.connect();
			} catch (SmackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return SMACK_EXCEPTION_CODE;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// BaseApplication.isXmppConnected = false;
				e.printStackTrace();
			}
		} else {
			ShowUtil.log(TAG_LOGIN, "---------xmpp connected----------");
		}
		return 1;
	}

	/**
	 * 登录认证服务器
	 */
	private int doXmppServiceLogin() {
		if (isLogOut) {
			return 0;
		}
		if (xmppConnection != null) {
			if (xmppConnection.isConnected()
					&& !xmppConnection.isAuthenticated()) {
				String userId = IMClient.getInstance().getSSOUserId();
				String token = IMClient.getInstance().getSSOToken();
				ShowUtil.log(TAG_LOGIN, "UserId=" + userId + " Token=" + token);
				try {
					ShowUtil.log(TAG_LOGIN,
							"---------doXmppServiceLogin----------");
					Roster roster = Roster.getInstanceFor(xmppConnection);
					roster.setRosterLoadedAtLogin(false);// 默认加载好友列表，这边取消加载
					xmppConnection.login(userId + URLConfig.IM_SERVERNAME,
							token, URLConfig.IMResource);
					ShowUtil.log(TAG_LOGIN,
							"---------XmppServiceLogin  Success----------");
					sessionManager
							.sendBroadcastConnectSate(MessageInfoReceiver.EVENT_LOGIN_SUCCESS);
					pingFailed = false;
					ChatOfflineManager.getInstance().loadOfflineMessages(xmppConnection,sessionManager);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SmackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return SMACK_EXCEPTION_CODE;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return 1;
	}
	private Runnable loginRunable=new Runnable() {
		@Override
		public void run() {
			idle = false;// 设置非空闲
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(hasNetWrok()){
				int code1=doXmppConnect();
				int code2 = doXmppServiceLogin();
				if(!checkIsLogin()){
					portSelectIndex++;
				}
				idle = true;// 设置空闲
				if(code1==SMACK_EXCEPTION_CODE||code2==SMACK_EXCEPTION_CODE){
					if (!isLogOut) {
						authChanged();
					}
				}
			}else{
				idle = true;// 设置空闲
			}
		}
	};
	/**
	 * 执行连接和登录
	 */
	private synchronized void doCheckConnectAndLogin() {
		if (isLogOut) {
			return;
		}
		if (idle && !checkIsLogin()) {// 空闲状态才能进行连接和登录，防止多个线程同时进行。
			if (loginThreadPool==null){
				loginThreadPool = Executors.newFixedThreadPool(1);
			}
			loginThreadPool.execute(loginRunable);
		} else {
			if (!idle) {
				ShowUtil.log(TAG_LOGIN, "---------connect busy----------");
			} else {
				ShowUtil.log(TAG_LOGIN,
						"---------connected and authenticated----------");
			}
		}
	}

	private boolean hasNetWrok(){
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if(activeInfo!=null){
			return true;
		}else{
			return false;
		}
	}

	public static void wakeIMService() {
		if (instance != null) {
			instance.checkConnectAndLogin(true);
		}
	}

	public static void checkIMConnectAndLogin() {
		if (instance != null) {
			instance.checkConnectAndLogin(false);
		}
	}

	/**
	 * 检查连接和登录
	 */
	private void checkConnectAndLogin(boolean isWakeService) {
		if (canCheckLogin) {
			ShowUtil.log(TAG_LOGIN, "---------check connect and Login----------");
			checkPingResponseTime();
			if(pingFailed){
				if (!isLogOut) {
					authChanged();
				}
			}else{
				doCheckConnectAndLogin();
			}
		}
	}

	/**
	 * 连接和登录服务器
	 */
	public void startConnectService() {
		ShowUtil.log(TAG_LOGIN, "---------startConnectService----------");
		authChanged();
	}

	public synchronized void authChanged() {
		if(idle){
			doExit();
			isLogOut = false;
			canCheckLogin = true;
			doCheckConnectAndLogin();
		}
	}

	/**
	 * 检查是否连接
	 *
	 * @return
	 */
	public boolean checkIsLogin() {
		if (instance != null && xmppConnection != null) {
			return xmppConnection.isAuthenticated()
					&& xmppConnection.isConnected();
		}
		return false;
	}

	/**
	 * 连接改变
	 *
	 * @param isConnected
	 */
	public void connectChanged(boolean isConnected) {
		if (!isLogOut) {
			if (!isConnected) {
				pingFailed = true;
				doCheckConnectAndLogin();
			}
			sessionManager.sendBroadcastConnectSate(MessageInfoReceiver.EVENT_CONNECTION_CHANGED);
		}
	}

	/**
	 * 获取tcp连接对象
	 *
	 * @return
	 */
	public XMPPTCPConnection getXmppConnection() {
		// TODO Auto-generated method stub
		if (xmppConnection == null) {
			initXmppConnection();
		}
		return xmppConnection;
	}

	public interface OnMessageSendListener {
		public void onSuccess(MessageBean messageBean);
	}
}
