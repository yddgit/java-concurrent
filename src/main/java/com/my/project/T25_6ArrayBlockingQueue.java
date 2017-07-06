package com.my.project;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 高并发场景下使得Queue
 * 1.ConcurrentLinkedQueue 加锁的队列
 * 2.BlockingQueue阻塞式队列
 *   - LinkedBlockingQueue无界队列（只要内存空间足够就可以一直添加）
 *   - ArrayBlockingQueue有界队列（队列大小固定）
 * @author yang
 *
 */
public class T25_6ArrayBlockingQueue {
	private static BlockingQueue<String> strs = new ArrayBlockingQueue<>(10);
	public static void main(String[] args) throws InterruptedException {
		for(int i=0; i<10; i++) {
			strs.put("a" + i);
		}

		strs.put("aaa");//如果满了就会等待（阻塞）
		//strs.add("aaa");//如果添加不成功会抛异常：Queue full
		//strs.offer("aaa");//根据返回值是否添加成功
		//strs.offer("aaa", 1, TimeUnit.SECONDS);//按时间段阻塞，如果指定时间内添加不成功返回false

		System.out.println(strs);
	}
}
