package com.my.project.java;

/**
 * 关于@FuncionalInterface，主要用于编译级错误检查，并不是必须的。
 * 它只是提醒编译器去检查接口是否仅包含一个抽象方法
 * 1. 标记在有且仅有一个抽象方法的接口上
 * 2. 允许覆盖Object中的方法
 * 3. 允许定义默认方法
 * 4. 允许定义静态方法
 * @author by84
 */
@FunctionalInterface
public interface GreetingService {

	/**
	 * 只能有一个抽象方法，Single Abstract Method Interface
	 * @param message Message
	 */
	void sayMessage(String message);

	/**
	 * 允许定义默认方法
	 */
	default void doSomeMoreWork() {
		System.out.println("do some more work...");
	}

	/**
	 * 允许定义静态方法
	 */
	static void printHello() {
		System.out.println("Hello");
	}

	/**
	 * 允许覆盖Object中的方法
	 */
	@Override
	boolean equals(Object obj);

}
