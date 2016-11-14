package chat.listener;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imlibrary.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import chat.common.util.output.ShowUtil;
import chat.image.ImageUtils;

/**
 * Description:
 * Created  by: weyko on 2016/6/8.
 */
public class ImImageLoadingListener implements ImageLoadingListener {
    private boolean resizeFlag = false;
    private boolean isFatherLinearLayout = false;
    private LinearLayout view_loading_img;
    private TextView loadView;
    private ImageView iv_loading_img, iv_reload_img;
    private AnimationDrawable drawable;
    private ImageView iv_content_img;
    private ImageUtils.ImageSize limitSize;
    public ImImageLoadingListener(boolean flag, boolean isFatherLinearLayout,ImageUtils.ImageSize limitSize) {
        resizeFlag = flag;
        this.isFatherLinearLayout = isFatherLinearLayout;
        this.limitSize=limitSize;
    }

    public void setLoadView(LinearLayout view_loading_img,
                            TextView loadView, ImageView iv_loading_img,
                            ImageView iv_reload_img) {
        this.view_loading_img = view_loading_img;
        this.loadView = loadView;
        this.iv_loading_img = iv_loading_img;
        this.iv_reload_img = iv_reload_img;
    }

    public void setImgInitView(ImageView iv_content_img) {
        this.iv_content_img = iv_content_img;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        if (view != null) {
            ImageView imgView = (ImageView) view;
            imgView.setImageResource(R.drawable.bg_img_loading);
            view.setVisibility(View.GONE);
        }
        if (iv_content_img != null) {
            iv_content_img.setVisibility(View.VISIBLE);
            iv_content_img.setBackgroundResource(R.drawable.bg_img_loading);
        }
        if (view_loading_img != null)
            view_loading_img.setVisibility(View.VISIBLE);
        if (iv_loading_img != null) {
            drawable = (AnimationDrawable) iv_loading_img.getBackground();
            drawable.start();
        }
    }

    public void onLoadingComplete(String imageUri, View view,
                                  Bitmap loadedImage) {
        if(!imageUri.equals(view.getTag()))
            return;
        view.setVisibility(View.VISIBLE);
        if (iv_content_img != null) {
            iv_content_img.setVisibility(View.GONE);
        }
        if (view_loading_img != null)
            view_loading_img.setVisibility(View.GONE);
        if (drawable != null)
            drawable.stop();
        if (resizeFlag && loadedImage != null) {
            ImageUtils.ImageSize size = ImageUtils.reSize(loadedImage.getWidth(),
                    loadedImage.getHeight(), limitSize);
            if (isFatherLinearLayout) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        size.width, size.height);
                view.setLayoutParams(layoutParams);
            } else {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        size.width, size.height);
                view.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public void onLoadingFailed(final String imageUri, View view,
                                FailReason failReason) {
        if(view!=null)
        view.setVisibility(View.GONE);
        if (iv_content_img != null) {
            iv_content_img.setVisibility(View.VISIBLE);
            iv_content_img.setBackgroundResource(R.drawable.bg_img_fail);
        }
        if (drawable != null)
            drawable.stop();
        if (loadView != null) {
            loadView.setText(R.string.chat_img_loadfail);
            loadView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            iv_reload_img.setVisibility(View.VISIBLE);
        }
        String message = null;
        switch (failReason.getType()) { // 获取图片失败类型
            case IO_ERROR: // 文件I/O错误
                message = "Input/Output error";
                break;
            case DECODING_ERROR: // 解码错误
                message = "Image can't be decoded";
                break;
            case NETWORK_DENIED: // 网络延迟
                message = "Downloads are denied";
                break;
            case OUT_OF_MEMORY: // 内存不足
                message = "Out Of Memory error";
                break;
            case UNKNOWN: // 原因不明
                message = "Unknown error";
                break;
        }
        ShowUtil.log("weyko",
                "onLoadingFailed-----imageUri=" + imageUri
                        + "--------message----" + message);
    }

    @Override
    public void onLoadingCancelled(final String imageUri, View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        if (iv_content_img != null) {
            iv_content_img.setVisibility(View.VISIBLE);
            iv_content_img.setBackgroundResource(R.drawable.bg_img_fail);
        }
        if (drawable != null)
            drawable.stop();
        if (loadView != null) {
            loadView.setText(R.string.chat_img_loadfail);
            loadView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            iv_reload_img.setVisibility(View.VISIBLE);
        }
    }
}
