package chat.media;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;
import com.imlibrary.R;
import java.io.File;
import chat.common.config.Constant;
import chat.common.util.output.ShowUtil;
import chat.common.util.string.MD5;
import video.itguy.utils.FileUtil;
import video.itguy.wxlikevideo.recorder.WXLikeVideoRecorder;
import video.itguy.wxlikevideo.views.CameraPreviewView;

/**
 * Description:
 * Created  by: weyko on 2016/6/14.
 */
public class CameraUtil {
    // 最长录制时间6秒
    private static final long MAX_RECORD_TIME = 6000;
    // 最短录制时间6秒
    private static final long MIN_RECORD_TIME = 1000;
    // 输出宽度
    private static final int OUTPUT_WIDTH = 320;
    // 输出高度
    private static final int OUTPUT_HEIGHT = 240;
    // 宽高比
    private static final float RATIO = 1f * OUTPUT_WIDTH / OUTPUT_HEIGHT;
    private String TAG = "CameraUtil";
    private Camera mCamera;
    private WXLikeVideoRecorder mRecorder;
    private Activity activity;
    private int cameraId;
    //是否正在录制
    private boolean isRecording = false;
    private Dialog popupWindow;
    private CameraPreviewView camera_chat;
    //录制按钮
    private TextView record_camera_chat;
    //录制提示文字
    private TextView hint_camera_chat;
    //录制进度条
    private TextView progress_camera_chat;
    //进度条宽度
    private int width;
    private ValueAnimator va;
    private boolean isCancelRecord = false;
    //录制时间
    private long cameraTime = 0;
    private Handler handler = new Handler();
    //本地获取视频请求码
    private int localRequestCode;
    //监听视频选择
    private VideoSelectListener listener;
    //视频录制监听器
    private OnCameraLitener onCameraLitener;
    public CameraUtil(Activity activity,String sessionId) {
        this.activity = activity;
        cameraId = CameraHelper.getDefaultCameraID();
        initPopWindow();
        initCamera(sessionId);
    }

    /***
     * 存储文件夹
     * String sessionId 会话ID
     * @return
     */
    public static String getOutputMediaDir(String sessionId) {
        sessionId= MD5.getStringMD5(sessionId);
        File mediaStorageDir = new File(Constant.VIDEO_FOLDER+sessionId);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir.getAbsolutePath();
    }

    /**
     * 会话ID，用于区分对象
     * @param sessionId
     */
    private void initCamera(String sessionId) {
        mCamera = CameraHelper.getCameraInstance(cameraId);
        if (null == mCamera) {
            return;
        }
        // 初始化录像机
        mRecorder = new WXLikeVideoRecorder(activity, getOutputMediaDir(sessionId));
        mRecorder.setOutputSize(OUTPUT_WIDTH, OUTPUT_HEIGHT);
        camera_chat.setCamera(mCamera, cameraId);
        mRecorder.setCameraPreviewView(camera_chat);
        mRecorder.prepareCamera();
    }

