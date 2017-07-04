package com.my.project;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 有N张火车票，每张票都有一个编号
 * 同时有10个窗口对外售票
 * 请写一个模拟程序
 * 
 * 分析下面的程序可能会产生哪些问题？
 * 重复销售？超量销售？没有问题，但效率较低
 * 
 * @author yang
 *
 */
public class T24TicketSeller3 {
	static List<String> tickets = new LinkedList<String>();

	static {
		for(int i=0; i<1000; i++) tickets.add("票编号：" + i);
	}

	public static void main(String[] args) {
		for(int i=0; i<10; i++) {
			new Thread(()->{
				while(true) {
					synchronized(tickets) {
						if(tickets.size() <= 0) break;

						try {
							TimeUnit.MILLISECONDS.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						System.out.println("销售了--" + tickets.remove(0));
					}
				}
			}).start();
		}
	}
}
