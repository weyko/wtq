package chat.common.config;

import android.os.Environment;

import java.io.File;

/**
 * Description: 公用配置文件
 */
public class Constant {
    public static final String LOGINAPPTYPE = "sk";
    /** 缓存文件的根目录 */
    private static final String BASE_DIR = "sk";
    /** SDCard 文件路径名 */
    public static final String SDCARD_PATH = Environment
            .getExternalStorageDirectory().toString();
    /** 保存公共缓存路径 */
    public static final String CACHE_COMMON_PATH = SDCARD_PATH + File.separator
            + BASE_DIR + File.separator + "common" + File.separator;
    public static final String DOMAIN_IMAGE_TYPE = "image";
    public static final String DOMAIN_MEDIA_TYPE = "media";
    /**Domain缓存*/
    public static final String CACHE_W_DOMAIN = CACHE_COMMON_PATH + "sk_domain.txt";

    public static final String HTTP_STARTS = "http://";
    public static final String HTTP_SIGN = "/";
    public static final String HTTP_IMAGE_STRING = "http://image.";

    public static final String FILE_STARTS = "file:";
    public static final String DRAWABLE_STARTS = "drawable://";
    public static final String ASSETS_STARTS = "assets://";
    /** 图片缓存地址 */
    public static final String IMAGES_FOLDER = SDCARD_PATH + File.separator
            + BASE_DIR + File.separator + "images" + File.separator;
    /** 保存照相图片路径 */
    public static final String CAMERA_PATH = SDCARD_PATH + File.separator
            + BASE_DIR + File.separator + "camera" + File.separator;
    /** 动态图片缓存地址 */
    public static final String GIF_FOLDER = SDCARD_PATH + File.separator
            + BASE_DIR + File.separator + "gif" + File.separator;
    /** 录音缓存地址 */
    public static final String VOICES_FOLDER = SDCARD_PATH + File.separator
            + BASE_DIR + File.separator + "voices" + File.separator;
    /** 视频缓存地址 */
    public static final String  VIDEO_FOLDER = SDCARD_PATH + File.separator
            + BASE_DIR + File.separator + "videos" + File.separator;
    public static final String SSO_USERID = "SSO_USERID";
    /**好友信息*/
    public static final String FRIEND_INFO = "FRIEND_INFO";
}
