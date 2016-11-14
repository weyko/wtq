package chat.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.v4.util.LruCache;

public class WImageLoader {
	private LruCache<String, Bitmap> imageCache;
	private Context context=null;
	
	public WImageLoader() {
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		imageCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}
	
	public WImageLoader(Context context) {
		this.context = context;
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		imageCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	public void clearMemory(){
		imageCache.evictAll();
		System.gc();
	}
	
	public Bitmap decodeFile(final String path,int limitwidth,int limitHeight){
		if(limitwidth<=0||limitwidth>1080){
			limitwidth = 1080;
		}
		if(limitHeight<=0||limitHeight>1920){
			limitHeight = 1920;
		}
		// 如果缓存过就从缓存中取出数据
		if (imageCache.get(path) != null) {
			return imageCache.get(path);
		}
		Bitmap bmp=null;
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		int scale = 1;
		if (opts.outWidth > limitwidth) {
			scale = opts.outWidth / limitwidth;
		}
		if (opts.outHeight > limitHeight) {
			scale = opts.outHeight / limitHeight > scale ? opts.outHeight / limitHeight
					: scale;
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		try {
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			bmp = BitmapFactory.decodeFile(path, opts);
		}catch (OutOfMemoryError e) {
			System.gc();
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			limitwidth = limitwidth/2;
			limitHeight = limitHeight/2;
			try {
				scale = 1;
				if (opts.outWidth > limitwidth) {
					scale = opts.outWidth / limitwidth;
				}
				if (opts.outHeight > limitHeight) {
					scale = opts.outHeight / limitHeight > scale ? opts.outHeight / limitHeight
							: scale;
				}
				opts.inSampleSize = scale;
				bmp = BitmapFactory.decodeFile(path, opts);
			}catch (OutOfMemoryError e1) {
				System.gc();
			}
		}
		// 存入map
		if(path!=null&&bmp!=null){
			imageCache.put(path, bmp);
		}
		return bmp;
	}
	
	public Bitmap decodeFile(final String path){
		return decodeFile(path,1080,1920);
	}
	
	public Bitmap decodeResource(Resources res, int id, int limitwidth, int limitHeight){
		return decodeResource(res, id, limitwidth, limitHeight, true);
	}
	
	/**
	 * @param res 资源类
	 * @param id 资源id
	 * @param limitwidth 宽度限制
	 * @param limitHeight 高度限制
	 * @param limit 是否对已传递过来的宽高做进一步的限制，true：做限制(限制后的宽高最大不超过540x640)， false：不做限制，按照传递过来的值直接进行decode
	 * @return 按指定宽高decode出的bitmap
	 * @Description 按指定宽高decode本地图片资源
	 */
	public Bitmap decodeResource(Resources res,int id,int limitwidth,int limitHeight, boolean limit){
		if(limit) {
			if(limitwidth<=0||limitwidth>540){
				limitwidth = 540;
			}
			if(limitHeight<=0||limitHeight>640){
				limitHeight = 640;
			}
		}
		String path = "Android/drawable/"+id;
		if (imageCache.get(path) != null) {
			return imageCache.get(path);
		}
		Bitmap bmp=null;
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, id, opts);
		int scale = 1;
		if (opts.outWidth > limitwidth) {
			scale = opts.outWidth / limitwidth;
		}
		if (opts.outHeight > limitHeight) {
			scale = opts.outHeight / limitHeight > scale ? opts.outHeight / limitHeight
					: scale;
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		try {
			bmp = BitmapFactory.decodeResource(res, id, opts);
		}catch (OutOfMemoryError e) {
		}
		// 存入map
		if(path!=null&&bmp!=null){
			imageCache.put(path, bmp);
		}
		return bmp;
	}
	
	public Bitmap decodeResource(Resources res,int id){
		return decodeResource(res,id,540,640);
	}
}