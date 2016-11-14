package chat.qrcode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.io.Serializable;

import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.util.output.ShowUtil;
import chat.contact.activity.PersonCenterActivity;
import chat.contact.bean.UserBean;
import chat.dialog.CustomBaseDialog;
import chat.session.group.activity.ChatGroupInviteActivity;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;
import web.CommonWebActivity;

public class QRCodeResultAnalysis {
	final static int CHECK_UPDATE_CODE=20150827;
	public static void analysisAndHandleMXQRCodeResult(Context context,String text,final QRCodeObserver observer){
		QRContent qrContent = analysisMXQRCodeResult(text);
		if (qrContent.type==1){//魔友二维码
			if (qrContent.content != null && qrContent.content.length() > 0) {
				System.out.println("user id:" + qrContent.content);
				try {
					long userId = Long.parseLong(qrContent.content);
					String loginID = IMClient.getInstance().getSSOUserId();
					if (!loginID.equals("" + userId)) {
						checkUserIdExsit(context,""+userId,observer);
					} else {
						Intent intent = new Intent(context,PersonCenterActivity.class);
						context.startActivity(intent);
						if (observer!=null){
							observer.onAnalysisFinished();
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					showFailedDialog(context,context.getString(R.string.qrcode_handle_no_user),
							context.getString(R.string.qrcode_handle_error),observer);
				}
			}
		}else if (qrContent.type==2){//充值卡二维码
			Intent resultIntent = new Intent();
			resultIntent.putExtra(QRCodeActivity.QR_CODE_RESULT, qrContent.content);
			resultIntent.setClass(context, QRCodeResultActivity.class);
			context.startActivity(resultIntent);
			if (observer!=null){
				observer.onAnalysisFinished();
			}
		}
		if (qrContent.type==5){//群二维码
			Intent resultIntent = new Intent();
			resultIntent.setClass(context, ChatGroupInviteActivity.class);
			resultIntent.putExtra(ChatGroupInviteActivity.ROOMID, qrContent.content);
			resultIntent.putExtra(ChatGroupInviteActivity.KEY, qrContent.key);
			context.startActivity(resultIntent);
			if (observer!=null){
				observer.onAnalysisFinished();
			}
		}else if (qrContent.type==-1){//第三方
			Intent resultIntent = new Intent();
			resultIntent.putExtra(QRCodeActivity.QR_CODE_RESULT, qrContent.content);
			resultIntent.setClass(context, QRCodeResultActivity.class);
			context.startActivity(resultIntent);
			if (observer!=null){
				observer.onAnalysisFinished();
			}
		}else{
			Intent resultIntent = new Intent();
			resultIntent.putExtra(QRCodeActivity.QR_CODE_RESULT, qrContent.content);
			resultIntent.setClass(context, QRCodeResultActivity.class);
			context.startActivity(resultIntent);
			if (observer!=null){
				observer.onAnalysisFinished();
			}
		}
	}
	private static void openWeb(Context context, String linkClose){
		Intent intent = new Intent(context, CommonWebActivity.class);
		intent.putExtra(CommonWebActivity.WEB_URL, linkClose);
		intent.putExtra(CommonWebActivity.WEB_TITLE, context.getString(R.string.qrcode_result));
		context.startActivity(intent);
	}

	public static QRContent analysisMXQRCodeResult(String text){
		QRContent content= new QRContent();
		content.type=-1;//默认为无法识别的二维码
		content.content = text;
		if (text!=null&&text.length()>0){
			final String TAG = "http://m.moxian.com";
			final String TAG_SPECIAL = "http://grab.sk.com";
			if(text.startsWith(TAG) || text.startsWith(TAG_SPECIAL)){//魔线可识别二维码//新增的需要弹框的特殊二维码
				final String TAG_TYPE = "type=";
				if(text.contains(TAG_TYPE)){	
					String sType=getValueByTag(text,TAG_TYPE);
					try{
						content.type = Integer.valueOf(sType);
						if(content.type==0){//兑换券
							//content.content=getValueByTag(text,"code=");
						}if(content.type==1){//好友二维码
							content.content=getValueByTag(text,"userid=");
						}else if(content.type==5){//群二维码
							content.content=getValueByTag(text,"roomid=");
							content.key=getValueByTag(text,"key=");
						}
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}else{
					//检测升级
					content.type = CHECK_UPDATE_CODE;
				}
			}
		}
		return content;
	}
	
	private static String getValueByTag(String text,final String tag){
		if(text.contains(tag)){
			String tmp=text.substring(text.indexOf(tag)+tag.length());
			int posLast=tmp.indexOf("&");
			String sType="";
			if(posLast>=0){
				sType = tmp.substring(0, posLast);
			}else{
				sType = tmp;
			}
			return sType;
		}
		return null;
	}
	
	public static class QRContent implements Serializable{
		private static final long serialVersionUID = -7987341334730144170L;
		public int type=-1;
		public String content;
		public String key;
	}
	
	/**
	 * 检测好友是否存在
	* @Title: checkUserIdExsit 
	* @param: 
	* @Description: (这里用一句话描述这个方法的作用) 
	* @return void
	 */
	private static void checkUserIdExsit(final Context context,final String userId,final QRCodeObserver observer){
		WBaseModel<UserBean> model = new WBaseModel<UserBean>(context, UserBean.class);
		model.httpJsonRequest(Method.GET, URLConfig.GET_FRIENDINFO + userId, null, new WRequestCallBack(){
			@Override
			public void receive(int httpStatusCode, Object data) {
				if(data!=null && data instanceof WBaseBean){
					WBaseBean userBean=(WBaseBean)data;
					if(!userBean.isResult() &&
							(userBean.getCode().equals("mx1103036")||userBean.getCode().equals("mx1103022")) ){//好友不存在
						showFailedDialog(context,context.getString(R.string.qrcode_handle_no_user),
								context.getString(R.string.qrcode_handle_error),observer);
					}else{
						Intent intent = new Intent(context,PersonCenterActivity.class);
						intent.putExtra(Constant.SSO_USERID,userId);
						context.startActivity(intent);
						if (observer!=null){
							observer.onAnalysisFinished();
						}
					}
				}else{
					ShowUtil.showHttpRequestErrorResutToast(context, httpStatusCode, data);
					if (observer!=null){
						observer.onAnalysisFinished();
					}
				}
			}
		});
	}
	private static void showFailedDialog(final Context context,String title,String content,final QRCodeObserver observer) {
		final CustomBaseDialog dialog = CustomBaseDialog.getDialog(context, content,
				context.getString(R.string.ok),new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (observer!=null){
						observer.onResetScan();
					}
				}
			});
		dialog.setTitle(title);
		dialog.show();
	}
}