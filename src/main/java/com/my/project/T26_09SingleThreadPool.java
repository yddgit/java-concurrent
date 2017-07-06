package com.my.project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SingleThreadPool
 * 
 * 线程池中只有一个线程，可以保证任务执行的顺序性，先加入的任务先被执行
 * 
 * @author yang
 *
 */
public class T26_09SingleThreadPool {
	public static void main(String[] args) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		for(int i=0; i<5; i++) {
			final int j = i;
			service.execute(()->{
				System.out.println(j + " " + Thread.currentThread().getName());
			});
		}
		service.shutdown();
	}
}
