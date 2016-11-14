package net.skjr.wtq.core.utils.sys;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池帮助类
 * 
 * @author SongTao
 * 
 */
public class ThreadPoolUtils {
	private static ExecutorService pool = Executors.newCachedThreadPool();

	/**
	 * 在线程池中执行一个任务
	 * 
	 * @param runnable
	 */
	public static void execute(Runnable runnable) {
		pool.execute(runnable);
	}

}
