package com.my.project;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Atomic类也具有可见性（可从构造函数的源码中看到value值是volatile）
 * @author yang
 *
 */
public class T15AtomXXXVisable {

	AtomicBoolean running = new AtomicBoolean(true);

	void m() {
		System.out.println("m start");
		while(running.get()) {
			//当循环体里CPU有空闲时间时可能会刷新缓存内容
			//volatile即是打开缓存过期通知
			/*
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}
		System.out.println("m end");
	}

	public static void main(String[] args) {
		T15AtomXXXVisable t = new T15AtomXXXVisable();

		new Thread(t::m, "t1").start();

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		t.running.set(false);
	}

}
