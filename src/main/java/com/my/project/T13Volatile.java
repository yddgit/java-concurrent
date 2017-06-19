package com.my.project;

import java.util.ArrayList;
import java.util.List;

/**
 * volatile并不能保证多个线程共同修改变量时所带来的不一致问题，也就是说volatile不能替代synchronized
 * 运行下面的程序，并分析结果
 * - volatile只保证可见性
 * - synchronized即保证可见性，又保证原子性
 * @author yang
 *
 */
public class T13Volatile {

	volatile int count = 0;

	void m() {
		for(int i=0; i<10000; i++) count++;
	}

	public static void main(String[] args) {
		T13Volatile t = new T13Volatile();

		List<Thread> threads = new ArrayList<Thread>();

		for(int i=0; i<10; i++) {
			threads.add(new Thread(t::m, "thread-"+i));
		}

		threads.forEach((o) -> o.start());

		//等待所有线程执行结束
		threads.forEach((o) -> {
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		System.out.println(t.count);
	}

}
