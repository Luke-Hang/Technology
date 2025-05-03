package com.multithreading.utils;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//创建线程池
public class MsgThreadPool {

	//Spring提供的线程池ThreadPoolTaskExecutor
	private static ThreadPoolTaskExecutor executor=null;

	public static ThreadPoolTaskExecutor getPoolInstance() {
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
			//设置阻塞队列大小为500，表示有界队列
			executor.setQueueCapacity(500);
			/**
			 * 线程池队列：https://blog.csdn.net/qq_39666711/article/details/140486386
			 * 	1.无界队列：
			 * 		队列容量理论上是无限的，容量受限于JVM内存。当内存耗尽时，会抛出OutOfMemoryError
			 * 		用于任务量非常大，且任务执行时间较长，LinkedBlockingQueue 不指定容量或指定容量为Integer.MAX_VALUE
			 * 	2、有界队列：
			 * 		队列有一个固定的容量限制，当队列满时，尝试添加新任务的操作会被阻塞，直到队列中有空间可用
			 * 		ArrayBlockingQueue
			 * 		适用于需要控制任务数量，防止资源耗尽的场景。通过调整队列大小和线程池大小，可以灵活控制任务的并发执行
			 * 	3、直接提交队列（SynchronousQueue）：
			 * 		这种队列实际上并不存储任何元素，要添加新任务必须得有空闲的线程才能添加
			 * 		适用于任务处理时间较短，且生产者和消费者速度大致匹配的场景。它可以有效减少任务在队列中的等待时间，提高系统的响应速度。
			 * 	4、优先级队列：
			 * 		队列中的元素会根据其优先级进行排序，优先级高的元素会先被取出执行
			 * 		PriorityBlockingQueue，适用于需要按照任务优先级顺序执行的场景。通过调整任务的优先级，可以确保重要任务得到优先处理。
			 *
			 *
			 * 在选择线程池中的队列时，需要根据具体的应用场景和需求来决定：
			 *
			 * 	1.任务类型和特点：如果任务处理时间较长，且任务量不确定，可以选择无界队列；如果任务量较大且需要控制并发数，可以选择有界队列。
			 * 	2.系统资源：考虑系统的内存和CPU资源。无界队列虽然可以处理大量任务，但存在内存溢出的风险；
			 * 	  有界队列可以避免内存溢出，但需要合理设置队列大小和线程池大小。
			 * 	3.性能要求：如果要求系统响应速度快，且任务处理时间较短，可以选择直接提交队列或优先级队列。
			 * 	4.任务优先级：如果任务有明确的优先级要求，可以选择优先级队列。
			 */


			/*
			 * 设置除核心线程外的线程存活时间(最大线程数-核心线程数就是非核心线程数，
			 *  也就是救急线程数，这里的时间就是救急线程存活的时间)
			 */
			//非核心线程存活时间,默认60s
			executor.setKeepAliveSeconds(60);
			//设置线程池拒绝策略
			/*https://blog.csdn.net/suifeng629/article/details/98884972
			 * 如果线程到达 maximumPoolSize 仍然有新任务这时会执行拒绝策略。拒绝策略 jdk 提供了 4 种实现
				1.AbortPolicy（中止策略，线程池默认拒绝策略--中止任务，抛出异常）：当任务不能再提交时，抛出异常，及时反馈程序运行状态。
					* 如果是比较关键的业务，推荐使用此拒绝策略，在系统不能承载更大的并发量时，能够及时的通过异常发现。
				2.CallerRunsPolicy(呼叫者运行策略--原调用线程处理)：由调用线程处理该任务
					* 如果任务被拒绝了，则由原调用线程（提交任务的线程）直接执行此任务。
				3.DiscardPolicy(丢弃策略--不抛异常，直接丢弃)：丢弃任务，但是不抛异常。如果线程队列已满，后续提交的任务都会被丢弃。
					* 无法发现系统的异常状态。对于一些无关紧要的业务采用此策略。
				4.DiscardOldestPolicy(丢弃旧任务策略--丢旧执新)：丢弃队列最前面的任务，重新提交被拒绝的任务
					* 一种喜新厌旧的拒绝策略，根据实际业务是否允许丢弃老任务来认真衡量是否采用此策略
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
