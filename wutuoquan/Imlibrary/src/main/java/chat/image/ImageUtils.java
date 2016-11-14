/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chat.image;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;


public class ImageUtils {
	
	public static class ImageSize{
		public int width;
		public int height;
	}
	public static void showImage(ImageLoader imageLoader, ImageView view,
			String imageUrl, final ImageSize limitSize) {
		showImage(imageLoader,view,imageUrl,limitSize,1);
	}
	
	private static void showImage(ImageLoader imageLoader, ImageView view,
			String imageUrl, final ImageSize limitSize, final int parentLayoutType) {
		imageLoader.displayImage(imageUrl, view,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
												  Bitmap loadedImage) {
						ImageSize size = reSize(loadedImage.getWidth(), loadedImage.getHeight(), limitSize);
						if (parentLayoutType == 1) {
							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
									size.width, size.height);
							view.setLayoutParams(layoutParams);
						} else {
							RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
									size.width, size.height);
							view.setLayoutParams(layoutParams);
						}
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
	}
	public static Bitmap doBlurAndBlackApha(Bitmap sentBitmap, int destW,int destH,int radius) {
		try{
			return doBlur(sentBitmap,destW,destH,radius,false,true);
		}catch(OutOfMemoryError e){
			e.printStackTrace();
			System.gc();
			return sentBitmap;
		}catch(ArrayIndexOutOfBoundsException e){
			System.gc();
			return sentBitmap;
		}
	}
	public static Bitmap doBlurAndBlackApha(Bitmap sentBitmap, int radius) {
		try{
			return doBlur(sentBitmap,0,0,radius,false,true);
		}catch(OutOfMemoryError e){
			e.printStackTrace();
			System.gc();
			return sentBitmap;
		}catch(ArrayIndexOutOfBoundsException e){
			System.gc();
			return sentBitmap;
		}
	}
	/***
	 *@描述TODO
	 *@图片工具类
	 *@处理高斯模糊的工具
	 *@用于登录页面
	 *@处理方式16进制图片压缩
	 *@深圳魔线科技
	 * @return
	 */
	private static Bitmap doBlur(Bitmap sentBitmap, int destW,int destH,int radius, boolean canReuseInBitmap,boolean blackApha) {
		Bitmap bitmap;
		if (canReuseInBitmap) {
			bitmap = sentBitmap;
		} else {
			bitmap = copyBtimap(sentBitmap,destW,destH);
			if (blackApha){
				// 定义ColorMatrix，并指定RGBA矩阵
				float[] src = new float[] {
						0.9f, 0, 0, 0, 0,
						0, 0.9f, 0, 0, 0,
						0, 0, 0.9f, 0, 0,
						0, 0, 0, 1.0f, 0 };
				Paint paint = new Paint();
				paint.setColorFilter(new ColorMatrixColorFilter(src));
				Canvas canvas = new Canvas(bitmap);
				canvas.drawBitmap(bitmap, new Matrix(), paint);
				canvas.save(Canvas.ALL_SAVE_FLAG);
				canvas.restore();
			}
			System.gc();
		}

		if (radius < 1||bitmap==null) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		pix = null;
		r = null;
		g = null;
		b = null;
		vmin = null;
		dv = null;
		stack = null;
		sir=null;
		System.gc();
		return (bitmap);
	}
	private static Bitmap copyBtimap(Bitmap sentBitmap,int destW,int destH){
		Bitmap bitmap=null;
		// add by h.j.huang 图片压缩处理 防止高斯模糊处理内存溢出
		if (destW>0&&destH>0){
			float scale=1.0f;
			if (destW > 400 || destH > 600) {
				scale=0.25f;
			} else if (destW > 150 || destH > 150) {
				scale=0.5f;
			}
			destW = (int)(destW*scale);
			destH = (int)(destH*scale);
			bitmap = Bitmap.createBitmap(destW,destH,Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			int srcW=0;
			int srcH=0;
			if (destW>destH){
				srcW=sentBitmap.getWidth();
				srcH=srcW*destH/destW;
			}else{
				srcH=sentBitmap.getHeight();
				srcW=srcH*destW/destH;
			}
			Rect src=new Rect((sentBitmap.getWidth()-srcW)/2,(sentBitmap.getHeight()-srcH)/2
					,(sentBitmap.getWidth()+srcW)/2,(sentBitmap.getHeight()+srcH)/2);
			Rect dst=new Rect(0,0,destW,destH);
			canvas.drawBitmap(sentBitmap, src, dst, null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			return bitmap;
		}else{
			if (sentBitmap.getWidth() > 400 || sentBitmap.getHeight() > 600) {
				Matrix matrix = new Matrix();
				matrix.postScale(0.25f, 0.25f);
				bitmap = Bitmap.createBitmap(sentBitmap, 0, 0,
						sentBitmap.getWidth(), sentBitmap.getHeight(), matrix,
						true);
			} else if (sentBitmap.getWidth() > 150
					|| sentBitmap.getHeight() > 150) {
				Matrix matrix = new Matrix();
				matrix.postScale(0.5f, 0.5f);
				bitmap = Bitmap.createBitmap(sentBitmap, 0, 0,
						sentBitmap.getWidth(), sentBitmap.getHeight(), matrix,
						true);
			} else {
				bitmap = sentBitmap.copy(Bitmap.Config.ARGB_8888, true);
			}
			return bitmap;
		}
	}
	public static ImageSize reSize(int bmpWidth,int bmpHeight,final ImageSize limitSize){
		ImageSize size=new ImageSize();
		int orgW = bmpWidth;
		int orgH = bmpHeight;
		int newW = orgW;
		int newH = orgH;

		if (orgW >= orgH) {
			int limitWidth = limitSize.width;
			if (orgW < limitWidth) {
				newW = orgW;
			} else {
				newW = limitWidth;
			}
			newH = newW * orgH / orgW;
		} else {

			int limitHeight = limitSize.height;
			if (orgH < limitHeight) {
				newH = orgH;
			} else {
				newH = limitHeight;
			}
			newW = newH * orgW / orgH;
		}
		
		if(newW>limitSize.width){
			newW = limitSize.width;
			newH = newW * orgH / orgW;
		}
		size.width = newW;
		size.height = newH;
		return size;
	}
	
	public static ImageSize getBitmapSize(String filePath){
		ImageSize size=new ImageSize();
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		size.width = options.outWidth;
		size.height = options.outHeight;
		return size;
	}

	public static ImageSize getBitmapSize(Context context, int resId){
		ImageSize size=new ImageSize();
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, options);
		size.width = options.outWidth;
		size.height = options.outHeight;
		return size;
	}
	
	/**
	 * 修正旋转图片
	 */
	public static void fixPicture(String filePath) {
		/**
		 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
		 */
		int degree = readPictureDegree(filePath);
		if (degree!=0){
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);
			int scale = 1;
			if (opts.outWidth > 640) {
				scale = opts.outWidth / 640;
			}
			if (opts.outHeight > 960) {
				scale = opts.outHeight / 960 > scale ? opts.outHeight / 960
						: scale;
			}
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = scale;
			Bitmap bitmap = null;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeFile(filePath, opts);
			if (bitmap!=null){
				bitmap = BitmapUtil.rotaingImageView(degree, bitmap);
				System.gc();
				BitmapUtil.saveBitmapToSDCard(bitmap, filePath);
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
		}
	}
	
	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
