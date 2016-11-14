package chat.manager;

import java.util.ArrayList;

/**
 * @ClassName: MXObservable
 * @Description: 观察者抽象类
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年8月4日 下午2:57:59
 *
 * @param <T>
 */
public abstract class MXObservable<T> {

	protected final ArrayList<T> observers = new ArrayList<T>();

	/**
	 * @Title: registerObserver
	 * @param:
	 * @Description: 注册观察者
	 * @return void
	 */
	public void registerObserver(T observer) {
		if (observer == null) {
			throw new IllegalArgumentException("The observer is null.");
		}
		synchronized (observers) {
			if (observers.contains(observer)) {
				throw new IllegalStateException("MXObservable " + observer
						+ " is already registered.");
			}
			observers.add(observer);
		}
	}

	/**
	 * @Title: unregisterObserver
	 * @param:
	 * @Description: 取消注册观察者
	 * @return void
	 */
	public void unRegisterObserver(T observer) {
		if (observer == null) {
			throw new IllegalArgumentException("The observer is null.");
		}
		synchronized (observers) {
			int index = observers.indexOf(observer);
			if (index == -1) {
				throw new IllegalStateException("MXObservable " + observer
						+ " was not registered.");
			}
			observers.remove(index);
		}
	}

	/**
	 * 移除所有观察着
	 */
	public void removeAllRegister() {
		synchronized (observers) {
			observers.clear();
		}
	}
}
