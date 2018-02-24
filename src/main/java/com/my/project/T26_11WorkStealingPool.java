package com.my.project;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 工作窃取（线程池内所有线程都是Daemon线程，内部也是ForkJoinPool）
 * 
 * 1.每个线程维护自己的任务队列
 * 2.当某个线程自己的任务队列执行完后，会主动从其他线程的任务队列中获取任务来执行
 * 
 * @author yang
 *
 */
public class T26_11WorkStealingPool {
	public static void main(String[] args) throws IOException {

		ExecutorService service = Executors.newWorkStealingPool();

		//WorkStealingPool根据CPU核数启动对应个数的线程
		System.out.println("CPU核数：" + Runtime.getRuntime().availableProcessors());

		service.execute(new RunTask(1000));
		service.execute(new RunTask(2000));
		service.execute(new RunTask(2000));
		service.execute(new RunTask(2000));
		service.execute(new RunTask(2000));

		//由于产生的是Daemon线程（守护线程、后台线程），主线程不阻塞的话，看不到输出
		System.in.read();
	}

	static class RunTask implements Runnable {
		int time;

		RunTask(int t) {
			this.time = t;
		}

		@Override
		public void run() {
			try {
				TimeUnit.MILLISECONDS.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(time + " " + Thread.currentThread().getName());
		}
	}
}
