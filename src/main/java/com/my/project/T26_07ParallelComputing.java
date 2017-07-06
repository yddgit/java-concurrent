package com.my.project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池的概念：计算1-200000之间的所有质数，对比两种方式的耗时情况
 * 
 * @author yang
 *
 */
public class T26_07ParallelComputing {
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		//串行计算
		long start = System.currentTimeMillis();
		List<Integer> result1 = getPrime(1, 200000);
		long end = System.currentTimeMillis();
		System.out.println("质数个数：" + result1.size() + "，串行耗时：" + (end-start));

		//使用线程池进行并行计算
		final int cpuCoreNum = 4; //一般线程池的数量可以定义大于等于CPU的核数
		ExecutorService service = Executors.newFixedThreadPool(cpuCoreNum);
		//以下不平均分段与质数计算的算法有关，数字越大，判断质数需要的时间越长
		MyTask t1 = new MyTask(1, 80000);//80000个数
		MyTask t2 = new MyTask(80001, 130000);//50000个数
		MyTask t3 = new MyTask(130001, 170000);//40000个数
		MyTask t4 = new MyTask(170001, 200000);//30000个数
		//将任务传递给线程池运行
		Future<List<Integer>> f1 = service.submit(t1);
		Future<List<Integer>> f2 = service.submit(t2);
		Future<List<Integer>> f3 = service.submit(t3);
		Future<List<Integer>> f4 = service.submit(t4);
		//获取结果并计算耗时
		start = System.currentTimeMillis();
		List<Integer> result2 = new ArrayList<>();
		result2.addAll(f1.get());
		result2.addAll(f2.get());
		result2.addAll(f3.get());
		result2.addAll(f4.get());
		end = System.currentTimeMillis();
		System.out.println("质数个数：" + result2.size() + "，并行耗时：" + (end-start));
		service.shutdown();
	}

	/**
	 * 计算质数的任务
	 * @author yang
	 *
	 */
	static class MyTask implements Callable<List<Integer>> {
		
		int startPos, endPos;

		MyTask(int s, int e) {
			this.startPos = s;
			this.endPos = e;
		}

		@Override
		public List<Integer> call() throws Exception {
			List<Integer> r = getPrime(startPos, endPos);
			return r;
		}

	}

	/**
	 * 判断num是否是质数
	 * @param num
	 * @return num是质数返回true，否则返回false
	 */
	static boolean isPrime(int num) {
		for(int i=2; i<num/2; i++) {
			if(num%i == 0) return false;
		}
		return true;
	}

	/**
	 * 计算start到end之间的所有质数
	 * @param start 开始数字
	 * @param end 结束数字
	 * @return start到end之间的所有质数
	 */
	static List<Integer> getPrime(int start, int end) {
		List<Integer> results = new ArrayList<>();
		for(int i=start; i<=end; i++) {
			if(isPrime(i)) results.add(i);
		}
		return results;
	}
}
