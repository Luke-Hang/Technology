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
				1.AbortPolicy 让调用者抛出 RejectedExecutionException 异常，这是默认策略
				2.CallerRunsPolicy 调用者运行策略实现了一种调节机制，该策略既不会抛弃任务，也不会抛出异常，
					* 而是将某些任务回退到调用者，降低新任务的流量。
					* 当线程池中的所有线程都被占用，并且工作队列被填满后，主线程至少在一段时间内不能提交任何任务，
					* 从而使得工作者线程有时间来处理完正在执行的任务。
				3.DiscardPolicy 放弃本次任务
				4.DiscardOldestPolicy 放弃队列中最早的任务，本任务取而代之
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

/*			unsigned
			decimal
			char
			varchar
			blob
			datetime
			timestamp
			tinyint

		*/
		}
		return executor;
	}
}
