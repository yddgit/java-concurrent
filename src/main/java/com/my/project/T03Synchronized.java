package com.my.project;

/**
 * synchronized关键字
 * 对某个对象加锁
 * @author yang
 *
 */
public class T03Synchronized {

	private int count = 10;
	
	//当整个方法的代码都需要锁定时，可以直接在方法声明中加上synchronized关键字
	public synchronized void m() { //等同于在方法的代码执行时要synchronized(this)
		count --;
		System.out.println(Thread.currentThread().getName() + " count = " + count);
	}
}
