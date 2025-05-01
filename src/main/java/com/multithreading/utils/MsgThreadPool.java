package com.multithreading.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//创建线程池
public class MsgThreadPool {

	//Spring提供的线程池ThreadPoolTaskExecutor
	private static ThreadPoolTaskExecutor executor=null;

	public static ThreadPoolTaskExecutor getPoolInstanc() {
/*		java线程池如何合理配置核心线程数
			1.获取机器的CPU核数，：int n = Runtime.getRuntime().availableProcessors();
			2.判断线程池处理的程序是CPU密集型，还是IO密集型
				IO密集型(读写密集型):   核心线程数 = CPU核数 * 2=2n
				CPU密集型(计算密集型):  核心线程数 = CPU核数 + 1=n+1
			*/
		//获取CPU核数
		int cpuNum = Runtime.getRuntime().availableProcessors();
		try {
			executor = new ThreadPoolTaskExecutor();
			//设置核心线程数
			executor.setCorePoolSize(cpuNum);
			//设置最大线程数
			executor.setMaxPoolSize(2*cpuNum);
			//设置阻塞队列大小
			executor.setQueueCapacity(500);


			/*
			 * 设置除核心线程外的线程存活时间(最大线程数-核心线程数就是非核心线程数，
			 *  也就是救急线程数，这里的时间就是救急线程存活的时间)
			 */
			//非核心线程存活时间,默认60s
			executor.setKeepAliveSeconds(60);
			//设置线程池拒绝策略
			/*
			 * 如果线程到达 maximumPoolSize 仍然有新任务这时会执行拒绝策略。拒绝策略 jdk 提供了 4 种实现
				1.AbortPolicy（中止策略，默认策略）：直接抛出一个RejectedExecutionException异常。这会将问题抛给调用者进行处理。
					* 当任务无法被线程池执行时，会抛出一个RejectedExecutionException异常。
					* 这种策略适用于对任务丢失敏感的场景，即希望立即知道并处理这种情况。
				2.CallerRunsPolicy(呼叫者运行策略)：不会抛出异常，而是尝试在调用execute方法的线程中执行该任务。
												 这种方式可以减缓新任务提交的速度，从而让系统有机会恢复。
					*当任务无法被线程池执行时，会直接在调用者线程中运行这个任务。
					*如果调用者线程正在执行一个任务，则会创建一个新线程来执行被拒绝的任务（取决于当前线程池的状态和配置）。
					*这种策略适用于可以容忍任务在调用者线程中执行的业务场景，它允许任务继续执行而不会因为线程池资源不足而被丢弃。
				3.DiscardPolicy(丢弃策略)：直接丢弃任务，不做任何处理也不抛出异常。这个策略适合于可以丢失一些任务请求的场景。
					* 当任务无法被线程池执行时，任务将被直接丢弃，不抛出异常，也不执行任务。
					* 这种策略适用于对任务丢失不敏感的场景，即当线程池无法接受新任务时，简单地丢弃被拒绝的任务。
				4.DiscardOldestPolicy(丢弃旧任务策略)：丢弃位于工作队列头部的任务（即最早进入队列的任务），然后尝试重新提交被拒绝的任务。
													如果工作队列为空，则此策略等同于DiscardPolicy。
					* 当任务无法被线程池执行时，线程池会丢弃队列中最旧的未处理任务，然后尝试重新提交当前任务。
					* 这种策略适用于对新任务优先级较高的场景，即当线程池无法接受新任务时，会丢弃一些等待时间较长的旧任务，以便接受新任务。
			*/
			executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
			executor.initialize();
			return executor;
		} catch (Exception e) {
			executor.setCorePoolSize(3);
			executor.setMaxPoolSize(5);
			executor.setKeepAliveSeconds(60);
			executor.setQueueCapacity(20);
			executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return executor;
	}
}
