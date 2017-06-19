package com.my.project;

import java.util.concurrent.TimeUnit;

/**
 * 不要以字符串常量作为锁定对象
 * 在下面的例子中，m1和m2其实锁定的是同一个对象
 * 这种情况还会发生比较诡异的现象，比如用到了一个类库，在该类库中代码锁定了字符串"hello"，
 * 但是你读不到源码，所以你在自己的代码中也锁定了"hello"，这时候就有可能发生非常诡异的死锁阻塞，
 * 因为你的程序和你用到的类库不经意间使用了同一把锁
 * @author yang
 *
 */
public class T18Synchronized {

	String s1 = "hello";
	String s2 = "hello";

	void m1() {
		synchronized(s1) {
			while(true) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("m1 invoked");
			}
		}
	}

	void m2() {
		synchronized(s2) {
			System.out.println("m2 invoked");
		}
	}

	public static void main(String[] args) {
		T18Synchronized t = new T18Synchronized();
		new Thread(t::m1, "t1").start();
		new Thread(t::m2, "t2").start(); //m2方法一直得不到执行
	}
}