    /**
     * 弹出录制窗口
     */
    public void showCameraWindow(OnCameraLitener onCameraLitener) {
        this.onCameraLitener=onCameraLitener;
        if (null == mCamera) {
            Toast.makeText(activity, "打开相机失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        initPopWindow();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hint_camera_chat.setVisibility(View.INVISIBLE);
            }
        }, 1500);
        if (!popupWindow.isShowing())
            popupWindow.show();
    }
    public void dismissWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            onPause();
        }
    }

    private void initPopWindow() {
        if (popupWindow == null) {
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_camera_chat, null);
            camera_chat = (CameraPreviewView) view.findViewById(R.id.camera_chat);
            record_camera_chat = (TextView) view.findViewById(R.id.record_camera_chat);
            hint_camera_chat = (TextView) view.findViewById(R.id.hint_camera_chat);
            progress_camera_chat = (TextView) view.findViewById(R.id.progress_camera_chat);
            popupWindow = new Dialog(activity, R.style.dialog_full_style);
            popupWindow.setContentView(view);
            record_camera_chat.setOnTouchListener(new RecordButtonTouchListener(this));
            progress_camera_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (width == 0)
                        width = progress_camera_chat.getMeasuredWidth();
                }
            });
        }
        initProgress(false);
    }

    private void initProgress(boolean isCancal) {
        if (width == 0)
            return;
        if (!isCancal) {
            hint_camera_chat.setVisibility(View.VISIBLE);
            hint_camera_chat.setText(R.string.hint_double_click);
        }
        progress_camera_chat.setVisibility(View.INVISIBLE);
        hint_camera_chat.setBackgroundColor(activity.getResources().getColor(R.color.tm));
        ViewGroup.LayoutParams params = progress_camera_chat.getLayoutParams();
        params.width = width;
        progress_camera_chat.setLayoutParams(params);
        progress_camera_chat.requestLayout();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            // 释放前先停止预览
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mRecorder.isRecording()) {
            Toast.makeText(activity, "正在录制中…", Toast.LENGTH_SHORT).show();
            return;
        }

        // initialize video camera
        if (prepareVideoRecorder()) {
            // 录制视频
            if (!mRecorder.startRecording()) {
                Toast.makeText(activity, "录制失败…", Toast.LENGTH_SHORT).show();
                return ;
            }
            cameraTime = System.currentTimeMillis();
            startAnimation();
        }
    }

    /**
     * 准备视频录制器
     *
     * @return
     */
    private boolean prepareVideoRecorder() {
        if (!FileUtil.isSDCardMounted()) {
            Toast.makeText(activity, "SD卡不可用！", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        mRecorder.stopRecording();
        String videoPath = mRecorder.getFilePath();
        // 没有录制视频
        if (null == videoPath) {
            return;
        }
        // 若取消录制，则删除文件，否则通知宿主页面发送视频
        if (isCancelRecord) {
            FileUtil.deleteFile(videoPath);
        } else {
            // 告诉宿主页面录制视频的路径
//            startActivity(new Intent(this, PlayVideoActiviy.class).putExtra(PlayVideoActiviy.KEY_FILE_PATH, videoPath));
        }
    }

    public void onPause() {
        // 页面销毁时要停止录制
        if (mRecorder != null) {
            boolean recording = mRecorder.isRecording();
            // 页面不可见就要停止录制
            mRecorder.stopRecording();
            // 录制时退出，直接舍弃视频
            if (recording) {
                FileUtil.deleteFile(mRecorder.getFilePath());
            }
        }
        releaseCamera();              // release the camera immediately on pause event
    }

    /**
     * 开始执行录制动画
     */
    private void startAnimation() {
        if (isRecording)
            return;
        va = ObjectAnimator.ofInt(width, 0);
        va.setDuration(MAX_RECORD_TIME);
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isRecording) {
                    isRecording = false;
                    if(popupWindow!=null){
                        popupWindow.dismiss();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = progress_camera_chat.getLayoutParams();
                params.width = value;
                progress_camera_chat.setLayoutParams(params);
                progress_camera_chat.requestLayout();
            }
        });
        va.start();
    }

    /**
     * 释放资源
     */
    public void onDestory() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        if (camera_chat != null) {
            camera_chat.onDestory();
        }
        if (va != null) {
            va.cancel();
            va = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if(onCameraLitener!=null)
            onCameraLitener=null;
        onPause();
    }

    /**
     * 从本地相册中选择视频
     */
    public void chooseVideoFromLocal(int localRequestCode, VideoSelectListener listener) {
        this.localRequestCode = localRequestCode;
        this.listener = listener;

    }

    /**
     * 迭代文件夹里所有文件
     */
    public void iteratesFileDir(File file) {

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                iteratesFileDir(f);
            }
        } else if (file.exists()) {
            //得到视频的缩略图
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        }
    }

    public interface VideoSelectListener {
        void onVideoPicked(File file, String md5);
    }

    private class RecordButtonTouchListener implements View.OnTouchListener {

        private static final int CANCEL_RECORD_OFFSET = -100;

        private float mDownX, mDownY;

        private CameraUtil cameraUtil;

        public RecordButtonTouchListener(CameraUtil cameraUtil) {
            this.cameraUtil = cameraUtil;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isCancelRecord = false;
                    mDownX = event.getX();
                    mDownY = event.getY();
                    handler.removeCallbacksAndMessages(null);
                    hint_camera_chat.setText(R.string.hint_moveup_cancal_camera);
                    hint_camera_chat.setBackgroundColor(activity.getResources().getColor(R.color.b_tm));
                    hint_camera_chat.setVisibility(View.VISIBLE);
                    progress_camera_chat.setVisibility(View.VISIBLE);
                    cameraUtil.startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = event.getY();
                    hint_camera_chat.setVisibility(View.VISIBLE);
                    if (y - mDownY < CANCEL_RECORD_OFFSET) {
                        if (!isCancelRecord) {
                            // cancel record
                            isCancelRecord = true;
                            hint_camera_chat.setText(R.string.hint_loosen_cancal_camera);
                            hint_camera_chat.setBackgroundColor(activity.getResources().getColor(R.color.holo_red_light));
                        }
                    } else {
                        hint_camera_chat.setText(R.string.hint_moveup_cancal_camera);
                        hint_camera_chat.setBackgroundColor(activity.getResources().getColor(R.color.b_tm));
                        isCancelRecord = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    hint_camera_chat.setVisibility(View.INVISIBLE);
                    progress_camera_chat.setVisibility(View.INVISIBLE);
                    if (va != null) {
                        va.cancel();
                    }
                    initProgress(true);
                    if (!isCancelRecord) {
                        long nowTime = System.currentTimeMillis();
                        if (nowTime - cameraTime > MIN_RECORD_TIME) {
                            if (popupWindow != null)
                                popupWindow.dismiss();
                            if(onCameraLitener!=null)
                                onCameraLitener.onCameraSuccess(mRecorder.getFilePath(),(mRecorder.getStopTime()-mRecorder.getStartTime()));
                        } else {
                            ShowUtil.showToast(activity, activity.getString(R.string.camera_time_short_hint));
                            isCancelRecord = true;
                        }
                    }
                    stopRecord();
                    isRecording=false;
                    break;
            }

            return true;
        }
    }
    public interface OnCameraLitener{
        public void onCameraSuccess(String filePath, long time);
    }
}
