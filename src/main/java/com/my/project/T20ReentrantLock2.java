package com.my.project;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock用于替代synchronized
 * 本例中由于m1锁定this，只有m1执行完毕的时候，m2才能执行
 * 这里是复习synchronized最原始的语义
 * 
 * 使用ReentrantLock可以完成同样的功能
 * 需要注意的是，必须要必须要必须要手动释放锁（重要的事情说三遍）
 * 使用synchronized锁定的话如果遇到异常，JVM会自动释放锁，但是Lock必须手动释放锁，因此经常在finally中进行锁的释放
 * @author yang
 *
 */
public class T20ReentrantLock2 {

	Lock lock = new ReentrantLock();

	void m1() {
		try {
			lock.lock(); //synchronized(this)
			for(int i=0; i<10; i++) {
				TimeUnit.SECONDS.sleep(1);
				System.out.println(i);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
 
	void m2() {
		lock.lock();
		System.out.println("m2 ...");
		lock.unlock();
	}

	public static void main(String[] args) {
		T20ReentrantLock2 t = new T20ReentrantLock2();
		new Thread(t::m1).start();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(t::m2).start();
	}
}
