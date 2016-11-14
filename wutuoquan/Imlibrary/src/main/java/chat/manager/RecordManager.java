package chat.manager;

import android.content.Context;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.imlibrary.R;

import java.io.File;

import chat.common.config.Constant;
import chat.common.util.output.ShowUtil;

public class RecordManager {

	private static final int MSG_VOICE_CHANGE = 1;
	private static final int MSG_VOICE_STOP = 2;
	private Handler handler = new Handler(new Handler.Callback() {

		public boolean handleMessage(Message msg) {

			if (msg.what == MSG_VOICE_CHANGE) {
				if (listener != null) {
					listener.onRecordVoiceChange((Integer) msg.obj);
				}
			}else if (msg.what == MSG_VOICE_STOP){
				notifyError();
				cancel();
			}

			return false;
		}
	});

	public interface RecordStateListener {

		public void onRecordStartLoading();

		public void onRecordStart();

		public void onRecordFinish(String file);

		public void onRecordCancel();

		public void onRecordVoiceChange(int v);

		public void onTimeChange(int millseconds);

		public void onRecordError();

		public void onTooShoot();

	}
	public void notifyStartLoading() {
		if (listener != null) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					listener.onRecordStartLoading();
				}
			});

		}
	}

	private void notifyTooShoot() {

		if (listener != null) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					listener.onTooShoot();

				}
			});

		}
	}

	private void notifyStart() {
		if (listener != null) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					listener.onRecordStart();

				}
			});

		}
	}

	private void notifyFinish(final String file) {

		if (listener != null) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					listener.onRecordFinish(file);

				}
			});

		}
	}

	private void notifyCancal() {

		if (listener != null) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					listener.onRecordCancel();

				}
			});

		}
	}

	private void notifyVoiceChange(int v) {

		Message message = new Message();
		message.what = MSG_VOICE_CHANGE;
		message.obj = v;

		handler.sendMessage(message);

	}

	private RecordStateListener listener;

	private Context context=null;
	
	public RecordManager(){
		
	}
	
	public RecordManager(Context context) {
		this.context = context;
	}

	MediaRecorder mr;

	private String name;
	private File file;

	public synchronized String genName() {

		return System.currentTimeMillis() + "_my.amr";
	}

	public boolean isRunning() {
		return running;
	}

	private long startTime = System.currentTimeMillis();

	public void startRecord(String savePath) {
		file = null;
		try {//fix by weyko 2015.11.16 修改语音录制触发点，保证只有在弹出录音框才开始录制
			if (mr != null) {
				mr.release();
				mr = null;
			}
			mr = new MediaRecorder();
			mr.setAudioSource(AudioSource.MIC);
			mr.setOutputFormat(OutputFormat.RAW_AMR);//OutputFormat.RAW_AMR
			mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			//以下是参考环信的3项配置.
			mr.setAudioChannels(1);
			mr.setAudioSamplingRate(8000);
			mr.setAudioEncodingBitRate(64);
		      
			if (savePath == null || savePath.length() <= 0) {
				savePath = Constant.VOICES_FOLDER;
			}
			name = savePath + genName();
			file=new File(name);
			// 编码
			mr.setOutputFile(name);

			long t1=System.currentTimeMillis();
			
			mr.prepare();

			notifyStart();

			// 做些准备工作
			mr.start();
			
			long t2=(System.currentTimeMillis()-t1);
			
			if(t2>800){
				if(context!=null){
					ShowUtil.showToast(context, context.getString(R.string.chat_record_to_short));
				}
				cancel();
				return;
			}

			startTime = System.currentTimeMillis();

			running = true;

			//timer = new Timer();

			notifyStartLoading();

			new Thread(new Runnable() {
				public void run() {
					try
					{
						while (running) {
							int i = mr.getMaxAmplitude();//max value 32767
							if (listener != null) {
								int millseconds = (int) ((System.currentTimeMillis() - startTime));
								notifyVoiceSecondsChange(millseconds);
								notifyVoiceChange(i);
							}
							if(file.length()<=0){
								handler.sendEmptyMessage(MSG_VOICE_STOP);
							}
							SystemClock.sleep(100L);
						}
					}
					catch (Exception localException) {
						localException.printStackTrace();
						//EMLog.e("voice", localException.toString());
					}
				}
			}).start();
			/*timer.schedule(new TimerTask() {
				@Override
				public void run() {
				}
			}, 0, 50);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//notifyError();
		}
	}

	//private Timer timer = new Timer();

	private void notifyError() {
		handler.post(new Runnable() {
			public void run() {
				listener.onRecordError();
			}
		});
	}

	private void notifyVoiceSecondsChange(final int millseconds) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				listener.onTimeChange(millseconds);
			}
		});
	}

	private boolean running = false;

	private void stopVolumeListener() {
		/*if (timer != null) {
			timer.cancel();
		}*/
	}

	public void waitRunning() {
	}

	public synchronized boolean stopRecord() {
		stopVolumeListener();
		if (mr != null) {
			try {
				mr.stop();
				mr.release();
				mr = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

			long delay = System.currentTimeMillis() - startTime;

			if (delay <= 1000) {
				notifyTooShoot();
			} else {
				notifyFinish(name);
			}
			if ((file == null) || (!file.exists()) || (!file.isFile())) {
				return false;
			}
			if (file.length() == 0L) {
				file.delete();
				return false;
			}
		}
		running = false;
		return true;
	}

	public synchronized void cancel() {
		stopVolumeListener();
		if (mr != null) {
			try {
				mr.stop();
				mr.release();
				mr = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			File file = new File(name);
			file.deleteOnExit();
			notifyCancal();
		}
		running = false;
	}

	public void setVoiceVolumeListener(RecordStateListener listener) {
		this.listener = listener;
	}
}
