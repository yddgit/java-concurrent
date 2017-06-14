package com.my.project;

import java.util.concurrent.TimeUnit;

/**
 * 对业务写方法加锁
 * 对业务读方法不加锁
 * 容易产生脏读问题(dirtyRead)
 * @author yang
 *
 */
public class T08Account {

	String name;
	double balance;
	
	public synchronized void set(String name, double balance) {
		this.name = name;

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.balance = balance;
	}

	public /*synchronized*/ double getBalance(String name) {
		return this.balance;
	}

	public static void main(String[] args) {

		T08Account t = new T08Account();

		//设置balance=100.0
		new Thread(()->t.set("zhangsan", 100.0)).start();
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//第一次读时没有读到正确的balance
		System.out.println(t.getBalance("zhangsan"));
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//第二次读时才读到正确的balance
		System.out.println(t.getBalance("zhangsan"));
		
	}
}
