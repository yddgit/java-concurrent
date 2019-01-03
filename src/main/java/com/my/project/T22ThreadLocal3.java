package com.my.project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadLocal线程局部变量
 * 
 * ThreadLocal是使用空间换时间，synchronized是使用时间换空间
 * 比如在hibernate中session就存在ThreadLocal中，避免synchronized的使用
 * 
 * 当使用线程池时，因为存在线程复用，ThreadLocal的变量会有问题：
 * 线程池执行任务时仅仅是调用用户提交的run方法，如果复用了已有的空闲线程
 * 则该线程之前对ThreadLocal变量的修改就会被run方法读到
 * 
 * @author yang
 *
 */
public class T22ThreadLocal3<T> {

	static ThreadLocal<String> tl = new ThreadLocal<>();

	public static void main(String[] args) {
		// 固定存活3个线程
		ExecutorService service = Executors.newFixedThreadPool(3);
		for(int i=0; i<3; i++) {
			service.execute(()->{
				tl.set("Hello");
				System.out.println("T1: " + tl.get());
				// 当ThreadLocal变量使用完，记得调用remove方法
				//tl.remove();
			});
			service.execute(()->{
				System.out.println("T2: " + tl.get());
			});
		}
		System.out.println("线程池：" + service);
		service.shutdown();
	}
}
