package chat.common.user;

import android.content.Context;

import chat.common.util.storage.PreferencesHelper;
import chat.session.bind.MessageRemindBean;

/**
 * @ClassName: UserSettingHelper
 * @Description: 用户设置配置页面
 */
public class UserSettingHelper {

	/** mSharedPreferences工具 */
	protected PreferencesHelper mHelper = null;

	private MessageRemindBean mBean = null;
	/** 提醒开关 */
	public final static String ISREMINDOPEN = "isRemindOpen";
	/** 免打扰开关开关 */
	public final static String DISTURB = "disturb";
	/** 免打扰开始时间 */
	public final static String STARTTIME = "startTime";
	/** 免打扰结束时间 */
	public final static String ENDTIME = "endTime";
	/** 声音开关 */
	public final static String SOUDN = "sound";
	/** 震动开关 */
	public final static String VIBRATION = "vibration";

	public final static String LOCATIONSTATUS = "locationStatus";

	public UserSettingHelper(Context mContext, String fileName) {
		mHelper = new PreferencesHelper(mContext, fileName);
	}

	public PreferencesHelper getmHelper() {
		return mHelper;
	}

	/**
	 * 获取免打扰开始时间
	* @Title: getStartTime 
	* @param: 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @return long
	 */
	public long getStartTime()
	{
		return mHelper.getLong(STARTTIME);
	}
	
	/**
	 * 获取免打扰结束时间
	* @Title: getEndTime 
	* @param: 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @return long
	 */
	public long getEndTime()
	{
		return mHelper.getLong(ENDTIME);
	}
	
	/**
	 * @Title: saveUserRemindSet
	 * @param:
	 * @Description: 保存用户的消息设置
	 * @return void
	 */
	public void saveUserRemindSet(MessageRemindBean.MessageRemindData mData) {
		if (mData == null) {
			return;
		}
		try {
			mHelper.put(ISREMINDOPEN, mData.getIsRemindOpen());
			mHelper.put(DISTURB, mData.getDisturb());
			mHelper.put(STARTTIME, mData.getStartTime());
			mHelper.put(ENDTIME, mData.getEndTime());
			mHelper.put(SOUDN, mData.getSound());
			mHelper.put(VIBRATION, mData.getVibration());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: getUserRemindSet
	 * @param:
	 * @Description: 获取用户保存的消息设置
	 * @return MessageRemindBean
	 */
	public MessageRemindBean getUserRemindSet() {
		MessageRemindBean mBean = new MessageRemindBean();
		MessageRemindBean.MessageRemindData mRemindData = mBean.new MessageRemindData();
		mRemindData.setIsRemindOpen(mHelper.getInt(ISREMINDOPEN));
		mRemindData.setDisturb(mHelper.getInt(DISTURB));
		mRemindData.setStartTime(mHelper.getLong(STARTTIME));
		mRemindData.setEndTime(mHelper.getLong(ENDTIME));
		mRemindData.setSound(mHelper.getInt(SOUDN));
		mRemindData.setVibration(mHelper.getInt(VIBRATION));
		mBean.setData(mRemindData);
		return mBean;
	}
}
