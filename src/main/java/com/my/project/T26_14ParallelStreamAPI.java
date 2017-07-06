package com.my.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ParallelStreamAPI
 * 
 * @author yang
 *
 */
public class T26_14ParallelStreamAPI {

	public static void main(String[] args) {
		//初始化数据
		List<Integer> nums = new ArrayList<>();
		Random r = new Random();
		for(int i=0; i<10000; i++) {
			nums.add(1_000_000 + r.nextInt(1_000_000));
		}
		System.out.println("要计算质数的List：" + nums);

		//使用普通forEach计算质数
		long start = System.currentTimeMillis();
		nums.forEach(v->isPrime(v));
		long end = System.currentTimeMillis();
		System.out.println("普通forEach耗时：" + (end-start));

		//使用parallelStream的forEach计算质数
		start = System.currentTimeMillis();
		nums.parallelStream().forEach(T26_14ParallelStreamAPI::isPrime);
		end = System.currentTimeMillis();
		System.out.println("parallelStream的forEach耗时：" + (end-start));
	}

	static boolean isPrime(int num) {
		for(int i=2; i<=num/2; i++) {
			if(num%i == 0) {
				return false;
			}
		}
		return true;
	}
}
