package com.graphics.cpu;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
	static int maxThreads = Runtime.getRuntime().availableProcessors();
	static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(maxThreads, maxThreads, 1000, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

	public void execute(Runnable... commands) {
		for (Runnable command : commands) {
			threadPoolExecutor.execute(command);
		}
	}

	public <R> Set<R> executeForResult(Collection<Callable<R>> callables) {
		try {
			Set<R> results = new HashSet<R>();

			Set<Future<R>> futures = new HashSet<Future<R>>();

			for (Callable<R> callable : callables) {
				futures.add(threadPoolExecutor.submit(callable));
			}

			boolean done = false;
			while (!done) {

				done = true;
				for (Future<R> future : futures) {
					done &= future.isDone();
				}

				if (!done) {
					Thread.sleep(100);
				}
			}

			for (Future<R> future : futures) {
				results.add(future.get());
			}

			return results;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public <T, R> Set<R> executeForResult(Collection<T> input, final ThreadedAction<T, R> action) {

		Set<Callable<R>> callables = new HashSet<Callable<R>>();

		for (final Collection<T> splitInput : splitInputs(input)) {
			callables.add(new Callable<R>() {
				@Override
				public R call() throws Exception {
					return action.execute(splitInput);
				}
			});
		}

		return executeForResult(callables);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T, R> void executeForResult(Collection<T> input, final ThreadedAction<T, R> action, R resultSet) {
		Set<Callable<R>> callables = new HashSet<Callable<R>>();

		for (final Collection<T> splitInput : splitInputs(input)) {
			callables.add(new Callable<R>() {
				@Override
				public R call() throws Exception {
					return action.execute(splitInput);
				}
			});
		}

		Set<R> results = executeForResult(callables);
		for (R result : results) {

			if (result instanceof Map) {
				((Map) resultSet).putAll((Map) result);
			} else {
				((Collection) resultSet).addAll((Collection) result);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<Collection<T>> splitInputs(Collection<T> input) {
		try {
			Map<Integer, Collection<T>> output = new HashMap<Integer, Collection<T>>();

			int i = 0;
			for (T inputVal : input) {
				int index = i % maxThreads;

				if (!output.containsKey(index)) {
					output.put(index, input.getClass().newInstance());
				}
				output.get(index).add(inputVal);
				i++;
			}

			return output.values();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int getMaxThreads() {
		return maxThreads;
	}

	public interface ThreadedAction<T, R> {
		public R execute(Collection<T> input);
	}
}
