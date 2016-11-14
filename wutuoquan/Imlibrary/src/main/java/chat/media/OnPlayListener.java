package chat.media;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public interface OnPlayListener {
    void onPrepared();

    void onCompletion();

    void onInterrupt();

    void onError(String var1);

    void onPlaying(long var1);
}