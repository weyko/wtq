package video.itguy.wxlikevideo.views;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.imlibrary.R;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import video.itguy.utils.ScalableType;
import video.itguy.utils.ScaleManager;
import video.itguy.utils.Size;

/**
 * Description:
 * Created  by: weyko on 2016/6/15.
 */
public class ScalableVideoView extends TextureView implements TextureView.SurfaceTextureListener,
        MediaPlayer.OnVideoSizeChangedListener {

    protected MediaPlayer mMediaPlayer;
    protected ScalableType mScalableType = ScalableType.NONE;

    public ScalableVideoView(Context context) {
        this(context, null);
    }

    public ScalableVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScalableVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.scaleStyle, 0, 0);
        if (a == null) {
            return;
        }

        String scaleType = a.getString(R.styleable.scaleStyle_scalableType);
        a.recycle();
        mScalableType = ScalableType.valueOf(scaleType.toUpperCase());
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(surface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMediaPlayer == null) {
            return;
        }

        if (isPlaying()) {
            stop();
        }
        release();
        mMediaPlayer = null;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        scaleVideoSize(width, height);
    }

    private void scaleVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
            return;
        }

        Size viewSize = new Size(getWidth(), getHeight());
        Size videoSize = new Size(videoWidth, videoHeight);
        ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
        Matrix matrix = scaleManager.getScaleMatrix(mScalableType);
        if (matrix != null) {
            setTransform(matrix);
        }
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            setSurfaceTextureListener(this);
        } else {
            mMediaPlayer.reset();
        }
    }

    public void setRawData(@RawRes int id) throws IOException {
        AssetFileDescriptor afd = getResources().openRawResourceFd(id);
        setDataSource(afd);
    }

    public void setAssetData(@NonNull String assetName) throws IOException {
        AssetManager manager = getContext().getAssets();
        AssetFileDescriptor afd = manager.openFd(assetName);
        setDataSource(afd);
    }

    private void setDataSource(@NonNull AssetFileDescriptor afd) throws IOException {
        setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        afd.close();
    }

    public void setDataSource(@NonNull String path) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(path);
    }

    public void setDataSource(@NonNull Context context, @NonNull Uri uri,
                              @Nullable Map<String, String> headers) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(context, uri, headers);
    }

    public void setDataSource(@NonNull Context context, @NonNull Uri uri) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(context, uri);
    }

    public void setDataSource(@NonNull FileDescriptor fd, long offset, long length)
            throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(fd, offset, length);
    }

    public void setDataSource(@NonNull FileDescriptor fd) throws IOException {
        initializeMediaPlayer();
        mMediaPlayer.setDataSource(fd);
    }

    public void setScalableType(ScalableType scalableType) {
        mScalableType = scalableType;
        scaleVideoSize(getVideoWidth(), getVideoHeight());
    }

    public void prepare(@Nullable MediaPlayer.OnPreparedListener listener)
            throws IOException, IllegalStateException {
        mMediaPlayer.setOnPreparedListener(listener);
        mMediaPlayer.prepare();
    }

    public void prepareAsync(@Nullable MediaPlayer.OnPreparedListener listener)
            throws IllegalStateException {
        mMediaPlayer.setOnPreparedListener(listener);
        mMediaPlayer.prepareAsync();
    }

    public void prepare() throws IOException, IllegalStateException {
        prepare(null);
    }

    public void prepareAsync() throws IllegalStateException {
        prepareAsync(null);
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }

    public void setLooping(boolean looping) {
        if (mMediaPlayer != null)
            mMediaPlayer.setLooping(looping);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pause() {
        if (mMediaPlayer != null)
            mMediaPlayer.pause();
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekTo(msec);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void start() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    public void stop() {
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }
}
