package com.my.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;

/**
 * ConcurrentSkipListMap
 * http://blog.csdn.net/sunxianghuang/article/details/52221913
 * http://www.educity.cn/java/498061.html
 * 
 * 关于Map/Set的使用
 * 1.不加锁无多线程
 *  - HashMap
 *  - TreeMap
 *  - LinkedHashMap
 * 2.要加锁(并发低)
 *  - HashTable
 *  - Collections.synchronizedXXX
 * 3.并发要求高
 *  - ConcurrentHashMap
 * 4.并发要求高且要排序
 *  - ConcurrentSkipListMap
 * 
 * @author yang
 *
 */
public class T25_1ConcurrentMap {
	public static void main(String[] args) {
		//默认同步，但会锁定整个对象，效率较低
		Map<String, String> hashTable = new Hashtable<>();
		runAndComputeTime(hashTable);
		//可通过Collections.synchronizedMap(new HashMap<>())实现同步
		Map<String, String> hashMap = new HashMap<>();
		runAndComputeTime(hashMap);
		//非并发且有序
		Map<String, String> treeMap = Collections.synchronizedMap(new TreeMap<>());
		runAndComputeTime(treeMap);
		//分段锁，效率较高
		Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();
		runAndComputeTime(concurrentHashMap);
		//高并发有序且插入效率较高
		Map<String, String> concurrentSkipListMap = new ConcurrentSkipListMap<>();
		runAndComputeTime(concurrentSkipListMap);

	}

	/**
	 * Map性能测试
	 * @param map
	 */
	private static void runAndComputeTime(Map<String, String> map) {
		Random r = new Random();
		Thread[] ths = new Thread[100];
		CountDownLatch latch = new CountDownLatch(ths.length);
		long start = System.currentTimeMillis();
		for(int i=0; i<ths.length; i++) {
			ths[i] = new Thread(()->{
				for(int j=0; j<10000; j++) {
					map.put("a" + r.nextInt(100000), "a" + r.nextInt(100000));
				}
				latch.countDown();
			});
		}
		//执行测试线程
		Arrays.asList(ths).forEach(t->t.start());
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//计算耗时
		long end = System.currentTimeMillis();
		System.out.println(map.getClass().getName() + ": " + (end - start));
	}
}
