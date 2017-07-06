package com.my.project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的概念
 * 1.一堆线程等待执行任务
 * 2.维护一个待执行任务队列（BlockingQueue）
 * 3.维护一个已结束任务队列（BlockingQueue）
 * 
 * @author yang
 *
 */
public class T26_05ThreadPool {
	public static void main(String[] args) throws InterruptedException {
		//新建一个线程池：execute执行Runnable，submit执行Callable
		ExecutorService service = Executors.newFixedThreadPool(5);
		for(int i=0; i<6; i++) {
			service.execute(()->{
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName());
			});
		}
		System.out.println("线程池：" + service);

		service.shutdown();//正常关闭线程池，等待所有任务执行完毕后关闭线程池
		//service.shutdownNow();//直接关闭，不管任务是否执行完
		System.out.println("所有任务是否执行完毕: " + service.isTerminated());
		System.out.println("线程池是否已关闭: " + service.isShutdown());
		System.out.println("线程池：" + service);

		TimeUnit.SECONDS.sleep(5);
		System.out.println("所有任务是否执行完毕: " + service.isTerminated());
		System.out.println("线程池是否已关闭: " + service.isShutdown());
		System.out.println("线程池：" + service);
	}
}
