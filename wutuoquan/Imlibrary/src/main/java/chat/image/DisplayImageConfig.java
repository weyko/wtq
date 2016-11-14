package chat.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.imlibrary.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * @author liqing li.qing@moxiangroup.com
 * @ClassName: DisplayImageConfig
 * @Description: Imageloader加载图片配置
 * @Company moxian
 * @date 2015年3月25日 上午10:44:17
 */
public class DisplayImageConfig {

    /**
     * 默认头像请求服务器压缩后的图片->!tWxH.png
     */
    public static int headThumbnailSize = 150;
    /**
     * 登录用户历史记录头像
     */
    public static DisplayImageOptions userLoginItemImageOptions = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.default_head)
            .showImageForEmptyUri(R.drawable.default_head)
            .showImageOnFail(R.drawable.default_head).cacheInMemory(true)
            .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(180))
            .imageScaleType(ImageScaleType.EXACTLY).build();
    /**
     * 聊天用户历史记录头像
     */
    public static DisplayImageOptions getChatHeaderOptions(int defaultIcon) {
        DisplayImageOptions userChatItemImageOptions = new DisplayImageOptions.Builder()
                .showStubImage(defaultIcon)
                .showImageForEmptyUri(defaultIcon)
                .showImageOnFail(defaultIcon).cacheInMemory(true)
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(180))
                .imageScaleType(ImageScaleType.EXACTLY).build();
        return userChatItemImageOptions;
    }

    public static DisplayImageOptions normalAvatarImageOptions = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.default_head)
            .showImageForEmptyUri(R.drawable.default_head)
            .showImageOnFail(R.drawable.default_head).cacheInMemory(false)
            .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(120))
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public static DisplayImageOptions QRcodeImageOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_head)
            .showImageOnFail(R.drawable.default_head)
            .cacheInMemory(false).cacheOnDisc(false)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();
    /**
     * 群头像
     */
    public static DisplayImageOptions avatarGroupOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_head)
            .showImageOnFail(R.drawable.default_head).cacheInMemory(false)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(120))
            .imageScaleType(ImageScaleType.EXACTLY).build();

    /**
     * @return DisplayImageOptions
     * @Title: getRightAngleDisplayImageOptions
     * @param: 直角
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static DisplayImageOptions getRightAngleDisplayImageOptions() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .showImageOnFail(R.drawable.default_head).cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(3))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        return imageOptions;
    }
    public static DisplayImageOptions getDisplayImageOptionsForLocal() {
        DisplayImageOptions mPhotoOptionsoptions = new DisplayImageOptions.Builder()
                .decodingOptions(GetBitmapFactoryOptions())
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        return mPhotoOptionsoptions;
    }
    private static BitmapFactory.Options GetBitmapFactoryOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return options;
    }
    public static DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions mPhotoOptionsoptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).decodingOptions(GetBitmapFactoryOptions())
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        return mPhotoOptionsoptions;
    }
    /**
     * 没有缓存和没有默认图片的Options 1,欢迎页面图片展示Option
     */
    public static DisplayImageOptions mNoCacheOptionsoptions = new DisplayImageOptions.Builder()
            .cacheInMemory(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();
    /**
     * @param cornerRadiusPixels
     * @return
     */
    public static DisplayImageOptions getListOfUserPupupWindowDisplayImageOptions(
            int cornerRadiusPixels) {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .showImageOnFail(R.drawable.default_head).cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(cornerRadiusPixels))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        return imageOptions;
    }
}
