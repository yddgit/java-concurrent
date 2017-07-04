package com.my.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 写时复制容器 copy on write
 * 多线程环境下，写时效率低，读时效率高
 * 适合写少读多的环境
 * 
 * 事件监听器可以应用
 * 
 * @author yang
 *
 */
public class T25CopyOnWriteList {
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
