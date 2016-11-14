package net.skjr.wtq.common.utils;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * 图片加载工具
 * 目前使用glide
 */
public class ImageLoaderUtils {

    /**
     * 加载图片
     *
     * @param url
     * @param imageView
     */
    public static void displayImage(Activity activity, String url, ImageView imageView) {
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    public static void displayImage(Fragment fragment, String url, ImageView imageView) {
        Glide.with(fragment).load(url).into(imageView);
    }

    /**
     * 加载图片,加载前使用占位图片
     */
    public static void displayImage(Activity activity, String url, int placeholder, ImageView imageView) {
        Glide.with(activity).load(url).placeholder(placeholder).into(imageView);
    }

    /**
     * 加载图片,加载前使用占位图片
     */
    public static void displayImage(Fragment fragment, String url, int placeholder, ImageView imageView) {
        Glide.with(fragment).load(url).placeholder(placeholder).into(imageView);
    }

    /**
     * 加载图片,加载前使用占位图片,加载错误显示错误图片
     */
    public static void displayImage(Activity activity, String url, int placeholder, int error, ImageView imageView) {
        Glide.with(activity).load(url).placeholder(placeholder).error(error).into(imageView);
    }

    /**
     * 加载图片,加载前使用占位图片,加载错误显示错误图片
     */
    public static void displayImage(Fragment fragment, String url, int placeholder, int error, ImageView imageView) {
        Glide.with(fragment).load(url).placeholder(placeholder).error(error).into(imageView);
    }

    /**
     * 从resource加载
     */
    public static void displayImage(Fragment fragment, int resourceId, ImageView imageView) {
        Glide.with(fragment).load(resourceId).into(imageView);
    }

    /**
     * 从resource加载
     */
    public static void displayImage(Activity activity, int resourceId, ImageView imageView) {
        Glide.with(activity).load(resourceId).into(imageView);
    }

    /**
     * 从图片文件加载
     */
    public static void displayImage(Activity activity, File file, ImageView imageView) {
        Glide.with(activity).load(file).into(imageView);
    }

    public static void displayImage(Fragment fragment, File file, ImageView imageView) {
        Glide.with(fragment).load(file).into(imageView);
    }

    /**
     * 从Uri加载
     */
    public static void displayImage(Activity activity, Uri uri, ImageView imageView) {
        Glide.with(activity).load(uri).into(imageView);
    }

    public static void displayImage(Fragment fragment, Uri uri, ImageView imageView) {
        Glide.with(fragment).load(uri).into(imageView);
    }
}
