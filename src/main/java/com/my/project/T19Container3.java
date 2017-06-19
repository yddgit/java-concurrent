package com.my.project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 面试题：
 * 实现一个容器，提供两个方法，add，size
 * 写两个线程，线程1添加10个元素到容器中，线程2实现监控元素的个数，当个数到5个时，线程2给出提示并结束
 * 
 * 给lists添加volatile之后，t2能够接到通知，但是，t2线程的死循环很浪费cpu，如果不用死循环，该怎么做呢？
 * 
 * 这里使用wait和notify做到，wait会释放锁，而notify不会释放锁
 * 需要注意的是，运用这种方法，必须要保证t2先执行，也就是首先让t2监听才可以
 * wait/notify必须在锁定对象上调用
 * - wait当前线程进入等待状态并释放锁，只有其他线程调用notify方法才能唤醒该线程
 * - notify唤醒在锁定对象上等待的某个线程
 * - notifyAll唤醒在锁定对象上等待的所有线程
 * 
 * 阅读下面的程序，并分析输出结果
 * 可以读到输出结果并不是size=5时t2退出，而是t1结束时t2才接收到通知而退出
 * 想想这是为什么？
 * @author yang
 *
 */
public class T19Container3 {

	//添加volatile使用t2得到通知
	volatile List<Object> lists = new ArrayList<Object>();

	public void add(Object o) {
		lists.add(o);
	}

	public int size() {
		return lists.size();
	}

	public static void main(String[] args) {
		T19Container3 c = new T19Container3();

		final Object lock = new Object();
		
		new Thread(() -> {
			synchronized(lock) {
				System.out.println("t2 start");
				if(c.size() != 5) {
					try {
						lock.wait();//释放锁
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("t2 end");
			}
		}, "t2").start();

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			System.out.println("t1 start");
			synchronized(lock) {
				for(int i=0; i<10; i++) {
					c.add(new Object());
					System.out.println("add " + i);

					if(c.size() == 5) {
						lock.notify();//不会释放锁，所以等t1执行完锁被释放后t2才能继续执行直至结束
					}

					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "t1").start();
	}
}
