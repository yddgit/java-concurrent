package com.my.project;

import java.util.Arrays;

/**
 * 线程安全的单例模式
 * 
 * 阅读文章：http://www.cnblogs.com/xudong-bupt/p/3433643.html
 * 
 * 更好的是采用下面的方式，既不用加锁，也能实现懒加载
 * 
 * @author yang
 *
 */
public class T23Singleton {
	private T23Singleton() {
		System.out.println("single");
	}

	private static class Inner {
		private static T23Singleton s = new T23Singleton();
	}

	public static T23Singleton getSingle() {
		return Inner.s;
	}

	public static void main(String[] args) {
		Thread[] ths = new Thread[200];
		for(int i=0; i<ths.length; i++) {
			ths[i] = new Thread(()->{
				T23Singleton.getSingle();
			});
		}
		Arrays.asList(ths).forEach(o->o.start());
	}
}
