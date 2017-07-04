package com.my.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class T25SynchronizedList {
	public static void main(String[] args) {
		List<String> arrayList = new ArrayList<>();
		//给List加锁
		List<String> syncArrayList = Collections.synchronizedList(arrayList);
		runAndComputeTime(syncArrayList);
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
