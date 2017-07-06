package com.my.project;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 高并发场景下使得Queue
 * 1.ConcurrentLinkedQueue 加锁的队列
 * 2.BlockingQueue阻塞式队列
 *   - LinkedBlockingQueue无界队列（只要内存空间足够就可以一直添加）
 *   - ArrayBlockingQueue有界队列（队列大小固定）
 * @author yang
 *
 */
public class T25_5LinkedBlockingQueue {
	private static BlockingQueue<String> strs = new LinkedBlockingQueue<>();
	private static Random r = new Random();
	public static void main(String[] args) {
		new Thread(()->{
			for(int i=0; i<100; i++) {
				try {
					strs.put("a" + i);//如果满了就会等待
					TimeUnit.MILLISECONDS.sleep(r.nextInt(1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "p1").start();

		for(int i=0; i<5; i++) {
			new Thread(()->{
				for(;;) {
					try {
						//如果空了就会等待
						String s = strs.take();
						System.out.println(Thread.currentThread().getName() + " take - " + s);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}, "c" + i).start();
		}
	}
}
