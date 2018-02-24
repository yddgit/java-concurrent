package com.my.project;

import java.util.concurrent.ExecutorService;

/**
 * 认识 java.util.concurrent.ExecutorService ，阅读API文档
 * 
 * 用来执行任务的Executor的服务
 * 
 * 认识submit方法，扩展了execute方法，具有一个返回值
 * 
 * @author yang
 *
 */
public class T26_02ExecutorService {
	public static void main(String[] args) {
		System.out.println(ExecutorService.class.getName());
	}
}
