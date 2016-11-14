package chat.session.viewholder;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.imlibrary.R;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;
import java.util.HashMap;

import chat.base.IMClient;
import chat.common.util.output.ShowUtil;
import chat.common.util.sys.ScreenUtil;
import chat.image.DisplayImageConfig;
import chat.image.ImageUploadEntity;
import chat.image.ImageUtils;
import chat.image.ProgressImageView;
import chat.listener.ImImageLoadingListener;
import chat.session.bean.IMChatImageBody;
import chat.session.enums.AttachStatusEnum;
import chat.session.enums.MsgStatusEnum;

/**
 *   子布局浏览基类
 */
public abstract class MsgViewHolderThumbBase extends MsgViewHolderBase {

    protected ProgressImageView thumbnail;
    private ImageView iv_content_img;
    private ImageUtils.ImageSize limitSize = new ImageUtils.ImageSize();
    @Override
    protected void inflateContentView() {
        thumbnail = findViewById(R.id.message_item_thumb_thumbnail);
        iv_content_img = findViewById(R.id.iv_content_img);
        int screenWidth = ShowUtil.getScreenSize(IMClient.getInstance().getContext(), ShowUtil.ScreenEnum.WIDTH);
        int screenHeight = ShowUtil.getScreenSize(IMClient.getInstance().getContext(), ShowUtil.ScreenEnum.HEIGHT);
        limitSize.width = screenWidth * 2 / 5;
        limitSize.height = screenHeight * 2 / 5;
    }

    @Override
    protected void bindContentView() {
        IMChatImageBody msgAttachment = message.getAttachment();
        String path = msgAttachment.getAttr1();
        String thumbPath = msgAttachment.getLocalUrl();
        if(!TextUtils.isEmpty(thumbPath)){
            path="file:///"+ Uri.decode(thumbPath);
        }
        boolean resizeFlag= setImageParams();
        final ImImageLoadingListener listener = new ImImageLoadingListener(
                resizeFlag, false,limitSize);
        listener.setImgInitView(iv_content_img);
        thumbnail.setTag(path);
        IMClient.sImageLoader.displayThumbnailImage(path,
                thumbnail,
                DisplayImageConfig.getDisplayImageOptions(), listener,
                loadingProgressListener, 250, 260, R.drawable.bg_img_fail);
        refreshStatus();
    }
    /**
     * 图片加载监听器
     */
    private ImageLoadingProgressListener loadingProgressListener = new ImageLoadingProgressListener() {

        @Override
        public void onProgressUpdate(String imageUri, View view, int current,
                                     int total) {
            if (view == null)
                return;
            ProgressImageView imageView = (ProgressImageView) view;
            int progress = 0;
            if (total != 0) {
                progress = current / total;
            }
            imageView.updateProgress(progress);
            ShowUtil.log(view.getContext(), "current=" + current + " total="
                    + total + " progress=" + progress);
            imageView.invalidate();
        }
    };
    /**
     * @return boolean
     * @Title: setImageParam
     * @Description: 设置图片显示宽高
     */
    private boolean setImageParams() {
        IMChatImageBody imageBody= message.getAttachment();
        boolean resizeFlag = true;
        if (imageBody.getWidth() > 0) {
            ImageUtils.ImageSize size = ImageUtils.reSize(imageBody.getWidth(),
                    imageBody.getHeight(), limitSize);
            if (size != null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        size.width, size.height);
                thumbnail.setLayoutParams(layoutParams);
                iv_content_img.setLayoutParams(layoutParams);
            }
            resizeFlag = false;
        } else {
            File file = new File(imageBody.getLocalUrl() + "");
            if (file.exists()) {
                ImageUtils.ImageSize bmpSize = ImageUtils.getBitmapSize(file
                        .getAbsolutePath());
                ImageUtils.ImageSize size = ImageUtils.reSize(bmpSize.width,
                        bmpSize.height, limitSize);
                if (size != null) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            size.width, size.height);
                    thumbnail.setLayoutParams(layoutParams);
                    iv_content_img.setLayoutParams(layoutParams);
                }
                resizeFlag = false;
            } else {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        limitSize.width, limitSize.height);
                thumbnail.setLayoutParams(layoutParams);
                iv_content_img.setLayoutParams(layoutParams);
            }
        }
        return resizeFlag;
    }
    private void refreshStatus() {
        IMChatImageBody attachment = message.getAttachment();
        if (TextUtils.isEmpty(attachment.getAttr1()) && TextUtils.isEmpty(attachment.getLocalUrl())) {
            if (message.getAttachStatus() == AttachStatusEnum.fail || message.getMsgStatus() == MsgStatusEnum.fail) {
                alertButton.setVisibility(View.VISIBLE);
            } else {
                alertButton.setVisibility(View.GONE);
            }
        }
        setProgress(message.getMsgStatus() == MsgStatusEnum.sending || message.getAttachStatus() == AttachStatusEnum.transferring,attachment.getLocalUrl());
    }
    private void setProgress(boolean isProgressMode,String path){
        HashMap<String, ImageUploadEntity> uploadList = getAdapter().getUploadList();
        if (uploadList!=null&&uploadList.containsKey(path)) {
            ImageUploadEntity entity = uploadList.get(path);
            thumbnail.setIsProgressMode(isProgressMode);
            entity.setImageView(thumbnail);
        }
    }
    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static int getImageMinEdge() {
        return (int) (76.0 / 320.0 * ScreenUtil.screenWidth);
    }

    protected abstract String thumbFromSourceFile(String path);
}
