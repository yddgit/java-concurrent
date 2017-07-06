package com.my.project;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 认识Future
 * 
 * @author yang
 *
 */
public class T26_06Future {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		//指定任务执行后的返回值是Integer
		FutureTask<Integer> task = new FutureTask<>(()->{
			TimeUnit.MILLISECONDS.sleep(500);
			return 1000;
		});
		new Thread(task).start();//执行任务
		System.out.println(task.get());//阻塞：等待任务执行完获取其返回值

		//在线程池中使用Future
		ExecutorService service = Executors.newFixedThreadPool(5);
		Future<Integer> f = service.submit(()->{
			TimeUnit.MILLISECONDS.sleep(500);
			return 1;
		});
		//System.out.println(f.get());
		System.out.println(f.isDone());//是否完成
		System.out.println(f.get());
		System.out.println(f.isDone());
		service.shutdown();
	}
}
