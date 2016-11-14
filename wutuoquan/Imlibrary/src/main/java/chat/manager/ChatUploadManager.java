package chat.manager;

import android.text.TextUtils;

import com.imlibrary.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.fileupload.UploadBean;
import chat.common.fileupload.UploadFileTasks;
import chat.common.util.output.ShowUtil;
import chat.image.ImageUploadEntity;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.IMChatVideoBody;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.util.ChatUtil;

/**
 * Description: 文件上传管理类
 */
public class ChatUploadManager {
    public static final String UPLOADFILE_KEY = "files";
    private static ChatUploadManager instance;
    private OnUploadListener onUploadListener;
    // 图片上传的线程池
    private Executor poorImgUpload = Executors.newFixedThreadPool(3);

    private HashMap<String, ImageUploadEntity> uploadList;

    public static ChatUploadManager getInstance() {
        if (instance == null)
            instance = new ChatUploadManager();
        return instance;
    }

    /**
     * 上传文件
     */
    public void uploadFile(final MessageBean message, OnUploadListener onUploadListener) {
        MsgTypeEnum type = message.getMsgType();
        String filePath = "";
        int userid=27889;
//        String userid=BaseApplication.getInstance().getSSOUserId();
        int fileType=0;
        Map<String, Object> params = new HashMap<String, Object>();
        switch (type) {
            case image:
                IMChatImageBody imageBody= message.getAttachment();
                filePath = imageBody.getLocalUrl();
                break;
            case audio:
                IMChatAudioBody audioBody= message.getAttachment();
                filePath = audioBody.getLocalUrl();
                fileType=1;
                break;
            case video:
                IMChatVideoBody videoBody= message.getAttachment();
                filePath = videoBody.getLocalUrl();
                fileType=3;
                break;
            default:
                break;
        }
        ImageUploadEntity uploadListener = new ImageUploadEntity();
        try {
            MyUploadFileTasks fileTasks = new MyUploadFileTasks(params,null,
                    UPLOADFILE_KEY, filePath,
                    URLConfig.UP_LOAD+fileType+"/"+userid, message, type,
                    uploadListener,onUploadListener);
            fileTasks.executeOnExecutor(poorImgUpload);
        } catch (Exception e) {
            e.printStackTrace();
            if (onUploadListener != null && message != null) {
                message.setMsgStatus(MsgStatusEnum.fail);
                onUploadListener.onUploadFail(message);
            }
        }
    }
    /**
     * 上传监听类
     */
    public interface OnUploadListener {
        public void onUploadPre(String filePath, ImageUploadEntity upImgloadListener);

        public void onUploadPost(String filePath);

        public void onUploadFail(MessageBean messageBean);
    }

    /**
     * 文件上传
     */
    class MyUploadFileTasks extends UploadFileTasks {
        private MessageBean message;
        private MsgTypeEnum type;
        private ImageUploadEntity upImgloadListener;
        private String filePath;
        private OnUploadListener onUploadListener;
        public MyUploadFileTasks(Map<String, Object> headMap,Map<String, Object> map, String fileKey,
                                 String filePath, String url, MessageBean message, MsgTypeEnum type,
                                 ImageUploadEntity upImgloadListener, OnUploadListener onUploadListener) {
            super(headMap, map, fileKey, filePath, url, upImgloadListener);
            this.message = message;
            this.type = type;
            this.filePath = filePath;
            this.upImgloadListener = upImgloadListener;
            this.onUploadListener = onUploadListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (type == MsgTypeEnum.image) {
                if (onUploadListener != null) {
                    onUploadListener.onUploadPre(filePath, upImgloadListener);
                }
            }
        }

        @Override
        protected void onPostExecute(final UploadBean bean) {
            super.onPostExecute(bean);
            if (onUploadListener != null) {
                onUploadListener.onUploadPost(filePath);
            }
            if (bean != null && bean.isResult()) {
                String url = bean.getData().getSavePath();
                if (TextUtils.isEmpty(url))
                    return;
                String sendBody="";
                switch (type) {
                    case image:
                        IMChatImageBody imageBody = message.getAttachment();
                        imageBody.setAttr1(url);
                        message.setMsgStatus(MsgStatusEnum.success);
                        sendBody=imageBody.getSendBody();
                        break;
                    case audio:
                        IMChatAudioBody voiceBody = message.getAttachment();
                        voiceBody.setAttr1(url);
                        sendBody=voiceBody.getSendBody();
                        break;
                    case video:
                        IMChatVideoBody videoBody = message.getAttachment();
                        videoBody.setAttr1(url);
                        message.setMsgStatus(MsgStatusEnum.success);
                        sendBody=videoBody.getSendBody();
                        break;
                    default:
                        break;
                }
                if(TextUtils.isEmpty(sendBody))
                    return;
                ChatMessageManager.getInstance().updateMessage(message);
                ChatUtil.getInstance().sendChatMessage(message, sendBody);
            } else {
                if (bean != null) {
                    if (bean.getStatus()==-2) {
                        if (type == MsgTypeEnum.image)
                            ShowUtil.showToast(IMClient.getInstance().getContext(),
                                    IMClient.getInstance().getContext().getString(R.string.chat_pic_limit_hint));
                    }
                }
                if (onUploadListener != null && message != null) {
                    onUploadListener.onUploadFail(message);
                }
            }

        }
    }
    public void addUpload(String url, ImageUploadEntity uploadEntity) {
        if (uploadList != null && !uploadList.containsKey(url)) {
            uploadList.put(url, uploadEntity);
        }
    }

    public void removeUpload(String url) {
        if (uploadList != null && uploadList.containsKey(url)) {
            uploadList.remove(url);
        }
    }
}
