package chat.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public final class AudioPlayer {
    public static final String TAG = "AudioPlayer";
    private MediaPlayer mPlayer;
    private String mAudioFile;
    private long mIntervalTime;
    private AudioManager audioManager;
    private OnPlayListener mListener;
    private boolean mCanPlay;
    private int audioStreamType;
    private static final int WHAT_COUNT_PLAY = 0;
    private static final int WHAT_DECODE_SUCCEED = 1;
    private static final int WHAT_DECODE_FAILED = 2;
    private Handler mHandler;
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;

    public AudioPlayer(Context var1) {
        this(var1, (String)null, (OnPlayListener)null);
    }

    public AudioPlayer(Context var1, String var2, OnPlayListener var3) {
        this.mIntervalTime = 500L;
        this.mCanPlay = false;
        this.audioStreamType = 0;
        this.mHandler = new Handler() {
            public void handleMessage(Message var1) {
                switch(var1.what) {
                    case 0:
                        if(AudioPlayer.this.mListener != null) {
                            AudioPlayer.this.mListener.onPlaying((long)AudioPlayer.this.mPlayer.getCurrentPosition());
                        }

                        this.sendEmptyMessageDelayed(0, AudioPlayer.this.mIntervalTime);
                        return;
                    case 1:
                        AudioPlayer.this.startInner();
                    default:
                        return;
                    case 2:
                        Log.d("AudioPlayer", "convert() error: " + AudioPlayer.this.mAudioFile);
                }
            }
        };
        this.onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int var1) {
                String var2 = "onAudioFocusChange:" + var1;
                Log.d("Vincent", var2);
                switch(var1) {
                    case -3:
                        if(AudioPlayer.this.isPlaying()) {
                            AudioPlayer.this.mPlayer.setVolume(0.1F, 0.1F);
                        }
                        break;
                    case -2:
                        AudioPlayer.this.stop();
                        return;
                    case -1:
                        AudioPlayer.this.stop();
                        return;
                    case 0:
                    default:
                        break;
                    case 1:
                        if(AudioPlayer.this.isPlaying()) {
                            AudioPlayer.this.mPlayer.setVolume(1.0F, 1.0F);
                            return;
                        }
                }

            }
        };
        this.audioManager = (AudioManager)var1.getSystemService(Context.AUDIO_SERVICE);
        this.mAudioFile = var2;
        this.mListener = var3;
    }

    public final void setDataSource(String var1) {
        if(!TextUtils.equals(var1, this.mAudioFile)) {
            this.mAudioFile = var1;
            this.mCanPlay = !this.needConvert();
        }

    }

    public final void setOnPlayListener(OnPlayListener var1) {
        this.mListener = var1;
    }

    public final OnPlayListener getOnPlayListener() {
        return this.mListener;
    }

    public final void start(int var1) {
        this.audioStreamType = var1;
        this.startPlay();
    }

    public final void stop() {
        if(this.mPlayer != null) {
            this.endPlay();
            if(this.mListener != null) {
                this.mListener.onInterrupt();
            }
        }

    }

    public final boolean isPlaying() {
        return this.mPlayer != null && this.mPlayer.isPlaying();
    }

    public final long getDuration() {
        return this.mPlayer != null?(long)this.mPlayer.getDuration():0L;
    }

    public final long getCurrentPosition() {
        return this.mPlayer != null?(long)this.mPlayer.getCurrentPosition():0L;
    }

    public final void seekTo(int var1) {
        this.mPlayer.seekTo(var1);
    }

    private void startPlay() {
        Log.d("AudioPlayer", "start() called");
        this.endPlay();
        if(!this.mCanPlay) {
            Thread var1;
            (var1 = new Thread(new Runnable() {
                public void run() {
                    if(AudioPlayer.this.mHandler != null) {
                        AudioPlayer.this.mHandler.sendEmptyMessage(AudioPlayer.this.convert()?1:2);
                    }

                }
            })).setPriority(10);
            var1.start();
        } else {
            this.startInner();
        }
    }

    private void endPlay() {
        this.audioManager.abandonAudioFocus(this.onAudioFocusChangeListener);
        if(this.mPlayer != null) {
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
            this.mHandler.removeMessages(0);
        }

    }

    private boolean needConvert() {
        return Build.VERSION.SDK_INT < 12 && b(this.mAudioFile);
    }

    private boolean convert() {
        this.mCanPlay = false;
        String var1;
        if(!TextUtils.isEmpty(var1 = a(this.mAudioFile))) {
            this.mCanPlay = true;
            this.mAudioFile = var1;
            this.deleteOnExit();
        }

        return this.mCanPlay;
    }

    private void startInner() {
        this.mPlayer = new MediaPlayer();
        this.mPlayer.setLooping(false);
        this.mPlayer.setAudioStreamType(this.audioStreamType);
        if(this.audioStreamType == 3) {
            this.audioManager.setSpeakerphoneOn(true);
        } else {
            this.audioManager.setSpeakerphoneOn(false);
        }

        this.audioManager.requestAudioFocus(this.onAudioFocusChangeListener, this.audioStreamType, 2);
        this.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer var1) {
                Log.d("AudioPlayer", "player:onPrepared");
                AudioPlayer.this.mHandler.sendEmptyMessage(0);
                if(AudioPlayer.this.mListener != null) {
                    AudioPlayer.this.mListener.onPrepared();
                }

            }
        });
        this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer var1) {
                Log.d("AudioPlayer", "player:onCompletion");
                AudioPlayer.this.endPlay();
                if(AudioPlayer.this.mListener != null) {
                    AudioPlayer.this.mListener.onCompletion();
                }

            }
        });
        this.mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer var1, int var2, int var3) {
                AudioPlayer.this.endPlay();
                if(AudioPlayer.this.mListener != null) {
                    AudioPlayer.this.mListener.onError(String.format("OnErrorListener what:%d extra:%d", new Object[]{Integer.valueOf(var2), Integer.valueOf(var3)}));
                }

                return true;
            }
        });

        try {
            if(this.mAudioFile != null) {
                this.mPlayer.setDataSource(this.mAudioFile);
                this.mPlayer.prepare();
                this.mPlayer.start();
            } else {
                if(this.mListener != null) {
                    this.mListener.onError("no datasource");
                }

            }
        } catch (Exception var2) {
            var2.printStackTrace();
            this.endPlay();
            if(this.mListener != null) {
                this.mListener.onError("Exception\n" + var2.toString());
            }

        }
    }

    private void deleteOnExit() {
        File var1;
        if((var1 = new File(this.mAudioFile)).exists()) {
            var1.deleteOnExit();
        }

    }
    public static boolean b(String var0) {
        boolean var1 = false;
        if(!TextUtils.isEmpty(var0)) {
            BufferedInputStream var2 = null;
            boolean var8 = false;
            label99: {
                try {
                    var8 = true;
                    var2 = new BufferedInputStream(new FileInputStream(var0));
                    byte[] var14 = new byte[2];
                    var2.read(var14, 0, 2);
                    if((var14[0] & 255) == 255) {
                        if((var14[1] & 246) == 240) {
                            var1 = true;
                            var8 = false;
                        } else {
                            var8 = false;
                        }
                    } else {
                        var8 = false;
                    }
                    break label99;
                } catch (Exception var12) {
                    var12.printStackTrace();
                    var8 = false;
                } finally {
                    if(var8) {
                        if(var2 != null) {
                            try {
                                var2.close();
                            } catch (IOException var9) {
                                var9.printStackTrace();
                            }
                        }

                    }
                }

                if(var2 != null) {
                    try {
                        var2.close();
                    } catch (IOException var10) {
                        var10.printStackTrace();
                    }

                    return var1;
                }

                return var1;
            }

            try {
                var2.close();
            } catch (IOException var11) {
                var11.printStackTrace();
            }
        }
        return var1;
    }
    public static String a(String var0) {
        String var2 = null;
        File var1;
        if(!TextUtils.isEmpty(var0) && (var1 = new File(var0)).exists() && var1.isFile()) {
            String var3 = var2 = var1.getName();
            int var4;
            if((var4 = var2.lastIndexOf(".")) != -1) {
                var3 = var2.substring(0, var4);
            }

            var3 = var3 + ".wav";
            var2 = (new File(var1.getParent(), var3)).getAbsolutePath();
        }

        String var5 = var2;
        int var6 = -1;
        if(!TextUtils.isEmpty(var5)) {
            if(!(new File(var5)).exists()) {
//                var6 = AacUtil.ToWav(var0, var5);
            } else {
                var6 = 1;
            }
        }

        return var6 >= 0?var5:null;
    }
}
