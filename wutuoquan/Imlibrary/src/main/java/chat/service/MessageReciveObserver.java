package chat.service;


public interface MessageReciveObserver {
	public void handleReciveMessage(int event, String msgCode, int sendStatus, String sessionId, long time, String url, String nickName, String roomId);
}
