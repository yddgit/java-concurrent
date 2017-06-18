package com.my.project;

import java.util.concurrent.TimeUnit;

/**
 * synchronized优化
 * 同步代码块中的语句越少越好
 * 比较m1和m2
 * @author yang
 *
 */
public class T16Synchronized {

	int count = 0;

	/**
	 * 粗粒度锁效率低
	 */
	synchronized void m1() {
		//do something need not sync
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//业务逻辑中只有下面这句需要sync，这时不应该给整个方法上锁
		count++;
		//do something need not sync
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 细粒度锁效率高
	 */
	void m2() {
		//do something need not sync
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//业务逻辑中只有下面这句需要sync，这时不应该给整个方法上锁
		synchronized(this) {
			count++;
		}
		//do something need not sync
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
