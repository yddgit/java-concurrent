package com.my.project;

import java.util.concurrent.LinkedTransferQueue;

/**
 * TransferQueue
 * 应用场景：游戏服务器转发消息
 * 消费者线程先启动，生产在生产完成后，如果发现有消费者线程已经在等待，
 * 则transfer方法会直接将任务传递给消费者线程，而不将任务存入队列
 * @author yang
 *
 */
public class T25_8TransferQueue {
	public static void main(String[] args) throws InterruptedException {
		LinkedTransferQueue<String> strs = new LinkedTransferQueue<>();

		new Thread(()->{
			try {
				System.out.println(strs.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		strs.transfer("aaa");//如果当前没有消费者线程在等待，则该方法阻塞
		//strs.put("aaa");//不会因为没有消费者线程而阻塞
		//strs.add("aaa");//不会因为没有消费者线程而阻塞

		/*
		new Thread(()->{
			try {
				System.out.println(strs.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}).start();
		*/
	}
}
