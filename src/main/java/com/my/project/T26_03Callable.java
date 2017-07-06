package com.my.project;

import java.util.concurrent.Callable;

/**
 * 认识Callable，对Runnable进行了扩展
 * 对Callable的call()方法调用，相比Runnable，Callable可以有返回值也可以抛出异常
 * 
 * @author yang
 *
 */
public class T26_03Callable {
	public static void main(String[] args) {
		System.out.println(Callable.class.getName());
	}
}
