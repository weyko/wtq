package chat.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.imlibrary.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;

/**
 * @Description: 检查图片路径是否有Domain路径，并符合规则
 *
 */
public class IMImageLoader extends ImageLoader {
	private DisplayImageOptions options = null;

	public void initConfig(Context context,int memory){
		if (!isInited()){
			if (memory<=0){
				long maxMemory = Runtime.getRuntime().maxMemory();
				memory = (int) (maxMemory / 8);
				int maxLimit = (int) (maxMemory / 5);
				int minSize = 30 * 1024 * 1024;
				if (memory < minSize) {
					memory = minSize;
				}
				if (memory > maxLimit) {
					memory = maxLimit;
				}
			}
			UnlimitedDiscCache discCache = IMClient.getInstance().getUnlimitedDiscCache();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context)
					.memoryCacheExtraOptions(480, 640)
					// maxwidth, max height，即保存的每个缓存文件的最大长宽
					.threadPoolSize(2)
					// 线程池内加载的数量
					.threadPriority(Thread.NORM_PRIORITY - 2)
					// implementation/你可以通过自己的内存缓存实现
					.denyCacheImageMultipleSizesInMemory()
					// 禁止缓存多张图片
					.memoryCache(new WeakMemoryCache())
					// 缓存策略
					.memoryCacheSize(memory)
					.diskCacheFileNameGenerator(new Md5FileNameGenerator())
					// 将保存的时候的URI名称用MD5 加密
					.tasksProcessingOrder(QueueProcessingType.LIFO)
					.diskCacheFileCount(500)
					// 缓存的文件数量
					.diskCache(discCache)
					// 自定义缓存路径
					.defaultDisplayImageOptions(getDisplayImageOptions())
					.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
					.build();
			init(config);
			config = null;
			System.gc();
		}
	}
	public void initConfig(Context context,int memory,boolean isNeedToSDCache){
		if (!isInited()){
			if (memory<=0){
				long maxMemory = Runtime.getRuntime().maxMemory();
				memory = (int) (maxMemory / 8);
				int maxLimit = (int) (maxMemory / 5);
				int minSize = 30 * 1024 * 1024;
				if (memory < minSize) {
					memory = minSize;
				}
				if (memory > maxLimit) {
					memory = maxLimit;
				}
			}
			ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
					context)
					.memoryCacheExtraOptions(480, 640)
							// maxwidth, max height，即保存的每个缓存文件的最大长宽
					.threadPoolSize(2)
							// 线程池内加载的数量
					.threadPriority(Thread.NORM_PRIORITY - 2)
							// implementation/你可以通过自己的内存缓存实现
					.denyCacheImageMultipleSizesInMemory()
							// 禁止缓存多张图片
					.memoryCache(new WeakMemoryCache())
							// 缓存策略
					.memoryCacheSize(memory);
			init(config.build());
			config = null;
			System.gc();
		}
	}
	public void clearMemory(){
		if (options!=null) {
			this.clearMemoryCache();
		}
		options = null;
		this.destroy();
		System.gc();
	}
	
	@Override
	public void loadImage(String uri, DisplayImageOptions options,
			ImageLoadingListener listener) {
		uri = convateUrl(uri);
		super.loadImage(uri, options, listener);
	}

	@Override
	public void loadImage(String uri, ImageLoadingListener listener) {
		uri = convateUrl(uri);
		super.loadImage(uri, listener);
	}

	@Override
	public void loadImage(String uri, ImageSize targetImageSize,
			DisplayImageOptions options, ImageLoadingListener listener) {
		uri = convateUrl(uri);
		super.loadImage(uri, targetImageSize, options, listener);
	}

	@Override
	public void loadImage(String uri, ImageSize targetImageSize,
			DisplayImageOptions options, ImageLoadingListener listener,
			ImageLoadingProgressListener progressListener) {
		uri = convateUrl(uri);
		super.loadImage(uri, targetImageSize, options, listener,
				progressListener);
	}

	@Override
	public void displayImage(String uri, ImageAware imageAware) {
		uri = convateUrl(uri);
		super.displayImage(uri, imageAware);
	}

	@Override
	public void loadImage(String uri, ImageSize targetImageSize,
			ImageLoadingListener listener) {
		uri = convateUrl(uri);
		super.loadImage(uri, targetImageSize, listener);
	}

	@Override
	public void displayImage(String uri, ImageAware imageAware,
			DisplayImageOptions options) {
		uri = convateUrl(uri);
		super.displayImage(uri, imageAware, options);
	}

	@Override
	public void displayImage(String uri, ImageAware imageAware,
			DisplayImageOptions options, ImageLoadingListener listener) {
		uri = convateUrl(uri);
		super.displayImage(uri, imageAware, options, listener);
	}

	@Override
	public void displayImage(String uri, ImageAware imageAware,
			ImageLoadingListener listener) {
		uri = convateUrl(uri);
		super.displayImage(uri, imageAware, listener);
	}

	@Override
	public void displayImage(String uri, ImageView imageView) {
		if(imageView!=null) {
			uri = convateUrl(uri);
			//对缺省图片显示参数的方法自动增加显示参数，减少内存消耗。
			super.displayImage(uri, imageView, getDisplayImageOptions());
		}
	}

	@Override
	public void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options) {
		if(imageView!=null) {
			uri = convateUrl(uri);
			super.displayImage(uri, imageView, options);
		}
	}

	@Override
	public void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener) {
		if(imageView!=null) {
			uri = convateUrl(uri);
			super.displayImage(uri, imageView, options, listener);
		}
	}
	/**
	 * 增加缩略图显示
	 * @Title: displayThumbnailImage
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return void
	 */
	public void displayThumbnailImage(String uri, ImageView imageView, int w,
			int h) {
		if(imageView!=null) {
			uri = convateThumbnailUrl(uri, w, h);
			super.displayImage(uri, imageView, getDisplayImageOptions());
		}
	}

	/**
	 * 增加缩略图显示
	 * @Title: displayThumbnailImage
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return void
	 */
	public void displayThumbnailImage(String uri, ImageView imageView,
			DisplayImageOptions options, int w, int h) {
		if(imageView!=null){
			uri = convateThumbnailUrl(uri, w, h);
			super.displayImage(uri, imageView, options);
		}
	}
	
	/**
	 * 增加默认图片自定义
	 * @Title: displayThumbnailImage
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return void
	 */
	public void displayThumbnailImage(String uri, ImageView imageView,
			DisplayImageOptions options, int w, int h,int  defaultDrawable) {
		if(imageView!=null) {
			uri = convateThumbnailUrl(uri, w, h, defaultDrawable);
			super.displayImage(uri, imageView, options);
		}
	}
	
	/**
	 * 增加缩略图显示，并添加一个图片加载的监听
	 * @Title: displayThumbnailImage
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return void
	 */
	public void displayThumbnailImage(String uri, ImageView imageView,
			DisplayImageOptions options, int w, int h, ImageLoadingListener listener) {
		if(imageView!=null) {
			uri = convateThumbnailUrl(uri, w, h);
			super.displayImage(uri, imageView, options, listener);
		}
	}
	
	/**
	 *增加缩略图显示
	 * @Title: displayThumbnailImage
	 * @param:
	 * @Description: TODO(用于广告栏)
	 * @return void
	 */
	public void displayThumbnailImageByBanner(String uri, ImageView imageView,
			DisplayImageOptions options, int w, int h) {
		if(imageView!=null) {
			uri = convateThumbnailUrlByBanner(uri, w, h);
			super.displayImage(uri, imageView, options);
		}
	}

	/**
	 * add by h.j.huang 2015/8/15 增加缩略图显示
	 * 
	 * @Title: displayThumbnailImage
	 * @param:
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return void
	 */
	public void displayThumbnailImage(String uri, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener, int w,
			int h) {
		if(imageView!=null) {
			uri = convateThumbnailUrl(uri, w, h);
			super.displayImage(uri, imageView, options, listener);
		}
	}

	public void displayThumbnailImage(String uri, ImageView imageView,
			ImageLoadingListener listener, int w, int h) {
		if(imageView!=null) {
			uri = convateThumbnailUrl(uri, w, h);
			super.displayImage(uri, imageView, getDisplayImageOptions(), listener);
		}
	}

	public void displayThumbnailImage(String uri, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener,
			ImageLoadingProgressListener loadingProgressListener, int w, int h) {
		if(imageView!=null) {
			uri = convateThumbnailUrl(uri, w, h);
			super.displayImage(uri, imageView, options, listener,
					loadingProgressListener);
		}
	}
	public void displayThumbnailImage(String uri, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener,
			ImageLoadingProgressListener loadingProgressListener, int w, int h,int defaultDrawable) {
		if(imageView!=null) {
			uri = convateThumbnailUrl(uri, w, h, defaultDrawable);
			super.displayImage(uri, imageView, options, listener,
					loadingProgressListener);
		}
	}

	@Override
	public void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener,
			ImageLoadingProgressListener progressListener) {
		if(imageView!=null) {
			uri = convateUrl(uri);
			super.displayImage(uri, imageView, options, listener, progressListener);
		}
	}

	@Override
	public void displayImage(String uri, ImageView imageView,
			ImageLoadingListener listener) {
		if(imageView!=null) {
			uri = convateUrl(uri);
			super.displayImage(uri, imageView, getDisplayImageOptions(), listener);
		}
	}
	
	/**
	 * 缩略图URL
	 * @param uri
	 * @param w
	 * @param h
	 * @return
	 */
	public String displayThumbnailImage(String uri, int w, int h) {
		uri = convateThumbnailUrl(uri, w, h);
		return uri;
	}


	public static String convateUrl(String url) {
		String newUrl = url;
		if (null != newUrl && newUrl.trim().length() > 0
				&& !newUrl.startsWith(Constant.HTTP_STARTS)
				&& !newUrl.startsWith(Constant.FILE_STARTS)
				&& !newUrl.startsWith(Constant.ASSETS_STARTS)
				&& !newUrl.startsWith(Constant.DRAWABLE_STARTS)) {
			newUrl = URLConfig.getDomainUrl(Constant.DOMAIN_IMAGE_TYPE)
					+ newUrl;
		} else if (null != newUrl && newUrl.trim().length() == 0) {
			return "";
		}
		return newUrl;
	}
	public String convateThumbnailUrl(String url, int w, int h) {
		String newUrl = url;
		if (null != newUrl && newUrl.trim().length() > 0
				&& !newUrl.startsWith(Constant.HTTP_STARTS)
				&& !newUrl.startsWith(Constant.FILE_STARTS)
				&& !newUrl.startsWith(Constant.DRAWABLE_STARTS)) {
			newUrl = URLConfig.getDomainUrl(Constant.DOMAIN_IMAGE_TYPE)
					+ newUrl;
			if (URLConfig.conditionFlag >= 1) {
				if (w <= 0 || h <= 0) {
					newUrl += "!m200x200.jpg";
				} else {
					newUrl += "!m" + w + "x" + h + ".jpg";
				}
			}
		} else if (null != newUrl && newUrl.trim().length() == 0) {
			return "drawable://" + R.drawable.default_head;
		}
		return newUrl;
	}
	public String convateThumbnailUrl(String url, int w, int h,int defaultDrawable) {
		String newUrl = url;
		if (null != newUrl && newUrl.trim().length() > 0
				&& !newUrl.startsWith(Constant.HTTP_STARTS)
				&& !newUrl.startsWith(Constant.FILE_STARTS)
				&& !newUrl.startsWith(Constant.DRAWABLE_STARTS)) {
			newUrl = URLConfig.getDomainUrl(Constant.DOMAIN_IMAGE_TYPE)
					+ newUrl;
			if (URLConfig.conditionFlag >= 1) {
				if (w <= 0 || h <= 0) {
					newUrl += "!m200x200.jpg";
				} else {
					newUrl += "!m" + w + "x" + h + ".jpg";
				}
			}
		} else if (null != newUrl && newUrl.trim().length() == 0) {
			return "drawable://" + defaultDrawable;
		}
		return newUrl;
	}
	//广告栏用!t
	public String convateThumbnailUrlByBanner(String url, int w, int h) {
		String newUrl = url;
		if (null != newUrl && newUrl.trim().length() > 0
				&& !newUrl.startsWith(Constant.HTTP_STARTS)
				&& !newUrl.startsWith(Constant.FILE_STARTS)
				&& !newUrl.startsWith(Constant.DRAWABLE_STARTS)) {
			newUrl = URLConfig.getDomainUrl(Constant.DOMAIN_IMAGE_TYPE)
					+ newUrl;
			if (URLConfig.conditionFlag >= 1) {
				if (w <= 0 || h <= 0) {
					newUrl += "!t200x200.jpg";
				} else {
					newUrl += "!t" + w + "x" + h + ".jpg";
				}
			}
		} else if (null != newUrl && newUrl.trim().length() == 0) {
			return "drawable://" + R.drawable.im_bg_message_tip;
		}
		return newUrl;
	}

	@SuppressWarnings("deprecation")
	private DisplayImageOptions getDisplayImageOptions() {
		if (options == null) {
			BitmapFactory.Options bOptions = new BitmapFactory.Options();
			bOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			bOptions.inPurgeable = true;
			bOptions.inInputShareable = true;
			options = new DisplayImageOptions.Builder().cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
//					.showImageForEmptyUri(R.drawable.im_bg_message_tip)
//					.showImageOnLoading(R.drawable.im_bg_message_tip)
//					.showImageOnFail(R.drawable.im_bg_message_tip)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.decodingOptions(bOptions).build();
		}
		return options;
	}
}