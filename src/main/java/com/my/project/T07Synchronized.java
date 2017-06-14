package com.my.project;

/**
 * 同步和非同步方法是否可以同时调用？
 * 答：可以同时调用，非同步方法执行时不需要获取对象锁
 * @author yang
 *
 */
public class T07Synchronized {

	public synchronized void m1() {
		System.out.println(Thread.currentThread().getName() + " m1 start...");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + " m1 end");
	}

	public void m2() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + " m2 ");
	}

	public static void main(String[] args) {
		T07Synchronized t = new T07Synchronized();

		new Thread(()->t.m1(), "t1").start();
		new Thread(()->t.m2(), "t2").start();
		
		//另外一种写法
		//new Thread(t::m1, "t1").start();
		//new Thread(t::m2, "t2").start();

		//输出：
		//t1 m1 start...
		//t2 m2
		//t1 m1 end
	}
}
