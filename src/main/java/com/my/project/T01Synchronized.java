package com.my.project;

/**
 * synchronized关键字
 * 对某个对象加锁
 * @author yang
 *
 */
public class T01Synchronized {

	private int count = 10;
	private Object o = new Object(); //锁是记录在堆内存里实际对象中的，不是引用
	
	public void m() {
		synchronized(o) { //任何线程要执行下面的代码，必须先拿到o的锁
			count --;
			System.out.println(Thread.currentThread().getName() + " count = " + count);
		}
	}
}
