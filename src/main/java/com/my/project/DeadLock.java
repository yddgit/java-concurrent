package com.my.project;

import java.util.concurrent.TimeUnit;

/**
 * 模拟死锁
 * @author yang
 *
 */
public class DeadLock {

	public static void main(String[] args) {
		Object o1 = new Object();
		Object o2 = new Object();
		//线程1要先获得o1的锁再获得o2的锁
		new Thread(() -> {
			synchronized(o1) {
				System.out.println("Thread 1 get the lock of o1");
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized(o2) {
					System.out.println("Thread 1 get the lock of o2");
				}
			}
		}).start();
		//线程2要先获得o2的锁再获得o1的锁，但此时o1的锁已被线程1持有，线程2无法获得
		new Thread(() -> {
			synchronized(o2) {
				System.out.println("Thread 2 get the lock of o2");
				synchronized(o1) {
					System.out.println("Thread 2 get the lock of o1");
				}
			}
		}).start();
	}

}
