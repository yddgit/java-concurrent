package com.my.project;

import java.util.concurrent.TimeUnit;

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
 * InheritableThreadLocal：子线程可以共享父线程的InheritableThreadLocal变量
 * 
 * @author yang
 *
 */
public class T22ThreadLocal4<T> {

	static InheritableThreadLocal<String> tl = new InheritableThreadLocal<>();

	public static void main(String[] args) {
		new Thread(()->{
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			tl.set("Hello");
			System.out.println("T1: " + tl.get());

			// 子线程可以读到父线程中对tl的更改
			new Thread(()->{
				System.out.println("T1->C1: " + tl.get());
			}).start();

		}).start();

		// 非子线程读不到tl的更改
		new Thread(()->{
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("T2: " + tl.get());
		}).start();
	}
}
