package com.my.project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;

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
 * 当使用线程池时，因为存在线程复用，InheritableThreadLocal的变量也会有问题：
 * 线程池在执行队列中的任务时，如果复用了已有的空闲线程
 * 则该线程之前对ThreadLocal变量的修改也会被子线程继承并读到
 * 
 * 总之，不论是ThreadLocal还是InheritableThreadLocal，在线程池环境下都会有问题
 * 此时，可以采用Alibaba开源的TransmittableThreadLocal解决这一问题
 * https://github.com/alibaba/transmittable-thread-local
 * 
 * 使用TransmittableThreadLocal时需要对提交到线程池的Runnable和Callable
 * 分别使用TtlRunnable和TtlCallable进行修饰
 * 
 * @author yang
 *
 */
public class T22ThreadLocal6<T> {

	static TransmittableThreadLocal<String> tl = new TransmittableThreadLocal<>();

	/**
	 * 如下是Runnable的示例，Callable的处理类似
	 */
	public static void main(String[] args) throws InterruptedException {
		// 固定存活3个线程
		ExecutorService service = Executors.newFixedThreadPool(3);
		for(int i=0; i<3; i++) {
			service.execute(TtlRunnable.get(()->{
				tl.set("Hello1");
				System.out.println("T1: " + tl.get());

				service.execute(TtlRunnable.get(()->{
					System.out.println("T1->C1: " + tl.get());
				}));
				
				service.execute(TtlRunnable.get(()->{
					System.out.println("T1->C2: " + tl.get());
				}));
			}));
			service.execute(TtlRunnable.get(()->{
				tl.set("Hello2");
				System.out.println("T2: " + tl.get());
			}));
		}
		System.out.println("线程池：" + service);
		service.awaitTermination(10, TimeUnit.SECONDS);
		service.shutdown();
	}
}
