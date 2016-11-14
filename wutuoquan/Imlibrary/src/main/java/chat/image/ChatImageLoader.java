package chat.image;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.support.v4.util.LruCache;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * 聊天图片加载类
 */
public class ChatImageLoader {
	private static LruCache<String, GifDrawable> imageCache=null;
	public static void init() {
		if (imageCache==null){
			int maxMemory = (int) Runtime.getRuntime().maxMemory();
			int cacheSize = maxMemory / 4;
			//System.out.println("----------------------gif cacheSize="+cacheSize);
			imageCache = new LruCache<String, GifDrawable>(cacheSize) {
				@Override
				protected int sizeOf(String key, GifDrawable value) {
					return (int)value.getInputSourceByteCount();
				}
			};
		}
	}

	public static void clearMemory(){
		if (imageCache!=null){
			imageCache.evictAll();
		}
		imageCache = null;
		System.gc();
	}
	
	public static GifDrawable loadGif(Context context,final int drawableId){
		init();
		if (imageCache.get(String.valueOf(drawableId)) != null) {
			return imageCache.get(String.valueOf(drawableId));
		}
		GifDrawable gifFromResource=null;
		try {
			gifFromResource = new GifDrawable( context.getResources(),drawableId);
			imageCache.put(String.valueOf(drawableId), gifFromResource);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return gifFromResource;
	}
	public static GifDrawable loadGif(Context context, String assetName){
		init();
		if (imageCache.get(assetName) != null) {
			return imageCache.get(assetName);
		}
		GifDrawable gifFromResource=null;
		try {
			gifFromResource = new GifDrawable(context.getAssets(),assetName);
			imageCache.put(assetName, gifFromResource);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gifFromResource;
	}
}