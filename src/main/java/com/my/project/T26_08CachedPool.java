package com.my.project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CachedThreadPool
 * 
 * 新任务加入时：
 * 1.若有空闲线程则使用已有空闲线程执行该任务
 * 2.若没有空闲线程则新开一个线程执行该任务
 * 3.新开线程数上限是直到不能再新开线程为止，一般数量级可以到万（系统能支持的最大线程数）
 * 4.默认的，线程空闲时间超过keepAliveTime=60s则自动销毁
 * 
 * @author yang
 *
 */
public class T26_08CachedPool {
	public static void main(String[] args) throws InterruptedException {
		ExecutorService service = Executors.newCachedThreadPool();

		System.out.println("初始时：" + service);
		for(int i=0; i<2; i++) {
			service.execute(()->{
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName());
			});
		}
		System.out.println("执行时：" + service);
		TimeUnit.SECONDS.sleep(80);
		System.out.println("80秒后：" + service);
	}
}
