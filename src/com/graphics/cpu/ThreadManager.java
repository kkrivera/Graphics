package com.graphics.cpu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
	static int maxThreads = Runtime.getRuntime().availableProcessors();
	static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, maxThreads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	public void execute(Runnable... commands) {
		for (Runnable command : commands) {
			threadPoolExecutor.execute(command);
		}
	}

	// public void execute(Run){
	//
	// }

	public static int getMaxThreads() {
		return maxThreads;
	}
}
