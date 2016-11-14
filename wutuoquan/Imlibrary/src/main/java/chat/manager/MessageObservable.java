/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package chat.manager;

/**
 * @ClassName: MessageObservable
 * @Description: 消息观察者
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年8月4日 下午3:01:52
 *
 */
public class MessageObservable extends MXObservable<OnMessageChange> {

	/**
	 * @Title: notifyChanged
	 * @param:
	 * @Description: 分发数据库改变通知
	 * @return void
	 */
	public void notifyChanged(String session) {
		synchronized (observers) {
			for (int i = observers.size() - 1; i >= 0; i--) {
				observers.get(i).onChanged(session);
			}
		}
	}
}
