package com.my.project;

/**
 * 分析程序的输出
 * @author yang
 *
 */
public class T05Synchronized implements Runnable {

	private static int count = 10;
	
	public /*synchronized*/ void run() {
		count --;
		System.out.println(Thread.currentThread().getName() + " count = " + count);
	}

	public static void main(String[] args) {
		T05Synchronized t = new T05Synchronized();
		//新建5个线程共同访问对象T
		for(int i=0; i<5; i++) {
			new Thread(t, "THREAD" + i).start();
		}
		//因为--运算不具有原子性，所以每个线程输出count值都不一定对，有线程重入的问题
		//给run方法加上synchronized关键字就可以保证count值被正确修改并输出
	}
}
