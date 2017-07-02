package com.my.project;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 面试题：
 * 写一个固定容量同步容器，拥有put和get方法，以及getCount方法，
 * 能够支持2个生产者线程以及10个消费者线程的阻塞调用
 * 
 * 使用wait和notify/notifyAll来实现
 * @author yang
 *
 */
public class T21Container1<T> {

	final private LinkedList<T> lists = new LinkedList<>();
	final private int MAX = 10; //最多10个元素
	private int count = 0;

	public synchronized void put(T t) {
		//用while可以保证线程被唤醒的时候再次通过while条件判断一次容器的容量是否已满
		//若用if则线程被唤醒之后就会直接往下执行，而不检查容器容量，多线程下就容易出错
		while(lists.size() == MAX) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lists.add(t);
		++count;
		//如果用notify只会叫醒一个线程，可能又叫醒一个生产者线程，此时就会出错
		this.notifyAll(); //通知消费者线程进行消费
	}

	public synchronized T get() {
		T t = null;
		while(lists.size() == 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t = lists.removeFirst();
		count --;
		this.notifyAll(); //通知生产者线程进行生产
		return t;
	}

	public static void main(String[] args) {
		T21Container1<String> c = new T21Container1<>();
		//启动消费者线程
		for(int i=0; i<10; i++) {
			new Thread(()->{
				for(int j=0; j<5; j++) {
					System.out.println(c.get());
				}
			}, "c" + i).start();
		}

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//启动生产者线程
		for(int i=0; i<2; i++) {
			new Thread(()->{
				for(int j=0; j<25; j++) {
					c.put(Thread.currentThread().getName() + " " + j);
				}
			}, "p" + i).start();
		}
	}
}
