package com.my.project;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class T25ConcurrentQueue {
	public static void main(String[] args) {
		Queue<String> strs = new ConcurrentLinkedQueue<>();

		for(int i=0; i<10; i++) {
			//添加元素，相比add不会出异常，可以根据返回值判断是否添加成功
			strs.offer("a" + i);
		}

		System.out.println(strs);
		System.out.println(strs.size());
		System.out.println(strs.poll());//取出一个元素，并删除之
		System.out.println(strs.size());
		System.out.println(strs.peek());//取出一个元素，但不删除
		System.out.println(strs.size());

		//双端队列
		//Deque<String> strs = new ConcurrentLinkedDeque<>();
	}
}
