package chat.session.util;

import android.media.AudioManager;

import chat.base.BaseActivity;
public class VoiceControl {
	// 打开扬声器
	public static boolean openSpeaker(BaseActivity activity,
			AudioManager audioManager) {
		boolean isSetted=false;
		try {
			if (!audioManager.isSpeakerphoneOn()){
				audioManager.setSpeakerphoneOn(true);
				audioManager.setMode(AudioManager.MODE_NORMAL);
			}
			isSetted=true;
			System.out.println("扬声器模式打开");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSetted;
	}

	/**
	 * 打开听筒
	 */
	public static boolean openEarphone(BaseActivity activity,
			AudioManager audioManager) {
		boolean isSetted=false;
		try {
			if (audioManager.isSpeakerphoneOn()){
				audioManager.setSpeakerphoneOn(false);
				int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);
				audioManager.setMode(AudioManager.MODE_IN_CALL);
				audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
			}
			isSetted=true;
			System.out.println("听筒模式打开");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSetted;
	}
}
