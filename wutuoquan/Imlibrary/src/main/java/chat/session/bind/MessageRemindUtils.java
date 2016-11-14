package chat.session.bind;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import chat.base.IMClient;
import chat.common.user.UserSettingHelper;

/**
 * 
* @ClassName: MessageRemindUtils 
* @Description: 消息提醒判断工具类
 */
public class MessageRemindUtils {

	/**
	 * 获取消息提醒数据结构
	 * @param context
	 * @return
	 */
	public static MessageRemindBean.MessageRemindData getMessageRemind(Context context){
		UserSettingHelper helper = new UserSettingHelper(context,
				IMClient.getInstance().getSSOUserId());
		return helper.getUserRemindSet().getData();
	}
	
	/**
	 * 是否需要消息提醒
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isMessageRemind(MessageRemindBean.MessageRemindData data){
		if(data.getIsRemindOpen()==1){//消息提醒开启
			if(data.getDisturb()==1){//免打扰开启
				Date date= Calendar.getInstance().getTime();
				int curTime=date.getHours()*60*60+date.getMinutes()*60+date.getSeconds();
				System.out.println("--------curTime="+curTime);
				int days=24*60*60;
				if(data.getEndTime()>days){
					long endT2=data.getEndTime()-days;
					if( (curTime>data.getStartTime()&&curTime<=days)||
							(curTime<=endT2) ){
						return false;
					}else{
						return true;
					}
				}else{
					if(curTime<data.getStartTime()||curTime>data.getEndTime()){//不在免打扰时间段内
						return true;
					}
				}
			}else{//免打扰关闭
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 声音提醒是否打开
	 * @return
	 */
	public static boolean isSoundRemindOpen(MessageRemindBean.MessageRemindData data){
		if(data.getSound()==1){
			return true;
		}
		return false;
	}
	
	/**
	 * 震动提醒是否打开
	 * @return
	 */
	public static boolean isVibrationRemindOpen(MessageRemindBean.MessageRemindData data){
		if(data.getVibration()==1){
			return true;
		}
		return false;
	}
}