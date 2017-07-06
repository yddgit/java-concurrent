package com.my.project;

import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal线程局部变量
 * 
 * @author yang
 *
 */
public class T22ThreadLocal1<T> {

	volatile static Person p = new Person();

	public static void main(String[] args) {

		new Thread(()->{
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(p.name);
		}).start();

		new Thread(()->{
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p.name = "Lily";
		}).start();
	}

	static class Person {
		String name = "Lucy";
	}
}
