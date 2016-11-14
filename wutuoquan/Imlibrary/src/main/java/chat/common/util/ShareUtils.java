package chat.common.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;

import com.imlibrary.R;

import java.util.HashMap;

import chat.base.IMClient;
import chat.common.util.output.ShowUtil;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat.ShareParams;

/**
 * shareSDK分享工具类
 */
public class ShareUtils implements PlatformActionListener {
	private Context context;
	public ShareUtils(Context context) {
		this.context = context;
		ShareSDK.initSDK(context);
	}

	/**
	 * QQ空间、QQ好友
	 * @param sharePlat
	 * @param title
	 * @param content
	 * @param picurl
	 * @param url
	 */
	public void shareQqOrZone(String sharePlat, String title, String content, String picurl, String url) {
		Platform qzone = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		sp.setTitleUrl(url); // 标题的超链接
		sp.setText(content);
		String picUrL = IMClient.sImageLoader.displayThumbnailImage(
				picurl, 100, 100);
		sp.setImageUrl(picUrL);
		// sp.setSite("发布分享的网站名称");
		// sp.setSiteUrl("www.baidu.com");
		qzone.setPlatformActionListener(this); // 设置分享事件回调
		qzone.share(sp);
	}

	/**
	 * QQ空间、QQ好友
	 * @param sharePlat
	 * @param title
	 * @param content
	 * @param imageDatas
	 * @param url
	 */
	public void shareQqOrZone(String sharePlat, String title, String content, Bitmap imageDatas, String url) {
		Platform qzone = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		sp.setTitleUrl(url); // 标题的超链接
		sp.setText(content);
		sp.setImageData(imageDatas);
		// sp.setSite("发布分享的网站名称");
		// sp.setSiteUrl("www.baidu.com");
		qzone.setPlatformActionListener(this); // 设置分享事件回调
		qzone.share(sp);
	}

	/**
	 * 微信好友
	 * @param sharePlat 类型
	 * @param title 标题
	 * @param content 内容
	 * @param picurl 图片
	 * @param url 链接地址
	 */
	public void shareWeChatFriends(String sharePlat, String title,
			String content, String picurl, String url) {
		Platform plat = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = getShareParams(title, content, url);
		String picUrL = IMClient.sImageLoader.displayThumbnailImage(
				picurl, 100, 100);
		sp.setImageUrl(picUrL);
		plat.setPlatformActionListener(this);
		plat.share(sp);
	}

	/**
	 * 微信好友
	 * @param sharePlat 类型
	 * @param title 标题
	 * @param content 内容
	 * @param imageDatas Bitmap图片
	 * @param url 链接地址
	 */
	public void shareWeChatFriends(String sharePlat, String title,
								   String content, Bitmap imageDatas, String url) {
		Platform plat = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = getShareParams(title, content, url);
		sp.setImageData(imageDatas);
		plat.setPlatformActionListener(this);
		plat.share(sp);
	}

	/**
	 * 微信朋友圈
	 * @param sharePlat 类型
	 * @param title 标题
	 * @param picurl 图片
	 * @param url 链接地址
	 */
	public void shareWechatMoments(String sharePlat, String title,
			String picurl, String url) {
		Platform plat = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = getShareParams(title, null, url);
		String picUrL = IMClient.sImageLoader.displayThumbnailImage(
				picurl, 100, 100);
		sp.setImageUrl(picUrL);
		plat.setPlatformActionListener(this);
		plat.share(sp);
	}

	/**
	 * 微信朋友圈
	 * @param sharePlat 类型
	 * @param title 标题
	 * @param imageData Bitmap图片
	 * @param url 链接地址
	 */
	public void shareWechatMoments(String sharePlat, String title,
								   Bitmap imageData, String url) {
		Platform plat = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = getShareParams(title, null, url);
		sp.setImageData(imageData);
		plat.setPlatformActionListener(this);
		plat.share(sp);
	}

	/**
	 * 新浪微博
	 * @param sharePlat
	 * @param title
	 * @param imageData
	 * @param url
	 */
	public void shareWeiboMoments(String sharePlat, String title, String content,
								  Bitmap imageData, String url) {
		Platform plat = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = getShareParams(title, content, url);
		sp.setImageData(imageData);
		plat.setPlatformActionListener(this);
		plat.share(sp);
	}

	/**
	 * 新浪微博
	 * @param sharePlat
	 * @param title
	 * @param picurl
	 * @param url
	 */
	public void shareWeiboMoments(String sharePlat, String title,
								  String picurl, String url) {
		Platform plat = ShareSDK.getPlatform(sharePlat);
		ShareParams sp = getShareParams(title, null, url);
		String picUrL = IMClient.sImageLoader.displayThumbnailImage(
				picurl, 100, 100);
		sp.setImageUrl(picUrL);
		plat.setPlatformActionListener(this);
		plat.share(sp);
	}
	/**
	 * 调起系统发短信功能
	 * @param phoneNumber
	 * @param message
	 */
	public void shareSMS(String phoneNumber, String message){
		if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
			intent.putExtra("sms_body", message);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			IMClient.getInstance().getContext().startActivity(intent);
		}
	}
	/**
	 * 设置数据
	 * @param title 标题
	 * @param content 内容
	 * @param url 链接地址
	 * @return ShareParams
	 */
	private ShareParams getShareParams(String title, String content, String url) {
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		if (content != null) {
			sp.setText(content);
		}
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setUrl(url);
		return sp;
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0:
					ShowUtil.showToast(context, context.getString(R.string.share_completed));
					break;
				case 1:
					ShowUtil.showToast(context, context.getString(R.string.share_canceled));
					break;
				case 2:
					ShowUtil.showToast(context, context.getString(R.string.share_failed));
					break;
			}
		}
	};
	@Override
	public void onComplete(Platform plat, int action,
						   HashMap<String, Object> res) {
		handler.sendEmptyMessage(0);
	}

	@Override
	public void onCancel(Platform plat, int action) {
		handler.sendEmptyMessage(1);
	}

	@Override
	public void onError(Platform plat, int action, Throwable t) {
		t.printStackTrace();
		handler.sendEmptyMessage(2);
	}
}
