package com.my.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 关于队列的使用
 * 1.不加锁无多线程
 *  - ArrayList
 *  - LinkedList
 * 2.要加锁(并发低)
 *  - Vector
 *  - Collections.synchronizedXXX
 * 3.并发要求高
 *  - CopyOnWriteArrayList适合写少读多的场景，事件监听器可以应用
 *  - ConcurrentLinkedQueue
 *    阻塞式队列
 *  - LinkedBlockingQueue无界队列
 *  - ArrayBlockingQueue有界队列
 *  - TransferQueue可直接将生产的任务传递给消费者线程
 *  - SynchronousQueue零容量的TransferQueue
 *  - DelayQueue执行定时任务
 * 
 * 写时复制容器在多线程环境下，写时效率低，读时效率高
 * 
 * @author yang
 *
 */
public class T25_2CopyOnWriteList {
	public static void main(String[] args) {
		List<String> arrayList = new ArrayList<>(); //会出现并发问题
		runAndComputeTime(arrayList);
		List<String> vector = new Vector<>();
		runAndComputeTime(vector);
		List<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
		runAndComputeTime(copyOnWriteArrayList);
	}

	private static void runAndComputeTime(List<String> lists) {
		Random r = new Random();
		Thread[] ths = new Thread[100];

		for(int i=0; i<ths.length; i++) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					for(int i=0; i<1000; i++) {
						lists.add("a" + r.nextInt(10000));
					}
				}
			};
			ths[i] = new Thread(task);
		}

		long start = System.currentTimeMillis();
		//执行测试线程
		Arrays.asList(ths).forEach(t->t.start());
		Arrays.asList(ths).forEach(t->{
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		//计算耗时
		long end = System.currentTimeMillis();

		System.out.println(lists.getClass().getName() + ": " + (end - start) + ", size=" + lists.size());
	}
}
