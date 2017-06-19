package com.my.project;

/**
 * 对比上一个小程序，分析一下这个程序的输出
 * @author yang
 *
 */
public class T06Synchronized implements Runnable {

	private static int count = 10;
	
	public synchronized void run() {
		count --;
		System.out.println(Thread.currentThread().getName() + " count = " + count);
	}

	public static void main(String[] args) {
		T06Synchronized t = new T06Synchronized();
		//新建5个线程共同访问对象T
		for(int i=0; i<5; i++) {
			new Thread(t, "THREAD" + i).start();
		}
		//给run方法加上synchronized关键字就可以保证count值被正确修改并输出
	}
}
