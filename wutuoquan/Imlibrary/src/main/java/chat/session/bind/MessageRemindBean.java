package chat.session.bind;
import java.io.Serializable;

import chat.volley.WBaseBean;

/**
 * 
 * @ClassName: MessageRemindBean
 * @Description: 消息提醒Bean
 */
public class MessageRemindBean extends WBaseBean {
	private static final long serialVersionUID = -2587434552893134566L;
	private MessageRemindData data = new MessageRemindData();

	public class MessageRemindData implements Serializable {
		private static final long serialVersionUID = 8221350060240031893L;
		/** isRemindOpen 是 int 是否开启 0 否 1 是 */
		private int isRemindOpen;
		/** disturb 是 int 免打扰是否开启 0 否 1 是 */
		private int disturb;
		/** (单位 时间戳) 采用秒数转换 例如 23:59 则传值 86340 ( 23*60*60+59*60)  */
		private long startTime;
		/** (单位 时间戳) 采用秒数转换 例如 23:59 则传值 86340 ( 23*60*60+59*60)  */
		private long endTime;
		/** sound 是 int 声音是否开启 0 否 1 是 */
		private int sound;// 0 1
		/** vibration 是 int 震动是否开启 0 否 1 是 */
		private int vibration;

		/** isRemindOpen 是 int 是否开启 0 否 1 是 */
		public int getIsRemindOpen() {
			return isRemindOpen;
		}

		public void setIsRemindOpen(int isRemindOpen) {
			this.isRemindOpen = isRemindOpen;
		}

		/** disturb 是 int 免打扰是否开启 0 否 1 是 */
		public int getDisturb() {
			return disturb;
		}

		public void setDisturb(int disturb) {
			this.disturb = disturb;
		}

		/** (单位 时间戳) 采用秒数转换 例如 23:59 则传值 86340 ( 23*60*60+59*60) */
		public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		/** (单位 时间戳) 采用秒数转换 例如 23:59 则传值 86340 ( 23*60*60+59*60)*/
		public long getEndTime() {
			return endTime;
		}

		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}

		/** sound 是 int 声音是否开启 0 否 1 是 */
		public int getSound() {
			return sound;
		}

		public void setSound(int sound) {
			this.sound = sound;
		}

		/** vibration 是 int 震动是否开启 0 否 1 是 */
		public int getVibration() {
			return vibration;
		}

		public void setVibration(int vibration) {
			this.vibration = vibration;
		}
	}

	public MessageRemindData getData() {
		return data;
	}

	public void setData(MessageRemindData data) {
		this.data = data;
	}
}
