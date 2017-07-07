package com.my.project;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Java语言自己可以创建两种进程
 * 1.用户线程：创建的普通线程
 * 2.守护线程：主要用来服务用户线程
 * 
 * 守护线程：
 * The Java Virtual Machine exits when the only threads running are all daemon threads.
 * 当运行的线程只剩下守护线程的时候，JVM就会退出，如果还有其他的任意一个用户线程还在运行，JVM就不会退出
 * 
 * 运行以下程序，并使用JDK中自带的查看堆栈工具jstack查看线程堆栈(用法：jstack PID)
 * 
 * @author yang
 *
 */
public class DaemonThread implements Runnable {

	public void run() {
		while (true) {
			for (int i = 1; i <= 100; i++) {
				System.out.println(i);
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Thread daemonThread = new Thread(new DaemonThread(), "DAEMON-THREAD-TEST");
		// 设置为守护进程
		daemonThread.setDaemon(true);
		// 启动线程
		daemonThread.start();
		System.out.println(daemonThread.getName() + " isDaemon: " + daemonThread.isDaemon());
		// 使程序在此阻塞，一旦接受到用户输入，main线程结束，JVM退出
		System.in.read();
		// AddShutdownHook方法增加JVM停止时要做处理事件：
		// 当JVM退出时，打印JVM Exit语句
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("JVM Exit!");
			}
		});
	}

}