package com.my.project;

import java.util.concurrent.TimeUnit;

/**
 * 锁定某对象o，如果o的属性发生改变，不影响锁的使用
 * 但是如果o变成另外一个对象，则锁定的对象发生改变
 * 应该避免将锁定对象的引用变成另外的对象
 * @author yang
 *
 */
public class T17Synchronized {

	Object o = new Object();

	void m() {
		synchronized(o) {
			while(true) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName());
			}
		}
	}

	public static void main(String[] args) {
		T17Synchronized t = new T17Synchronized();

		//启动一个线程
		new Thread(t::m, "t1").start();

		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//创建第二个线程
		Thread t2 = new Thread(t::m, "t2");
		//锁定对象发生改变，t2可以获取新对象的锁并得以运行
		//再次证明锁是加在堆内存中的对象上，而不是在引用上
		t.o = new Object(); //如果注释掉这句，线程t2将永远得不到执行机会
		t2.start();
	}
}
