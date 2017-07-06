package com.my.project;

import java.util.concurrent.Executor;

/**
 * 认识Executor
 * 
 * 实现Executer接口，可以将任务传递给execute方法直接执行或用线程执行
 * 
 * @author yang
 *
 */
public class T26_01MyExecutor implements Executor {
	public static void main(String[] args) {
		new T26_01MyExecutor().execute(()->System.out.println("hello executor"));
	}

	@Override
	public void execute(Runnable command) {
		//new Thread(command).run();
		command.run();
	}
}
