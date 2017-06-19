package com.my.project;

import java.util.ArrayList;
import java.util.List;

/**
 * 对比上一个程序，可以用synchronized解决
 * - volatile只保证可见性
 * - synchronized即保证可见性，又保证原子性
 * @author yang
 *
 */
public class T14Volatile {

	/*volatile*/ int count = 0;

	synchronized void m() {
		for(int i=0; i<10000; i++) count++;
	}

	public static void main(String[] args) {
		T14Volatile t = new T14Volatile();

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
