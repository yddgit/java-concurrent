package com.my.project;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * SynchronousQueue容量为0，特殊的TransferQueue
 * 生产者在生产完成后，必须被消费者线程立即消费
 * @author yang
 *
 */
public class T25_9SynchronousQueue {
	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<String> strs = new SynchronousQueue<>();

		new Thread(()->{
			try {
				System.out.println(strs.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		strs.put("aaa"); //阻塞等待消费者消费，内部调用的transfer
		//strs.add("aaa"); //会抛异常：Queue full
		System.out.println(strs.size());
	}
}
