package com.my.project;

/**
 * synchronized关键字
 * 对某个对象加锁
 * @author yang
 *
 */
public class T02Synchronized {

	private int count = 10;
	
	public void m() {
		//synchronized锁定的是对象不是代码块
		synchronized(this) { //任何线程要执行下面的代码，必须先拿到this的锁
			count --;
			System.out.println(Thread.currentThread().getName() + " count = " + count);
		}
	}
}
