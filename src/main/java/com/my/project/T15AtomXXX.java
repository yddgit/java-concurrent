package com.my.project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 解决同样的问题的更高效的方法，使用AtomXXX类
 * AtomXXX类本身方法都是原子性的，但不能保证多个方法调用是原子性的
 * @author yang
 *
 */
public class T15AtomXXX {

	/*volatile*/ //int count = 0;
	AtomicInteger count = new AtomicInteger(0);

	/*synchronized*/ void m() {
		for(int i=0; i<10000; i++) {
			//count++; //++运算不具有原子性
			count.incrementAndGet(); //此方法具有原子性
			//注意：下面一段代码不具有原子性
			/*
			if(count.get() < 1000) {
				count.incrementAndGet();
			}
			*/
		}
	}

	public static void main(String[] args) {
		T15AtomXXX t = new T15AtomXXX();

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
