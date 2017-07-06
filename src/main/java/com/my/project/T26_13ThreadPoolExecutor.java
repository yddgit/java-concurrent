package com.my.project;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池
 * 
 * @author yang
 *
 */
public class T26_13ThreadPoolExecutor {
	public static void main(String[] args) {

		System.out.println(ThreadPoolExecutor.class.getName());

		/*
		new ThreadPoolExecutor(
				corePoolSize,//最小线程数
				maximumPoolSize,//最大线程数
				keepAliveTime,//最大存活时间，0表示线程不销毁
				unit,//存活时间单位
				workQueue //BlockingQueue
			);
		*/
	}
}
