package com.my.project;

/**
 * synchronized关键字
 * 对某个对象加锁
 * @author yang
 *
 */
public class T04Synchronized {

	private static int count = 10;
	
	//当锁定一个静态方法时，相当于锁定当前类的class对象
	public synchronized static void m() { //这里等同于synchronized(T04Synchronized.class)
		count --;
		System.out.println(Thread.currentThread().getName() + " count = " + count);
	}

	public static void mm() {
		synchronized(T04Synchronized.class) { //这里不能写成synchronized(this)
			count --;
		}
	}
}
